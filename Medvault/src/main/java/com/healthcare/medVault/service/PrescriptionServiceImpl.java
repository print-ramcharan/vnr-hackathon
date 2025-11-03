package com.healthcare.medVault.service;

import com.healthcare.medVault.dto.PrescriptionItemDTO;
import com.healthcare.medVault.dto.PrescriptionRequestDTO;
import com.healthcare.medVault.dto.PrescriptionResponseDTO;
import com.healthcare.medVault.entity.Appointment;
import com.healthcare.medVault.entity.Doctor;
import com.healthcare.medVault.entity.Patient;
import com.healthcare.medVault.entity.Prescription;
import com.healthcare.medVault.entity.PrescriptionItem;
import com.healthcare.medVault.exception.ResourceNotFoundException;
import com.healthcare.medVault.repository.AppointmentRepository;
import com.healthcare.medVault.repository.DoctorRepository;
import com.healthcare.medVault.repository.PatientRepository;
import com.healthcare.medVault.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public PrescriptionResponseDTO createPrescription(PrescriptionRequestDTO requestDTO) {
        Appointment appointment = appointmentRepository.findById(requestDTO.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + requestDTO.getAppointmentId()));

        Patient patient = patientRepository.findById(requestDTO.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + requestDTO.getPatientId()));

        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + requestDTO.getDoctorId()));

        Prescription prescription = new Prescription();
        prescription.setAppointment(appointment);
        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setNotes(requestDTO.getNotes());

        List<PrescriptionItem> items = requestDTO.getItems().stream().map(i -> {
            PrescriptionItem item = new PrescriptionItem();
            item.setMedicationName(i.getMedicationName());
            item.setDose(i.getDose());
            item.setFrequency(i.getFrequency());
            item.setDuration(i.getDuration());
            item.setInstructions(i.getInstructions());
            item.setPrescription(prescription);
            return item;
        }).collect(Collectors.toList());

        prescription.setItems(items);

        Prescription saved = prescriptionRepository.save(prescription);

        return convertToDTO(saved);
    }

    @Override
    public List<PrescriptionResponseDTO> getByAppointmentId(Long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionResponseDTO> getByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionResponseDTO> getByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private PrescriptionResponseDTO convertToDTO(Prescription p) {
        PrescriptionResponseDTO dto = new PrescriptionResponseDTO();
        dto.setId(p.getId());
        dto.setAppointmentId(p.getAppointment() != null ? p.getAppointment().getId() : null);
        dto.setPatientId(p.getPatient() != null ? p.getPatient().getId() : null);
        dto.setDoctorId(p.getDoctor() != null ? p.getDoctor().getId() : null);
        dto.setNotes(p.getNotes());
        dto.setCreatedAt(p.getCreatedAt());
        List<PrescriptionItemDTO> items = p.getItems().stream().map(it -> {
            PrescriptionItemDTO i = new PrescriptionItemDTO();
            i.setMedicationName(it.getMedicationName());
            i.setDose(it.getDose());
            i.setFrequency(it.getFrequency());
            i.setDuration(it.getDuration());
            i.setInstructions(it.getInstructions());
            return i;
        }).collect(Collectors.toList());
        dto.setItems(items);
        // include doctor's display name for frontend convenience
        if (p.getDoctor() != null) {
            String first = p.getDoctor().getFirstName() != null ? p.getDoctor().getFirstName() : "";
            String last = p.getDoctor().getLastName() != null ? p.getDoctor().getLastName() : "";
            dto.setDoctorName((first + " " + last).trim());
        }
        return dto;
    }
}
