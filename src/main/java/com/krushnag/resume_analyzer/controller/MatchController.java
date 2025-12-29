package com.krushnag.resume_analyzer.controller;
 
import com.krushnag.resume_analyzer.model.JobDescription;
import com.krushnag.resume_analyzer.model.Resume;
import com.krushnag.resume_analyzer.repository.JobRepository;
import com.krushnag.resume_analyzer.repository.ResumeRepository;
import com.krushnag.resume_analyzer.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/match")
public class MatchController {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final MatchService matchService;

    public MatchController(ResumeRepository resumeRepository, JobRepository jobRepository, MatchService matchService) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<?> matchResumeAndJob(@RequestBody Map<String, Long> request) {
        try {
            Long resumeId = request.get("resumeId");
            Long jobId = request.get("jobId");

            Resume resume = resumeRepository.findById(resumeId).orElseThrow();
            JobDescription job = jobRepository.findById(jobId).orElseThrow();

            Map<String, Object> result = matchService.matchResumeWithJob(resume, job);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Matching failed");
        }
    }
}
