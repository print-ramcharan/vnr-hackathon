package com.healthcare.medVault.dto;

import lombok.Data;

@Data
public class PrescriptionItemDTO {
    private String medicationName;
    private String dose;
    private String frequency;
    private String duration;
    private String instructions;
}
