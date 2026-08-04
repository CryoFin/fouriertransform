package fouriertransform.transforms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fouriertransform.signals.MonoSignal;
import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyData;
import fouriertransform.sounddata.FrequencyMatrix;

public class STFT {
    private static final double PI = (double) Math.PI;

    private static final HashMap<Window, HashMap<Integer, double[]>> WINDOW_CACHE = new HashMap<>();

    public static void main(String[] args) {

    }

    public static FrequencyMatrix ShortTimeFourierTransform(Signal signal,
            final int proposedWindow) {
        double[] samples = signal.getSamples();
        final int N = samples.length;
        int W = proposedWindow;
        if (((proposedWindow) & (proposedWindow - 1)) != 0) {
            int shift = 30;
            while (shift > 0) {
                if (proposedWindow >> shift == 0x1)
                    break;
                else
                    shift--;
            }
            W = 0x1 << shift;
        }
        if (W > N)
            throw new IllegalArgumentException("Window is too large");
        final int STEP = W / 8;

        ArrayList<FrequencyData[]> spectrumMatrix = new ArrayList<>();
        for (int i = -W / 2; i < N + W / 2; i += STEP) {
            int left = i;
            int right = i + W;

            double[] sampleSegment;
            if (left < 0) {
                sampleSegment = new double[W];
                System.arraycopy(samples, 0, sampleSegment, -left, W + left);
            } else if (left >= N) {
                sampleSegment = new double[W];
            } else if (right > N) {
                sampleSegment = new double[W];
                System.arraycopy(samples, left, sampleSegment, 0, N - left);
            } else {
                sampleSegment = Arrays.copyOfRange(samples, left, right);
            }

            applyWindow(sampleSegment, Window.BLACKMAN_HARRIS);
            spectrumMatrix.add(FFT.FastFourierTransformCooleyTukey(
                    new MonoSignal(sampleSegment, signal.getSamplingRate())));
        }

        return new FrequencyMatrix(spectrumMatrix.toArray(FrequencyData[][]::new),
                signal.getSamplingRate(), W / 2, STEP);
    }

    private static void applyWindow(double[] samples, Window WINDOWTYPE) {
        final int N = samples.length;
        if (((N - 1) & (N)) != 0)
            throw new IllegalArgumentException("Sample size is not a power of two");

        double[] windowCoefficients = getWindowCoefficients(N, WINDOWTYPE);
        for (int i = 0; i < N; i++) {
            samples[i] *= windowCoefficients[i];
        }
    }

    private static double[] getWindowCoefficients(final int length, Window WINDOWTYPE) {
        if (((length - 1) & (length)) != 0)
            throw new IllegalArgumentException("Sample size is not a power of two");

        if (getWindowMap(WINDOWTYPE).containsKey(length)) {
            return WINDOW_CACHE.get(WINDOWTYPE).get(length);
        } else {
            double[] windowCoefficients = new double[length];

            switch (WINDOWTYPE) {
                case COSINE -> {
                    for (int i = 0; i < length; i++) {
                        double s = (double) Math.sin(PI * i / (double) length);
                        windowCoefficients[i] = s * s;
                    }
                }
                case BLACKMAN_HARRIS -> {
                    final double A_0 = 0.4243801;
                    final double A_1 = 0.4973406;
                    final double A_2 = 0.0782793;
                    for (int i = 0; i < length; i++) {
                        double s = A_0 - A_1 * Math.cos(2 * PI * i / (double) length)
                                + A_2 * Math.cos(4 * PI * i / (double) length);
                        windowCoefficients[i] = s;
                    }
                }
            }
            getWindowMap(WINDOWTYPE).put(length, windowCoefficients);
            return windowCoefficients;
        }
    }

    private static HashMap<Integer, double[]> getWindowMap(Window WINDOWTYPE) {
        HashMap<Integer, double[]> windowMap = WINDOW_CACHE.get(WINDOWTYPE);
        if (windowMap == null) {
            windowMap = new HashMap<>();
            WINDOW_CACHE.put(WINDOWTYPE, windowMap);
        }
        return windowMap;
    }
}
