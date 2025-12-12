package com.krushnag.resume_analyzer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SkillLoader {
    private final Set<String> skills = new HashSet<>();

    @PostConstruct
    public void load() {
        try {
            ObjectMapper om = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/skills.json");

            List<String> list = om.readValue(is, new TypeReference<List<String>>() {});
            for (String s : list) {
                skills.add(s.toLowerCase());
            }

            System.out.println("Loaded " + skills.size() + " skills");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load skills.json", e);
        }
    }

    public Set<String> getSkills() {
        return skills;
    }
}
