package com.onenet.controller;

import com.onenet.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class KafkaController {
    @Autowired
    private KafkaService kafkaService;
}
