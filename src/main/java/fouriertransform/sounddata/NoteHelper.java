package fouriertransform.sounddata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoteHelper {
    public static final HashMap<String, Integer> NOTE_TO_INT = new HashMap<>();
    public static final HashMap<Integer, String> INT_TO_NOTE = new HashMap<>();

    static {
        createNOTE_TO_INT();
        createINT_TO_NOTE();
    }

    public static void main(String[] args) {
        generateNoteParameters();
    }

    public static String convertSpectrumToNotes(FrequencyData[] spectrum, int count) {
        FrequencyData.filter(spectrum, 0.001f);
        FrequencyData.sort(spectrum);
        final int L = spectrum.length;

        ArrayList<String> notes = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < Math.min(count, L); i++) {
            String note = NoteHelper.convertFrequencyToNote(spectrum[i].getFrequency());
            if (note.equals("LOW") || note.equals("HIGH") || notes.contains(note)) {
            } else {
                notes.add(note);
            }
        }

        for (String note : notes) {
            stringBuilder.append(note).append(" ");
        }

        return stringBuilder.toString();
    }

    public static NoteData[][] convertSpectrumMatrixToNoteDataMatrix(
            FrequencyData[][] spectrumMatrix, final float Threshold) {
        ArrayList<NoteData[]> noteData = new ArrayList<>(spectrumMatrix.length);
        HashMap<String, Float> noteAmplitudes;
        for (FrequencyData[] spectrum : spectrumMatrix) {
            noteAmplitudes = new HashMap<>();
            for (FrequencyData fd : spectrum) {
                float amplitude = fd.getAmplitude();
                if (amplitude < Threshold)
                    amplitude = 0;
                String note = NoteHelper.convertFrequencyToNote(fd.getFrequency());
                if (noteAmplitudes.containsKey(note)) {
                    noteAmplitudes.put(note, amplitude + amplitude);
                } else {
                    noteAmplitudes.put(note, amplitude);
                }
            }
            ArrayList<NoteData> notes = new ArrayList<>();
            for (HashMap.Entry<String, Float> noteAmplitude : noteAmplitudes.entrySet()) {
                notes.add(new NoteData(noteAmplitude.getKey(), noteAmplitude.getValue()));
            }
            noteData.add(NoteData.sort(notes.toArray(NoteData[]::new)));
        }
        return noteData.toArray(NoteData[][]::new);
    }

    private static void createINT_TO_NOTE() {
        for (HashMap.Entry<String, Integer> entry : NOTE_TO_INT.entrySet()) {
            INT_TO_NOTE.put(entry.getValue(), entry.getKey());
        }
    }

    private static void createNOTE_TO_INT() {
        NOTE_TO_INT.put("LOW", -1);
        NOTE_TO_INT.put("C0", 0);
        NOTE_TO_INT.put("C#/Db0", 1);
        NOTE_TO_INT.put("D0", 2);
        NOTE_TO_INT.put("D#/Eb0", 3);
        NOTE_TO_INT.put("E0", 4);
        NOTE_TO_INT.put("F0", 5);
        NOTE_TO_INT.put("F#/Gb0", 6);
        NOTE_TO_INT.put("G0", 7);
        NOTE_TO_INT.put("G#/Ab0", 8);
        NOTE_TO_INT.put("A0", 9);
        NOTE_TO_INT.put("A#/Bb0", 10);
        NOTE_TO_INT.put("B0", 11);
        NOTE_TO_INT.put("C1", 12);
        NOTE_TO_INT.put("C#/Db1", 13);
        NOTE_TO_INT.put("D1", 14);
        NOTE_TO_INT.put("D#/Eb1", 15);
        NOTE_TO_INT.put("E1", 16);
        NOTE_TO_INT.put("F1", 17);
        NOTE_TO_INT.put("F#/Gb1", 18);
        NOTE_TO_INT.put("G1", 19);
        NOTE_TO_INT.put("G#/Ab1", 20);
        NOTE_TO_INT.put("A1", 21);
        NOTE_TO_INT.put("A#/Bb1", 22);
        NOTE_TO_INT.put("B1", 23);
        NOTE_TO_INT.put("C2", 24);
        NOTE_TO_INT.put("C#/Db2", 25);
        NOTE_TO_INT.put("D2", 26);
        NOTE_TO_INT.put("D#/Eb2", 27);
        NOTE_TO_INT.put("E2", 28);
        NOTE_TO_INT.put("F2", 29);
        NOTE_TO_INT.put("F#/Gb2", 30);
        NOTE_TO_INT.put("G2", 31);
        NOTE_TO_INT.put("G#/Ab2", 32);
        NOTE_TO_INT.put("A2", 33);
        NOTE_TO_INT.put("A#/Bb2", 34);
        NOTE_TO_INT.put("B2", 35);
        NOTE_TO_INT.put("C3", 36);
        NOTE_TO_INT.put("C#/Db3", 37);
        NOTE_TO_INT.put("D3", 38);
        NOTE_TO_INT.put("D#/Eb3", 39);
        NOTE_TO_INT.put("E3", 40);
        NOTE_TO_INT.put("F3", 41);
        NOTE_TO_INT.put("F#/Gb3", 42);
        NOTE_TO_INT.put("G3", 43);
        NOTE_TO_INT.put("G#/Ab3", 44);
        NOTE_TO_INT.put("A3", 45);
        NOTE_TO_INT.put("A#/Bb3", 46);
        NOTE_TO_INT.put("B3", 47);
        NOTE_TO_INT.put("C4", 48);
        NOTE_TO_INT.put("C#/Db4", 49);
        NOTE_TO_INT.put("D4", 50);
        NOTE_TO_INT.put("D#/Eb4", 51);
        NOTE_TO_INT.put("E4", 52);
        NOTE_TO_INT.put("F4", 53);
        NOTE_TO_INT.put("F#/Gb4", 54);
        NOTE_TO_INT.put("G4", 55);
        NOTE_TO_INT.put("G#/Ab4", 56);
        NOTE_TO_INT.put("A4", 57);
        NOTE_TO_INT.put("A#/Bb4", 58);
        NOTE_TO_INT.put("B4", 59);
        NOTE_TO_INT.put("C5", 60);
        NOTE_TO_INT.put("C#/Db5", 61);
        NOTE_TO_INT.put("D5", 62);
        NOTE_TO_INT.put("D#/Eb5", 63);
        NOTE_TO_INT.put("E5", 64);
        NOTE_TO_INT.put("F5", 65);
        NOTE_TO_INT.put("F#/Gb5", 66);
        NOTE_TO_INT.put("G5", 67);
        NOTE_TO_INT.put("G#/Ab5", 68);
        NOTE_TO_INT.put("A5", 69);
        NOTE_TO_INT.put("A#/Bb5", 70);
        NOTE_TO_INT.put("B5", 71);
        NOTE_TO_INT.put("C6", 72);
        NOTE_TO_INT.put("C#/Db6", 73);
        NOTE_TO_INT.put("D6", 74);
        NOTE_TO_INT.put("D#/Eb6", 75);
        NOTE_TO_INT.put("E6", 76);
        NOTE_TO_INT.put("F6", 77);
        NOTE_TO_INT.put("F#/Gb6", 78);
        NOTE_TO_INT.put("G6", 79);
        NOTE_TO_INT.put("G#/Ab6", 80);
        NOTE_TO_INT.put("A6", 81);
        NOTE_TO_INT.put("A#/Bb6", 82);
        NOTE_TO_INT.put("B6", 83);
        NOTE_TO_INT.put("C7", 84);
        NOTE_TO_INT.put("C#/Db7", 85);
        NOTE_TO_INT.put("D7", 86);
        NOTE_TO_INT.put("D#/Eb7", 87);
        NOTE_TO_INT.put("E7", 88);
        NOTE_TO_INT.put("F7", 89);
        NOTE_TO_INT.put("F#/Gb7", 90);
        NOTE_TO_INT.put("G7", 91);
        NOTE_TO_INT.put("G#/Ab7", 92);
        NOTE_TO_INT.put("A7", 93);
        NOTE_TO_INT.put("A#/Bb7", 94);
        NOTE_TO_INT.put("B7", 95);
        NOTE_TO_INT.put("C8", 96);
        NOTE_TO_INT.put("C#/Db8", 97);
        NOTE_TO_INT.put("D8", 98);
        NOTE_TO_INT.put("D#/Eb8", 99);
        NOTE_TO_INT.put("E8", 100);
        NOTE_TO_INT.put("F8", 101);
        NOTE_TO_INT.put("F#/Gb8", 102);
        NOTE_TO_INT.put("G8", 103);
        NOTE_TO_INT.put("G#/Ab8", 104);
        NOTE_TO_INT.put("A8", 105);
        NOTE_TO_INT.put("A#/Bb8", 106);
        NOTE_TO_INT.put("B8", 107);
        NOTE_TO_INT.put("HIGH", 108);
    }

    public static String convertFrequencyToNote(float frequency) {
        if (frequency < 0)
            throw new IllegalArgumentException("Negative Frequency");
        else if (frequency < 15.88)
            return "LOW";
        else if (frequency < 16.828012)
            return "C0";
        else if (frequency < 17.827562)
            return "C#/Db0";
        else if (frequency < 18.891996)
            return "D0";
        else if (frequency < 20.016743)
            return "D#/Eb0";
        else if (frequency < 21.206083)
            return "E0";
        else if (frequency < 22.465744)
            return "F0";
        else if (frequency < 23.799999)
            return "F#/Gb0";
        else if (frequency < 25.219437)
            return "G0";
        else if (frequency < 26.718906)
            return "G#/Ab0";
        else if (frequency < 28.308126)
            return "A0";
        else if (frequency < 29.992529)
            return "A#/Bb0";
        else if (frequency < 31.771828)
            return "B0";
        else if (frequency < 33.660885)
            return "C1";
        else if (frequency < 35.665131)
            return "C#/Db1";
        else if (frequency < 37.784279)
            return "D1";
        else if (frequency < 40.028339)
            return "D#/Eb1";
        else if (frequency < 42.407310)
            return "E1";
        else if (frequency < 44.931198)
            return "F1";
        else if (frequency < 47.605148)
            return "F#/Gb1";
        else if (frequency < 50.434017)
            return "G1";
        else if (frequency < 53.432667)
            return "G#/Ab1";
        else if (frequency < 56.611397)
            return "A1";
        else if (frequency < 59.979912)
            return "A#/Bb1";
        else if (frequency < 63.548515)
            return "B1";
        else if (frequency < 67.326912)
            return "C2";
        else if (frequency < 71.330261)
            return "C#/Db2";
        else if (frequency < 75.568558)
            return "D2";
        else if (frequency < 80.061539)
            return "D#/Eb2";
        else if (frequency < 84.824623)
            return "E2";
        else if (frequency < 89.867538)
            return "F2";
        else if (frequency < 95.210297)
            return "F#/Gb2";
        else if (frequency < 100.872887)
            return "G2";
        else if (frequency < 106.870483)
            return "G#/Ab2";
        else if (frequency < 113.222794)
            return "A2";
        else if (frequency < 119.954971)
            return "A#/Bb2";
        else if (frequency < 127.087021)
            return "B2";
        else if (frequency < 134.643814)
            return "C3";
        else if (frequency < 142.650513)
            return "C#/Db3";
        else if (frequency < 151.131973)
            return "D3";
        else if (frequency < 160.118225)
            return "D#/Eb3";
        else if (frequency < 169.639252)
            return "E3";
        else if (frequency < 179.729935)
            return "F3";
        else if (frequency < 190.420593)
            return "F#/Gb3";
        else if (frequency < 201.740921)
            return "G3";
        else if (frequency < 213.735825)
            return "G#/Ab3";
        else if (frequency < 226.445587)
            return "A3";
        else if (frequency < 239.909943)
            return "A#/Bb3";
        else if (frequency < 254.178909)
            return "B3";
        else if (frequency < 269.292786)
            return "C4";
        else if (frequency < 285.301025)
            return "C#/Db4";
        else if (frequency < 302.268829)
            return "D4";
        else if (frequency < 320.246429)
            return "D#/Eb4";
        else if (frequency < 339.288513)
            return "E4";
        else if (frequency < 359.460175)
            return "F4";
        else if (frequency < 380.836029)
            return "F#/Gb4";
        else if (frequency < 403.481842)
            return "G4";
        else if (frequency < 427.471649)
            return "G#/Ab4";
        else if (frequency < 452.891174)
            return "A4";
        else if (frequency < 479.819885)
            return "A#/Bb4";
        else if (frequency < 508.352936)
            return "B4";
        else if (frequency < 538.585266)
            return "C5";
        else if (frequency < 570.612061)
            return "C#/Db5";
        else if (frequency < 604.537903)
            return "D5";
        else if (frequency < 640.482849)
            return "D#/Eb5";
        else if (frequency < 678.571838)
            return "E5";
        else if (frequency < 718.925171)
            return "F5";
        else if (frequency < 761.672363)
            return "F#/Gb5";
        else if (frequency < 806.963379)
            return "G5";
        else if (frequency < 854.948425)
            return "G#/Ab5";
        else if (frequency < 905.787170)
            return "A5";
        else if (frequency < 959.649719)
            return "A#/Bb5";
        else if (frequency < 1016.710999)
            return "B5";
        else if (frequency < 1077.165649)
            return "C6";
        else if (frequency < 1141.218994)
            return "C#/Db6";
        else if (frequency < 1209.080688)
            return "D6";
        else if (frequency < 1280.975708)
            return "D#/Eb6";
        else if (frequency < 1357.144043)
            return "E6";
        else if (frequency < 1437.845215)
            return "F6";
        else if (frequency < 1523.344727)
            return "F#/Gb6";
        else if (frequency < 1613.926758)
            return "G6";
        else if (frequency < 1709.896851)
            return "G#/Ab6";
        else if (frequency < 1811.574341)
            return "A6";
        else if (frequency < 1919.294678)
            return "A#/Bb6";
        else if (frequency < 2033.416870)
            return "B6";
        else if (frequency < 2154.331299)
            return "C7";
        else if (frequency < 2282.437988)
            return "C#/Db7";
        else if (frequency < 2418.161377)
            return "D7";
        else if (frequency < 2561.951416)
            return "D#/Eb7";
        else if (frequency < 2714.292969)
            return "E7";
        else if (frequency < 2875.695557)
            return "F7";
        else if (frequency < 3046.689453)
            return "F#/Gb7";
        else if (frequency < 3227.853516)
            return "G7";
        else if (frequency < 3419.793701)
            return "G#/Ab7";
        else if (frequency < 3623.143799)
            return "A7";
        else if (frequency < 3838.588867)
            return "A#/Bb7";
        else if (frequency < 4066.843750)
            return "B7";
        else if (frequency < 4308.667969)
            return "C8";
        else if (frequency < 4564.871094)
            return "C#/Db8";
        else if (frequency < 4836.312500)
            return "D8";
        else if (frequency < 5123.897949)
            return "D#/Eb8";
        else if (frequency < 5428.581055)
            return "E8";
        else if (frequency < 5751.381348)
            return "F8";
        else if (frequency < 6093.378906)
            return "F#/Gb8";
        else if (frequency < 6455.712402)
            return "G8";
        else if (frequency < 6839.587402)
            return "G#/Ab8";
        else if (frequency < 7246.287598)
            return "A8";
        else if (frequency < 7677.172852)
            return "A#/Bb8";
        else if (frequency < 8133.680083)
            return "B8";
        else
            return "HIGH";
    }

    private static void generateNoteParameters() {
        String c =
                "16.35 Hz\t32.70 Hz\t65.41 Hz\t130.81 Hz\t261.63 Hz\t523.25 Hz\t1046.50 Hz\t2093.00 Hz\t4186.01 Hz";
        String cs =
                "17.32 Hz\t34.65 Hz\t69.30 Hz\t138.59 Hz\t277.18 Hz\t554.37 Hz\t1108.73 Hz\t2217.46 Hz\t4434.92 Hz";
        String d =
                "18.35 Hz\t36.71 Hz\t73.42 Hz\t146.83 Hz\t293.66 Hz\t587.33 Hz\t1174.66 Hz\t2349.32 Hz\t4698.63 Hz";
        String ds =
                "19.45 Hz\t38.89 Hz\t77.78 Hz\t155.56 Hz\t311.13 Hz\t622.25 Hz\t1244.51 Hz\t2489.02 Hz\t4978.03 Hz";
        String e =
                "20.60 Hz\t41.20 Hz\t82.41 Hz\t164.81 Hz\t329.63 Hz\t659.25 Hz\t1318.51 Hz\t2637.02 Hz\t5274.04 Hz";
        String f =
                "21.83 Hz\t43.65 Hz\t87.31 Hz\t174.61 Hz\t349.23 Hz\t698.46 Hz\t1396.91 Hz\t2793.83 Hz\t5587.65 Hz";
        String fs =
                "23.12 Hz\t46.25 Hz\t92.50 Hz\t185 Hz\t369.99 Hz\t739.99 Hz\t1479.98 Hz\t2959.96 Hz\t5919.91 Hz";
        String g =
                "24.50 Hz\t49 Hz\t98 Hz\t196 Hz\t392 Hz\t783.99 Hz\t1567.98 Hz\t3135.96 Hz\t6271.93 Hz";
        String gs =
                "25.96 Hz\t51.91 Hz\t103.83 Hz\t207.65 Hz\t415.30 Hz\t830.61 Hz\t1661.22 Hz\t3322.44 Hz\t6644.88 Hz";
        String a = "27.50 Hz\t55 Hz\t110 Hz\t220 Hz\t440 Hz\t880 Hz\t1760 Hz\t3520 Hz\t7040 Hz";
        String as =
                "29.14 Hz\t58.27 Hz\t116.54 Hz\t233.08 Hz\t466.16 Hz\t932.33 Hz\t1864.66 Hz\t3729.31 Hz\t7458.62 Hz";
        String b =
                "30.87 Hz\t61.74 Hz\t123.47 Hz\t246.94 Hz\t493.88 Hz\t987.77 Hz\t1975.53 Hz\t3951.07 Hz\t7902.13 Hz";
        HashMap<String, String> notes = new HashMap<>();
        HashMap<Integer, String> nts = new HashMap<>();
        notes.put("C", c);
        notes.put("C#/Db", cs);
        notes.put("D", d);
        notes.put("D#/Eb", ds);
        notes.put("E", e);
        notes.put("F", f);
        notes.put("F#/Gb", fs);
        notes.put("G", g);
        notes.put("G#/Ab", gs);
        notes.put("A", a);
        notes.put("A#/Bb", as);
        notes.put("B", b);
        nts.put(0, "C");
        nts.put(1, "C#/Db");
        nts.put(2, "D");
        nts.put(3, "D#/Eb");
        nts.put(4, "E");
        nts.put(5, "F");
        nts.put(6, "F#/Gb");
        nts.put(7, "G");
        nts.put(8, "G#/Ab");
        nts.put(9, "A");
        nts.put(10, "A#/Bb");
        nts.put(11, "B");

        HashMap<String, Float[]> notesArr = new HashMap<>();

        notes.forEach((k, v) -> notesArr.put(k,
                Stream.of(v.split("\t")).map(s -> (String) s.substring(0, s.length() - 3))
                        .map(s -> Float.valueOf(s)).collect(Collectors.toList())
                        .toArray(Float[]::new)));

        HashMap<String, Float> ul = new HashMap<>();

        for (int o = 0; o < notesArr.get("C").length; o++) {
            for (int n = 0; n < notesArr.size(); n++) {
                int next = (n + 1) % 12;
                if (o == 8 && n == 11)
                    continue;

                String currN = nts.get(n);
                String nextN = nts.get(next);

                float cf = notesArr.get(currN)[o];
                float nf = notesArr.get(nextN)[next == 0 ? o + 1 : o];
                float logmid = (float) Math.sqrt(cf * nf);
                ul.put(currN + o, logmid);
            }
        }

        PriorityQueue<HashMap.Entry<String, Float>> pq = new PriorityQueue<>(
                (HashMap.Entry<String, Float> a1, HashMap.Entry<String, Float> b1) -> {
                    if (Objects.equals(a1.getValue(), b1.getValue())) {
                        return 0;
                    }
                    return a1.getValue() < b1.getValue() ? -1 : 1;
                });
        pq.addAll(ul.entrySet());

        while (!pq.isEmpty()) {
            HashMap.Entry<String, Float> es = pq.poll();
            float fr = es.getValue();
            String n = es.getKey();

            System.out.println(
                    (String) String.format("else if (frequency < %f) return \"%s\";", fr, n));
        }
    }
}
