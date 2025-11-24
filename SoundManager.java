import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    
    public static void play(String filename) {
        try {
            File soundFile = new File(filename);
            if (!soundFile.exists()) {
                System.out.println("File suara tidak ditemukan: " + filename);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}