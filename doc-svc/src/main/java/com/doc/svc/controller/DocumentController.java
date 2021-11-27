package com.doc.svc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/docs")
public class DocumentController {


    @GetMapping
    public String getDoc() {
        log.info("received request to send document details");
        return "test_docs";
    }
}
