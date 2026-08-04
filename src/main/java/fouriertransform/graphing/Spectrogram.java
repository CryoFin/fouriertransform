package fouriertransform.graphing;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fouriertransform.sounddata.NoteData;
import fouriertransform.sounddata.NoteHelper;
import fouriertransform.sounddata.NoteMatrix;
import processing.core.PApplet;

public class Spectrogram extends PApplet {
    private final float MIN_X_LABEL_DISTANCE = 10f;

    private final double[] X_LABELS;
    private final String[] Y_LABELS;
    private final double[][] DATA;
    private final Sketchbook SKETCHBOOK;
    private final int GRAPH_WIDTH;
    private final int GRAPH_HEIGHT;
    private final float CELL_HEIGHT;
    private final int ORIGIN_X;
    private final int ORIGIN_Y;
    private final int X_COUNT;
    private final int Y_COUNT;
    private final int SAMPLING_RATE;
    private int previousLineX = Integer.MIN_VALUE;
    private int previousLineY = Integer.MIN_VALUE;
    private int displayStart;
    private int displayEnd;
    private boolean highlighted = false;
    private boolean noteHighlighted = false;

    public int getHeight() {
        return GRAPH_HEIGHT;
    }

    public int getWidth() {
        return GRAPH_WIDTH;
    }

    public Spectrogram(NoteMatrix noteMatrix, Sketchbook _sketchbook, int _originX, int _originY,
            int _graphWidth, int _graphHeight) {
        NoteData[][] notedata = noteMatrix.getMatrix();
        NoteData.normalize(notedata);
        this.SAMPLING_RATE = noteMatrix.getSamplingRate();
        this.SKETCHBOOK = _sketchbook;
        this.GRAPH_WIDTH = _graphWidth;
        this.GRAPH_HEIGHT = _graphHeight;
        this.ORIGIN_X = _originX;
        this.ORIGIN_Y = _originY;
        this.X_COUNT = notedata.length;
        this.displayStart = 0;

        X_LABELS = new double[X_COUNT];
        for (int i = 0; i < X_COUNT; i++) {
            X_LABELS[i] = (double) (noteMatrix.getFirstMidpoint() + i * noteMatrix.getStepSize())
                    / SAMPLING_RATE;
        }

        this.displayEnd = (int) Math.ceil(X_LABELS[X_COUNT - 1]);

        Y_LABELS = Stream.of(notedata[0]).map(n -> n.getNote()).sorted((s1,
                s2) -> NoteHelper.NOTE_TO_INT.get(s1).compareTo(NoteHelper.NOTE_TO_INT.get(s2)))
                .toArray(String[]::new);

        this.Y_COUNT = Y_LABELS.length;
        CELL_HEIGHT = (float) _graphHeight / Y_COUNT;

        DATA = new double[X_COUNT][Y_COUNT];
        for (int x = 0; x < X_COUNT; x++) {
            for (int y = 0; y < Y_COUNT; y++) {
                DATA[x][y] += Math.pow(notedata[x][y].getAmplitude(), 1);
            }
        }
    }

    public void handleKey(char key, int keyCode) {
        switch (key) {
            case 'a' -> displayStart = Math.max(0, displayStart - 1);
            case 'd' -> displayStart = Math.min(displayStart + 1, displayEnd - 1);
            case 'j' -> displayEnd = Math.max(displayStart + 1, displayEnd - 1);
            case 'l' -> displayEnd =
                    Math.min(displayEnd + 1, (int) Math.ceil(X_LABELS[X_COUNT - 1]));
            case 'h' -> highlighted = !highlighted;
            case 'n' -> noteHighlighted = !noteHighlighted;
            case 's' -> new Spectrum(500, 500, getNoteList());
        }
        switch (keyCode) {
            case LEFT -> {
                if (displayStart > 0) {
                    displayStart--;
                    displayEnd--;
                }
            }
            case RIGHT -> {
                if (displayEnd < (int) Math.ceil(X_LABELS[X_COUNT - 1])) {
                    displayStart++;
                    displayEnd++;
                }
            }
        }
    }

