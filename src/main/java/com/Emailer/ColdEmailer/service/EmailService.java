package com.Emailer.ColdEmailer.service;

import com.Emailer.ColdEmailer.entity.Candidate;
import java.util.List;
import java.util.Map;

public interface EmailService {
    /**
     * Sends cold emails to a list of candidates and returns
     * a summary of success and failure counts.
     *
     * @param candidates list of candidates to email
     * @return map containing successCount, failureCount, successfulEmails, and failedEmails
     */
    Map<String, Object> sendColdEmails(List<Candidate> candidates);
}
