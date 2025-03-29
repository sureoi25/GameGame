package org.example;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
    Clip clip;
    URL sound_url[] = new URL[10];

    public Sound(){
        stop();
        try{
            sound_url[0] = getClass().getResource("/Sound/encounter_enemy.wav");//wa nagamit lisod implement encounter enemy bruh
            sound_url[1] = getClass().getResource("/Sound/Death sound(wav).wav");
            sound_url[2] = getClass().getResource("/Sound/hurt.wav");
            sound_url[3] = getClass().getResource("/Sound/Game theme.wav");
            sound_url[4] = getClass().getResource("/Sound/Eating.wav");
            sound_url[5] = getClass().getResource("/Sound/Open chest.wav");
            sound_url[6] = getClass().getResource("/Sound/power_up.wav");
            sound_url[7] = getClass().getResource("/Sound/click.wav");
            sound_url[8] = getClass().getResource("/Sound/game over.wav");
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public void setFile(int i){
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(sound_url[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {

        }

    }
    public void play(){
        clip.start();
    }
    public void loop(){
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void stop(){
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

}
