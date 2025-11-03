package com.healthcare.medVault.controller;

import com.healthcare.medVault.dto.PrescriptionRequestDTO;
import com.healthcare.medVault.dto.PrescriptionResponseDTO;
import com.healthcare.medVault.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
// Allow both the old CRA port 3000 and Vite default 5173 during local development
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @PostMapping
    public ResponseEntity<PrescriptionResponseDTO> createPrescription(@Valid @RequestBody PrescriptionRequestDTO requestDTO) {
        PrescriptionResponseDTO response = prescriptionService.createPrescription(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getByAppointment(@PathVariable Long appointmentId) {
        List<PrescriptionResponseDTO> list = prescriptionService.getByAppointmentId(appointmentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getByPatient(@PathVariable Long patientId) {
        List<PrescriptionResponseDTO> list = prescriptionService.getByPatientId(patientId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PrescriptionResponseDTO>> getByDoctor(@PathVariable Long doctorId) {
        List<PrescriptionResponseDTO> list = prescriptionService.getByDoctorId(doctorId);
        return ResponseEntity.ok(list);
    }
}
