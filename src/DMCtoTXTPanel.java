import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.BufferStrategy;

public class DMCtoTXTPanel
{
	public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new scriptChecker();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
 
}

class scriptChecker extends JPanel
{
	private boolean readyToStart;
		
	private JButton importScript;

	private int HEIGHT = 250;
	private int WIDTH = 250;
	private int currentAd;
	private int strategyCreated;
	
	private JFrame frame;
	private JPanel pane;
	
	private JFileChooser fc;
	private File scriptFile;
	private String outString;
	private File outFile;
	
	BorderLayout bord = new BorderLayout();
	GridLayout experimentLayout = new GridLayout(0,2);
	GridLayout buttonLayout = new GridLayout(3,0);
	
	public scriptChecker()
	{
		setTitle("DMC to TXT");
		
		setBounds(0, 0,WIDTH,HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pane = new JPanel();
		pane.setLayout(bord);
		
		importScript = new JButton("Import Script");
		importScript.addActionListener(new importListener());
		
		pane.add(importScript);

		add(pane);
		
		pane.setVisible(true);
		setVisible(true);
		
		currentAd = 1;
		strategyCreated = 0;
	}
	
	public void runCheck()
	{
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
			 
		    while ((str = in.readLine()) != null) {											// While file still has Next()
				  				 
				
		       if(str.contains("Uploaded"))														// First slug Updated occurence.
				 {
				 	slateInfo = new ArrayList<String>();
					
				 	numAds += 1;
					out = new BufferedWriter(new FileWriter(outString + "\\0" + numAds + ".txt"));
				 
				 	str = in.readLine();
					System.out.println(str);
					System.out.println(str.length());
					
					while(str.length() < 1)
					{
						str = in.readLine();								// the position line offset
					}
					
					out.write(str);
					out.newLine();
										
					while(str.length() < 50 && str.length()!= 0)
					{
						str = in.readLine();
						System.out.println(str);
						out.write(str);
						if(str.length() > 0)
						{
							out.newLine();
						}
					}
					while(str.length() < 50)
					{
						str = in.readLine();
						System.out.println(str);
						str.trim();
						if(str.length() < 50 && str.length() > 0 && str.contains("eoe") != true && 
							str.contains("E0E") != true && str.contains("Eoe") != true)
						{
							if(str.equals(" ") == false && str.equals("\t") == false && str.equals("") == false)
							{
								out.write(str);
							}
							//out.newLine();                       
						}
					}
					out.close();
				 }
		    }
		    in.close();
		} catch (IOException e) {
		}
	}
	
	public class importListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
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
				returnVal = -50;
				JFileChooser outPath = new JFileChooser("Z:\\00 CC TEXT");
				outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
	
				returnVal = outPath.showOpenDialog(scriptChecker.this);
				
				if(returnVal == JFileChooser.APPROVE_OPTION){
					File temp = outPath.getSelectedFile();
					outString = temp.getPath();
				}
				runCheck();
			} else {
				System.out.println("Open Command cancelled by user.");
			}
		}
	}
	
	
	private boolean isReady()
	{
		return readyToStart;
	}
	
}