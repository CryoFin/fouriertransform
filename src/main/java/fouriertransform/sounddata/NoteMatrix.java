package fouriertransform.sounddata;

public class NoteMatrix {
    private NoteData[][] matrix;
    private final int SAMPLING_RATE;
    private final int STEP_SIZE;
    private final int FIRST_MIDPOINT;

    public NoteData[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(NoteData[][] _matrix) {
        this.matrix = _matrix;
    }

    public int getSamplingRate() {
        return SAMPLING_RATE;
    }

    public int getStepSize() {
        return STEP_SIZE;
    }

    public int getFirstMidpoint() {
        return FIRST_MIDPOINT;
    }

    public NoteMatrix(NoteData[][] noteMatrix, int _samplingRate, int _firstMidpoint,
            int _stepSize) {
        matrix = noteMatrix;
        this.SAMPLING_RATE = _samplingRate;
        this.FIRST_MIDPOINT = _firstMidpoint;
        this.STEP_SIZE = _stepSize;
    }

    public NoteMatrix(FrequencyMatrix frequencyMatrix) {
        matrix = NoteHelper.convertSpectrumMatrixToNoteDataMatrix(frequencyMatrix.getMatrix(), 0);
        this.SAMPLING_RATE = frequencyMatrix.getSamplingRate();
        this.FIRST_MIDPOINT = frequencyMatrix.getFirstMidpoint();
        this.STEP_SIZE = frequencyMatrix.getStepSize();
    }

    public NoteMatrix(FrequencyMatrix frequencyMatrix, double threshold) {
        matrix = NoteHelper.convertSpectrumMatrixToNoteDataMatrix(frequencyMatrix.getMatrix(),
                threshold);
        this.SAMPLING_RATE = frequencyMatrix.getSamplingRate();
        this.FIRST_MIDPOINT = frequencyMatrix.getFirstMidpoint();
        this.STEP_SIZE = frequencyMatrix.getStepSize();
    }
}
