package com.dani.luggagebackend.Controller;


import com.dani.luggagebackend.Service.BookingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController("/Bookings")
@CrossOrigin
public class BookingController {

     @Autowired
    private BookingsService service;



}
