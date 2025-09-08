package fouriertransform.signals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StereoSignal extends Signal {
    public float[] leftSamples;
    public float[] rightSamples;
    public float[] samples;

    public StereoSignal(float[] samples, int samplingRate) {
        super(SoundType.STEREO, samplingRate);
        this.samples = samples;
        leftSamples = new float[samples.length / 2];
        rightSamples = new float[samples.length / 2];
        for (int i = 0; i < samples.length / 2; i++) {
            leftSamples[i] = samples[2 * i];
            rightSamples[i] = samples[2 * i + 1];
        }
    }

    public StereoSignal(float[] leftSamples, float[] rightSamples, int samplingRate) {
        super(SoundType.STEREO, samplingRate);
        this.leftSamples = leftSamples;
        this.rightSamples = rightSamples;
        samples = new float[leftSamples.length + rightSamples.length];
        for (int i = 0; i < leftSamples.length; i++) {
            samples[2 * i] = leftSamples[i];
            samples[2 * i + 1] = rightSamples[i];
        }
    }

    @Override
    public float[] getSamples() {
        return leftSamples;
    }

    @Override
    void generateByteData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(leftSamples.length * 8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < leftSamples.length; i++) {
            byteBuffer.putFloat(i * 8, leftSamples[i]);
            byteBuffer.putFloat(i * 8 + 4, rightSamples[i]);
        }
        getByeData(byteBuffer.array());
    }
}
