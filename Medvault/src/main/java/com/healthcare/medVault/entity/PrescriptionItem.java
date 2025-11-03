package com.healthcare.medVault.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prescription_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    private String medicationName;
    private String dose; // e.g., 500 mg
    private String frequency; // e.g., Once a day, Twice a day
    private String duration; // e.g., 7 days
    @Column(columnDefinition = "TEXT")
    private String instructions;
}
