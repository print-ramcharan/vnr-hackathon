import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { medications } from '@/lib/medications';
import { PrescriptionItemDTO, PrescriptionRequestDTO } from '@/types/prescription';
import { useToast } from '@/hooks/use-toast';
import { prescriptionAPI } from '@/services/prescriptionApi';

interface Props {
  appointment: any;
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
}

const CreatePrescriptionModal: React.FC<Props> = ({ appointment, isOpen, onOpenChange, onSuccess }) => {
  const { toast } = useToast();
  const [query, setQuery] = useState('');
  const [selected, setSelected] = useState<string | null>(null);
  const [items, setItems] = useState<PrescriptionItemDTO[]>([]);
  const [notes, setNotes] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const addSelected = () => {
    if (!selected) return;
    setItems(prev => [...prev, { medicationName: selected, dose: '', frequency: '', duration: '', instructions: '' }]);
    setSelected(null);
    setQuery('');
  };

  const updateItem = (index: number, key: keyof PrescriptionItemDTO, value: string) => {
    setItems(prev => {
      const copy = [...prev];
      // @ts-ignore
      copy[index][key] = value;
      return copy;
    });
  };

  const removeItem = (index: number) => {
    setItems(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async () => {
    if (!appointment) return;
    if (items.length === 0) {
      toast({ title: 'No medications', description: 'Please add at least one medication', variant: 'destructive' });
      return;
    }
    setSubmitting(true);
    try {
      const payload: any = {
        appointmentId: Number(appointment.id),
        patientId: Number(appointment.patientId),
        doctorId: Number(appointment.doctorId),
        items,
        notes
      };

      await prescriptionAPI.createPrescription(payload);
      toast({ title: 'Prescription saved', description: 'Prescription added successfully' });
      onOpenChange(false);
      if (onSuccess) onSuccess();
    } catch (err) {
      console.error(err);
      toast({ title: 'Error', description: 'Failed to save prescription', variant: 'destructive' });
    } finally {
      setSubmitting(false);
    }
  };

  const filtered = medications.filter(m => m.toLowerCase().includes(query.toLowerCase()));

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <DialogTitle>Create Prescription</DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div>
            <Input placeholder="Search medications" value={query} onChange={(e) => setQuery(e.target.value)} />
            {query && (
              <div className="mt-2 max-h-40 overflow-y-auto border rounded">
                {filtered.map(m => (
                  <div key={m} className="p-2 hover:bg-muted cursor-pointer flex justify-between items-center">
                    <div>{m}</div>
                    <Button size="sm" onClick={() => { setSelected(m); addSelected(); }}>Add</Button>
                  </div>
                ))}
                {filtered.length === 0 && <div className="p-2 text-sm text-muted-foreground">No matches</div>}
              </div>
            )}
          </div>

          <div className="space-y-2">
            {items.map((it, idx) => (
              <div key={idx} className="p-3 border rounded grid grid-cols-1 md:grid-cols-4 gap-2 items-start">
                <div>
                  <div className="font-medium">{it.medicationName}</div>
                </div>
                <Input placeholder="Dose (e.g., 500 mg)" value={it.dose} onChange={(e) => updateItem(idx, 'dose', e.target.value)} />
                <Input placeholder="Frequency (e.g., Twice a day)" value={it.frequency} onChange={(e) => updateItem(idx, 'frequency', e.target.value)} />
                <div className="space-y-2">
                  <Input placeholder="Duration (e.g., 7 days)" value={it.duration} onChange={(e) => updateItem(idx, 'duration', e.target.value)} />
                  <div className="flex gap-2">
                    <Button variant="destructive" size="sm" onClick={() => removeItem(idx)}>Remove</Button>
                  </div>
                </div>
                <div className="md:col-span-4">
                  <Textarea placeholder="Instructions" value={it.instructions} onChange={(e) => updateItem(idx, 'instructions', e.target.value)} />
                </div>
              </div>
            ))}
          </div>

          <div>
            <Textarea placeholder="Notes (optional)" value={notes} onChange={(e) => setNotes(e.target.value)} />
          </div>
        </div>

        <DialogFooter>
          <div className="flex gap-2">
            <Button variant="outline" onClick={() => onOpenChange(false)}>Cancel</Button>
            <Button onClick={handleSubmit} disabled={submitting}>{submitting ? 'Saving...' : 'Save Prescription'}</Button>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default CreatePrescriptionModal;
