import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    
    // Method untuk SFX (Main sekali) - SUDAH ADA
    public static void play(String filename) {
        try {
            File soundFile = new File(filename);
            if (!soundFile.exists()) return;
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- TAMBAHAN BARU: Method untuk Backsound (Looping) ---
    public static void playBackground(String filename) {
        new Thread(() -> { // Jalankan di thread terpisah agar tidak membuat lag
            try {
                File soundFile = new File(filename);
                if (!soundFile.exists()) {
                    System.out.println("File BGM tidak ditemukan: " + filename);
                    return;
                }
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                
                // Kurangi volume sedikit (opsional, biar tidak berisik)
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f); // Turunkan 10 decibel

                clip.loop(Clip.LOOP_CONTINUOUSLY); // PUTAR TERUS
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}