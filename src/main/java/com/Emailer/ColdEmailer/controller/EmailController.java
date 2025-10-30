package com.Emailer.ColdEmailer.controller;

import com.Emailer.ColdEmailer.entity.Candidate;
import com.Emailer.ColdEmailer.service.EmailServiceImpl;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailServiceImpl emailService;

    /**
     * ✅ Send cold emails via JSON request body.
     * Endpoint: POST /api/email/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmails(@RequestBody List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Candidate list cannot be empty.");
        }

        Map<String, Object> result = emailService.sendColdEmails(candidates);

        if ((int) result.get("failureCount") > 0) {
            return ResponseEntity.internalServerError().body(result);
        } else {
            return ResponseEntity.ok(result);
        }
    }

    /**
     * ✅ Send cold emails by uploading CSV file.
     * CSV Format: name,email,companyName
     * Endpoint: POST /api/email/send/csv
     */
    @PostMapping("/send/csv")
    public ResponseEntity<?> sendEmailsFromCsv(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Please upload a valid CSV file.");
        }

        List<Candidate> candidates = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean isHeader = true;
            while ((line = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                if (line.length >= 3) {
                    String name = line[0].trim();
                    String email = line[1].trim();
                    String company = line[2].trim();

                    if (email != null && email.contains("@")) {
                        candidates.add(new Candidate(name, email, company));
                    }
                }
            }

            if (candidates.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ No valid candidate entries found in CSV.");
            }

            Map<String, Object> result = emailService.sendColdEmails(candidates);

            if ((int) result.get("failureCount") > 0) {
                return ResponseEntity.internalServerError().body(result);
            } else {
                return ResponseEntity.ok(result);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("❌ Failed to process CSV file: " + e.getMessage());
        }
    }
}
