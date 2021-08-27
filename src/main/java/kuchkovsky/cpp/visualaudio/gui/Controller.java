package kuchkovsky.cpp.visualaudio.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import javafx.application.Platform;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import kuchkovsky.cpp.visualaudio.core.AudioRecorder;
import kuchkovsky.cpp.visualaudio.core.Player;
import kuchkovsky.cpp.visualaudio.core.AudioAreaChartPlayer;
import kuchkovsky.cpp.visualaudio.core.AudioBarChartPlayer;


import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    private BorderPane mainPane;

    @FXML
    private SubScene visualScene;

    @FXML
    private JFXButton openFileButton;

    @FXML
    private JFXRadioButton audioBarChartRadioButton;

    @FXML
    private JFXRadioButton audioAreaChartRadioButton;

    @FXML
    private Label currentFileLabel;

    @FXML
    private JFXButton playButton;

    @FXML
    private JFXButton pauseButton;

    @FXML
    private JFXButton startAudioRecordingButton;

    @FXML
    private JFXButton stopAudioRecordingButton;

    @FXML
    private JFXListView<String> recordListView;

    @FXML
    private Label recorderTimeLabel;

    @FXML
    private Label playerTimeLabel;

    private ObservableList<String> recordings = FXCollections.observableArrayList();

    private Player player;

    private File currentTrack;

    private AudioRecorder audioRecorder;

    private Timer timer;

    @FXML
    private void initialize() {

        initInterface();

        openFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter(
                    "Audio files", "*.mp3", "*.wav");
            fileChooser.getExtensionFilters().add(fileExtensions);
            File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());
            if (file != null) {
                setFile(file);
                playFile();
            }
        });

        playButton.setOnAction(event -> {
            if (currentTrack != null) {
                playFile();
            }
        });

        pauseButton.setOnAction(event -> {
            if (currentTrack != null) {
                pauseFile();
            }
        });

        audioBarChartRadioButton.setOnAction(event -> {
            pauseFile();
            player = new AudioBarChartPlayer(currentTrack);
            visualScene.setRoot(player.createContent());
        });

        audioAreaChartRadioButton.setOnAction(event -> {
            pauseFile();
            player = new AudioAreaChartPlayer(currentTrack);
            visualScene.setRoot(player.createContent());
        });

        startAudioRecordingButton.setOnAction(event -> {
            startAudioRecordingButton.setDisable(true);
            stopAudioRecordingButton.setDisable(false);
            audioRecorder = new AudioRecorder();
            Thread thread = new Thread(audioRecorder);
            thread.start();
            startRecorderTimer();
        });

        stopAudioRecordingButton.setOnAction(event -> {
            if (audioRecorder != null) {
                stopAudioRecordingButton.setDisable(true);
                startAudioRecordingButton.setDisable(false);
                audioRecorder.finish();
                stopRecorderTimer();
                readRecordingsFromDirectory();
            }
        });

        recordListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String item = recordListView.getSelectionModel().getSelectedItem();
                setFile(new File("recordings" + File.separator + item));
                playFile();
            }
        });

    }

    private void initInterface() {
        bindSubSceneSize();
        player = new AudioBarChartPlayer();
        visualScene.setRoot(player.createContent());
        initRecordListView();
        audioBarChartRadioButton.setDisableVisualFocus(true);
    }

    private void bindSubSceneSize() {
        visualScene.widthProperty().bind(mainPane.widthProperty().subtract(215));
        visualScene.heightProperty().bind(mainPane.heightProperty().subtract(125));
    }

    private void initRecordListView() {
        recordListView.setPlaceholder(new Label("Список пустий"));
        readRecordingsFromDirectory();
        recordListView.setItems(recordings);
        recordListView.prefHeightProperty().bind(mainPane.heightProperty().subtract(150));
    }

    private void setFile(File file) {
        if (currentTrack != null) {
            player.pause();
        }
        currentTrack = file;
        player.setAudioFile(currentTrack);
        currentFileLabel.setText(currentTrack.getName());
        playButton.setDisable(false);
        pauseButton.setDisable(true);
    }

    private void bindPlayerTimeLabel() {
        playerTimeLabel.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    Duration time = player.getAudioMediaPlayer().getCurrentTime();
                    return String.format("%02d:%02d:%02d",
                            (int) time.toHours(), (int) time.toMinutes() % 60, (int) time.toSeconds() % 60);
                }, player.getAudioMediaPlayer().currentTimeProperty()));
    }

    private void playFile() {
        bindPlayerTimeLabel();
        player.play();
        playButton.setDisable(true);
        pauseButton.setDisable(false);
    }

    private void pauseFile() {
        player.pause();
        pauseButton.setDisable(true);
        playButton.setDisable(false);
    }

    private void readRecordingsFromDirectory() {
        File dir = new File("recordings");
        File[] recordFiles = dir.listFiles((d, name) -> name.endsWith(".wav"));
        if (recordFiles == null || recordFiles.length == 0) {
            return;
        }
        recordings.setAll(Arrays.stream(recordFiles).map(File::getName).collect(Collectors.toList()));
    }

    private void startRecorderTimer() {
        timer = new Timer();
        Long startTime = System.currentTimeMillis();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    Long recordDurationMillis = System.currentTimeMillis() - startTime;
                    String recordDuration = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(recordDurationMillis),
                            TimeUnit.MILLISECONDS.toMinutes(recordDurationMillis) -
                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(recordDurationMillis)),
                            TimeUnit.MILLISECONDS.toSeconds(recordDurationMillis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(recordDurationMillis)));
                    recorderTimeLabel.setText(recordDuration);
                });
            }
        }, 0, 1000);
    }

    private void stopRecorderTimer() {
        timer.cancel();
        recorderTimeLabel.setText("00:00:00");
    }

}
