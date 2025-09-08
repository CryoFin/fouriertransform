package fouriertransform.files.reader;

public class FileDetails {
    public int numChannels;
    public int samplingRate;
    public int dataRate;
    public int dataBlockSize;
    public int bitsPerSample;
    public int bytesPerSample;
    public int duration;
    public int totalbytes;

    public void printToTerminal() {
        System.out.println("numChannels: " + numChannels);
        System.out.println("samplingRate: " + samplingRate);
        System.out.println("dataRate: " + dataRate);
        System.out.println("dataBlockSize: " + dataBlockSize);
        System.out.println("bitsPerSample: " + bitsPerSample);
        System.out.println("bytesPerSample: " + bytesPerSample);
        System.out.println("Duration: " + duration);
        System.out.println("TotalBytes: " + totalbytes);
    }
}
