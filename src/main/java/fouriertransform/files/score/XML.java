package fouriertransform.files.score;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fouriertransform.sounddata.NoteData;
import fouriertransform.sounddata.NoteMatrix;

public class XML {
    private final static HashMap<String, String> KEY_TO_FIFTHS = new HashMap<>();
    private final static String FILE = "test.xml";

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;

    static {
        KEY_TO_FIFTHS.put("Cb", "-7");
        KEY_TO_FIFTHS.put("Gb", "-6");
        KEY_TO_FIFTHS.put("Db", "-5");
        KEY_TO_FIFTHS.put("Ab", "-4");
        KEY_TO_FIFTHS.put("Eb", "-3");
        KEY_TO_FIFTHS.put("Bb", "-2");
        KEY_TO_FIFTHS.put("F", "-1");
        KEY_TO_FIFTHS.put("C", "0");
        KEY_TO_FIFTHS.put("G", "1");
        KEY_TO_FIFTHS.put("D", "2");
        KEY_TO_FIFTHS.put("A", "3");
        KEY_TO_FIFTHS.put("E", "4");
        KEY_TO_FIFTHS.put("B", "5");
        KEY_TO_FIFTHS.put("F#", "6");
        KEY_TO_FIFTHS.put("C#", "7");
    }

    public XML() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserConfigurationException) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder "
                    + parserConfigurationException);
        }
        document = documentBuilder.newDocument();
    }

    public static void main(String[] args) {
        XML xml = new XML();
        xml.saveToXML(FILE, null);
    }

    public void saveToXML(String xml, NoteMatrix _noteMatrix) {
        int divisions = 16;
        double tempo = (double) (_noteMatrix.getSamplingRate() * 60)
                / (_noteMatrix.getStepSize() * divisions);

        Element rootElement = document.createElement("score-partwise");
        rootElement.setAttribute("version", "4.0");

        rootElement.appendChild(createPartsList());

        Element part = createPart("P1");

        Element measure = document.createElement("measure");
        measure.setAttribute("number", "1");
        measure.appendChild(createAttributesElement(divisions, "C", 4, 4, Clef.TREBLE));
        measure.appendChild(createDirection(tempo));

        final double THRESHOLD = 0.2;
        int mn = 1;

        NoteData[][] noteMatrix = _noteMatrix.getMatrix();
        final int SAMPLES = noteMatrix.length;
        final int NOTES = noteMatrix[0].length;

        for (int sample = 0; sample < SAMPLES; sample++) {
            boolean first = true;
            for (int note = 0; note < NOTES; note++) {
                if (noteMatrix[sample][note].getAmplitude() < THRESHOLD) {
                    continue;
                }
                boolean end = false;
                boolean start = false;
                if (sample > 0 && noteMatrix[sample - 1][note].getAmplitude() > THRESHOLD) {
                    end = true;
                }
                if (sample < SAMPLES - 1
                        && noteMatrix[sample + 1][note].getAmplitude() > THRESHOLD) {
                    start = true;
                }
                if (first) {
                    measure.appendChild(createNote(
                            new Note(noteMatrix[sample][note].getNote(), start, end), true));
                    first = false;
                } else {
                    measure.appendChild(createNote(
                            new Note(noteMatrix[sample][note].getNote(), start, end), false));
                }
            }

            if (first) {
                measure.appendChild(createRest());
            }

            if (sample % divisions == 0 && sample != 0) {
                mn++;
                part.appendChild(measure);
                measure = document.createElement("measure");
                measure.setAttribute("number", Integer.toString(mn));
            }

        }

        // Element note = document.createElement("note");
        // Element pitch = document.createElement("pitch");
        // Element step = document.createElement("step");
        // step.appendChild(document.createTextNode("C"));
        // Element octave = document.createElement("octave");
        // octave.appendChild(document.createTextNode("4"));
        // pitch.appendChild(step);
        // pitch.appendChild(octave);
        // note.appendChild(pitch);
        // Element duration = document.createElement("duration");
        // duration.appendChild(document.createTextNode("4"));
        // note.appendChild(duration);
        // Element type = document.createElement("type");
        // type.appendChild(document.createTextNode("whole"));
        // note.appendChild(type);
        // Element tie = document.createElement("tie");
        // tie.setAttribute("type", "start");
        // note.appendChild(tie);
        // measure.appendChild(note);

        // note = document.createElement("note");
        // pitch = document.createElement("pitch");
        // step = document.createElement("step");
        // step.appendChild(document.createTextNode("C"));
        // octave = document.createElement("octave");
        // octave.appendChild(document.createTextNode("4"));
        // pitch.appendChild(step);
        // pitch.appendChild(octave);
        // note.appendChild(pitch);
        // duration = document.createElement("duration");
        // duration.appendChild(document.createTextNode("4"));
        // note.appendChild(duration);
        // type = document.createElement("type");
        // type.appendChild(document.createTextNode("whole"));
        // note.appendChild(type);
        // tie = document.createElement("tie");
        // tie.setAttribute("type", "end");
        // note.appendChild(tie);
        // tie = document.createElement("tie");
        // tie.setAttribute("type", "start");
        // note.appendChild(tie);
        // measure.appendChild(note);

        // note = document.createElement("note");
        // pitch = document.createElement("pitch");
        // step = document.createElement("step");
        // step.appendChild(document.createTextNode("C"));
        // octave = document.createElement("octave");
        // octave.appendChild(document.createTextNode("4"));
        // pitch.appendChild(step);
        // pitch.appendChild(octave);
        // note.appendChild(pitch);
        // duration = document.createElement("duration");
        // duration.appendChild(document.createTextNode("4"));
        // note.appendChild(duration);
        // type = document.createElement("type");
        // type.appendChild(document.createTextNode("whole"));
        // note.appendChild(type);
        // tie = document.createElement("tie");
        // tie.setAttribute("type", "end");
        // note.appendChild(tie);
        // measure.appendChild(note);

        part.appendChild(measure);
        rootElement.appendChild(part);

        document.appendChild(rootElement);

        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                    "http://www.musicxml.org/dtds/partwise.dtd");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    "-//Recordare//DTD MusicXML 4.0 Partwise//EN");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(new DOMSource(document),
                    new StreamResult(new FileOutputStream(xml)));
        } catch (TransformerException transformerException) {
            System.out.println(transformerException.getMessage());
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    private Element createHeader() {
        return null;
    }

    private Element createPartsList() {
        Element partList = document.createElement("part-list");
        Element scorePart = document.createElement("score-part");
        scorePart.setAttribute("id", "P1");
        Element partName = document.createElement("part-name");
        partName.appendChild(document.createTextNode("Music"));
        scorePart.appendChild(partName);
        partList.appendChild(scorePart);
        return partList;
    }

    private Element createPart(String id) {
        Element part = document.createElement("part");
        part.setAttribute("id", id);
        return part;
    }

    private Element createAttributesElement(int divisor, String _key, int timeNum, int timeDen,
            Clef _clef) {
        Element attributes = document.createElement("attributes");

        Element divisions = document.createElement("divisions");
        divisions.appendChild(document.createTextNode(Integer.toString(divisor)));
        attributes.appendChild(divisions);


        Element key = document.createElement("key");
        Element fifths = document.createElement("fifths");
        fifths.appendChild(document.createTextNode(KEY_TO_FIFTHS.get(_key)));
        key.appendChild(fifths);
        attributes.appendChild(key);

        Element time = document.createElement("time");
        Element beats = document.createElement("beats");
        beats.appendChild(document.createTextNode(Integer.toString(timeNum)));
        Element beatType = document.createElement("beat-type");
        beatType.appendChild(document.createTextNode(Integer.toString(timeDen)));
        time.appendChild(beats);
        time.appendChild(beatType);
        attributes.appendChild(time);

        Element clef = document.createElement("clef");
        Element sign = document.createElement("sign");
        sign.appendChild(document.createTextNode(_clef.getSign()));
        Element line = document.createElement("line");
        line.appendChild(document.createTextNode(_clef.getLine()));
        clef.appendChild(sign);
        clef.appendChild(line);
        attributes.appendChild(clef);

        return attributes;
    }

    private Element createDirection(double tempo) {
        Element direction = document.createElement("direction");

        Element sound = document.createElement("sound");
        sound.setAttribute("tempo", String.valueOf(tempo));
        direction.appendChild(sound);

        return direction;
    }

    private Element createNote(Note _note, boolean isFirst) {
        Element note = document.createElement("note");

        if (!isFirst) {
            note.appendChild(document.createElement("chord"));
        }

        if (_note.isEnd()) {
            Element tie = document.createElement("tie");
            tie.setAttribute("type", "end");
            note.appendChild(tie);
        }

        if (_note.isStart()) {
            Element tie = document.createElement("tie");
            tie.setAttribute("type", "start");
            note.appendChild(tie);
        }

        Element pitch = document.createElement("pitch");
        Element step = document.createElement("step");
        step.appendChild(document.createTextNode(_note.getNote()));
        pitch.appendChild(step);
        Element octave = document.createElement("octave");
        octave.appendChild(document.createTextNode(_note.getOctave()));
        pitch.appendChild(octave);
        if (_note.getAlter() != null) {
            Element alter = document.createElement("alter");
            alter.appendChild(document.createTextNode(_note.getAlter()));
            pitch.appendChild(alter);
        }
        note.appendChild(pitch);

        Element duration = document.createElement("duration");
        duration.appendChild(document.createTextNode("1"));
        note.appendChild(duration);

        // Element type = document.createElement("type");
        // type.appendChild(document.createTextNode("quarter"));
        // note.appendChild(type);

        return note;
    }

    private Element createRest() {
        Element note = document.createElement("note");

        Element rest = document.createElement("rest");
        rest.setAttribute("measure", "no");
        note.appendChild(rest);

        Element duration = document.createElement("duration");
        duration.appendChild(document.createTextNode("1"));
        note.appendChild(duration);

        return note;
    }
}
