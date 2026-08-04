package fouriertransform.graphing;

import java.util.ArrayList;

import fouriertransform.sounddata.FrequencyMatrix;
import fouriertransform.sounddata.NoteMatrix;
import processing.core.PApplet;

public class Sketchbook extends PApplet {
    private final static String[] processingArgs = {"Sketchbook"};

    private final int WIDTH;
    private final int HEIGHT;
    private final ArrayList<Spectrogram> spectrograms = new ArrayList<>();
    private final NoteList NOTE_LIST;
    int spectrogramIndex = 0;
    private final int SG_OX = 100;
    private final int SG_OY = 0;
    private final int SG_PX = 120;
    private final int SG_PY = 50;

    public Sketchbook(int _width, int _height) {
        this.WIDTH = _width;
        this.HEIGHT = _height;
        NOTE_LIST =
                new NoteList(this, WIDTH - SG_PX + 10, SG_OY, SG_PX - 20, HEIGHT - SG_PY - SG_OY);
    }

    public static void main(String[] args) {
        Sketchbook sketchbook = new Sketchbook(0, 0);
        PApplet.runSketch(processingArgs, sketchbook);
    }

    public void startDisplay() {
        PApplet.runSketch(processingArgs, this);
    }

    public void addSpectrogram(FrequencyMatrix frequencyMatrix) {
        addSpectrogram(new NoteMatrix(frequencyMatrix));
    }

    public void addSpectrogram(NoteMatrix noteMatrix) {
        Spectrogram spectrogram = new Spectrogram(noteMatrix, this, SG_OX, SG_OY,
                WIDTH - SG_PX - SG_OX, HEIGHT - SG_PY - SG_OY);
        addSpectrogram(spectrogram);
    }

    public void addSpectrogram(Spectrogram spectrogram) {
        spectrograms.add(spectrogram);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup() {
        noStroke();
    }

    @Override
    public void draw() {
        background(255);
        if (!spectrograms.isEmpty()) {
            Spectrogram spectrogram = spectrograms.get(spectrogramIndex);
            spectrogram.display();
            spectrogram.displayLines(mouseX, mouseY);
            NOTE_LIST.display(spectrogram.getNoteList());
        }
    }

    @Override
    public void mousePressed() {
        Spectrogram spectrogram = spectrograms.get(spectrogramIndex);
        spectrogram.updateLines(mouseX, mouseY);
    }

    @Override
    public void keyPressed() {
        Spectrogram spectrogram = spectrograms.get(spectrogramIndex);
        spectrogram.handleKey(key, keyCode);
    }

}
