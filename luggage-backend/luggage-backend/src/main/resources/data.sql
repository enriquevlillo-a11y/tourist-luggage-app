
-- Mock Data for Luggage Storage Application

-- ========================================
-- USERS
-- ========================================
-- Note: created_at and updated_at are handled by @PrePersist/@PreUpdate

-- Customers (USER role)
-- Password: password123 (BCrypt hashed)
INSERT INTO users (id, email, password_hash, full_name, role)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'john.doe@email.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'John Doe', 'USER'),
    ('22222222-2222-2222-2222-222222222222', 'sarah.wilson@email.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'Sarah Wilson', 'USER'),
    ('33333333-3333-3333-3333-333333333333', 'mike.chen@email.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'Mike Chen', 'USER'),
    ('44444444-4444-4444-4444-444444444444', 'emma.brown@email.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'Emma Brown', 'USER');

-- Hosts (HOST role)
-- Password: password123 (BCrypt hashed)
INSERT INTO users (id, email, password_hash, full_name, role)
VALUES
    ('55555555-5555-5555-5555-555555555555', 'maria.garcia@hotel.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'Maria Garcia', 'HOST'),
    ('66666666-6666-6666-6666-666666666666', 'david.kim@hostel.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'David Kim', 'HOST'),
    ('77777777-7777-7777-7777-777777777777', 'lisa.anderson@cafe.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'Lisa Anderson', 'HOST'),
    ('88888888-8888-8888-8888-888888888888', 'james.murphy@shop.com', '$2a$10$yEJAp.RiVNMR6dC2F7ewKOuauyjaI5bDVh3WCxpharyYS1e4UNnwe', 'James Murphy', 'HOST');

-- Admin (ADMIN role)
-- Password: admin123 (BCrypt hashed)
INSERT INTO users (id, email, password_hash, full_name, role)
VALUES
    ('99999999-9999-9999-9999-999999999999', 'admin@luggage.com', '$2a$10$tgwjh7CCy5jjA8XjcO6Vv.hlkfpQQz0YGRXAhN9wd0JvHSbNbptH2', 'System Admin', 'ADMIN');

-- ========================================
-- LOCATIONS
-- ========================================

-- Maria's locations (NYC area)
INSERT INTO locations (id, name, address, city, lat, lng, price_per_hour, capacity, hours, is_active, host_id)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Times Square Luggage Hub', '1560 Broadway', 'New York', 40.7580, -73.9855, 5.00, 50, '24/7', true, '55555555-5555-5555-5555-555555555555'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Grand Central Storage', '89 E 42nd St', 'New York', 40.7527, -73.9772, 4.50, 30, '6:00 AM - 11:00 PM', true, '55555555-5555-5555-5555-555555555555');

-- David's locations (Paris area)
INSERT INTO locations (id, name, address, city, lat, lng, price_per_hour, capacity, hours, is_active, host_id)
VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Eiffel Tower Lockers', '5 Avenue Anatole France', 'Paris', 48.8584, 2.2945, 6.00, 40, '8:00 AM - 10:00 PM', true, '66666666-6666-6666-6666-666666666666'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Louvre Museum Storage', 'Rue de Rivoli', 'Paris', 48.8606, 2.3376, 5.50, 25, '9:00 AM - 9:00 PM', true, '66666666-6666-6666-6666-666666666666');

-- Lisa's locations (Tokyo area)
INSERT INTO locations (id, name, address, city, lat, lng, price_per_hour, capacity, hours, is_active, host_id)
VALUES
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Shibuya Station Lockers', '2 Chome-1 Dogenzaka', 'Tokyo', 35.6580, 139.7016, 500.00, 60, '24/7', true, '77777777-7777-7777-7777-777777777777'),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Shinjuku Luggage Center', '3 Chome Shinjuku', 'Tokyo', 35.6938, 139.7034, 450.00, 45, '7:00 AM - 11:00 PM', true, '77777777-7777-7777-7777-777777777777');

-- James's locations (London area)
INSERT INTO locations (id, name, address, city, lat, lng, price_per_hour, capacity, hours, is_active, host_id)
VALUES
    ('10101010-1010-1010-1010-101010101010', 'Kings Cross Storage', 'Euston Rd', 'London', 51.5308, -0.1238, 4.00, 35, '6:00 AM - 12:00 AM', true, '88888888-8888-8888-8888-888888888888'),
    ('20202020-2020-2020-2020-202020202020', 'Covent Garden Lockers', 'Covent Garden', 'London', 51.5117, -0.1225, 4.50, 20, '8:00 AM - 10:00 PM', true, '88888888-8888-8888-8888-888888888888');

-- ========================================
-- BOOKINGS
-- ========================================

-- Confirmed bookings
INSERT INTO bookings (id, user_id, location_id, start_time, end_time, price_cents, status)
VALUES
    ('b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1', '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
     CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '2 days', 7200, 'CONFIRMED'),

    ('b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2', '22222222-2222-2222-2222-222222222222', 'cccccccc-cccc-cccc-cccc-cccccccccccc',
     CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day 6 hours', 3600, 'CONFIRMED'),

    ('b3b3b3b3-b3b3-b3b3-b3b3-b3b3b3b3b3b3', '33333333-3333-3333-3333-333333333333', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
     CURRENT_TIMESTAMP + INTERVAL '3 days', CURRENT_TIMESTAMP + INTERVAL '3 days 8 hours', 400000, 'CONFIRMED');

-- Pending bookings
INSERT INTO bookings (id, user_id, location_id, start_time, end_time, price_cents, status)
VALUES
    ('b4b4b4b4-b4b4-b4b4-b4b4-b4b4b4b4b4b4', '44444444-4444-4444-4444-444444444444', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
     CURRENT_TIMESTAMP + INTERVAL '5 days', CURRENT_TIMESTAMP + INTERVAL '5 days 4 hours', 2200, 'PENDING'),

    ('b5b5b5b5-b5b5-b5b5-b5b5-b5b5b5b5b5b5', '11111111-1111-1111-1111-111111111111', '10101010-1010-1010-1010-101010101010',
     CURRENT_TIMESTAMP + INTERVAL '7 days', CURRENT_TIMESTAMP + INTERVAL '7 days 5 hours', 2000, 'PENDING');

-- Completed bookings
INSERT INTO bookings (id, user_id, location_id, start_time, end_time, price_cents, status)
VALUES
    ('b6b6b6b6-b6b6-b6b6-b6b6-b6b6b6b6b6b6', '22222222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
     CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP - INTERVAL '10 days 3 hours', 1350, 'COMPLETED'),

    ('b7b7b7b7-b7b7-b7b7-b7b7-b7b7b7b7b7b7', '33333333-3333-3333-3333-333333333333', 'ffffffff-ffff-ffff-ffff-ffffffffffff',
     CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days 6 hours', 270000, 'COMPLETED');

-- Cancelled booking
INSERT INTO bookings (id, user_id, location_id, start_time, end_time, price_cents, status)
VALUES
    ('b8b8b8b8-b8b8-b8b8-b8b8-b8b8b8b8b8b8', '44444444-4444-4444-4444-444444444444', '20202020-2020-2020-2020-202020202020',
     CURRENT_TIMESTAMP + INTERVAL '2 days', CURRENT_TIMESTAMP + INTERVAL '2 days 3 hours', 1350, 'CANCELLED');