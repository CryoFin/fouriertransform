package fouriertransform.files.score;

public class Note {
    private final boolean IS_TIE_END;
    private final boolean IS_TIE_START;
    private final String FULL_NOTE;
    private final String NOTE;
    private final String OCTAVE;
    private final String ALTER;

    public boolean isStart() {
        return IS_TIE_START;
    }

    public boolean isEnd() {
        return IS_TIE_END;
    }

    public String getFullNote() {
        return FULL_NOTE;
    }

    public String getNote() {
        return NOTE;
    }

    public String getOctave() {
        return OCTAVE;
    }

    public String getAlter() {
        return ALTER;
    }

    public Note(String _note, boolean _start, boolean _end) {
        this.FULL_NOTE = _note;
        this.IS_TIE_START = _start;
        this.IS_TIE_END = _end;
        if (_note.length() == 6) {
            this.NOTE = _note.substring(0, 1);
            this.OCTAVE = _note.substring(5, 6);
            if (_note.charAt(1) == '#') {
                this.ALTER = "1";
            } else {
                this.ALTER = "01";
            }
        } else {
            this.NOTE = _note.substring(0, 1);
            this.OCTAVE = _note.substring(1, 2);
            this.ALTER = null;
        }
    }

    public static void main(String[] args) {
        Note n = new Note("F#/Gb3", true, true);
        System.out.println(n.getNote());
        System.out.println(n.getOctave());
    }
}
