package com.Emailer.ColdEmailer.service;

import com.Emailer.ColdEmailer.entity.Candidate;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String RESUME_FILE = "Anish_Chandra_Das_Resume.pdf"; // inside src/main/resources

    /**
     * Send emails and return result summary.
     */
    public Map<String, Object> sendColdEmails(List<Candidate> candidates) {
        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Candidate candidate : candidates) {
            try {
                sendSingleEmail(candidate);
                System.out.println("✅ Email sent to: " + candidate.getEmail());
                success.add(candidate.getEmail());
            } catch (MailException e) {
                System.err.println("❌ Mail send failed for: " + candidate.getEmail() + " | Reason: " + e.getMessage());
                failed.add(candidate.getEmail() + " - " + e.getMessage());
            } catch (Exception e) {
                System.err.println("⚠️ Unexpected error for: " + candidate.getEmail());
                e.printStackTrace();
                failed.add(candidate.getEmail() + " - Unexpected error");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", success.size());
        result.put("failureCount", failed.size());
        result.put("successfulEmails", success);
        result.put("failedEmails", failed);

        return result;
    }

    private void sendSingleEmail(Candidate candidate) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("your_email@gmail.com"); // change this to your sender
        helper.setTo(candidate.getEmail());

        String subject = (candidate.getCompanyName() != null && !candidate.getCompanyName().isBlank())
                ? "Application for Software Developer Role at " + candidate.getCompanyName()
                : "Application for Software Developer Role";
        helper.setSubject(subject);

        String greeting = (candidate.getName() != null && !candidate.getName().isBlank())
                ? "Dear " + candidate.getName() + ","
                : "Dear Hiring Manager,";

        String companyLine = (candidate.getCompanyName() != null && !candidate.getCompanyName().isBlank())
                ? "I am particularly interested in contributing to <b>" + candidate.getCompanyName() + "</b> and its engineering culture."
                : "I am eager to contribute to impactful engineering teams.";

        String body = """
                <p>%s</p>
                <p>I am <b>Anish Chandra Das</b>, a <b>Full Stack Developer (FTC at Morgan Stanley)</b> with over 
                <b>5+ months of hands-on experience</b> working on production systems via <b>Bounteous x Accolite</b>. 
                I have built scalable modules, automated workflows, and handled live issue resolutions using 
                <b>Java, Spring Boot, React.js, MySQL, and Camunda BPMN</b>.</p>

                <p>Along with backend and frontend development, I have practical exposure to 
                <b>Docker, Kubernetes, and CI/CD pipelines using Jenkins</b>, ensuring smooth deployment automation 
                and containerized delivery for stable releases.</p>

                <p>As a graduate from <b>National Institute of Technology Delhi (NIT Delhi)</b>, 
                I have a strong foundation in software engineering principles and a keen interest in building efficient, 
                maintainable systems.</p>

                <p>%s I am currently looking for new opportunities where I can apply my technical and DevOps skills 
                to contribute to high-impact engineering teams.</p>

                <p>Attached is my updated resume for your consideration. I would appreciate the opportunity to discuss 
                how my background aligns with your team’s needs.</p>

                <p>Best regards,<br>
                <b>Anish Chandra Das</b><br>
                📞 +91 9319168997<br>
                📧 <a href="mailto:anisd988@gmail.com">anisd988@gmail.com</a><br>
                🔗 <a href="https://www.linkedin.com/in/anish-das1/">LinkedIn</a> | 
                <a href="https://github.com/AniOpd">GitHub</a> | 
                </p>
                """.formatted(greeting, companyLine);

        helper.setText(body, true);

        // ✅ Load resume from classpath
        Resource resume = new ClassPathResource(RESUME_FILE);
        if (!resume.exists()) {
            throw new IllegalStateException("Resume file not found in resources: " + RESUME_FILE);
        }

        helper.addAttachment("Anish_Chandra_Das_Resume.pdf", resume);
        mailSender.send(message);
    }
}
