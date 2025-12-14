package com.krushnag.resume_analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.krushnag.resume_analyzer.model.JobDescription;

public interface JobRepository extends JpaRepository<JobDescription, Long> {
}
