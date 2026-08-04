package fouriertransform.sounddata;

public class FrequencyMatrix {
    private FrequencyData[][] matrix;
    private final int SAMPLING_RATE;
    private final int STEP_SIZE;
    private final int FIRST_MIDPOINT;

    public int getSamplingRate() {
        return SAMPLING_RATE;
    }

    public int getStepSize() {
        return STEP_SIZE;
    }

    public int getFirstMidpoint() {
        return FIRST_MIDPOINT;
    }

    public FrequencyData[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(FrequencyData[][] _matrix) {
        this.matrix = _matrix;
    }

    public FrequencyMatrix(FrequencyData[][] _matrix, int _samplingRate, int _firstMidpoint,
            int _stepSize) {
        this.matrix = _matrix;
        this.SAMPLING_RATE = _samplingRate;
        this.FIRST_MIDPOINT = _firstMidpoint;
        this.STEP_SIZE = _stepSize;
    }

    public void trimByFrequencyRange(int minFrequency, int maxFrequency) {
        matrix = FrequencyData.trimByFrequencyRange(matrix, minFrequency, maxFrequency);
    }
}
