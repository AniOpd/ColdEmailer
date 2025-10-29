package com.Emailer.ColdEmailer.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.util.stream.Collectors;

@Service
public class PythonIntegrationService {

    public String runEmailCleaner(String inputFile, String outputFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3", "src/main/resources/scripts/clean_emails.py", inputFile, outputFile
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            return exitCode == 0 ? "✅ Script executed successfully:\n" + output
                    : "❌ Script failed (" + exitCode + "): " + output;

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}

