import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    
    public static void play(String filename) {
        playSound(filename, false);
    }

    public static void playBackground(String filename) {
        playSound(filename, true);
    }

    private static void playSound(String filename, boolean loop) {
        new Thread(() -> {
            try {
                File soundFile = new File(filename);
                if (!soundFile.exists()) {
                    System.err.println("❌ FILE TIDAK DITEMUKAN: " + soundFile.getAbsolutePath());
                    return; 
                }

                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                
                if (loop) {
                    try {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        gainControl.setValue(-10.0f); 
                    } catch (Exception e) {}
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                
                clip.start();
            } catch (Exception e) {
                System.err.println("❌ ERROR AUDIO (" + filename + "): " + e.getMessage());
            }
        }).start();
    }
}