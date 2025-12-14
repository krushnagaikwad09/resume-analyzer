package com.krushnag.resume_analyzer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_descriptions")
public class JobDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    @Column(columnDefinition = "text")
    private String jobText;

    @Column(columnDefinition = "text")
    private String requiredSkillsJson;

    private LocalDateTime createdAt;

    // Default constructor
    public JobDescription() {}

    // Constructor with all fields
    public JobDescription(Long id, String jobTitle, String jobText, String requiredSkillsJson, LocalDateTime createdAt) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobText = jobText;
        this.requiredSkillsJson = requiredSkillsJson;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobText() {
        return jobText;
    }

    public void setJobText(String jobText) {
        this.jobText = jobText;
    }

    public String getRequiredSkillsJson() {
        return requiredSkillsJson;
    }

    public void setRequiredSkillsJson(String requiredSkillsJson) {
        this.requiredSkillsJson = requiredSkillsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
