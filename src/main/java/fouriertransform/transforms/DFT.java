package fouriertransform.transforms;

import fouriertransform.parallel.Parallel;
import fouriertransform.sounddata.FrequencyData;

public class DFT {
    static final float TAU = (float) (2 * Math.PI);

    public static void main(String[] args) {

    }

    @SuppressWarnings("unused")
    public static FrequencyData[] DiscreteFourierTransform(float[] samples, int sampleRate) {
        final int N = samples.length;
        final int maxFrequency = samples.length / 2 + 1;
        float frequencyStepSize = sampleRate / (float) samples.length;
        FrequencyData[] spectrum = new FrequencyData[maxFrequency];

        Parallel parallel = new Parallel();
        parallel.For(0, maxFrequency, i -> {
            float real = 0;
            float imaginary = 0;

            for (int sample = 0; sample < N; sample++) {
                float angle = TAU * i * sample / (float) N;
                real += samples[sample] * Math.cos(angle);
                imaginary += samples[sample] * Math.sin(angle);
            }

            imaginary /= N;
            real /= N;
            boolean is0Hz = i == 0;
            boolean isNyquistFreq = i == spectrum.length - 1 && samples.length % 2 == 0;
            float amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            float amplitude =
                    (float) Math.sqrt(real * real + imaginary * imaginary) * amlpitudeScale;
            float phase = (float) -Math.atan2(imaginary, real);
            float frequency = i * frequencyStepSize;
            spectrum[i] = new FrequencyData(frequency, phase, amplitude);
        });

        return spectrum;
    }

    @SuppressWarnings("unused")
    public static FrequencyData[] DiscreteFourierTransformTutorial(float[] samples,
            int sampleRate) {
        int numFrequencies = samples.length / 2 + 1;
        FrequencyData[] spectrum = new FrequencyData[numFrequencies];
        float frequencyStepSize = sampleRate / (float) samples.length;

        Parallel parallel = new Parallel();
        parallel.For(0, spectrum.length, freqIndex -> {
            float real = 0;
            float imaginary = 0;
            for (int i = 0; i < samples.length; i++) {
                float angle = i / (float) (samples.length) * TAU * freqIndex;
                real += samples[i] * Math.cos(angle);
                imaginary += samples[i] * Math.sin(angle);
            }

            real /= samples.length;
            imaginary /= samples.length;
            boolean is0Hz = freqIndex == 0;
            boolean isNyquistFreq = freqIndex == spectrum.length - 1 && samples.length % 2 == 0;
            float amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            float amplitude =
                    (float) Math.sqrt(real * real + imaginary * imaginary) * amlpitudeScale;
            float phase = (float) -Math.atan2(imaginary, real);
            float frequency = freqIndex * frequencyStepSize;
            spectrum[freqIndex] = new FrequencyData(frequency, phase, amplitude);
        });

        return spectrum;
    }
}
