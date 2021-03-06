//Word panel class
//Hamza Amir
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.JPanel;
import java.lang.Thread;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**Word panel is used to paint the words onto the canvas and
   start the threads thst cause the words to drop.
   It also has functions to end the game and update the score as required*/
public class WordPanel extends JPanel implements Runnable{
		public static volatile boolean done;
		private WordRecord[] words;
		private int noWords;
		private int maxY;
      private int totalWords;
      private volatile Score s;
      private String inputText;
      private volatile JLabel missed;
      private volatile JLabel caught;
      private volatile JLabel scoreLabel;
      private Thread[] threads;
      private volatile JFrame frame;
      private volatile JButton start;
      AtomicBoolean flag = new AtomicBoolean();
      static volatile boolean complete=false;
      private controller[] run;
      private volatile int  num;
      /**Method to paint the words onto the canvas*/
      public void paintComponent(Graphics g) {
         int width = getWidth();
         int height = getHeight();
         g.clearRect(0,0,width,height);
         g.setColor(Color.red);
         g.fillRect(0,maxY-10,width,height);
         g.setColor(Color.green);
         g.fillRect(0,0,width,10);
         g.setColor(Color.black);
         g.setFont(new Font("Helvetica", Font.PLAIN, 26));
         //draw the words
         //animation must be added 
         for (int i=0;i<noWords;i++){	    	
            g.drawString(words[i].getWord(),words[i].getX(),words[i].getY());	 
         }   
      }
      /**COnstructor method*/
      WordPanel(WordRecord[] words, int maxY,int w,Score sc) {
         this.words=words;
         noWords = words.length;
         done=false;//Sets boolean flag to false
         this.maxY=maxY;		
         totalWords=w;
         s=sc;
      }
      /**Run method which is used to create th threads and repaint the word drops*/
      public void run() {  
         flag.set(true);
         complete=false;
         num=0;
         threads=new Thread[noWords];//creates an array of threads
         run=new controller[noWords];//creates an array of controller objects
         //For loop used to initialise the first few numbers that were required to fall
         for(int c=0;c<noWords;c++){
            run[c]=new controller(words[c],this,s,missed);//controller object is constructed
            threads[c]= new Thread(run[c]);
            threads[c].start();
         }
         
         num=num+noWords;//counter used to keep count of the threads  created
         //loop used to create a new thread once a thread has been compelted for a word
         //loop breaks if flag is raised or the total number of words have started dropping
         while(done==false && flag.get()==true){ 
            for(int g=0; g<noWords;g++){
               if (threads[g].isAlive()==false){//if statement checks if thread is alive
                  words[g].resetWord();
                  run[g]=new controller(words[g],this,s,missed);
                  threads[g]=new Thread(run[g]);
                  threads[g].start();
                  num=num+1;
               }
               //if thread is alive word is reset and a new comtroller object and thread are created
            } 
            if(num>=totalWords){done=true;}  //checks if the total num wordshave fallen hence thread creatation is complete
         }
         //Loop used to ensure that all the threads have completed exceution before this run() method is complete
         while(complete==false && flag.get()==true){complete=isDone();} //Checks if  all the threads are complete
         //If all threads are complete the game is over hence end(0 is called and score is reset
         if(complete==true){
            end();
            start.setEnabled(true);
            soundEffects(); 
            JOptionPane.showMessageDialog(frame,"   Game has ended \n"+"    Your score is "+s.getScore()+"\n    Words Missed " +s.getMissed()+"\n    Words Caught "+s.getCaught());
            //Option Pane displayed when game is complete
            reset();
         }
    }
      /**Method used to check if all the threads have been completed and returns value accordingly*/    
      public boolean isDone(){
         for(int k=0;k<noWords;k++){
            if(threads[k].isAlive()){return false;}
            else{words[k].setWord("");}
         }
         return true;//if all threads are done true is retuned
      }
      /**Get method for done*/
      public boolean getDone(){return done;}
      /**Method used to compare the text enterd in the text box and the words dropping
         If any of the words mathces those threads are ended and score is updated
         If the words done match nothing occurs*/
      public void userInput(String t){         
         inputText=t;
         for(int h=0;h<noWords;h++){
               if(words[h].matchWord(inputText)){  
                  s.caughtWord((words[h].getWord()).length());
                  caught.setText("Caught: " + s.getCaught() + "    ");
                  scoreLabel.setText("Score:" + s.getScore()+ "    ");
                  run[h].stops();
                  words[h].resetPos();
                  words[h].setWord("");
                  repaint();
            }  
         }
      }
      /**Method used to pass the labels, frame and buttons to allow us to edit 
      the canvas accordingly*/ 
      public void labelUpdate(JLabel c,JLabel m,JLabel s,JFrame f,JButton b){
         caught=c;
         missed=m;
         scoreLabel=s;
         frame=f;
         start=b;
      }
      /**End method used to stop the execution of al the threads and resets the canvas*/
      public void end(){
         done=true;
         for(int d=0;d<noWords;d++){
            if(threads[d].isAlive()){run[d].stops();}//Stops any threads that are running
            words[d].resetWord();
         }
      }
      /**Stop method use to end all the threads and raise the completion flag*/
      public void stopped() {
         end();
         flag.set(false);
      }
      /**Reset method used to reset the scores and update then=m on the canvas*/
      public void reset(){
         done=false;
         s.resetScore();
         caught.setText("Caught: " + s.getCaught() + "    ");
         scoreLabel.setText("Score:" + s.getScore()+ "    ");
         missed.setText("Missed:" + s.getMissed()+ "    ");
         repaint();

      }
      /**Method used to create game over sound effects*/
      public void soundEffects(){
      try
      {
          // create AudioInputStream object
          AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("soundEffects/gameOver.au").getAbsoluteFile());
          
        // create clip reference
        Clip clip = AudioSystem.getClip();
          
        // open audioInputStream to the clip
        clip.open(audioInputStream);
       
        clip.start();
      }
      catch(Exception e){System.out.println(e);}
   }

}
   

