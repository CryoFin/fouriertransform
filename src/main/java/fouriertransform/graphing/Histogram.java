package fouriertransform.graphing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

import fouriertransform.sounddata.FrequencyData;

public class Histogram {
    private final File file;

    public static void main(String[] args) {

    }

    public Histogram(File file) {
        this.file = file;
    }

    public void graph(FrequencyData[] spectrum) {
        CategoryChart chart = new CategoryChartBuilder().width(1600).height(1200).title("Spectrum")
                .xAxisTitle("Frequency").yAxisTitle("Intensity").build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAvailableSpaceFill(0.99);
        chart.getStyler().setOverlapped(true);
        // chart.getStyler().setXAxisLogarithmicDecadeOnly(true);
        chart.getStyler().setXAxisMaxLabelCount(50);

        List<Double> f =
                Stream.of(spectrum).map(s -> s.getFrequency()).collect(Collectors.toList());
        List<Double> a =
                Stream.of(spectrum).map(s -> s.getAmplitude()).collect(Collectors.toList());

        chart.addSeries(":)", f, a);

        new SwingWrapper<>(chart).displayChart();

        try {
            BitmapEncoder.saveBitmap(chart, file.getAbsolutePath(), BitmapFormat.PNG);
        } catch (IOException e) {
        }
    }
}