    private static Color ColorLerp(Color c1, Color c2, double param) {
        int red = c1.getRed() + (int) ((c2.getRed() - c1.getRed()) * param);
        int green = c1.getGreen() + (int) ((c2.getGreen() - c1.getGreen()) * param);
        int blue = c1.getBlue() + (int) ((c2.getBlue() - c1.getBlue()) * param);
        return new Color(red, green, blue);
    }

    private static Color AmplitudeToColor(double amplitude, Color[] gradient) {
        final int N = gradient.length - 1;
        amplitude *= N;

        if (amplitude <= 0) {
            return gradient[0];
        } else if (amplitude >= N) {
            return gradient[gradient.length - 1];
        } else {
            int bin = (int) Math.floor(amplitude);
            return ColorLerp(gradient[bin], gradient[bin + 1], amplitude % 1);
        }
    }

    public void display() {
        display(displayStart, displayEnd);
    }

    public void display(double start, double end) {
        int startIndex = 0;
        int endIndex = X_COUNT;
        for (int i = 0; i < X_COUNT; i++) {
            if (X_LABELS[i] > start) {
                startIndex = Math.max(0, i - 1);
                break;
            }
        }
        for (int i = startIndex; i < X_COUNT; i++) {
            if (X_LABELS[i] > end) {
                endIndex = i - 1;
                break;
            }
        }
        final int cellCount = endIndex - startIndex;
        final float cellWidth = (float) GRAPH_WIDTH / cellCount;
        float lastLabel = -2 * MIN_X_LABEL_DISTANCE;
        for (int x = startIndex; x < endIndex; x++) {
            float xPos = ORIGIN_X + cellWidth * (x - startIndex);
            for (int y = 0; y < Y_COUNT; y++) {
                fillGradient(DATA[x][y]);
                SKETCHBOOK.rect(xPos, ORIGIN_Y + CELL_HEIGHT * (Y_COUNT - 1 - y), cellWidth,
                        CELL_HEIGHT);
            }
            if (xPos - lastLabel >= MIN_X_LABEL_DISTANCE) {
                lastLabel = xPos;
                xLabel(X_LABELS[x], xPos);
            }
        }

        HashSet<String> topNotes = new HashSet<>();
        if (highlighted) {
            NoteData[] noteList = getNoteList();
            if (noteList != null) {
                topNotes = Stream
                        .of(Arrays.copyOfRange(NoteData.sortByAmplitudeDecreasing(noteList), 0, 5))
                        .map(n -> n.getNote()).collect(Collectors.toCollection(HashSet::new));
            }
        }

        for (int y = 0; y < Y_COUNT; y++) {
            if (topNotes != null && highlighted && topNotes.contains(Y_LABELS[y])) {
                SKETCHBOOK.noStroke();
                SKETCHBOOK.fill(255, 255, 0);
                SKETCHBOOK.rect(ORIGIN_X - 60, ORIGIN_Y + (Y_COUNT - 1 - y) * CELL_HEIGHT, 50,
                        CELL_HEIGHT);
            }

            if (noteHighlighted && isHorizontalLineNote(Y_LABELS[y])) {
                SKETCHBOOK.noStroke();
                SKETCHBOOK.fill(255, 0, 0);
                SKETCHBOOK.rect(ORIGIN_X - 60, ORIGIN_Y + (Y_COUNT - 1 - y) * CELL_HEIGHT, 50,
                        CELL_HEIGHT);
            }

            SKETCHBOOK.textAlign(RIGHT, CENTER);
            SKETCHBOOK.fill(0);
            SKETCHBOOK.stroke(0);
            SKETCHBOOK.text(Y_LABELS[y], ORIGIN_X - 10,
                    ORIGIN_Y + (Y_COUNT - 1 - y) * CELL_HEIGHT + ((float) CELL_HEIGHT / 2));
        }

        SKETCHBOOK.textAlign(CENTER);
        SKETCHBOOK.fill(0);
        SKETCHBOOK.stroke(0);
        SKETCHBOOK.text("Time (s)", ORIGIN_X + GRAPH_WIDTH / 2, ORIGIN_Y + GRAPH_HEIGHT + 40);
    }

