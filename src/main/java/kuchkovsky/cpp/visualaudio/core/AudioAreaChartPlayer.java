package kuchkovsky.cpp.visualaudio.core;

import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart;

import java.io.File;

public class AudioAreaChartPlayer extends VisualPlayer {

    private XYChart.Data<Number, Number>[] series1Data;

    public AudioAreaChartPlayer(File file) {
        super(file);
        init();
    }

    public Parent createContent() {
        final NumberAxis xAxis = new NumberAxis(0, 128, 8);
        final NumberAxis yAxis = new NumberAxis(0, 50, 10);
        final AreaChart<Number, Number> ac = new AreaChart<>(xAxis, yAxis);
        final String audioAreaChartCss = getClass().getResource("/audio-area-chart.css").toExternalForm();
        ac.getStylesheets().add(audioAreaChartCss);
        ac.setLegendVisible(false);
        ac.setTitle("Діаграма області аудіо");
        ac.setAnimated(false);
        xAxis.setLabel("Смуги частот");
        yAxis.setLabel("Рівень сигналу");
        yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, "dB"));
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Audio Spectrum");
        // noinspection unchecked
        series1Data = new XYChart.Data[(int) xAxis.getUpperBound()];
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(i, 0);
            series.getData().add(series1Data[i]);
        }
        ac.getData().add(series);
        return ac;
    }

    private void init() {
        audioSpectrumListener = (double timestamp, double duration,
                                 float[] magnitudes, float[] phases) -> {
            for (int i = 0; i < series1Data.length; i++) {
                series1Data[i].setYValue(magnitudes[i] + 60);
            }
        };
    }

}
