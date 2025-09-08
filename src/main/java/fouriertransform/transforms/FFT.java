package fouriertransform.transforms;

import java.util.Arrays;

import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyData;

public class FFT {
    private static final float TAU = (float) (2 * Math.PI);

    public static void main(String[] args) {

    }

    public static FrequencyData[] FastFourierTransformCooleyTukey(Signal signal) {
        float[] samples = signal.getSamples();
        int length = samples.length;
        int shift = 30;
        while (shift > 0) {
            if (length >> shift == 0x1)
                break;
            else
                shift--;
        }
        int N = 0x1 << shift;

        float[][] frequencyDomain =
                decimationInTimeInPlace(Arrays.copyOfRange(samples, 0, N), N, 1, 0);
        FrequencyData[] spectrum = new FrequencyData[N / 2];

        for (int i = 0; i < spectrum.length; i++) {
            float real = frequencyDomain[i][0] / N;
            float imaginary = frequencyDomain[i][1] / N;
            boolean is0Hz = i == 0;
            boolean isNyquistFreq = i == spectrum.length - 1 && samples.length % 2 == 0;
            float amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            float amplitude =
                    (float) Math.cbrt(real * real + imaginary * imaginary) * amlpitudeScale;
            float phase = (float) -Math.atan2(imaginary, real);
            spectrum[i] =
                    new FrequencyData((float) i * signal.getSamplingRate() / N, phase, amplitude);
        }

        return spectrum;
    }

    @SuppressWarnings("unused")
    private static float[][] decimationInTime(float[] inReal, float[] inImaginary) {
        final int N = inReal.length;
        if (N == 1)
            return new float[][] {{inReal[0], inImaginary[0]}};
        float[] outEvenReal = new float[N / 2];
        float[] outEvenImaginary = new float[N / 2];
        float[] outOddReal = new float[N / 2];
        float[] outOddImaginary = new float[N / 2];
        for (int i = 0; i < N / 2; i++) {
            outEvenReal[i] = inReal[2 * i];
            outEvenImaginary[i] = inImaginary[2 * i];
            outOddReal[i] = inReal[2 * i + 1];
            outOddImaginary[i] = inImaginary[2 * i + 1];
        }
        float[][] evenComplex = decimationInTime(outEvenReal, outEvenImaginary);
        float[][] oddComplex = decimationInTime(outOddReal, outOddImaginary);

        float[] twiddleReal = new float[N / 2];
        float[] twiddleImaginary = new float[N / 2];

        float[][] result = new float[N][2];

        for (int i = 0; i < N / 2; i++) {
            float angle = TAU * i / (float) N;
            twiddleReal[i] = (float) Math.cos(angle);
            twiddleImaginary[i] = (float) -Math.sin(angle);

            float[][] conjugates =
                    butterfly(evenComplex[i], oddComplex[i], twiddleReal[i], twiddleImaginary[i]);
            result[i] = conjugates[0];
            result[i + N / 2] = conjugates[1];
        }

        return result;
    }

    private static float[][] decimationInTimeInPlace(final float[] samples, final int N,
            final int stride, final int offset) {
        if (N == 1) {
            return new float[][] {{samples[offset], 0f}};
        } else {
            final int R = N / 2;

            float[][] evenComplex = decimationInTimeInPlace(samples, R, stride * 2, offset);
            float[][] oddComplex = decimationInTimeInPlace(samples, R, stride * 2, offset + stride);

            float[] twiddleReal = new float[R];
            float[] twiddleImaginary = new float[R];

            float[][] result = new float[N][2];

            for (int i = 0; i < R; i++) {
                float angle = TAU * i / (float) N;
                twiddleReal[i] = (float) Math.cos(angle);
                twiddleImaginary[i] = (float) Math.sin(angle);

                float[][] conjugates = butterfly(evenComplex[i], oddComplex[i], twiddleReal[i],
                        twiddleImaginary[i]);
                result[i] = conjugates[0];
                result[i + R] = conjugates[1];
            }

            return result;
        }
    }

    private static float[][] butterfly(float[] a, float[] b, float real, float imaginary) {
        float[] bTwiddle = multiplyComplex(b, real, imaginary);
        return new float[][] {addComplex(a, bTwiddle), subtractComplex(a, bTwiddle)};
    }

    private static float[] multiplyComplex(float[] a, float bReal, float bImaginary) {
        float[] result = new float[2];
        result[0] = a[0] * bReal - a[1] * bImaginary;
        result[1] = a[0] * bImaginary + a[1] * bReal;
        return result;
    }

    private static float[] addComplex(float[] a, float[] b) {
        return new float[] {a[0] + b[0], a[1] + b[1]};
    }

    private static float[] subtractComplex(float[] a, float[] b) {
        return new float[] {a[0] - b[0], a[1] - b[1]};
    }
}
