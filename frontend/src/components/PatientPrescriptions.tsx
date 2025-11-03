import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useProfile } from '@/hooks/useProfile';
import { prescriptionAPI } from '@/services/prescriptionApi';
import { useToast } from '@/hooks/use-toast';
import { format } from 'date-fns';

const PatientPrescriptions: React.FC = () => {
  const { patientProfile } = useProfile();
  const { toast } = useToast();
  const [prescriptions, setPrescriptions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!patientProfile) return;
    fetchPrescriptions();
  }, [patientProfile]);

  const fetchPrescriptions = async () => {
    if (!patientProfile) return;
    setLoading(true);
    try {
      const data = await prescriptionAPI.getByPatient(patientProfile.id);
      setPrescriptions(data || []);
    } catch (err) {
      console.error(err);
      toast({ title: 'Error', description: 'Failed to load prescriptions', variant: 'destructive' });
    } finally {
      setLoading(false);
    }
  };

  if (!patientProfile) return null;

  return (
    <div>
      <Card>
        <CardHeader>
          <CardTitle>Prescriptions</CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="text-center py-8">Loading prescriptions...</div>
          ) : prescriptions.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">No prescriptions found.</div>
          ) : (
            <div className="space-y-4">
              {prescriptions.map((pres: any) => (
                <div key={pres.id} className="p-4 border rounded">
                  <div className="flex items-center justify-between">
                    <div>
                      <div className="font-semibold">Prescription - {format(new Date(pres.createdAt), 'PPP p')}</div>
                      <div className="text-sm text-muted-foreground">Doctor: {pres.doctorName || pres.doctorId}</div>
                    </div>
                    <div className="text-sm text-muted-foreground">Appointment: {pres.appointmentId}</div>
                  </div>

                  <div className="mt-3">
                    <table className="w-full text-sm">
                      <thead>
                        <tr className="text-left">
                          <th className="pb-2">Medication</th>
                          <th className="pb-2">Dose</th>
                          <th className="pb-2">Frequency</th>
                          <th className="pb-2">Duration</th>
                          <th className="pb-2">Instructions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {pres.items && pres.items.map((it: any, idx: number) => (
                          <tr key={idx} className="border-t">
                            <td className="py-2">{it.medicationName}</td>
                            <td className="py-2">{it.dose}</td>
                            <td className="py-2">{it.frequency}</td>
                            <td className="py-2">{it.duration}</td>
                            <td className="py-2">{it.instructions}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {pres.notes && (
                    <div className="mt-3 p-3 bg-muted/10 rounded">Notes: {pres.notes}</div>
                  )}
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default PatientPrescriptions;
