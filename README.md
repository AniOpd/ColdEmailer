# ColdEmailer

Lightweight Spring Boot service to send personalized cold emails (with resume attachment) and to run a small Python-based email-cleaning utility on a supplied JSON list of HR / recruiter addresses.

## What this project contains

- A Spring Boot application (Java, Gradle) that exposes HTTP endpoints to:
  - Send cold emails from a JSON payload or an uploaded CSV.
  - Run a Python script that cleans a JSON list of emails.
- Email sending uses Spring's `JavaMailSender` and attaches `Anish_Chandra_Das_Resume.pdf` from classpath.
- A Python script `clean_emails.py` that validates email addresses and writes a cleaned JSON output.
- Example resource data: `src/main/resources/hr_email_list.json`.

## Tech stack

- Java 17+ (or compatible)
- Spring Boot (Gradle wrapper included)
- JavaMail (via Spring `JavaMailSender`)
- Python 3 (for the cleaning script)

## Quick setup (Windows)

1. Clone / open this project in a terminal.
2. Configure mail credentials before running (see notes below).
3. Run with Gradle wrapper:

```powershell
.\gradlew.bat bootRun
```

Or build then run the jar:

```powershell
.\gradlew.bat build
java -jar build\libs\ColdEmailer-*.jar
```

The app starts on port 8080 by default (see `src/main/resources/application.properties`).

## Configuration

Edit `src/main/resources/application.properties` or the `MailConfig` bean before sending emails.

- Important keys in `application.properties`:
  - `spring.mail.host` (default smtp.gmail.com)
  - `spring.mail.port` (587)
  - `spring.mail.username` (sender email)
  - `spring.mail.password` (app password — **do not** store production secrets in plaintext)
  - `server.port` (server HTTP port)

Also review `src/main/java/com/Emailer/ColdEmailer/config/MailConfig.java` which instantiates the `JavaMailSender`. The `EmailServiceImpl` currently sets a default `helper.setFrom("your_email@gmail.com")` — update that or keep consistent with `spring.mail.username`.

Security note: For Gmail, use an App Password (not your main account password) and allow SMTP for the account. Do not commit real credentials into the repo.

## Endpoints

1) POST /api/email/send

- Purpose: Send cold emails for a list of candidates provided as JSON.
- Content-Type: application/json
- Body: JSON array of candidate objects with fields: `name`, `email`, `companyName`.

Example payload:

```json
[
  { "name": "Priya Sharma", "email": "priya.sharma@tcs.com", "companyName": "TCS" }
]
```

Example curl (Windows PowerShell):

```powershell
curl -Uri "http://localhost:8080/api/email/send" -Method POST -ContentType "application/json" -Body (Get-Content -Raw .\sample_payload.json)
```

2) POST /api/email/send/csv

- Purpose: Upload a CSV file (multipart) with header `name,email,companyName` to send emails in bulk.
- Parameter name: `file` (multipart/form-data)

Example curl (file upload):

```powershell
curl -F "file=@C:\path\to\candidates.csv" http://localhost:8080/api/email/send/csv
```

CSV must have a header row, then rows with `name,email,companyName`.

3) GET /api/clean/emails

- Purpose: Runs `clean_emails.py` against `src/main/resources/hr_email_list.json` and writes `src/main/resources/hr_email_list_cleaned.json`.
- Implementation: `PythonIntegrationService` launches an external Python process. On *nix it runs `python3`; on Windows you may need to modify the ProcessBuilder command to use `python` instead of `python3`.

Example:

```powershell
curl http://localhost:8080/api/clean/emails
```

## How email content is built

- `EmailServiceImpl` composes an HTML body and attaches `Anish_Chandra_Das_Resume.pdf` (bundled in `src/main/resources`). It uses `JavaMailSender` and `MimeMessageHelper` to send mail and attaches the resume from the classpath.

Change the `helper.setFrom(...)` line in `EmailServiceImpl` or update `MailConfig` credentials to reflect your sender address.

## Resources included

- `src/main/resources/Anish_Chandra_Das_Resume.pdf` — resume attached to outgoing emails.
- `src/main/resources/hr_email_list.json` — sample HR/recruiter email list JSON.
- `src/main/resources/scripts/clean_emails.py` — script that validates addresses and writes cleaned JSON.

## Python script notes

- Script path: `src/main/resources/scripts/clean_emails.py`.
- It expects two CLI args: input JSON path and output JSON path. Example: `python3 clean_emails.py in.json out.json`.
- The Java service currently calls `python3` via `ProcessBuilder`. On Windows, change the command to `python` if `python3` is not available.

## CSV format for uploads

- Header: `name,email,companyName`
- Example row: `Priya Sharma,priya.sharma@tcs.com,TCS`

## Notes, caveats and next steps

- Credentials: Replace placeholder email and password with a secure app password for the SMTP account.
- Windows + Python: If Python isn't in PATH as `python3`, update `PythonIntegrationService` to use `python` or an absolute path to the interpreter.
- Error handling: The controllers and services print server-side messages; consider returning richer JSON error payloads for clients.
- Tests: There are tests under `src/test/java` — run `.\gradlew.bat test` to execute them.
- Docker: Not included; could containerize the app and mount credentials/secrets into environment variables.

## Contacts

Repository author and contact information are embedded in the email template (Anish Chandra Das).

---

If you want, I can:

- Add a sample `sample_payload.json` and a small `candidates.csv` to `src/main/resources`.
- Update `PythonIntegrationService` to auto-detect `python3` vs `python` on Windows.
- Add Gradle tasks or a short script to run the cleaner and show results.

Tell me which follow-up you'd like and I will implement it.
