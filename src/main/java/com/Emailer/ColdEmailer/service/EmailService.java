package com.Emailer.ColdEmailer.service;

import com.Emailer.ColdEmailer.entity.Candidate;

import java.util.List;

public interface EmailService {
    void sendColdEmails(List<Candidate> candidates);
}