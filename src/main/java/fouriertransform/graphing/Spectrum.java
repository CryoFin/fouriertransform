package fouriertransform.graphing;

import java.util.stream.Stream;

import fouriertransform.sounddata.FrequencyData;
import fouriertransform.sounddata.NoteData;
import fouriertransform.sounddata.NoteHelper;
import processing.core.PApplet;

public class Spectrum extends PApplet {
    public final static String[] processingArgs = {"Spectrum"};
    private final static int LEFT_MARGIN = 50;
    private final static int RIGHT_MARGIN = 50;
    private final static int TOP_MARGIN = 50;
    private final static int BOTTOM_MARGIN = 50;

    private int sketchHeight;
    private int sketchWidth;
    private int graphHeight;
    private int graphWidth;
    private FrequencyData[] frequencySpectrum;
    private NoteData[] noteSpectrum;

    public Spectrum(int _sketchHeight, int _sketchWidth) {
        this.sketchHeight = _sketchHeight;
        this.sketchWidth = _sketchWidth;
        this.graphHeight = sketchHeight - TOP_MARGIN - BOTTOM_MARGIN;
        this.graphWidth = sketchWidth - LEFT_MARGIN - RIGHT_MARGIN;

        startSketch();
    }

    public Spectrum(int _sketchHeight, int _sketchWidth, FrequencyData[] _frequencySpectrum) {
        this.sketchHeight = _sketchHeight;
        this.sketchWidth = _sketchWidth;
        this.frequencySpectrum = _frequencySpectrum;
        this.graphHeight = sketchHeight - TOP_MARGIN - BOTTOM_MARGIN;
        this.graphWidth = sketchWidth - LEFT_MARGIN - RIGHT_MARGIN;

        startSketch();
    }

    public Spectrum(int _sketchHeight, int _sketchWidth, NoteData[] _noteSpectrum) {
        this.sketchHeight = _sketchHeight;
        this.sketchWidth = _sketchWidth;
        this.noteSpectrum = _noteSpectrum;
        this.graphHeight = sketchHeight - TOP_MARGIN - BOTTOM_MARGIN;
        this.graphWidth = sketchWidth - LEFT_MARGIN - RIGHT_MARGIN;

        startSketch();
    }

    private void startSketch() {
        PApplet.runSketch(processingArgs, this);
    }

    @Override
    public void settings() {
        size(graphHeight, graphWidth);
    }

    @Override
    public void setup() {
        noStroke();
        surface.setResizable(true);
    }

    @Override
    public void draw() {
        drawHistogram();
    }

    @Override
    public void windowResized() {
        sketchHeight = height;
        sketchWidth = width;
        graphHeight = sketchHeight - TOP_MARGIN - BOTTOM_MARGIN;
        graphWidth = sketchWidth - LEFT_MARGIN - RIGHT_MARGIN;
    }

    private void drawHistogram() {
        double[] values;
        String[] labels;
        double maxValue;
        int length;

        if (noteSpectrum != null) {
            labels = Stream.of(noteSpectrum).map(n -> n.getNote()).sorted((s1,
                    s2) -> NoteHelper.NOTE_TO_INT.get(s1).compareTo(NoteHelper.NOTE_TO_INT.get(s2)))
                    .toArray(String[]::new);
            values = Stream.of(noteSpectrum)
                    .sorted((n1, n2) -> NoteHelper.NOTE_TO_INT.get(n1.getNote())
                            .compareTo(NoteHelper.NOTE_TO_INT.get(n2.getNote())))
                    .map(n -> n.getAmplitude()).mapToDouble(Double::doubleValue).toArray();
            maxValue = NoteData.getMaxAmplitude(noteSpectrum);
            length = noteSpectrum.length;
        } else if (frequencySpectrum != null) {
            labels = Stream.of(frequencySpectrum)
                    .sorted((f1, f2) -> Double.compare(f1.getFrequency(), f2.getFrequency()))
                    .map(f -> String.valueOf(f.getAmplitude())).toArray(String[]::new);
            values = Stream.of(frequencySpectrum)
                    .sorted((f1, f2) -> Double.compare(f1.getFrequency(), f2.getFrequency()))
                    .map(f -> f.getAmplitude()).mapToDouble(Double::doubleValue).toArray();
            maxValue = FrequencyData.getMaxAmplitude(frequencySpectrum);
            length = frequencySpectrum.length;
        } else {
            values = new double[0];
            labels = new String[0];
            maxValue = 1;
            length = 0;
        }

        final float CELL_WIDTH = (float) graphWidth / length;

        for (int i = 0; i < length; i++) {
            float percentage = (float) (values[i] / maxValue);
            float rectHeight = percentage * graphHeight;
            fill(0, 0, 255);
            rect(LEFT_MARGIN + CELL_WIDTH * i, sketchHeight - BOTTOM_MARGIN - rectHeight,
                    CELL_WIDTH, rectHeight);
        }
    }
}
