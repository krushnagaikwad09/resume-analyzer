package com.krushnag.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "sold_at", nullable = false)
    private LocalDateTime soldAt;

    protected Sale() {
    }

    public Sale(Batch batch, int quantity, LocalDateTime soldAt) {
        this.batch = batch;
        this.quantity = quantity;
        this.soldAt = soldAt;
    }

    public Long getId() {
        return id;
    }

    public Batch getBatch() {
        return batch;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }
}
