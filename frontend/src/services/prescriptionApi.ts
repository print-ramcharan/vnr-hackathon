import axios from 'axios';
import { PrescriptionRequestDTO, PrescriptionResponseDTO } from '@/types/prescription';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

export const prescriptionAPI = {
  createPrescription: async (data: PrescriptionRequestDTO): Promise<PrescriptionResponseDTO> => {
    const response = await api.post('/prescriptions', data);
    return response.data;
  },

  getByAppointment: async (appointmentId: string) => {
    const response = await api.get(`/prescriptions/appointment/${appointmentId}`);
    return response.data;
  },

  getByPatient: async (patientId: string) => {
    const response = await api.get(`/prescriptions/patient/${patientId}`);
    return response.data;
  },

  getByDoctor: async (doctorId: string) => {
    const response = await api.get(`/prescriptions/doctor/${doctorId}`);
    return response.data;
  }
};
