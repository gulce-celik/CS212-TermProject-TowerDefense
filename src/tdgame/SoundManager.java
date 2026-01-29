package tdgame;

import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager {

    // Basit arka plan müziği değişkeni
    private static Clip backgroundClip;

    // -------------------- BASİT MÜZİK --------------------

    // Menü müziği
    public static void playMenuMusic() {
        playLoopMusic("/sounds/main-panel.wav");
    }

    public static void playGameMusic() {
        playLoopMusic("/sounds/game-background.wav");
    }

    public static void stopMusic() {
        if (backgroundClip != null) {
            backgroundClip.stop();
        }
    }

    private static void playLoopMusic(String path) {
        try {
            URL url = SoundManager.class.getResource(path);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);

            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audio);

            // ---- SIMPLE VOLUME CONTROL FOR MUSIC ----
            try {
                FloatControl volume =
                        (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);

                if (path.contains("game-background")) {
                    // oyun içi müzik – kısık
                    volume.setValue(-20f);
                } else if (path.contains("main-panel")) {
                    // ana menü müziği – orta
                    volume.setValue(-20f);
                } else {
                    // bilinmeyen müzikler – normal
                    volume.setValue(-15f);
                }
            } catch (Exception ignored) {
                
            }

            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // -------------------- BASİT SES EFEKTLERİ --------------------

    public static void playShoot() {
        playSound("/sounds/shoot.wav");
    }

    public static void playPlaneBomb() {
        playSound("/sounds/plane-bomb.wav");
    }

    public static void playTowerExplosion() {
        playSound("/sounds/tower-explosion.wav");
    }

    public static void playGameOver() {
        playSound("/sounds/game-over.wav");
    }

    public static void playReadySetGo() {
        playSound("/sounds/ready-set-go-sound.wav");
    }

    public static void playSound(String path) {
        try {
            var url = SoundManager.class.getResource(path);
            var ais = AudioSystem.getAudioInputStream(url);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            // --- SIMPLE VOLUME CONTROL  ---
            try {
                FloatControl volume = (FloatControl)
                        clip.getControl(FloatControl.Type.MASTER_GAIN);

                if (path.contains("shoot")) {
                    // shoot sesi -> daha kısık
                    volume.setValue(-40f);   
                } 
                else if(path.contains("bomb")) {
                	volume.setValue(-28f);   
                }
                else if(path.contains("explosion")) {
                	volume.setValue(-20f);   
                }
                else {
                    // diğer sesler normal
                    volume.setValue(-15f);
                }
            } catch (Exception ignored) {
                
            }

            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

