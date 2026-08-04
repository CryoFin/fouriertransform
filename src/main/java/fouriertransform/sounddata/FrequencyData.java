package fouriertransform.sounddata;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.stream.Stream;

public class FrequencyData {
    private final double frequency;
    private final double phase;
    private final double amplitude;

    public FrequencyData(double frequency, double phase, double amplitude) {
        this.frequency = frequency;
        this.phase = phase;
        this.amplitude = amplitude;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getPhase() {
        return phase;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public static FrequencyData getPeakFrequency(FrequencyData[] spectrum) {
        double peakFrequency = Double.MIN_VALUE;
        int idx = -1;
        for (int i = 0; i < spectrum.length; i++) {
            if (spectrum[i].getFrequency() > peakFrequency) {
                peakFrequency = spectrum[i].getFrequency();
                idx = i;
            }
        }
        return spectrum[idx];
    }

    public static double getMaxAmplitude(FrequencyData[] spectrum) {
        double maxAmplitude = -1;
        for (FrequencyData frequencyData : spectrum) {
            maxAmplitude = Math.max(maxAmplitude, frequencyData.amplitude);
        }
        return maxAmplitude;
    }

    public static double getMaxAmplitude(FrequencyData[][] spectrumMatrix) {
        double maxAmplitude = -1;
        for (FrequencyData[] spectrum : spectrumMatrix) {
            maxAmplitude = Math.max(getMaxAmplitude(spectrum), maxAmplitude);
        }
        return maxAmplitude;
    }

    public static void normalize(FrequencyData[] spectrum) {
        double maxAmplitude = getMaxAmplitude(spectrum);
        for (int i = 0; i < spectrum.length; i++) {
            FrequencyData oldFrequencyData = spectrum[i];
            spectrum[i] = new FrequencyData(oldFrequencyData.frequency, oldFrequencyData.phase,
                    oldFrequencyData.amplitude / maxAmplitude);
        }
    }

    public static void normalize(FrequencyData[][] spectrumMatrix) {
        double maxAmplitude = getMaxAmplitude(spectrumMatrix);
        for (FrequencyData[] spectrum : spectrumMatrix) {
            for (int i = 0; i < spectrum.length; i++) {
                FrequencyData oldFrequencyData = spectrum[i];
                spectrum[i] = new FrequencyData(oldFrequencyData.frequency, oldFrequencyData.phase,
                        Math.min(oldFrequencyData.amplitude / maxAmplitude, 1));
            }
        }
    }

    public static FrequencyData[][] trimByAmplitudeThreshold(FrequencyData[][] spectrumMatrix,
            double threshold) {
        int lowestIdx = spectrumMatrix[0].length;
        for (FrequencyData[] spectrum : spectrumMatrix) {
            for (int i = 0; i < lowestIdx; i++) {
                if (spectrum[i].getAmplitude() > threshold) {
                    lowestIdx = i;
                }
            }
        }
        if (lowestIdx == spectrumMatrix[0].length)
            throw new IllegalArgumentException("Threshold is too high");

        int highestIdx = -1;
        for (FrequencyData[] spectrum : spectrumMatrix) {
            for (int i = spectrumMatrix[0].length - 1; i > highestIdx; i--) {
                if (spectrum[i].getAmplitude() > threshold) {
                    highestIdx = i;
                }
            }
        }
        final int li = lowestIdx;
        final int hi = highestIdx;
        if (highestIdx == -1)
            throw new IllegalArgumentException("Thrshold is too high");

        spectrumMatrix = Stream.of(spectrumMatrix)
                .map(s -> Arrays.asList(s).subList(li, hi + 1).toArray(FrequencyData[]::new))
                .toArray(FrequencyData[][]::new);
        return spectrumMatrix;
    }

    public static FrequencyData[][] trimByFrequencyRange(FrequencyData[][] spectrumMatrix,
            double minFrequency, double maxFrequency) {
        final int L = spectrumMatrix[0].length;

        int low = 0;
        while (spectrumMatrix[0][low].getFrequency() < minFrequency) {
            low++;
            if (low == L)
                throw new IllegalArgumentException("Minimum Frequency is too high");
        }

        int high = L - 1;
        while (spectrumMatrix[0][high].getFrequency() > maxFrequency) {
            high--;
            if (high == -1)
                throw new IllegalArgumentException("Maximum Frequency is too low");
        }

        for (int i = 0; i < spectrumMatrix.length; i++) {
            spectrumMatrix[i] = Arrays.copyOfRange(spectrumMatrix[i], low, high);
        }

        return spectrumMatrix;
    }

    public static FrequencyData[] sort(FrequencyData[] spectrum) {
        PriorityQueue<FrequencyData> priorityQueue =
                new PriorityQueue<>((FrequencyData fd1, FrequencyData fd2) -> compare(fd1, fd2));
        priorityQueue.addAll(Arrays.asList(spectrum));
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = priorityQueue.poll();
        }

        return spectrum;
    }

    public static FrequencyData[] order(FrequencyData[] spectrum) {
        PriorityQueue<FrequencyData> priorityQueue =
                new PriorityQueue<>((FrequencyData fd1, FrequencyData fd2) -> {
                    if (fd1.frequency == fd2.frequency) {
                        return 0;
                    } else {
                        return fd1.frequency < fd2.frequency ? -1 : 1;
                    }
                });
        priorityQueue.addAll(Arrays.asList(spectrum));
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = priorityQueue.poll();
        }

        return spectrum;
    }

    public static FrequencyData[] filter(FrequencyData[] spectrum, final double Threshold) {
        int filtered = spectrum.length;
        int idx = 0;
        while (idx < filtered) {
            if (spectrum[idx].getAmplitude() < Threshold) {
                filtered--;
                spectrum[idx] = spectrum[filtered];
            } else {
                idx++;
            }
        }

        spectrum = Arrays.copyOf(spectrum, filtered);
        return spectrum;
    }

    public static FrequencyData[][] filter(FrequencyData[][] spectrumMatrix,
            final double Threshold) {
        for (FrequencyData[] spectrum : spectrumMatrix) {
            filter(spectrum, Threshold);
        }
        return spectrumMatrix;
    }

    private static int compare(FrequencyData fd1, FrequencyData fd2) {
        if (fd1.amplitude == fd2.amplitude) {
            return 0;
        } else {
            return fd1.amplitude < fd2.amplitude ? -1 : 1;
        }
    }
}
