package com.krushnag.resume_analyzer.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class SimpleSkillExtractor {

    private static final Pattern NON_WORD = Pattern.compile("[^a-z0-9+#\\- ]");

    public Set<String> extractSkills(String text, Set<String> vocab) {
        Set<String> found = new HashSet<>();
        if (text == null || text.isBlank()) return found;

        String normalized = text.toLowerCase();
        normalized = NON_WORD.matcher(normalized).replaceAll(" ");
        normalized = " " + normalized + " ";

        for (String skill : vocab) {
            String sk = skill.toLowerCase();

            if (normalized.contains(" " + sk + " ")) {
                found.add(skill);
            } else if (normalized.contains(sk)) {
                found.add(skill);
            }
        }

        return found;
    }
}
