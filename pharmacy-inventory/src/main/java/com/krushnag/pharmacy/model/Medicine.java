package com.krushnag.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String sku;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "medicine")
    private List<Batch> batches = new ArrayList<>();

    protected Medicine() {
    }

    public Medicine(String sku, String name, String description) {
        this.sku = sku;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Batch> getBatches() {
        return batches;
    }
}
