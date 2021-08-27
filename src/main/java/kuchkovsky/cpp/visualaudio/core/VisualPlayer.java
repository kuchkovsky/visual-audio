package kuchkovsky.cpp.visualaudio.core;

import javafx.scene.Parent;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

abstract class VisualPlayer implements Player {

    protected AudioSpectrumListener audioSpectrumListener;
    private MediaPlayer audioMediaPlayer;
    private File audioFile;

    public VisualPlayer() {
    }

    public VisualPlayer(File file) {
        setAudioFile(file);
    }

    public void setAudioFile(File audioFile) {
        this.audioMediaPlayer = null;
        this.audioFile = audioFile;
    }

    @Override
    abstract public Parent createContent();

    @Override
    public void play() {
        if (audioFile != null) {
            getAudioMediaPlayer()
                    .setAudioSpectrumListener(audioSpectrumListener);
            getAudioMediaPlayer().play();
        }
    }

    @Override
    public void pause() {
        if (audioFile != null) {
            if (getAudioMediaPlayer().getAudioSpectrumListener() == audioSpectrumListener) {
                getAudioMediaPlayer().pause();
            }
        }
    }

    @Override
    public MediaPlayer getAudioMediaPlayer() {
        if (audioMediaPlayer == null) {
            Media audioMedia = new Media(audioFile.toURI().toString());
            audioMediaPlayer = new MediaPlayer(audioMedia);
            audioMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        }
        return audioMediaPlayer;
    }

}
