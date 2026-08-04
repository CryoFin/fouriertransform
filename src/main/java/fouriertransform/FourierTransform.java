package fouriertransform;

import java.io.File;

import fouriertransform.files.reader.Reader;
import fouriertransform.graphing.Heatmap;
import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyData;
import fouriertransform.transforms.STFT;

public class FourierTransform {
	static final File WAVE_FILE = new File("src/main/resources/Tsuzuku [music].wav");
	static final File SPECTRUM_FILE = new File("src/main/resources/Spectrum.txt");
	static final File OUTPUT_FILE = new File("src/main/resources/Output.txt");
	static final File GRAPH_FILE = new File("src/main/resources/Graph.png");

	public static void main(String[] args) {
		run();
	}

	private static void run() {
		Reader reader = new Reader(WAVE_FILE);
		Heatmap grapher = new Heatmap(GRAPH_FILE);
		// Histogram grapher = new Histogram(GRAPH_FILE);

		Signal signal = reader.readSignal(31, 33);
		FrequencyData[][] spectrumMatrix =
				STFT.ShortTimeFourierTransform(signal, (signal.getSamplingRate() / 10));

		grapher.graph(FrequencyData.trimByFrequencyRange(spectrumMatrix, 0, 2000));
		// grapher.graphFrequency(spectrumMatrix);
	}

	public static final File getWAV_FILE() {
		return WAVE_FILE;
	}

	public static final File getSPECTRUM_FILE() {
		return SPECTRUM_FILE;
	}

	public static final File getOUTPUT_FILE() {
		return OUTPUT_FILE;
	}

	public static final File getGRAPH_FLIE() {
		return GRAPH_FILE;
	}
}
