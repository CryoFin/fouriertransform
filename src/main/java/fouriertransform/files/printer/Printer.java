package fouriertransform.files.printer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import fouriertransform.signals.Signal;
import fouriertransform.signals.SoundType;
import fouriertransform.sounddata.FrequencyData;
import fouriertransform.sounddata.NoteHelper;

public class Printer {

    private FileOutputStream printer;

    public Printer(File file) {
        try {
            printer = new FileOutputStream(file);
        } catch (IOException e) {
        }
    }

    public void printSignal(Signal signal) {
        try {
            printer.write(signal.getByteData(), 0, signal.getByteData().length);
            printer.flush();
        } catch (IOException e) {
        }
    }

    public void printSignal(Signal signal, float startSeconds, float endSeconds) {
        int bytesPerSample = 0;
        switch (signal.getType()) {
            case SoundType.MONO -> bytesPerSample = 4;
            case SoundType.STEREO -> bytesPerSample = 8;
        }
        byte[] byteDataPortion = Arrays.copyOfRange(signal.getByteData(),
                (int) (startSeconds * signal.getSamplingRate()) * bytesPerSample,
                ((int) (endSeconds * signal.getSamplingRate())) * bytesPerSample);
        try {
            printer.write(byteDataPortion, 0, byteDataPortion.length);
            printer.flush();
        } catch (IOException e) {
        }
    }

    public void printSpectrumMatrix(FrequencyData[][] spectrumMatrix, int count) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < spectrumMatrix.length; i++) {
            stringBuilder.append(i).append(": ")
                    .append(NoteHelper.convertSpectrumToNotes(spectrumMatrix[i], count))
                    .append('\n');
        }

        try {
            printer.write((byte[]) stringBuilder.toString().getBytes());
            printer.flush();
        } catch (IOException e) {
        }
    }
}
