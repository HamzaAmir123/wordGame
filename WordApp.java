import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Scanner;
import java.util.concurrent.*;
//model is separate from the view.
/**
Class provided by university. Used to create the GUI and run the main().
There are action listeners to carry out cerain instructions when a certain
comman is given*/   

public class WordApp {
//shared variables
	static int noWords=4;
	static int totalWords;

   static int frameX=1000;
	static int frameY=600;
	static int yLimit=480;
   //AtomicBoolean flag = new AtomicBoolean(false);
	static WordDictionary dict = new WordDictionary(); //use default dictionary, to read from file eventually
   static boolean paused=false;
	static WordRecord[] words;
	static volatile boolean done;  //must be volatile
	static Score score = new Score();
	static WordPanel w;
	static Thread t1 ;//Thread used to run the word panel
   static int x_inc;
	
   
	/**method used to screate the gui and contains the actionListners*/
	public static void setupGUI(int frameX,int frameY,int yLimit) {
		// Frame init and dimensions
    	JFrame frame = new JFrame("WordGame"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(frameX, frameY);
      JPanel g = new JPanel();
      g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
      g.setSize(frameX,frameY);
    	
		w = new WordPanel(words,yLimit,totalWords,score);
		w.setSize(frameX,yLimit+100);
	   g.add(w); 
	    
      JPanel txt = new JPanel();
      txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS)); 
      JLabel caught =new JLabel("Caught: " + score.getCaught() + "    ");
      JLabel missed =new JLabel("Missed:" + score.getMissed()+ "    ");
      JLabel scr =new JLabel("Score:" + score.getScore()+ "    ");    
      txt.add(caught);
	   txt.add(missed);
	   txt.add(scr);
             
	   final JTextField textEntry = new JTextField("",20);
	   textEntry.addActionListener(new ActionListener()
	   {
	      public void actionPerformed(ActionEvent evt) {
            String text = textEntry.getText();
	         try{if(t1.isAlive()){w.userInput(text);}}//User text sent to word panel to compare with given words
	         catch(Exception e){}
            textEntry.setText("");//Sets text to blank after user has entered data
 	         textEntry.requestFocus();
            
	      }
	   });
	   
	   txt.add(textEntry);
	   txt.setMaximumSize( txt.getPreferredSize() );
	   g.add(txt);
      
	   JPanel b = new JPanel();
      b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS)); 
	   JButton startB = new JButton("Start");;
	   JButton endB = new JButton("End");;
      endB.setEnabled(false);
      //Sends the buttons and labels to wordpanel to edit when the game runs

			// add the listener to the jbutton to handle the "pressed" event
		startB.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {  
            startB.setEnabled(false);//Disables start button 
            endB.setEnabled(true);//enables end button
            startB.setText("Restart");//Start button text updated
            w.labelUpdate(caught,missed,scr,frame,startB);//labels and frame sent to word panel
            t1=new Thread(w);//Thread for w run is created
            t1.start();//Thread is started
  		      //return focus to the text entry field
            textEntry.requestFocus();
         }
         
		});
		
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
            startB.setEnabled(true);//Start button enabled
            endB.setEnabled(false);
            w.stopped();//Stops the run()in word panel
            w.reset();//resets values and score in word panel        
		   }
		});
      JButton quitB = new JButton("Quit");;
		quitB.addActionListener(new ActionListener()
		{
		   public void actionPerformed(ActionEvent e)
		   {
		      System.exit(0);//Exits program
		   }
		});

		
		b.add(startB);
     
		b.add(endB);
      b.add(quitB);
		
		g.add(b);
    	
      frame.setLocationRelativeTo(null);  // Center window on screen.
      frame.add(g); //add contents to window
      frame.setContentPane(g);     
       	//frame.pack();  // don't do this - packs it into small space
      frame.setVisible(true);
	}  
   public static String[] getDictFromFile(String filename) {
		String [] dictStr = null;
		try {
			Scanner dictReader = new Scanner(new FileInputStream(filename));
			int dictLength = dictReader.nextInt();
			//System.out.println("read '" + dictLength+"'");

			dictStr=new String[dictLength];
			for (int i=0;i<dictLength;i++) {
				dictStr[i]=new String(dictReader.next());
				//System.out.println(i+ " read '" + dictStr[i]+"'"); //for checking
			}
			dictReader.close();
		} catch (IOException e) {
	        System.err.println("Problem reading file " + filename + " default dictionary will be used");
	    }
		return dictStr;
	}
   /**MAin method used to create gui and run it*/
	public static void main(String[] args) {
    	
		//deal with command line arguments
		totalWords=Integer.parseInt(args[0]);  //total words to fall
		noWords=Integer.parseInt(args[1]); // total words falling at any point
		assert(totalWords>=noWords); // this could be done more neatly
		String[] tmpDict=getDictFromFile(args[2]); //file of words
		if (tmpDict!=null)
			dict= new WordDictionary(tmpDict);
		
		WordRecord.dict=dict; //set the class dictionary for the words.
		
		words = new WordRecord[noWords];  //shared array of current words
		
		System.setProperty("sun.java2d.opengl","true");
		setupGUI(frameX, frameY, yLimit);  
    	//Start WordPanel thread - for redrawing animation
     
      
		x_inc=(int)frameX/noWords;
	  	//initialize shared array of current words

		for (int i=0;i<noWords;i++) {
			words[i]=new WordRecord(dict.getNewWord(),i*x_inc,yLimit);
		}
	}
}