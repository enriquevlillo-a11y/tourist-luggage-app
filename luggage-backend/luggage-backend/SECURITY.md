# SECURITY.md

Security guidelines and best practices for the Luggage Storage Application.

## Table of Contents

1. [Environment Variables](#environment-variables)
2. [Authentication & Authorization](#authentication--authorization)
3. [Password Security](#password-security)
4. [Database Security](#database-security)
5. [API Security](#api-security)
6. [Data Privacy](#data-privacy)
7. [Production Checklist](#production-checklist)
8. [Vulnerability Reporting](#vulnerability-reporting)

---

## Environment Variables

### Overview

All sensitive configuration data is stored in environment variables, NOT in the codebase. The `.env` file contains these variables but is excluded from version control.

### Setup

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Update with your values:**
   - Never use default/example values in production
   - Generate secure random values for secrets
   - Use different values for each environment (dev, staging, prod)

3. **Verify .gitignore:**
   Ensure `.env` is in `.gitignore` to prevent accidental commits:
   ```bash
   grep -q "^\.env$" .gitignore && echo "✓ .env is ignored" || echo "✗ WARNING: .env is NOT ignored!"
   ```

### Sensitive Variables

#### JWT Secret (`JWT_SECRET`)

**Risk Level:** 🔴 CRITICAL

- Used to sign and verify JWT tokens
- If compromised, attackers can forge authentication tokens

**Generate a secure secret:**
```bash
# Generate a 256-bit (32-byte) secret
openssl rand -base64 32

# Or 512-bit (64-byte) for extra security
openssl rand -base64 64
```

**Best Practices:**
- Use at least 256 bits of entropy
- Rotate secrets periodically (implement token invalidation)
- Use different secrets for each environment
- Never log or display the secret
- Store in secure secret management systems (AWS Secrets Manager, HashiCorp Vault, etc.)

#### Database Credentials

**Risk Level:** 🔴 CRITICAL

- `DB_USERNAME` and `DB_PASSWORD`
- If compromised, attackers can access all application data

**Best Practices:**
- Use strong, randomly generated passwords
- Create separate database users for application and admin tasks
- Grant minimal required permissions (principle of least privilege)
- Enable SSL/TLS for database connections in production
- Rotate credentials periodically
- Never use default credentials (`luggo`/`luggo` is for development only!)

#### Database Connection String

**Risk Level:** 🟡 HIGH

- Contains host, port, and database name
- Exposure can reveal infrastructure details

**Best Practices:**
- Use private networking for database access
- Don't expose database ports to the public internet
- Use connection pooling to limit connections

---

## Authentication & Authorization

### JWT (JSON Web Tokens)

#### Token Lifecycle

1. **Generation:** User logs in with email/password
2. **Issuance:** Server generates JWT with user claims (userId, email, role)
3. **Usage:** Client includes token in `Authorization: Bearer <token>` header
4. **Validation:** Server validates token signature and expiration
5. **Expiration:** Tokens expire after 24 hours (configurable)

#### Security Measures

**✓ Implemented:**
- Stateless authentication (no session storage)
- Token expiration (24 hours default)
- HMAC-SHA256 signing algorithm
- User claims included in token (userId, email, role)
- Authorization header extraction

**⚠️ Production Improvements Needed:**

1. **Token Refresh Mechanism**
   - Implement refresh tokens with longer expiration
   - Short-lived access tokens (15-30 minutes)
   - Refresh endpoint to obtain new access tokens

2. **Token Revocation**
   - Maintain a blacklist of revoked tokens
   - Use Redis for fast token revocation lookups
   - Implement "logout" functionality

3. **Token Rotation**
   - Rotate signing keys periodically
   - Support multiple signing keys for zero-downtime rotation

4. **Rate Limiting**
   - Limit login attempts per IP (prevent brute force)
   - Limit token generation per user

#### Security Headers

**Required for Production:**

```java
// Add to SecurityConfig
http.headers(headers -> headers
    .contentSecurityPolicy("default-src 'self'")
    .xssProtection()
    .frameOptions().deny()
    .httpStrictTransportSecurity()
);
```

---

## Password Security

### BCrypt Hashing

**Implementation:** Spring Security's `BCryptPasswordEncoder`

**Configuration:**
- Algorithm: BCrypt
- Cost Factor: 10 (default, 2^10 iterations)
- Hash Format: `$2a$10$...` (60 characters)

**Verification:**
```java
// DO THIS:
passwordEncoder.matches(plainPassword, hashedPassword)

// NEVER DO THIS:
plainPassword.equals(hashedPassword)
```

### Password Requirements

**⚠️ Currently NOT Enforced - Implement Before Production:**

**Minimum Requirements:**
- Length: 8+ characters (recommend 12+)
- Complexity: At least 3 of:
  - Uppercase letters
  - Lowercase letters
  - Numbers
  - Special characters
- Not common passwords (check against dictionary)
- Not user's email or name

**Implementation Example:**
```java
@Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must be at least 8 characters and contain uppercase, lowercase, number, and special character"
)
private String password;
```

### Password Reset

**⚠️ NOT IMPLEMENTED - Required for Production:**

**Secure Password Reset Flow:**
1. User requests password reset
2. Generate secure random token (UUID or JWT)
3. Store token in database with expiration (1-24 hours)
4. Send reset link to user's email (never reveal if email exists)
5. User clicks link and enters new password
6. Validate token, update password, invalidate token
7. Notify user via email that password was changed

---

## Database Security

### Connection Security

**Current Setup (Development):**
```
postgresql://luggo:luggo@localhost:5432/luggage-backend
```

**Production Setup:**
```properties
# Use environment variables
spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?sslmode=require
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Enable SSL
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.ssl=true
```

### SQL Injection Prevention

**✓ Protected:**
- All queries use JPA/JDBC parameterized statements
- Native queries use positional parameters (`?1`, `?2`)
- No string concatenation of user input

**Example:**
```java
// ✓ SAFE - Parameterized
@Query("SELECT l FROM Location l WHERE l.city = ?1")
List<Location> findByCity(String city);

// ✗ DANGEROUS - String concatenation (NEVER DO THIS)
String query = "SELECT * FROM locations WHERE city = '" + city + "'";
```

### Database Access Control

**Production Recommendations:**

1. **Separate Database Users:**
   ```sql
   -- Application user (limited permissions)
   CREATE USER app_user WITH PASSWORD 'secure_password';
   GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;

   -- Admin user (full permissions)
   CREATE USER admin_user WITH PASSWORD 'admin_password';
   GRANT ALL PRIVILEGES ON DATABASE luggage_backend TO admin_user;
   ```

2. **Row-Level Security (RLS):**
   ```sql
   -- Example: Users can only see their own bookings
   ALTER TABLE bookings ENABLE ROW LEVEL SECURITY;
   CREATE POLICY user_bookings ON bookings
     FOR SELECT
     USING (user_id = current_setting('app.current_user_id')::uuid);
   ```

3. **Audit Logging:**
   - Log all database access
   - Track who accessed what data and when
   - Monitor for suspicious activity

---

## API Security

### CORS (Cross-Origin Resource Sharing)

**Current Configuration:**
```java
configuration.setAllowedOrigins(Arrays.asList("*")); // ⚠️ Development only!
```

**Production Configuration:**
```java
// SecurityConfig.java
configuration.setAllowedOrigins(Arrays.asList(
    "https://yourdomain.com",
    "https://app.yourdomain.com"
));
configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
configuration.setAllowCredentials(true); // If using cookies
configuration.setMaxAge(3600L); // Cache preflight for 1 hour
```

### Rate Limiting

**⚠️ NOT IMPLEMENTED - Required for Production:**

**Implement using Bucket4j or similar:**
```java
// Example: 10 requests per minute per IP
@Bean
public RateLimiter rateLimiter() {
    return RateLimiter.create(10.0 / 60.0); // 10 per minute
}
```

**Rate Limit Targets:**
- Login: 5 attempts per 15 minutes per IP
- Registration: 3 per hour per IP
- Password reset: 3 per hour per email
- API calls: 100 per minute per user

### Input Validation

**✓ Implemented:**
- Jakarta Validation annotations on DTOs
- `@Valid` annotation on controller methods
- Email format validation
- Positive number validation

**Additional Validation Needed:**

1. **File Upload Validation** (if implemented):
   - File size limits
   - File type whitelist
   - Virus scanning

2. **String Length Limits:**
   ```java
   @Size(max = 255, message = "Name cannot exceed 255 characters")
   private String name;
   ```

3. **Sanitization:**
   - Strip HTML/JavaScript from user input
   - Encode output to prevent XSS

### HTTPS/TLS

**⚠️ REQUIRED for Production:**

```properties
# application.properties (production)
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=luggage-app

# Redirect HTTP to HTTPS
server.http2.enabled=true
```

**Generate self-signed certificate (development):**
```bash
keytool -genkeypair -alias luggage-app -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

**Production:** Use Let's Encrypt or commercial CA for trusted certificates

---

## Data Privacy

### Sensitive Data

**Never Include in API Responses:**
- `password_hash` field
- JWT secrets
- Database credentials
- Internal IDs that could be guessed

**✓ Protected by DTOs:**
```java
// UserResponse DTO - password_hash is NOT included
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private Users.Role role;
    // passwordHash is NEVER exposed
}
```

### Personal Identifiable Information (PII)

**PII Fields:**
- Email addresses
- Full names
- Booking history
- Location preferences

**Protection Measures:**

1. **Access Control:**
   - Users can only access their own data
   - Hosts can only see data for their locations
   - Admins have full access (audit logged)

2. **Data Minimization:**
   - Only collect necessary data
   - Don't store unnecessary personal information

3. **Right to Erasure (GDPR):**
   - Implement user account deletion
   - Cascade delete or anonymize related data

4. **Data Encryption at Rest:**
   - Encrypt database backups
   - Consider encrypting sensitive fields (email, etc.)

### Logging

**⚠️ Never Log:**
- Passwords (plain or hashed)
- JWT tokens
- Credit card information
- Full email addresses (mask: j***@email.com)

**✓ Safe to Log:**
- Request methods and paths
- Response status codes
- User IDs (UUID)
- Timestamp and duration

---

## Production Checklist

### Environment Configuration

- [ ] Set `SPRING_PROFILES_ACTIVE=prod`
- [ ] Update `JWT_SECRET` with secure random value
- [ ] Use strong database credentials
- [ ] Configure proper CORS origins
- [ ] Enable HTTPS/TLS
- [ ] Set secure cookie flags (if using cookies)

### Application Configuration

- [ ] Change `spring.jpa.hibernate.ddl-auto` to `validate` (not `update`)
- [ ] Set `spring.jpa.show-sql=false`
- [ ] Configure proper logging levels
- [ ] Disable debug/trace logging in production
- [ ] Set appropriate connection pool size

### Database

- [ ] Create dedicated database user for application
- [ ] Grant minimal required permissions
- [ ] Enable SSL for database connections
- [ ] Set up automated backups
- [ ] Configure backup retention policy

### Security Headers

- [ ] Content-Security-Policy
- [ ] X-Content-Type-Options: nosniff
- [ ] X-Frame-Options: DENY
- [ ] X-XSS-Protection: 1; mode=block
- [ ] Strict-Transport-Security (HSTS)

### Monitoring & Logging

- [ ] Set up centralized logging (ELK, Splunk, etc.)
- [ ] Configure security alerts
- [ ] Monitor failed login attempts
- [ ] Track API usage patterns
- [ ] Set up automated security scanning

### Infrastructure

- [ ] Firewall configuration
- [ ] Network segmentation
- [ ] DDoS protection
- [ ] Regular security updates
- [ ] Penetration testing

---

## Vulnerability Reporting

### Reporting Security Issues

If you discover a security vulnerability, please DO NOT open a public issue.

**Contact:**
- Email: security@yourdomain.com (create this mailbox)
- PGP Key: [Provide public key]

**Include in Report:**
1. Description of the vulnerability
2. Steps to reproduce
3. Potential impact
4. Suggested fix (if any)

**Response Time:**
- Acknowledgment: Within 24 hours
- Initial assessment: Within 3 business days
- Fix timeline: Based on severity

### Severity Levels

**🔴 Critical:** Remote code execution, authentication bypass
**🟠 High:** SQL injection, XSS, sensitive data exposure
**🟡 Medium:** CSRF, information disclosure
**🟢 Low:** Minor configuration issues

---

## Security Updates

### Dependency Management

**Check for vulnerabilities regularly:**
```bash
# Maven
./mvnw dependency-check:check

# Or use OWASP Dependency-Check
```

**Update dependencies:**
```bash
./mvnw versions:display-dependency-updates
```

### CVE Monitoring

Subscribe to security advisories:
- [Spring Security Advisories](https://spring.io/security)
- [PostgreSQL Security](https://www.postgresql.org/support/security/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)

---

## Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [OWASP API Security Top 10](https://owasp.org/www-project-api-security/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/rfc8725)
- [NIST Password Guidelines](https://pages.nist.gov/800-63-3/)

---

**Last Updated:** 2025-01-29
**Review Frequency:** Quarterly or after security incidents