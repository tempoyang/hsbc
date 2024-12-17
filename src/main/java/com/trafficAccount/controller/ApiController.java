package com.trafficAccount.controller;


import com.trafficAccount.config.BizConstant;
import com.trafficAccount.service.TrafficControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TrafficControlService trafficControlService;

    @GetMapping("/api1")
    public ResponseEntity<String> api1(@RequestHeader("UserId") String userId) {
        if (trafficControlService.isRequestAllowed(userId, "api1", BizConstant.LIMIT_REQUEST_NUM)) {
            return ResponseEntity.ok("api1 called successfully");
        } else {
            return ResponseEntity.badRequest().body("rate limit exceeded for api1");
        }
    }

    @PostMapping("/api2")
    public ResponseEntity<String> api2(@RequestHeader("UserId") String userId) {
        if (trafficControlService.isRequestAllowed(userId, "api2", BizConstant.LIMIT_REQUEST_NUM)) {
            return ResponseEntity.ok("api2 called successfully");
        } else {
            return ResponseEntity.badRequest().body("rate limit exceeded for api2");
        }
    }

    @PutMapping("/api3")
    public ResponseEntity<String> api3(@RequestHeader("UserId") String userId) {
        if (trafficControlService.isRequestAllowed(userId, "api3", BizConstant.LIMIT_REQUEST_NUM)) {
            return ResponseEntity.ok("api3 called successfully");
        } else {
            return ResponseEntity.badRequest().body("rate limit exceeded for api2");
        }
    }
}

