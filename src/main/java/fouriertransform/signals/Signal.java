package fouriertransform.signals;

public abstract class Signal {
    private final SoundType type;
    private final int samplingRate;
    private byte[] byteData;
    static final double TAU = (double) (2 * Math.PI);

    public Signal(SoundType type, int samplingRate) {
        this.type = type;
        this.samplingRate = samplingRate;
    }

    public SoundType getType() {
        return type;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public byte[] getByteData() {
        if (byteData == null) {
            generateByteData();
        }
        return byteData;
    }

    void getByeData(byte[] byteData) {
        this.byteData = byteData;
    }

    abstract public double[] getSamples();

    public static Signal createArtificialSignal(int frequency, int samplingRate, int seconds) {
        double[] samples = new double[samplingRate * seconds];
        for (int i = 0; i < samplingRate * seconds; i++) {
            samples[i] = (double) Math.cos(TAU * frequency * i / (double) samplingRate);
        }

        return new MonoSignal(samples, samplingRate);
    }

    abstract void generateByteData();
}
