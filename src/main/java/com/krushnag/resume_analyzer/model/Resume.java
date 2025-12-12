package com.krushnag.resume_analyzer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;

    private String storagePath;

    @Column(columnDefinition = "text")
    private String extractedText;

    @Column(columnDefinition = "text")
    private String skillsJson;

    private LocalDateTime uploadedAt;

    public Resume() {
    }

    public Resume(Long id, String originalFilename, String storagePath, String extractedText, String skillsJson, LocalDateTime uploadedAt) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.storagePath = storagePath;
        this.extractedText = extractedText;
        this.skillsJson = skillsJson;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getSkillsJson() {
        return skillsJson;
    }

    public void setSkillsJson(String skillsJson) {
        this.skillsJson = skillsJson;
    }
}
