package com.krushnag.resume_analyzer.controller;

import com.krushnag.resume_analyzer.model.Resume;
import com.krushnag.resume_analyzer.repository.ResumeRepository;
import com.krushnag.resume_analyzer.service.SimpleSkillExtractor;
import com.krushnag.resume_analyzer.util.SkillLoader;
// removed Lombok constructor annotation to avoid Lombok dependency at compile time
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeRepository resumeRepository;
    private final SkillLoader skillLoader;
    private final SimpleSkillExtractor skillExtractor;
    private final ObjectMapper objectMapper;
    private final Path storageRoot = Paths.get("data/docs");

    public ResumeController(ResumeRepository resumeRepository, SkillLoader skillLoader, SimpleSkillExtractor skillExtractor, ObjectMapper objectMapper) {
        this.resumeRepository = resumeRepository;
        this.skillLoader = skillLoader;
        this.skillExtractor = skillExtractor;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResume(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (!Files.exists(storageRoot)) Files.createDirectories(storageRoot);

            String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            Path filepath = storageRoot.resolve(filename);
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            AutoDetectParser parser = new AutoDetectParser();
            ContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();

            try (InputStream stream = Files.newInputStream(filepath)) {
                parser.parse(stream, handler, metadata);
            }

            String extractedText = handler.toString();

            // Extract skills
            Set<String> skills = skillExtractor.extractSkills(extractedText, skillLoader.getSkills());
            String skillsJson = objectMapper.writeValueAsString(skills);

            Resume resume = new Resume();
            resume.setOriginalFilename(file.getOriginalFilename());
            resume.setStoragePath(filepath.toString());
            resume.setExtractedText(extractedText);
            resume.setSkillsJson(skillsJson);
            resume.setUploadedAt(LocalDateTime.now());

            resumeRepository.save(resume);

            return ResponseEntity.ok(Map.of(
                "id", resume.getId(),
                "message", "Resume uploaded successfully",
                "extractedTextSnippet", extractedText.substring(0, Math.min(400, extractedText.length())),
                "skills", skills
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/parsed")
    public ResponseEntity<?> getParsed(@PathVariable Long id) {
        return resumeRepository.findById(id).map(r -> {
            try {
                Set<String> skills = objectMapper.readValue(
                    r.getSkillsJson() == null ? "[]" : r.getSkillsJson(),
                    objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class)
                );

                return ResponseEntity.ok(Map.of(
                    "id", r.getId(),
                    "skills", skills,
                    "originalFilename", r.getOriginalFilename()
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing skills");
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
