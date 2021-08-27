package kuchkovsky.cpp.visualaudio.core;

import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart;

import java.io.File;

public class AudioBarChartPlayer extends VisualPlayer {

    private XYChart.Data<String, Number>[] series1Data;

    public AudioBarChartPlayer() {
        init();
    }

    public AudioBarChartPlayer(File file) {
        super(file);
        init();
    }

    public Parent createContent() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis(0, 50, 10);
        final BarChart<String, Number> bc = new BarChart<>(xAxis, yAxis);
        final String audioBarChartCss = getClass().getResource("/audio-bar-chart.css").toExternalForm();
        bc.getStylesheets().add(audioBarChartCss);
        bc.setLegendVisible(false);
        bc.setAnimated(false);
        bc.setBarGap(0);
        bc.setCategoryGap(1);
        bc.setVerticalGridLinesVisible(false);
        bc.setTitle("Діаграма аудіо");
        xAxis.setLabel("Смуги частот");
        yAxis.setLabel("Рівень сигналу");
        yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, null, "dB"));
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Data Series 1");
        //noinspection unchecked
        series1Data = new XYChart.Data[128];
        String[] categories = new String[128];
        for (int i = 0; i < series1Data.length; i++) {
            categories[i] = Integer.toString(i + 1);
            series1Data[i] = new XYChart.Data<>(categories[i], 0);
            series1.getData().add(series1Data[i]);
        }
        bc.getData().add(series1);
        return bc;
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
