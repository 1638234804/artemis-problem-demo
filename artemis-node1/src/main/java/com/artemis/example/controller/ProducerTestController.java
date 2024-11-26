package com.artemis.example.controller;

import com.artemis.example.constants.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Tag(name = "sendMessage", description = "sendMessage")
@RestController
@RequestMapping("/producer")
public class ProducerTestController {

    @Resource
    private JmsTemplate jmsTemplate;

    @Operation(summary = "sendMessage")
    @PostMapping(value = "/sendMessage")
    public ResponseEntity<Void> sendMessage(@RequestParam(value = "message") String message) {
        jmsTemplate.setPubSubDomain(true);
        jmsTemplate.convertAndSend(Constants.MULTICAST_ADDRESS, message);
        return ResponseEntity.ok().build();
    }
}
