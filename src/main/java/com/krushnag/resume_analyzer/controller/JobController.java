package com.krushnag.resume_analyzer.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krushnag.resume_analyzer.model.JobDescription;
import com.krushnag.resume_analyzer.repository.JobRepository;
import com.krushnag.resume_analyzer.service.SimpleSkillExtractor;
import com.krushnag.resume_analyzer.util.SkillLoader;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final SkillLoader skillLoader;
    private final SimpleSkillExtractor skillExtractor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobController(JobRepository jobRepository, SkillLoader skillLoader, SimpleSkillExtractor skillExtractor) {
        this.jobRepository = jobRepository;
        this.skillLoader = skillLoader;
        this.skillExtractor = skillExtractor;
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Map<String, String> request) {
        try {
            String title = request.get("jobTitle");
            String jobText = request.get("jobText");

            Set<String> skills = skillExtractor.extractSkills(jobText, skillLoader.getSkills());
            String skillsJson = objectMapper.writeValueAsString(skills);

            JobDescription job = new JobDescription();
            job.setJobTitle(title);
            job.setJobText(jobText);
            job.setRequiredSkillsJson(skillsJson);
            job.setCreatedAt(LocalDateTime.now());

            jobRepository.save(job);

            return ResponseEntity.ok(Map.of(
                "jobId", job.getId(),
                "jobTitle", title,
                "skillsDetected", skills
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating job");
        }
    }
}
