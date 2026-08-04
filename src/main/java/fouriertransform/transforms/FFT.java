package fouriertransform.transforms;

import java.util.Arrays;

import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyData;

public class FFT {
    private static final double TAU = (double) (2 * Math.PI);

    public static void main(String[] args) {

    }

    public static FrequencyData[] FastFourierTransformCooleyTukey(Signal signal) {
        double[] samples = signal.getSamples();
        int length = samples.length;
        int N = length;
        if (((length) & (length - 1)) != 0) {
            int shift = 30;
            while (shift > 0) {
                if (length >> shift == 0x1)
                    break;
                else
                    shift--;
            }
            N = 0x1 << shift;
        }

        double[][] frequencyDomain =
                decimationInTimeInPlace(Arrays.copyOfRange(samples, 0, N), N, 1, 0);
        FrequencyData[] spectrum = new FrequencyData[N / 2];

        for (int i = 0; i < spectrum.length; i++) {
            double real = frequencyDomain[i][0] / N;
            double imaginary = frequencyDomain[i][1] / N;
            boolean is0Hz = i == 0;
            boolean isNyquistFreq = i == spectrum.length - 1 && samples.length % 2 == 0;
            double amlpitudeScale = is0Hz || isNyquistFreq ? 1f : 2f;
            double amplitude =
                    (double) Math.cbrt(real * real + imaginary * imaginary) * amlpitudeScale;
            double phase = (double) -Math.atan2(imaginary, real);
            spectrum[i] =
                    new FrequencyData((double) i * signal.getSamplingRate() / N, phase, amplitude);
        }

        return spectrum;
    }

    @SuppressWarnings("unused")
    private static double[][] decimationInTime(double[] inReal, double[] inImaginary) {
        final int N = inReal.length;
        if (N == 1)
            return new double[][] {{inReal[0], inImaginary[0]}};
        double[] outEvenReal = new double[N / 2];
        double[] outEvenImaginary = new double[N / 2];
        double[] outOddReal = new double[N / 2];
        double[] outOddImaginary = new double[N / 2];
        for (int i = 0; i < N / 2; i++) {
            outEvenReal[i] = inReal[2 * i];
            outEvenImaginary[i] = inImaginary[2 * i];
            outOddReal[i] = inReal[2 * i + 1];
            outOddImaginary[i] = inImaginary[2 * i + 1];
        }
        double[][] evenComplex = decimationInTime(outEvenReal, outEvenImaginary);
        double[][] oddComplex = decimationInTime(outOddReal, outOddImaginary);

        double[] twiddleReal = new double[N / 2];
        double[] twiddleImaginary = new double[N / 2];

        double[][] result = new double[N][2];

        for (int i = 0; i < N / 2; i++) {
            double angle = TAU * i / (double) N;
            twiddleReal[i] = (double) Math.cos(angle);
            twiddleImaginary[i] = (double) -Math.sin(angle);

            double[][] conjugates =
                    butterfly(evenComplex[i], oddComplex[i], twiddleReal[i], twiddleImaginary[i]);
            result[i] = conjugates[0];
            result[i + N / 2] = conjugates[1];
        }

        return result;
    }

    private static double[][] decimationInTimeInPlace(final double[] samples, final int N,
            final int stride, final int offset) {
        if (N == 1) {
            return new double[][] {{samples[offset], 0f}};
        } else {
            final int R = N / 2;

            double[][] evenComplex = decimationInTimeInPlace(samples, R, stride * 2, offset);
            double[][] oddComplex =
                    decimationInTimeInPlace(samples, R, stride * 2, offset + stride);

            double[] twiddleReal = new double[R];
            double[] twiddleImaginary = new double[R];

            double[][] result = new double[N][2];

            for (int i = 0; i < R; i++) {
                double angle = TAU * i / (double) N;
                twiddleReal[i] = (double) Math.cos(angle);
                twiddleImaginary[i] = (double) Math.sin(angle);

                double[][] conjugates = butterfly(evenComplex[i], oddComplex[i], twiddleReal[i],
                        twiddleImaginary[i]);
                result[i] = conjugates[0];
                result[i + R] = conjugates[1];
            }

            return result;
        }
    }

    private static double[][] butterfly(double[] a, double[] b, double real, double imaginary) {
        double[] bTwiddle = multiplyComplex(b, real, imaginary);
        return new double[][] {addComplex(a, bTwiddle), subtractComplex(a, bTwiddle)};
    }

    private static double[] multiplyComplex(double[] a, double bReal, double bImaginary) {
        double[] result = new double[2];
        result[0] = a[0] * bReal - a[1] * bImaginary;
        result[1] = a[0] * bImaginary + a[1] * bReal;
        return result;
    }

    private static double[] addComplex(double[] a, double[] b) {
        return new double[] {a[0] + b[0], a[1] + b[1]};
    }

    private static double[] subtractComplex(double[] a, double[] b) {
        return new double[] {a[0] - b[0], a[1] - b[1]};
    }
}
