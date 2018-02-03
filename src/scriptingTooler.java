import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.BufferStrategy;
import javax.swing.UIManager.*;
import org.apache.poi.hwpf.extractor.WordExtractor;

public class scriptingTooler extends JFrame implements WindowListener,
                                            WindowFocusListener,
                                            WindowStateListener
{
	private boolean readyToStart;
	private boolean imported;
	private Canvas errorDisplay;
	private ArrayList<scriptError> errorsList;
	private ArrayList<String> adNumbersList;
	private ArrayList<String> repeatAdList;
	private ArrayList<String> dfLines;
	private dmcEditorPane slateE;
	private dmcEditorPane voE;
		
	private JButton importScript;
	private JButton nextError;
	private JButton previousError;
	private JButton editButton;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem createDataFile;
	private JMenuItem createErrorLog;
   private JMenuItem runCheck;
	private JMenuItem exitItem;
	
	private int HEIGHT;
	private int WIDTH;
	private int currentAd;
	private int strategyCreated;
	
	private JFrame frame;
	private JPanel pane;
	private JPanel labelPane;
	private errorPane errorText;
	
	private JFileChooser fc;
	private File saveLocation;
	private File scriptFile;
	
	private String scriptingLog = "\\\\Quickbooks\\scripting\\00 Logs\\";
	private String productionLog = "Z:\\Data\\00 Logs\\";
   private String stringFilePath;
	
   private scriptObject importedScript;
   
	GridLayout experimentLayout = new GridLayout(0,2);
	GridLayout buttonLayout = new GridLayout(4,0);
	GridLayout labels = new GridLayout(2,0);
	
	public scriptingTooler()
	{
		try {
	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	        if ("Nimbus".equals(info.getName())) {
	            UIManager.setLookAndFeel(info.getClassName());
	            break;
	        }
	    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
      stringFilePath = WindowsUtils.getCurrentUserDesktopPath();
      
      setTitle("DMC Script Chcker");
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
 		Dimension dim = toolkit.getScreenSize();
		WIDTH = dim.width / 2;
		HEIGHT = dim.height / 2;
		imported = false;
  		errorText = new errorPane();
      slateE = errorText.getSlate();
      voE = errorText.getVO();
		setBounds(0, 0,WIDTH,HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		createDataFile = new JMenuItem("Create Datafile");
		createDataFile.setMnemonic(KeyEvent.VK_C);
		createDataFile.addActionListener(new createDFListener());
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new exitListener());
		createErrorLog = new JMenuItem("Log Error");
		createErrorLog.addActionListener(new logListener());
      runCheck = new JMenuItem("Error Check");
      runCheck.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e)
         {
            if(importedScript != null)
            {
               runCheck();
            }
         }
      });
		
		fileMenu.add(createDataFile);
		fileMenu.add(exitItem);
		fileMenu.add(createErrorLog);
      fileMenu.add(runCheck);
		setJMenuBar(menuBar);
		
		pane = new JPanel();
		pane.setLayout(buttonLayout);
		labelPane = new JPanel();
		labelPane.setLayout(labels);
		
		importScript = new JButton("Import Script");
		nextError = new JButton("Next Error");
		previousError = new JButton("Previous Error");
		editButton = new JButton("Edit Import");
		
		labelPane.add(new JLabel("Errors:"));
		labelPane.add(new JLabel("VO:"));
		
		pane.add(importScript);
		pane.add(previousError);
		pane.add(nextError);
		pane.add(editButton);
		
		importScript.addActionListener(new importListener());
		nextError.addActionListener(new nextListener());
		previousError.addActionListener(new previousListener());
		editButton.addActionListener(new editListener());
		addWindowListener(this);
      addWindowFocusListener(this);
      addWindowStateListener(this);

		add(errorText, BorderLayout.CENTER);
		add(labelPane,BorderLayout.WEST);
		add(pane, BorderLayout.EAST);
		
		pane.setVisible(true);
		errorText.setVisible(true);
		setVisible(true);
		errorsList = new ArrayList();
		adNumbersList = new ArrayList();
		repeatAdList = new ArrayList();
		
		currentAd = 1;
		strategyCreated = 0;
		
		JPanel tempPane = (JPanel)getContentPane();
		tempPane.setBorder(BorderFactory.createEmptyBorder(10,2,10,2));

	}
	
	public void runCheck()
	{
      voE.setBody(" ");
      slateE.setBody(" ");
		ArrayList<String> slateInfo;
      ArrayList<String> voInfo;
      ArrayList<String> companyNames;
		boolean[] slateContains;
	 	boolean positionError = false;
		boolean companyError = false;
      
		slateInfo = importedScript.getSlates();
      voInfo = importedScript.getVoiceOverText();
      companyNames = importedScript.getCompanyNames();
		slateContains = new boolean[slateInfo.size()];
      
      int numAds = slateInfo.size();
      
		for(int i = 0;i < slateContains.length;i++){
			slateContains[i] = false;
		}
      
		for(int i = 0;i < slateInfo.size() - 1;i++){
		   String vo = voInfo.get(i);
         String currentSlate = slateInfo.get(i);
         ArrayList<String> slateSeparate = new ArrayList<String>();
         System.out.println("Slate before editing: \n" + currentSlate + "\n_______________");
         
         int counter = 0;
         while(currentSlate.contains("\n"))
         {
            counter++;
            System.out.println(currentSlate.substring(0,currentSlate.indexOf("\n") - 1) + "At count: " + counter);
            slateSeparate.add(currentSlate.substring(0,currentSlate.indexOf("\n") - 1).trim());
            currentSlate = currentSlate.substring(currentSlate.indexOf("\n") + 1);
         }
         slateSeparate.add(currentSlate);
         System.out.println("Adding final line: " + currentSlate);
         System.out.println("************************");
         for(int p = 0;p < slateSeparate.size();p++)
         {
            
            System.out.println(slateSeparate.get(p));
         }
         System.out.println("************************");
         for(int s = 0;s < slateSeparate.size();s++)
         {
				if(isPhone(slateSeparate.get(s)) == true)
				{
					int hyphenIndex = slateSeparate.get(s).indexOf("-");
					
					if(hyphenIndex > 0)
					{
						String formatString = slateSeparate.get(s).substring(hyphenIndex - 3);
						formatString.trim();
						slateSeparate.set(s, formatString);
					}
				}
				
				if(vo.toUpperCase().contains(slateSeparate.get(s).toUpperCase().replace("\n","")))
				{
					slateContains[i] = true;
				}else if(isPhone(slateSeparate.get(s)) == true || isEmail(slateSeparate.get(s)) == true){
						System.out.println("Line: " + s + " to false" + slateSeparate.get(s) + " Phone/Email check done\n");
                  scriptError inputError = new scriptError(i,slateSeparate);
                  inputError.setVO(vo);
                  inputError.addError("Missing or mismatched slate info, AD# " + (i + 1) + ":\n Missing: " + slateSeparate.get(s) + "\n");
                  errorsList.add(inputError);
					} else {
				}
         }
		}
		drawErrors();
	}
	
	
	public static void main(String[] args)
	{
		scriptingTooler programRun = new scriptingTooler();
	}
	
	public class importListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//fc = new JFileChooser("Z:\\00 SCRIPTS TO BE READ");
			fc = new JFileChooser("\\\\Quickbooks\\scripting\\01 Markets to Script");
			int returnVal = fc.showOpenDialog(scriptingTooler.this);
			errorsList.clear();
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                scriptFile = fc.getSelectedFile();
                System.out.println("Opening: " + scriptFile.getName() + ".");
                if(scriptFile.getName().contains(".doc"))
                {
                  System.out.println("Import contains .doc");
                   try{
                     WordExtractor inWord = new WordExtractor(new FileInputStream(scriptFile));
                     String document = inWord.getText();
                                          
                     try{
                        document = document.substring(document.indexOf("$#$#"));
                        importedScript = new scriptObject(document,slateE,scriptFile.getName());
                        importedScript.setStringFilePath(stringFilePath);
                        slateE.append("<strong><p>" + scriptFile.getName() + " imported successfully.</p></strong>");
                     }catch(Exception f)
                     {
                        JOptionPane.showMessageDialog(new JFrame(),
                        "Document file missing delimiter: $#$#");
                        importedScript = null;
                     }
                   }catch(IOException f)
                   {
                     JOptionPane.showMessageDialog(new JFrame(),
                     ".doc type file not imported.");
                   }
                }
            } else {
         }
		}
	}
	
	public class nextListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(currentAd < errorsList.size())
			{
				currentAd += 1;
				drawErrors();
			} 
		}
	}
	
	public class previousListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(currentAd > 1)
			{
				currentAd -= 1;
				drawErrors();
			} 
		}
	}
	public class editListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Runtime runtime = Runtime.getRuntime();
			try{
				
            if(scriptFile != null)
            {         
   		      Desktop.getDesktop().open(scriptFile);
            }else{
               JOptionPane.showMessageDialog(new JFrame(),
               "No scriptfile imported.");
            }

			}catch(IOException t)
			{
			   
			}
		}
	}
	public class logListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try{
				//System.out.println(scriptFile.getName());
				//BufferedWriter out = new BufferedWriter(new FileWriter(productionLog + scriptFile.getName()));
				BufferedWriter out = new BufferedWriter(new FileWriter(scriptingLog + scriptFile.getName()));
				BufferedReader in = new BufferedReader(new FileReader(scriptFile));
				String str = "";
				while((str = in.readLine()) != null)
				{
					out.write(str);
					out.newLine();
				}
				in.close();
				out.close();
			}catch(IOException f)
			{
				//System.out.println("Error creating log");
			}
		}
	}
	public class createDFListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(scriptFile != null)
			{
			   if(importedScript != null)
            {
               importedScript.generateDataFile();
            }	
         }else{
			   JOptionPane.showMessageDialog(scriptingTooler.this, "There is no file imported");
			}		
		}
	}
	public class exitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}
	
	private boolean isReady()
	{
		return readyToStart;
	}
	
	private boolean isEmail(String lineIn)
	{
		if(lineIn.contains("@"))
		{
			return true;
		} else if(lineIn.contains(".COM") || lineIn.contains(".NET") || lineIn.contains(".GOV") || lineIn.contains(".ORG")){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isPhone(String lineIn)
	{
		char firstDigit;
		int firstDigitValue;
		
		if(lineIn.contains("FAX")){
		
			return true;
		} else if(lineIn.contains("CALL")){
		
			return true;
		} else if(lineIn.contains("-")){
		
			int tempIndex = lineIn.indexOf("-");
			
			try{
				firstDigit = lineIn.charAt(tempIndex - 3);
				firstDigitValue = Character.getNumericValue(firstDigit);
			}catch(Exception e)
			{
				//System.out.println("Failure to parse Phone");
				firstDigit = 10;
				firstDigitValue = 10;
			}
			
			//System.out.println("The value of the first digit is...: " + firstDigitValue);
			
			if(firstDigitValue >= 0 && firstDigitValue <= 9)
			{
				//System.out.println("The value of the first digit is " + firstDigitValue);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public boolean hasError(boolean[] arrayIn)
	{
		int error = 0;
	
		for(int i = 0;i < arrayIn.length - 1;i++)
		{
			if(arrayIn[i] == false)
			{
				error = 1;
				break;
			} else {
				error = 0;
			}
			//System.out.println(arrayIn[i]);
		}
		
		if(error == 1)
		{
			//System.out.println(error);
			//System.out.println(arrayIn.length);
			//System.out.println("Has error");
			return true;
		} else{
			return false;
		}
		
	}
	
	public void drawErrors()
	{
		int xOffset = 10;
		//voE = errorText.getVO();
		//slateE = errorText.getSlate();
		
		voE.setBody(" ");
		slateE.setBody(" ");
		
		ArrayList<String> tempArray = new ArrayList<String>();
		
		if(errorsList.size() > 0)
		{
			tempArray = errorsList.get(currentAd - 1).getErrors();
			int adNumber = errorsList.get(currentAd - 1).getAdNum();
			String adNumStr = "<p>Ad Number: " + Integer.toString(adNumber + 1) + "</p>";
			
			//slateE.setText(adNumStr);	
			
			for(int i = 0;i < tempArray.size();i++)
			{
				slateE.append("<p>" + " Error Line: " + tempArray.get(i) + "</p>");
			}
			
         //slateE.append("\n");
		
			tempArray = errorsList.get(currentAd - 1).getSlateInfo();
			
			for(int i = 0;i<tempArray.size();i++)
			{
				slateE.append("<p>" + tempArray.get(i) + "</p>");
			}
			
			tempArray = errorsList.get(currentAd - 1).getVo();
			
			for(int i = 0;i<tempArray.size();i++)
			{
				voE.setBody(voE.getText() + "<p>" + tempArray.get(i) + "</p>");
			}
		} else {
			slateE.setBody("<p><strong>No Errors</strong></p>");
		}
				 	 
	}
	
	public void windowClosed(WindowEvent e) {
        System.exit(0);
    }
	 public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    public void windowOpened(WindowEvent e) {
    	if(imported == true)
		{ 
		  //System.out.println("windowOpened");
		  drawErrors();
		}
    }

    public void windowIconified(WindowEvent e) {
       if(imported == true)
		{ 
			//System.out.println("windowIconified");   
		  drawErrors();
		}
    }

    public void windowDeiconified(WindowEvent e) {
        if(imported == true)
		{ 
			//System.out.println("windowDeiconified");   
		  drawErrors();
		}  
	}
	
	public void windowActivated(WindowEvent e) {
        if(imported == true)
		{    
			//System.out.println("windowActivated");
		  drawErrors();
		}
    }

    public void windowDeactivated(WindowEvent e) {
        if(imported == true)
		{    
			//System.out.println("windowDeactivated");
		  drawErrors();
		}
    }

    public void windowGainedFocus(WindowEvent e) {
        if(imported == true)
		{    
			//System.out.println("windowFocus");
		  drawErrors();
		}
    }

    public void windowLostFocus(WindowEvent e) {
       if(imported == true)
		{    
			//System.out.println("windowDeFocus");
		  drawErrors();
		}    
	}

    public void windowStateChanged(WindowEvent e) {
      if(imported == true)
		{    
			//System.out.println("windowStateChange");
		  drawErrors();
		}    
	}
	
	public Boolean substituteCharacters(String sIn,String voIn)
	{
		String sTemp = sIn.toString();
		
		if(sTemp.contains("&"))
		{
			sTemp = sTemp.replaceAll("&","AND");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("ASST") || sTemp.contains("ASST."))
		{
			sTemp = sTemp.replace("ASST.","");
			sTemp = sTemp.replaceAll("ASST","ASSISTANT");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("ST."))
		{
			sTemp = sTemp.replace(".","");
			sTemp = sTemp.replaceAll("ST","SAINT");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("DIR"))
		{
			sTemp = sTemp.replaceAll("DIR","DIRECTOR");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("NJ"))
		{
			sTemp = sTemp.replaceAll("NJ","NEW JERSEY");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("SR") || sTemp.contains("SR."))
		{
			sTemp = sTemp.replace("SR.","SR");
			sTemp = sTemp.replaceAll("SR","SENIOR");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("MGR") || sTemp.contains("MGR."))
		{
			sTemp = sTemp.replace("MGR.","MGR");
			sTemp = sTemp.replaceAll("MGR","MANAGER");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("HR") || sTemp.contains("HR."))
		{
			sTemp = sTemp.replace("HR.","");
			sTemp = sTemp.replaceAll("HR","HUMAN RESOURCES");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("CTY"))
		{
			sTemp = sTemp.replaceAll("CTY","COUNTY");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("TWNSHP"))
		{
			sTemp = sTemp.replaceAll("TWNSHP","TOWNSHIP");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains(" TECH "))
		{
			sTemp = sTemp.replaceAll(" TECH ","TECHNICAL");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains(","))
		{
			sTemp = sTemp.replace(",","");
			voIn = voIn.replace(",","");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(sTemp.contains("."))
		{
			sTemp = sTemp.replace(".","");
			voIn = voIn.replace(".","");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(voIn.contains("."))
		{
			voIn = voIn.replace(".","");
			sTemp = sTemp.replace(".","");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		if(voIn.contains(","))
		{
			voIn = voIn.replace(",","");
			if(voIn.contains(sTemp))
			{
				return false;
			}
		}
		//System.out.println("Substituted Line: " + sTemp);
		return true;
	}
	
	public void showAdRepeats()
	{
		slateE.setText("");
		
		for(int i = 0;i < adNumbersList.size();i++)
		{
			for(int j = i + 1;j < adNumbersList.size();j++)
			{
				if(j > adNumbersList.size() == false)
				{
					if(adNumbersList.get(i).equals(adNumbersList.get(j)))
					{
						repeatAdList.add(adNumbersList.get(i));
						slateE.setBody("<p>Ad #" + Integer.toString(i + 1) + ": "  + adNumbersList.get(i)  +
						" shares an Ad Number with Ad # " + Integer.toString(j + 1) + "</p>");
					}
				}
			}
		}
		
		if(repeatAdList.size() < 1)
		{
			slateE.setBody("<strong><p>There are no repeat ad numbers</strong></p>");
		}
		
	}
	
}