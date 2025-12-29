package com.krushnag.resume_analyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krushnag.resume_analyzer.model.JobDescription;
import com.krushnag.resume_analyzer.model.Resume;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class MatchService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> matchResumeWithJob(Resume resume, JobDescription job) {
        try {
            // Convert JSON string to Set
            Set<String> resumeSkills = parseSkills(resume.getSkillsJson());

            Set<String> jobSkills = parseSkills(job.getRequiredSkillsJson());

            // Find matched skills
            Set<String> matchedSkills = new HashSet<>(resumeSkills);
            matchedSkills.retainAll(jobSkills);

            // Find missing skills
            Set<String> missingSkills = new HashSet<>(jobSkills);
            missingSkills.removeAll(resumeSkills);

            // Calculate fit score
            double fitScore = 0;
            if (!jobSkills.isEmpty()) {
                fitScore = ((double) matchedSkills.size() / jobSkills.size()) * 100;
            }

            return Map.of(
                    "resumeId", resume.getId(),
                    "jobId", job.getId(),
                    "fitScore", Math.round(fitScore),
                    "matchedSkills", matchedSkills,
                    "missingSkills", missingSkills
            );
        } catch (Exception e) {
            throw new RuntimeException("Error matching resume with job", e);
        }
    }

    private Set<String> parseSkills(String skillsJson) throws Exception {
        if (skillsJson == null || skillsJson.trim().isEmpty()) {
            return new HashSet<>();
        }
        return objectMapper.readValue(
                skillsJson,
                objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class)
        );
    }
}
