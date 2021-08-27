package kuchkovsky.cpp.visualaudio.core;

import javafx.concurrent.Task;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AudioRecorder extends Task<Void> {

    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    private TargetDataLine line;

    @Override
    protected Void call() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                throw new RuntimeException("Line not supported");
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            AudioInputStream ais = new AudioInputStream(line);
            AudioSystem.write(ais, fileType, getAudioFile());
        } catch (LineUnavailableException | IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private AudioFormat getAudioFormat() {
        return new AudioFormat(44100, 16, 2, true, true);
    }

    public void finish() {
        line.stop();
        line.close();
    }

    private File getAudioFile() {
        try {
            Files.createDirectories(Paths.get("recordings"));
            String time = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
            String fileName = "recording_" + time + ".wav";
            return new File("recordings" + File.separator + fileName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
