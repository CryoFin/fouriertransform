package fouriertransform;

import java.io.File;

import fouriertransform.files.reader.Reader;
import fouriertransform.files.score.XML;
import fouriertransform.graphing.Sketchbook;
import fouriertransform.signals.Signal;
import fouriertransform.sounddata.FrequencyMatrix;
import fouriertransform.sounddata.NoteMatrix;
import fouriertransform.transforms.STFT;

public class FourierTransform {
	static final File WAVE_FILE = new File("src/main/resources/mnbthmus.wav");
	static final File SPECTRUM_FILE = new File("src/main/resources/Spectrum.txt");
	static final File OUTPUT_FILE = new File("src/main/resources/Output.txt");
	static final File GRAPH_FILE = new File("src/main/resources/Graph.png");
	private final static String XML_FILE = "test.xml";

	public static void main(String[] args) {
		FrequencyMatrix spectrumMatrix = processSignal();
		// new Spectrum(500, 500, spectrumMatrix.getMatrix()[75]);
		spectrumMatrix.trimByFrequencyRange(0, 2000);
		run(spectrumMatrix);
		// runXML(spectrumMatrix);
	}

	private static FrequencyMatrix processSignal() {
		Reader reader = new Reader(WAVE_FILE);
		Signal signal = reader.readSignal(63, 80);
		return STFT.ShortTimeFourierTransform(signal, 4096);
	}

	private static void run(FrequencyMatrix spectrumMatrix) {
		Sketchbook sketchbook = new Sketchbook(1280, 720);
		sketchbook.addSpectrogram(spectrumMatrix);
		sketchbook.startDisplay();

		// Histogram grapher = new Histogram(GRAPH_FILE);
		// grapher.graph(spectrumMatrix.getMatrix()[64]);
	}

	private static void runXML(FrequencyMatrix spectrumMatrix) {
		XML xml = new XML();
		xml.saveToXML(XML_FILE, new NoteMatrix(spectrumMatrix));
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
