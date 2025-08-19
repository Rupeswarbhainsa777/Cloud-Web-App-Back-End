package com.code.CloudShare;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CloudShareController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from CloudShare API!";
    }
}
