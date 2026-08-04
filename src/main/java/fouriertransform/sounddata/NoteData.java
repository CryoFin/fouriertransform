package fouriertransform.sounddata;

import java.util.Arrays;
import java.util.PriorityQueue;

public class NoteData {
    private final String note;
    private final double amplitude;

    public static void main(String[] args) {

    }

    public NoteData(String note, double amplitude) {
        this.note = note;
        this.amplitude = amplitude;
    }

    public String getNote() {
        return note;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public static NoteData[] sort(NoteData[] notes) {
        PriorityQueue<NoteData> priorityQueue =
                new PriorityQueue<>((NoteData n1, NoteData n2) -> compare(n1, n2));
        priorityQueue.addAll(Arrays.asList(notes));
        for (int i = 0; i < notes.length; i++) {
            notes[i] = priorityQueue.poll();
        }
        return notes;
    }

    public static NoteData[] sortByAmplitudeIncreasing(NoteData[] notes) {
        PriorityQueue<NoteData> priorityQueue = new PriorityQueue<>(
                (NoteData n1, NoteData n2) -> Double.compare(n1.getAmplitude(), n2.getAmplitude()));
        priorityQueue.addAll(Arrays.asList(notes));
        for (int i = 0; i < notes.length; i++) {
            notes[i] = priorityQueue.poll();
        }
        return notes;
    }

    public static NoteData[] sortByAmplitudeDecreasing(NoteData[] notes) {
        PriorityQueue<NoteData> priorityQueue = new PriorityQueue<>(
                (NoteData n1, NoteData n2) -> Double.compare(n2.getAmplitude(), n1.getAmplitude()));
        priorityQueue.addAll(Arrays.asList(notes));
        for (int i = 0; i < notes.length; i++) {
            notes[i] = priorityQueue.poll();
        }
        return notes;
    }

    private static int compare(NoteData n1, NoteData n2) {
        return NoteHelper.NOTE_TO_INT.get(n1.getNote()) - NoteHelper.NOTE_TO_INT.get(n2.getNote());
    }

    public static double getMaxAmplitude(NoteData[] noteSpectrum) {
        double max = -1;
        for (NoteData noteData : noteSpectrum) {
            max = Math.max(max, noteData.amplitude);
        }
        return max;
    }

    public static double getMaxAmplitude(NoteData[][] noteMatrix) {
        double max = -1;
        for (NoteData[] noteDataArray : noteMatrix) {
            for (NoteData noteData : noteDataArray) {
                max = Math.max(max, noteData.amplitude);
            }
        }
        return max;
    }

    public static void normalize(NoteData[][] noteMatrix) {
        double maxAmplitude = getMaxAmplitude(noteMatrix);
        for (NoteData[] noteData : noteMatrix) {
            for (int i = 0; i < noteData.length; i++) {
                NoteData oldNoteData = noteData[i];
                noteData[i] = new NoteData(oldNoteData.note,
                        Math.min(oldNoteData.amplitude / maxAmplitude, 1));
            }
        }
    }
}
