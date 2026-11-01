package com.krushnag.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "batches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"medicine_id", "batch_number"}),
        indexes = @Index(name = "idx_batch_expiry_stock", columnList = "expiry_date, quantity_available"))
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "batch_number", nullable = false, length = 80)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_received", nullable = false)
    private int quantityReceived;

    @Column(name = "quantity_available", nullable = false)
    private int quantityAvailable;

    protected Batch() {
    }

    public Batch(Medicine medicine, String batchNumber, LocalDate expiryDate, int quantityReceived) {
        this.medicine = medicine;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.quantityReceived = quantityReceived;
        this.quantityAvailable = quantityReceived;
    }

    public void removeStock(int quantity) {
        if (quantity <= 0 || quantity > quantityAvailable) {
            throw new IllegalArgumentException("Sale quantity must be positive and cannot exceed available batch stock.");
        }
        quantityAvailable -= quantity;
    }

    public Long getId() {
        return id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }
}
