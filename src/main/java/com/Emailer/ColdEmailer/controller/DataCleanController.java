package com.Emailer.ColdEmailer.controller;


import com.Emailer.ColdEmailer.service.PythonIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clean")
public class DataCleanController {

    @Autowired
    private PythonIntegrationService pythonService;

    @GetMapping("/emails")
    public String cleanEmails() {
        String inputFile = "src/main/resources/hr_email_list.json";
        String outputFile = "src/main/resources/hr_email_list_cleaned.json";
        return pythonService.runEmailCleaner(inputFile, outputFile);
    }
}
