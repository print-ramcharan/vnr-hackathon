package com.healthcare.medVault.service;

import com.healthcare.medVault.dto.PrescriptionRequestDTO;
import com.healthcare.medVault.dto.PrescriptionResponseDTO;

import java.util.List;

public interface PrescriptionService {
    PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO requestDTO);
    List<PrescriptionResponseDTO> getByAppointmentId(Long appointmentId);
    List<PrescriptionResponseDTO> getByPatientId(Long patientId);
    List<PrescriptionResponseDTO> getByDoctorId(Long doctorId);
}
