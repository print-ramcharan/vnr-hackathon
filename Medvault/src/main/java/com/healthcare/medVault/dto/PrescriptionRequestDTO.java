package com.healthcare.medVault.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrescriptionRequestDTO {
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private List<PrescriptionItemDTO> items;
    private String notes;
}
