package fouriertransform.transforms;

import fouriertransform.parallel.Parallel;
import fouriertransform.sounddata.FrequencyData;

public class DFT {
    static final double TAU = (double) (2 * Math.PI);

    public static void main(String[] args) {

    }

    @SuppressWarnings("unused")
    public static FrequencyData[] DiscreteFourierTransform(double[] samples, int sampleRate) {
        final int N = samples.length;
        final int maxFrequency = samples.length / 2 + 1;
        double frequencyStepSize = sampleRate / (double) samples.length;
        FrequencyData[] spectrum = new FrequencyData[maxFrequency];

        Parallel parallel = new Parallel();
        parallel.For(0, maxFrequency, i -> {
            double real = 0;
            double imaginary = 0;

            for (int sample = 0; sample < N; sample++) {
                double angle = TAU * i * sample / (double) N;
                real += samples[sample] * Math.cos(angle);
                imaginary += samples[sample] * Math.sin(angle);
            }

            imaginary /= N;
            real /= N;
            boolean is0Hz = i == 0;
            boolean isNyquistFreq = i == spectrum.length - 1 && samples.length % 2 == 0;
            double amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            double amplitude =
                    (double) Math.sqrt(real * real + imaginary * imaginary) * amlpitudeScale;
            double phase = (double) -Math.atan2(imaginary, real);
            double frequency = i * frequencyStepSize;
            spectrum[i] = new FrequencyData(frequency, phase, amplitude);
        });

        return spectrum;
    }

    @SuppressWarnings("unused")
    public static FrequencyData[] DiscreteFourierTransformTutorial(double[] samples,
            int sampleRate) {
        int numFrequencies = samples.length / 2 + 1;
        FrequencyData[] spectrum = new FrequencyData[numFrequencies];
        double frequencyStepSize = sampleRate / (double) samples.length;

        Parallel parallel = new Parallel();
        parallel.For(0, spectrum.length, freqIndex -> {
            double real = 0;
            double imaginary = 0;
            for (int i = 0; i < samples.length; i++) {
                double angle = i / (double) (samples.length) * TAU * freqIndex;
                real += samples[i] * Math.cos(angle);
                imaginary += samples[i] * Math.sin(angle);
            }

            real /= samples.length;
            imaginary /= samples.length;
            boolean is0Hz = freqIndex == 0;
            boolean isNyquistFreq = freqIndex == spectrum.length - 1 && samples.length % 2 == 0;
            double amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            double amplitude =
                    (double) Math.sqrt(real * real + imaginary * imaginary) * amlpitudeScale;
            double phase = (double) -Math.atan2(imaginary, real);
            double frequency = freqIndex * frequencyStepSize;
            spectrum[freqIndex] = new FrequencyData(frequency, phase, amplitude);
        });

        return spectrum;
    }
}
