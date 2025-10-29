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

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String RESUME_FILE = "Anish_Chandra_Das_Resume.pdf"; // inside src/main/resources

    @Override
    public void sendColdEmails(List<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            try {
                sendSingleEmail(candidate);
                System.out.println("✅ Email sent to: " + candidate.getEmail());
            } catch (MailException e) {
                System.err.println("❌ Mail send failed for: " + candidate.getEmail() + " | Reason: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("⚠️ Unexpected error for: " + candidate.getEmail());
                e.printStackTrace();
            }
        }
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
                ? "I am particularly interested in opportunities at <b>" + candidate.getCompanyName() + "</b>."
                : "I am eager to contribute to impactful engineering teams.";

        String body = """
                <p>%s</p>
                <p>I am <b>Anish Chandra Das</b>, a Software Engineer at <b>Bounteous x Accolite</b>. 
                I have experience building REST APIs, fixing bugs, writing unit tests, and developing scalable modules using 
                <b>Java, Spring Boot, MySQL, and React.js</b>.</p>
                <p>Beyond work, I’ve built a <b>MERN tutor–student platform</b> and a <b>secure blog app</b>. 
                I’ve solved over <b>800+ DSA problems</b> across platforms, strengthening my problem-solving and logic-building skills.</p>
                <p>%s I believe my skills and enthusiasm would allow me to contribute effectively to your team.</p>
                <p>Attached is my resume for your review. I’d love the opportunity to connect.</p>
                <p>Best regards,<br>
                <b>Anish Chandra Das</b><br>
                📞 +91 9319168997<br>
                📧 <a href="mailto:anisd988@gmail.com">anisd988@gmail.com</a><br>
                🔗 <a href="https://www.linkedin.com/in/anish-das1/">LinkedIn</a> | 
                <a href="https://github.com/AniOpd">GitHub</a> | 
                <a href="https://www.anishdas.me/">Portfolio</a></p>
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
