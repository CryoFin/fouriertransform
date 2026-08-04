package fouriertransform.signals;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import fouriertransform.sounddata.FrequencyData;

public class MonoSignal extends Signal {
    public double[] samples;

    public MonoSignal(double[] samples, int samplingRate) {
        super(SoundType.MONO, samplingRate);
        this.samples = samples;
    }

    public MonoSignal(FrequencyData[] spectrum, final int samplingRate, int duration) {
        super(SoundType.MONO, samplingRate);

        int numSamples = samplingRate * duration;
        double[] createdSamples = new double[numSamples];

        for (int s = 0; s < numSamples; s++) {
            double t = (double) s / numSamples * duration;

            for (FrequencyData fd : spectrum) {
                double angle = t * TAU * fd.getFrequency() + fd.getPhase();
                createdSamples[s] += Math.cos(angle) * fd.getAmplitude();
            }
        }

        this.samples = createdSamples;
    }

    @Override
    public double[] getSamples() {
        return samples;
    }

    @Override
    void generateByteData() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(samples.length * 4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < samples.length; i++) {
            byteBuffer.putDouble(i * 4, samples[i]);
        }
        getByeData(byteBuffer.array());
    }
}
