package org.example;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class Sound {
    Clip clip;
    URL sound_url[] = new URL[70];

    public Sound(){

        sound_url[0] = getClass().getResource("/Sound/Cynthia boss battle theme.mp3");
        sound_url[1] = getClass().getResource("/Sound/Death sound.mp3");
        sound_url[2] = getClass().getResource("/Sound/hurt.wav");
        sound_url[3] = getClass().getResource("/Sound/little root town theme.mp3");
        sound_url[4] = getClass().getResource("/Sound/Minecraft Eating.mp3");
        sound_url[5] = getClass().getResource("/Sound/Open chest.wav");
        sound_url[6] = getClass().getResource("/Sound/power_up.wav");

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
        clip.stop();
    }
}
