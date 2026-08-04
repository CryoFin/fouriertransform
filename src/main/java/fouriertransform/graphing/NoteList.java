package fouriertransform.graphing;

import fouriertransform.sounddata.NoteData;
import processing.core.PApplet;

public class NoteList extends PApplet {
    private final Sketchbook SKETCHBOOK;
    private final int ORIGIN_X;
    private final int ORIGIN_Y;
    private final int LIST_WIDTH;
    private final int LIST_HEIGHT;

    public NoteList(Sketchbook _sketchbook, int _originX, int _originY, int _listWidth,
            int _listHeight) {
        this.SKETCHBOOK = _sketchbook;
        this.ORIGIN_X = _originX;
        this.ORIGIN_Y = _originY;
        this.LIST_WIDTH = _listWidth;
        this.LIST_HEIGHT = _listHeight;
    }

    public void display(NoteData[] noteList) {
        if (noteList == null) {
            return;
        }
        NoteData[] sortedNoteList = NoteData.sortByAmplitudeDecreasing(noteList);

        final float CELL_HEIGHT = (float) LIST_HEIGHT / noteList.length;

        SKETCHBOOK.fill(0);
        SKETCHBOOK.stroke(0);

        for (int i = 0; i < sortedNoteList.length; i++) {
            NoteData noteData = sortedNoteList[i];

            SKETCHBOOK.textAlign(LEFT, CENTER);
            SKETCHBOOK.text(noteData.getNote(), ORIGIN_X,
                    ORIGIN_Y + CELL_HEIGHT * i + ((float) CELL_HEIGHT / 2));
            SKETCHBOOK.textAlign(RIGHT, CENTER);
            SKETCHBOOK.text(String.format("%.2f", noteData.getAmplitude()), ORIGIN_X + LIST_WIDTH,
                    ORIGIN_Y + CELL_HEIGHT * i + ((float) CELL_HEIGHT / 2));
        }
    }
}
