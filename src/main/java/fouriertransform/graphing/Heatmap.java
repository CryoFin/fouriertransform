package fouriertransform.graphing;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.HeatMapChart;
import org.knowm.xchart.HeatMapChartBuilder;
import org.knowm.xchart.HeatMapSeries;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.LegendLayout;
import org.knowm.xchart.style.Styler.LegendPosition;

import fouriertransform.sounddata.FrequencyData;
import fouriertransform.sounddata.NoteData;
import fouriertransform.sounddata.NoteHelper;

public class Heatmap {
    private final File file;

    public static void main(String[] args) {
        testGraph();
    }

    public Heatmap(File file) {
        this.file = file;
    }

    public void graph(FrequencyData[][] spectrumMatrix) {
        final float Threshold = 0.001f;

        NoteData[][] noteMatrix =
                NoteHelper.convertSpectrumMatrixToNoteDataMatrix(spectrumMatrix, Threshold);

        NoteData[] fs = noteMatrix[0];

        List<String> n = Stream.of(fs).map(s -> s.getNote()).collect(Collectors.toList());
        List<Integer> t = new ArrayList<>(noteMatrix.length);
        for (int i = 0; i < noteMatrix.length; i++) {
            t.add(i);
        }
        List<Number[]> h = new ArrayList<>();

        for (int s = 0; s < noteMatrix.length; s++) {
            for (int fr = 0; fr < fs.length; fr++) {
                float a = noteMatrix[s][fr].getAmplitude();
                h.add(new Number[] {s, fr, a < Threshold ? 0 : a});
            }
        }

        HeatMapChart chart = new HeatMapChartBuilder().width(1600).height(1000).title("Spectrogram")
                .xAxisTitle(String.format("Time (%d samples)", noteMatrix.length))
                .yAxisTitle("Frequency").build();

        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLegendLayout(LegendLayout.Vertical);
        chart.getStyler()
                .setRangeColors(new Color[] {Color.blue, Color.cyan, Color.orange, Color.red});
        chart.getStyler().setXAxisMaxLabelCount(98);
        chart.getStyler().setPlotGridHorizontalLinesVisible(true);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        chart.getStyler().setPlotMargin(5);

        chart.addSeries(":)", t, n, h);

        new SwingWrapper<>(chart).displayChart();

        try {
            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.PNG);
        } catch (IOException e) {
        }
    }

    public void graphFrequency(FrequencyData[][] spectrumMatrix) {
        HeatMapChart chart = new HeatMapChartBuilder().width(1600).height(1000)
                .title("SpectrogramFrequency").xAxisTitle("Time").yAxisTitle("Frequency").build();

        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLegendLayout(LegendLayout.Vertical);
        chart.getStyler()
                .setRangeColors(new Color[] {Color.blue, Color.cyan, Color.orange, Color.red});
        chart.getStyler().setXAxisMaxLabelCount(50);
        chart.getStyler().setPlotGridHorizontalLinesVisible(true);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);

        spectrumMatrix = FrequencyData.trimByFrequencyRange(spectrumMatrix, 0f, 2000f);

        FrequencyData[] fs = spectrumMatrix[0];

        List<Float> f = Stream.of(fs).map(s -> s.getFrequency()).collect(Collectors.toList());
        List<Integer> t = new ArrayList<>(spectrumMatrix.length);
        for (int i = 0; i < spectrumMatrix.length; i++) {
            t.add(i);
        }
        List<Number[]> h = new ArrayList<>();

        final float Threshold = 0.000f;

        for (int s = 0; s < spectrumMatrix.length; s++) {
            for (int fr = 0; fr < fs.length; fr++) {
                float a = spectrumMatrix[s][fr].getAmplitude();
                h.add(new Number[] {s, fr, a < Threshold ? 0 : a});
            }
        }

        chart.addSeries(":)", t, f, h);

        new SwingWrapper<>(chart).displayChart();

        try {
            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.PNG);
        } catch (IOException e) {
        }
    }

    public static void testGraph() {
        HeatMapChart chart = new HeatMapChartBuilder().width(1600).height(1000).title("Spectrogram")
                .xAxisTitle("Time").yAxisTitle("Frequency").build();

        chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
        chart.getStyler().setLegendLayout(LegendLayout.Vertical);
        chart.getStyler()
                .setRangeColors(new Color[] {Color.blue, Color.cyan, Color.orange, Color.red});
        chart.getStyler().setXAxisMaxLabelCount(50);
        chart.getStyler().setAxisTicksLineVisible(false);

        HeatMapSeries s = chart.addSeries(":)", new int[] {1, 2, 3}, new int[] {4, 5, 6, 6, 16},
                new int[][] {{6, 7, 8, 16}, {6, 7, 8, 15}, {8, 9, 10, 17}});

        // s.setMax(4.0);
        // s.setYAxisGroup(1);
        // chart.getStyler().setYAxisMax(1, 2.0);

        new SwingWrapper<>(chart).displayChart();
    }
}

