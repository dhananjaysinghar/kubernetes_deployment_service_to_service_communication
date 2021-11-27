package com.importsvc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/imports")
public class ImportController {

    @Value("${app.external.doc-url}")
    private String doc_url;

    @GetMapping("/docs")
    public String getImportDetails() {
        log.info("calling doc-svc to get document details");
        return new RestTemplate()
                .getForObject(doc_url, String.class);
    }
}
