package fouriertransform.files.score;

public enum Clef {
    TREBLE("G", "2"), BASS("F", "4");

    final String SIGN;
    final String LINE;

    Clef(String _sign, String _line) {
        this.SIGN = _sign;
        this.LINE = _line;
    }

    public String getSign() {
        return SIGN;
    }

    public String getLine() {
        return LINE;
    }
}
