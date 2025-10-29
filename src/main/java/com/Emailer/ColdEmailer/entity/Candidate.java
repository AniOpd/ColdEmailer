package com.Emailer.ColdEmailer.entity;

public class Candidate {
    private String name;
    private String email;
    private String companyName;

    public Candidate() {}

    public Candidate(String name, String email, String companyName) {
        this.name = name;
        this.email = email;
        this.companyName = companyName;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
