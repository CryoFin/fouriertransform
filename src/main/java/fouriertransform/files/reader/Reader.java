package fouriertransform.files.reader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import fouriertransform.signals.MonoSignal;
import fouriertransform.signals.Signal;
import fouriertransform.signals.StereoSignal;

public class Reader {
    private BufferedInputStream reader;
    private HashMap<String, Long> chunkLookup;
    private FileDetails fileDetails;

    public static void main(String[] args) {

    }

    public Reader(File file) {
        try {
            reader = new BufferedInputStream(new FileInputStream(file));
            reader.mark(reader.available() + 1);
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't create reader as file was not found");
        } catch (IOException i) {
            System.out.println("Error marking reader");
        }
    }

    public BufferedInputStream getReader() {
        return reader;
    }

    public HashMap<String, Long> getChunkLookup() {
        return chunkLookup;
    }

    public FileDetails getFileDetails() {
        return fileDetails;
    }

    public Signal readSignal(double startSeconds, double endSeconds) {
        createWavChunkLookup();
        readFileDetails(false);
        return readSignalFromFile(startSeconds, endSeconds);
    }

    private void createWavChunkLookup() {
        fileDetails = new FileDetails();
        long position = 0;
        try {
            byte[] ckID = new byte[4];
            byte[] ckSize = new byte[4];
            byte[] waveID = new byte[4];
            reader.read(ckID, 0, 4);
            reader.read(ckSize, 0, 4);
            reader.read(waveID, 0, 4);
            String RIFFid = new String(ckID, StandardCharsets.UTF_8);
            String WAVEid = new String(waveID, StandardCharsets.UTF_8);
            if (!RIFFid.equals("RIFF") || !WAVEid.equals("WAVE"))
                System.out.println("Invalid .WAV file");
            int chunkSize = BitHelper.convertFourBytesLittleEndian(ckSize);
            fileDetails.totalbytes = chunkSize + 8;
            position += 12;
        } catch (IOException e) {
            System.out.println("Could not read RIFF chunk");
        }
        chunkLookup = new HashMap<>();
        chunkLookup.put("riffID", (long) 0);
        try {
            for (int i = 0; i < 3; i++) {
                byte[] ckID = new byte[4];
                byte[] ckSize = new byte[4];
                reader.read(ckID, 0, 4);
                reader.read(ckSize, 0, 4);
                String chunkID = new String(ckID, StandardCharsets.UTF_8);
                int chunkSize = BitHelper.convertFourBytesLittleEndian(ckSize);
                chunkLookup.put(chunkID, position);
                reader.skip(chunkSize);
                position += 8 + chunkSize;
            }
        } catch (IOException e) {
            System.out.println("Could not read WAVE chunks");
        }
        try {
            reader.reset();
        } catch (IOException e) {
            System.out.println("Could not reset position");
        }
    }

    private void readFileDetails(boolean doList) {
        try {
            byte[] twoByteBuffer = new byte[2];
            byte[] fourByteBuffer = new byte[4];
            reader.skip(chunkLookup.get("fmt ") + 10);
            reader.read(twoByteBuffer, 0, 2);
            fileDetails.numChannels = BitHelper.convertTwoBytesLittleEndian(twoByteBuffer);
            reader.read(fourByteBuffer, 0, 4);
            fileDetails.samplingRate = BitHelper.convertFourBytesLittleEndian(fourByteBuffer);
            reader.read(fourByteBuffer, 0, 4);
            fileDetails.dataRate = BitHelper.convertFourBytesLittleEndian(fourByteBuffer);
            reader.read(twoByteBuffer, 0, 2);
            fileDetails.dataBlockSize = BitHelper.convertTwoBytesLittleEndian(twoByteBuffer);
            reader.read(twoByteBuffer, 0, 2);
            fileDetails.bitsPerSample = BitHelper.convertTwoBytesLittleEndian(twoByteBuffer);
            fileDetails.bytesPerSample = fileDetails.bitsPerSample / 8;
            fileDetails.duration =
                    reader.available() / fileDetails.dataBlockSize / fileDetails.samplingRate;
        } catch (IOException e) {
            System.out.println("error in GetFileDetails");
        }
        try {
            reader.reset();
        } catch (IOException e) {
            System.out.println("error in reseting");
        }
        if (doList) {
            fileDetails.printToTerminal();
        }
    }

    private Signal readSignalFromFile(double startSeconds, double endSeconds) {
        boolean isFull = false;
        if (startSeconds == 0 && endSeconds == 0) {
            isFull = true;
        }
        int numSamples = 0;
        double normalizationFactor =
                (double) 1 / (double) (Math.pow(2, (fileDetails.bitsPerSample - 1)) - 1);
        try {
            reader.reset();
            long bytesToSkip = chunkLookup.get("data") + 8
                    + (long) (startSeconds * fileDetails.samplingRate * fileDetails.dataBlockSize);
            while (bytesToSkip > 0) {
                bytesToSkip -= reader.skip(bytesToSkip);
            }
            numSamples = isFull ? reader.available() / fileDetails.dataBlockSize
                    : Math.min(reader.available() / fileDetails.dataBlockSize,
                            (int) ((endSeconds - startSeconds) * fileDetails.samplingRate));
        } catch (IOException e) {
        }

        switch (fileDetails.numChannels) {
            case 1 -> {
                try {
                    byte[] dataBlock = new byte[fileDetails.dataBlockSize];
                    double[] samples = new double[numSamples];
                    for (int i = 0; i < numSamples; i++) {
                        reader.read(dataBlock, 0, fileDetails.dataBlockSize);
                        samples[i] =
                                BitHelper.convertBytesToDouble(dataBlock) * normalizationFactor;
                    }
                    return new MonoSignal(samples, fileDetails.samplingRate);
                } catch (IOException e) {
                    System.out.println("Error getting samples");
                }
            }
            case 2 -> {
                try {
                    byte[] dataBlockLeft = new byte[fileDetails.bytesPerSample];
                    byte[] dataBlockRight = new byte[fileDetails.bytesPerSample];
                    double[] leftSamples = new double[numSamples];
                    double[] rightSamples = new double[numSamples];
                    for (int i = 0; i < numSamples; i++) {
                        reader.read(dataBlockLeft, 0, fileDetails.bytesPerSample);
                        reader.read(dataBlockRight, 0, fileDetails.bytesPerSample);
                        leftSamples[i] =
                                BitHelper.convertBytesToDouble(dataBlockLeft) * normalizationFactor;
                        rightSamples[i] = BitHelper.convertBytesToDouble(dataBlockRight)
                                * normalizationFactor;
                    }
                    return new StereoSignal(leftSamples, rightSamples, fileDetails.samplingRate);
                } catch (IOException e) {
                    System.out.println("Error getting samples");
                }
            }
            default -> {
                System.out.println("Unknown track type");
                return new MonoSignal(new double[0], 0);
            }
        }
        return new MonoSignal(new double[0], 0);
    }
}
