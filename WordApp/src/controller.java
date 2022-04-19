import javax.swing.*;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.awt.Graphics;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
/**Controller class used to drop 1 word until it reaches the end or is told to stop*/
public class controller implements Runnable{
   private WordRecord word;
   private volatile WordPanel panel;
   private volatile Score score;
   private volatile JLabel missed;
   AtomicBoolean flag = new AtomicBoolean();
   private boolean exit=false;
   private int speed;
   /**Controller class constructor*/
   controller(WordRecord w,WordPanel p,Score s,JLabel m){
      word=w;
      panel=p;
      score=s;
      missed=m;
   }
   /**Run method used to drop the word by 1 position and repaint and wait for tie delay inorder tp create animation effect*/
   public void run() {
      flag.set(true);
      while(flag.get()==true){//Loops until it is told to stop
         word.drop(1);//Word is dropped then rapainted in next line
         panel.repaint();
         if(word.dropped()){//Checks if the word has reached the bottom
            score.missedWord();//Once the word has reached bottom missed counter increamented 
            missed.setText("Missed:" + score.getMissed()+ "    ");
            soundEffects();    
            stops();//Stop method called to raise flag and end the loop
         }  
         speed=word.getSpeed()/45 ;  //Gets word speed 
         try {TimeUnit.MILLISECONDS.sleep(speed);}
         catch (InterruptedException e) {}
      } 
      word.resetPos();
      panel.repaint();
   }
   /**Stop method used to raise the flag and hence stop execution of code*/
   public void stops() {flag.set(false);}
   /**Sound effect method to create effect when word is missed*/
   public void soundEffects(){
      try
      {
          // create AudioInputStream object
          AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/soundEffects/sound2.au").getAbsoluteFile());
          
        // create clip reference
        Clip clip = AudioSystem.getClip();
          
        // open audioInputStream to the clip
        clip.open(audioInputStream);
       
        clip.start();
     
      }
      catch(Exception e){}
   }
   
}