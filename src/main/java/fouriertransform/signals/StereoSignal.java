package fouriertransform.signals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StereoSignal extends Signal {
    public double[] leftSamples;
    public double[] rightSamples;
    public double[] samples;

    public StereoSignal(double[] samples, int samplingRate) {
        super(SoundType.STEREO, samplingRate);
        this.samples = samples;
        leftSamples = new double[samples.length / 2];
        rightSamples = new double[samples.length / 2];
        for (int i = 0; i < samples.length / 2; i++) {
            leftSamples[i] = samples[2 * i];
            rightSamples[i] = samples[2 * i + 1];
        }
    }

    public StereoSignal(double[] leftSamples, double[] rightSamples, int samplingRate) {
        super(SoundType.STEREO, samplingRate);
        this.leftSamples = leftSamples;
        this.rightSamples = rightSamples;
        samples = new double[leftSamples.length + rightSamples.length];
        for (int i = 0; i < leftSamples.length; i++) {
            samples[2 * i] = leftSamples[i];
            samples[2 * i + 1] = rightSamples[i];
        }
    }

    @Override
    public double[] getSamples() {
        return leftSamples;
    }

    @Override
    void generateByteData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(leftSamples.length * 8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < leftSamples.length; i++) {
            byteBuffer.putDouble(i * 8, leftSamples[i]);
            byteBuffer.putDouble(i * 8 + 4, rightSamples[i]);
        }
        getByeData(byteBuffer.array());
    }
}
