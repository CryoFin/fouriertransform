package fouriertransform.sounddata;

import java.util.Arrays;
import java.util.PriorityQueue;

public class NoteData {
    private final String note;
    private final float amplitude;

    public static void main(String[] args) {

    }

    public NoteData(String note, float amplitude) {
        this.note = note;
        this.amplitude = amplitude;
    }

    public String getNote() {
        return note;
    }

    public float getAmplitude() {
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

    private static int compare(NoteData n1, NoteData n2) {
        return NoteHelper.NOTE_TO_INT.get(n1.getNote()) - NoteHelper.NOTE_TO_INT.get(n2.getNote());
    }
}
