package com.healthcare.medVault.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionResponseDTO {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String doctorName;
    private List<PrescriptionItemDTO> items;
    private String notes;
    private LocalDateTime createdAt;
}
