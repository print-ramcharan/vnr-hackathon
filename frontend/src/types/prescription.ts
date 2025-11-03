export interface PrescriptionItemDTO {
  medicationName: string;
  dose?: string;
  frequency?: string;
  duration?: string;
  instructions?: string;
}

export interface PrescriptionRequestDTO {
  appointmentId: number;
  patientId: number;
  doctorId: number;
  items: PrescriptionItemDTO[];
  notes?: string;
}

export interface PrescriptionResponseDTO extends PrescriptionRequestDTO {
  id: number;
  createdAt: string;
  doctorName?: string;
}
