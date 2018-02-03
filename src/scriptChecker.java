import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.BufferStrategy;

public class scriptChecker extends JButton
	{
		private boolean readyToStart;
			
		private JButton importScript;
	
		private int HEIGHT = 250;
		private int WIDTH = 250;
		private int currentAd;
		private int strategyCreated;
		
		private JFrame frame;
		private JPanel pane;
		private dmcEditorPane windowTextBox;
		private JFileChooser fc;
		private File scriptFile;
		private String outString;
		private File outFile;
		
		BorderLayout bord = new BorderLayout();
		GridLayout experimentLayout = new GridLayout(0,2);
		GridLayout buttonLayout = new GridLayout(3,0);
		
		public scriptChecker(String s)
		{
			super(s);
			//pane = new JPanel();
			//pane.setLayout(bord);
			
			importScript = new JButton("Script to TXT");
			this.addActionListener(new importListener());
         windowTextBox = null;
			
			//pane.add(importScript);
	
			//add(pane);
			
			//pane.setVisible(true);
			//setVisible(true);
			
			currentAd = 1;
			strategyCreated = 0;
		}
      
		public void updateScriptFile(File fileIn)
		{
			scriptFile = fileIn;
		}
      
		public void runCheck(){
   	   
         if(scriptFile != null){
   			String position;
   			ArrayList<String> slateInfo;
   			boolean[] slateContains;
   			
   			int numAds = 0;
   			
   			try {
   			    BufferedReader in = new BufferedReader(new FileReader(scriptFile));			// load buffer with file
   				 BufferedWriter out = null;
   				 
   				 String str;																				// iterator string
   				 String vo = new String();
   				 boolean specialConsideration = false;
   				 /*
   				 if(scriptFile.toString().contains("Pennlive") || scriptFile.toString().contains("NJ") || scriptFile.toString().contains("Spartanburg"))									// Creating a path for non-standard scripts
   				 {
   				 	specialConsideration = true;
   				 }
   				 */
   				 
   			    while ((str = in.readLine()) != null) {											// While file still has Next()
   					  				 
   					 if(str.contains("COMPLETION CHECKLIST"))
                   {
                     break;
                   }
   			       if(str.contains("Uploaded") || str.contains("Uplaoded") || str.contains("Uploadde"))			// First slug Updated occurence.
   					 {
                     System.out.println("Contains Uploaded: " + str);
   					 	slateInfo = new ArrayList<String>();
   						
   					 	numAds += 1;
   						
   						if(numAds < 10)
   						{
   							out = new BufferedWriter(new FileWriter(outString + "\\0" + numAds + ".txt"));
   						}else {
   							out = new BufferedWriter(new FileWriter(outString + "\\" + numAds + ".txt"));
   						}
   						
   						System.out.println(outString);
   					 
   					 	str = in.readLine();
   						System.out.println(str);
   						System.out.println(str.length());
   						
   						while(str.length() < 1)
   						{
   							str = in.readLine();								// the position line offset
   						}
   						System.out.println("Position = " + str);
                     str = str.trim();
                    
   					   out.write(str);
   					   out.newLine();
                  
   											
   						while(str.length() < 50 && str.length()!= 0)
   						{
   							str = in.readLine();
   							System.out.println(str);
                        str = str.trim();
                        if(str.contains("(?") == false)
                        {
      							out.write(str);
                        }else{
                           str = str.substring(0,str.indexOf("(?"));
                           str = str.trim();
                           out.write(str);
                        }
   							if(str.length() > 0)
   							{
   								out.newLine();
   							}
   						}
   						
   						//while(str.length() == 0 || str.equals("eoe"))
   						while(str.length() < 50)
   						{
   							str = in.readLine();
                        if(str == null)
                        {
   							   break;
                        }
   							System.out.println("Outline Tests: " + str);
   							if(str.length() < 50 && str.length() > 0 && str.contains("eoe") != true && 
   								str.contains("E0E") != true && str.contains("Eoe") != true)
   							{
   								if(str.equals(" ") == false && str.equals("\t") == false && str.equals("") == false)
   								{
   									out.write(str);
   									out.newLine();
   								}
   							} else if(str.length() == 0){
   								str = in.readLine();
   							}
   							
   						}
   						out.close();
   					 }
   			    }
   			    in.close();
                if(scriptFile.exists())
                {
                  scriptFile.delete();
                }
   			} catch (IOException e) {
   			}
         if(windowTextBox != null)
         {
            String boxText = windowTextBox.getText();
            windowTextBox.setText(boxText + "\n\n" + numAds + " slate texts created.");
         }
   		}
        
		}
		
		public class importListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				/*
				fc = new JFileChooser("Z:\\00 SCRIPTS TO BE READ");
				int returnVal = fc.showOpenDialog(scriptChecker.this);
				
				if (returnVal == JFileChooser.APPROVE_OPTION) {
	                scriptFile = fc.getSelectedFile();
	                System.out.println("Opening: " + scriptFile.getName() + ".");
						 readyToStart = true;
	         } else {
	         	System.out.println("Open command cancelled by user.");
	         }
				
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
				*/
					int returnVal = -50;
					JFileChooser outPath = new JFileChooser("Z:\\00 CC TEXT");
					outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
		
					returnVal = outPath.showOpenDialog(scriptChecker.this);
					
					if(returnVal == JFileChooser.APPROVE_OPTION){
						File temp = outPath.getSelectedFile();
						outString = temp.getPath();
					}
					runCheck();
			   /*
				} else {
					System.out.println("Open Command cancelled by user.");
				}
				*/
			}
		}
		
		
		private boolean isReady()
		{
			return readyToStart;
		}
   public void setTextBox(dmcEditorPane textBoxIn)
   {
      windowTextBox = textBoxIn;
   }
}