    public void displayLines(int mouseX, int mouseY) {
        if (previousLineX != Integer.MIN_VALUE) {
            SKETCHBOOK.stroke(100);
            SKETCHBOOK.line(previousLineX, ORIGIN_Y, previousLineX, ORIGIN_Y + GRAPH_HEIGHT);
        }
        if (previousLineY != Integer.MIN_VALUE && noteHighlighted) {
            SKETCHBOOK.stroke(100);
            SKETCHBOOK.line(ORIGIN_X, previousLineY, ORIGIN_X + GRAPH_WIDTH, previousLineY);
        }
        if (mouseX >= ORIGIN_X && mouseX <= ORIGIN_X + GRAPH_WIDTH && mouseY >= ORIGIN_Y
                && mouseY <= ORIGIN_Y + GRAPH_HEIGHT) {
            SKETCHBOOK.stroke(0);
            SKETCHBOOK.line(mouseX, ORIGIN_Y, mouseX, ORIGIN_Y + GRAPH_HEIGHT);
        }
        if (mouseX >= ORIGIN_X && mouseX <= ORIGIN_X + GRAPH_WIDTH && mouseY >= ORIGIN_Y
                && mouseY <= ORIGIN_Y + GRAPH_HEIGHT && noteHighlighted) {
            SKETCHBOOK.stroke(0);
            SKETCHBOOK.line(ORIGIN_X, mouseY, ORIGIN_X + GRAPH_WIDTH, mouseY);
        }
    }

    public void updateLines(int mouseX, int mouseY) {
        if (mouseX >= ORIGIN_X && mouseX <= ORIGIN_X + GRAPH_WIDTH && mouseY >= ORIGIN_Y
                && mouseY <= ORIGIN_Y + GRAPH_HEIGHT) {
            previousLineX = mouseX;
            previousLineY = mouseY;
        }
    }

    public boolean isHorizontalLineNote(String testLabel) {
        if (previousLineY == Integer.MIN_VALUE) {
            return false;
        }
        for (int y = 0; y < Y_COUNT; y++) {
            if (ORIGIN_Y + (Y_COUNT - 1 - y) * CELL_HEIGHT <= previousLineY) {
                return Y_LABELS[y].equals(testLabel);
            }
        }
        return false;
    }

    public NoteData[] getNoteList() {
        if (previousLineX == Integer.MIN_VALUE) {
            return null;
        }
        int startIndex = 0;
        int endIndex = X_COUNT;
        for (int i = 0; i < X_COUNT; i++) {
            if (X_LABELS[i] > displayStart) {
                startIndex = Math.max(0, i - 1);
                break;
            }
        }
        for (int i = startIndex; i < X_COUNT; i++) {
            if (X_LABELS[i] > displayEnd) {
                endIndex = i - 1;
                break;
            }
        }
        final int cellCount = endIndex - startIndex;
        final float cellWidth = (float) GRAPH_WIDTH / cellCount;
        int selectedIndex = -1;
        for (int x = startIndex; x < endIndex; x++) {
            if (ORIGIN_X + cellWidth * (x - startIndex) + cellWidth > previousLineX) {
                selectedIndex = x;
                break;
            }
        }

        if (selectedIndex == -1) {
            return null;
        }
        NoteData[] noteList = new NoteData[Y_COUNT];
        for (int i = 0; i < Y_COUNT; i++) {
            noteList[i] = new NoteData(Y_LABELS[i], DATA[selectedIndex][i]);
        }
        return noteList;
    }

    private void xLabel(Double label, float xPos) {
        SKETCHBOOK.textAlign(LEFT);
        SKETCHBOOK.textSize(10);
        SKETCHBOOK.fill(0);
        SKETCHBOOK.stroke(0);
        SKETCHBOOK.strokeWeight(1);
        SKETCHBOOK.pushMatrix();
        SKETCHBOOK.translate(xPos, ORIGIN_Y + GRAPH_HEIGHT + 5);
        SKETCHBOOK.rotate(radians(90));
        SKETCHBOOK.text(String.format("%.2f", label), 0, 0);
        SKETCHBOOK.popMatrix();
    }

    private void fillGradient(double amplitude) {
        Color color = AmplitudeToColor(amplitude,
                new Color[] {Color.BLUE, Color.CYAN, Color.ORANGE, Color.RED});
        SKETCHBOOK.fill(color.getRed(), color.getGreen(), color.getBlue());
        SKETCHBOOK.stroke(color.getRed(), color.getGreen(), color.getBlue());
    }
}
