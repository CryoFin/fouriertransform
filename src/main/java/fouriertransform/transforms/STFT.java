package fouriertransform.transforms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import fouriertransform.parallel.Parallel;
import fouriertransform.signals.MonoSignal;
import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyData;

public class STFT {
    private static final float PI = (float) Math.PI;

    private static final HashMap<Integer, float[]> WINDOW_CACHE = new HashMap<>();

    public static void main(String[] args) {

    }

    public static FrequencyData[][] ShortTimeFourierTransform(Signal signal,
            final int proposedWindow) {
        float[] samples = signal.getSamples();
        final int N = samples.length;
        final int DifferenceThreshold = proposedWindow / 100;
        int shift = 30;
        while (shift > 0) {
            if (proposedWindow >> shift == 0x1)
                break;
            else
                shift--;
        }
        final int W =
                ((0x1 << (shift + 1)) - proposedWindow < DifferenceThreshold) ? (0x1 << (shift + 2))
                        : (0x1 << (shift + 1));
        if (W > N)
            throw new IllegalArgumentException("Sample size is too small");
        final int Margin = W - proposedWindow;
        final int ExtraMargin = Margin % 2 == 0 ? 0 : 1;

        ArrayList<FrequencyData[]> spectrumMatrix = new ArrayList<>();
        // List<FrequencyData[]> synchronizedSpectrumMatrix =
        // Collections.synchronizedList(spectrumMatrix);
        // for (int i = 0; i < N + Margin / 2; i += proposedWindow / 8) {
        Parallel parallel = new Parallel();
        parallel.For(0, N + Margin / 2, i -> {
            int left = i - Margin / 2 - ExtraMargin;
            int right = i + proposedWindow + 1 + Margin / 2;

            float[] sampleSegment;
            if (left < 0) {
                sampleSegment = new float[W + 1];
                System.arraycopy(samples, 0, sampleSegment, -left, W + left);
            } else if (left >= N) {
                sampleSegment = new float[W + 1];
            } else if (right > N) {
                sampleSegment = new float[W + 1];
                System.arraycopy(samples, left, sampleSegment, 0, N - left);
            } else {
                sampleSegment = Arrays.copyOfRange(samples, left, right);
            }

            applyWindow(sampleSegment);
            spectrumMatrix.add(FFT.FastFourierTransformCooleyTukey(
                    new MonoSignal(sampleSegment, signal.getSamplingRate())));
        });
        // }

        return spectrumMatrix.toArray(FrequencyData[][]::new);
    }

    private static void applyWindow(float[] samples) {
        final int N = samples.length;
        if (((N - 1) & (N - 2)) != 0)
            throw new IllegalArgumentException("Sample size is not one more than a power of two");

        float[] windowCoefficients = getWindowCoefficients(N);
        for (int i = 0; i < N; i++) {
            samples[i] *= windowCoefficients[i];
        }
    }

    private static float[] getWindowCoefficients(final int length) {
        if (((length - 1) & (length - 2)) != 0)
            throw new IllegalArgumentException("Sample size is not one more than a power of two");

        if (WINDOW_CACHE.containsKey(length)) {
            return WINDOW_CACHE.get(length);
        } else {
            float[] windowCoefficients = new float[length];
            for (int i = 0; i < length; i++) {
                float s = (float) Math.sin(PI * i / (float) length);
                windowCoefficients[i] = s * s;
            }
            WINDOW_CACHE.put(length, windowCoefficients);
            return windowCoefficients;
        }
    }
}
