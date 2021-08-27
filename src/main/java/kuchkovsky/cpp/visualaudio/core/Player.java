package kuchkovsky.cpp.visualaudio.core;

import javafx.scene.Parent;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public interface Player {

    void play();

    void pause();

    void setAudioFile(File audioFile);

    MediaPlayer getAudioMediaPlayer();

    Parent createContent();

}
