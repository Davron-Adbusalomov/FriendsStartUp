package com.example.demo.exam.service;

import jakarta.annotation.PostConstruct;
import org.jvnet.hk2.annotations.Service;

import java.util.TimeZone;

@Service
public class TimeZoneService {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC+5"));
    }
}
