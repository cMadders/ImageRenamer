import java.util.*;
import java.util.regex.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import javax.imageio.*;
import java.awt.image.BufferStrategy;
import java.awt.GradientPaint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import javax.swing.UIManager.*;
import org.apache.poi.poifs.filesystem.*;
import static java.nio.file.StandardCopyOption.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.*;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.util.TrustManagerUtils;

import java.net.URL;
import java.net.URLConnection;

public class imageRenamer extends JFrame
{
	private DefaultListModel marketsListsModel;
	private JList marketsLists;
   
	private backgroundJButton begin;
	private backgroundJButton importBut;
   	private backgroundJButton editButton;
   	private backgroundJButton scriptToTextButton;
	
   	private ArrayList<String> companies;
	
	private scriptChecker scriptCheck;
	
	private File scriptFile;
	private File targetLocation;
	private File source;

	private String sourceLoc;
	
	//private JTextArea infoText;
	private dmcEditorPane infoText;
	
	
	private BackgroundMenuBar menuBar;
   	private JMenu historyMenu;
	private JMenu fileMenu;
   	private JMenu settingsMenu;
   	private JMenuItem filePathMenu;
   	private JMenuItem updateImport;
	private JMenuItem createDataFile;
   	private JMenuItem compileFiles;
   	private JMenuItem createCaptions;
	private JMenuItem exitItem;
   	private ArrayList<JMenuItem> historyList;
	
	private Date currentDate;
	
	private int WIDTH;
	private int HEIGHT;
	
	private JFileChooser fc;
   
   	private scriptObject importedScript;
   	private ArrayList<scriptObject> importedScriptArray;
	
	private Font JButtonFont = new Font("sansserif",Font.BOLD,12);
	private Color dmcGray = new Color(75,75,75);
	private Color dmcLightGray = new Color(150,150,150);
	private Color dmcRed = new Color(175,50,50);
   	private Color dmcBlue = new Color(150,185,205);
	
	private String defaultImportPath = "Z:\\00 SCRIPTS TO BE READ\\";
	private String defaultExportDFPath ="Z:\\00 CC STRING FILES\\";
	private String defaultExportGFXPath = "Z:\\00 CC GRAPHICS\\";
	private String defaultLogosPath = "Z:\\0 Logos\\";
	private String backUpPath = "Z:\\00 BACKUPS\\";
	private String defaultSubLogoFolder = "web";
	private String configureFolder = ".\\configure";
	private String configurePathFile = ".\\configure\\filePaths.txt";
	private String configureTLDS = ".\\configure\\tlds.txt";
	private String defaultSlateTextFilePath = "Z:\\00 CC TEXT\\";
	private String compilationDirectory = "";
	private ArrayList<String> marketLogoPaths = new ArrayList<String>(); 

	public imageRenamer()
	{
	 	try {
		  for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		  }
		} catch (Exception e) {

		}
		WindowAdapter windowAdapter = new WindowAdapter()
		{
		   public void windowClosing(WindowEvent we)
		   {
		      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		   }
		};
		addWindowListener(windowAdapter);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		importedScriptArray = new ArrayList<scriptObject>();

		// The basic frame construction settings
		setTitle("DMC Image Renamer");
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
		Dimension dim = toolkit.getScreenSize();
		WIDTH = dim.width / 2;
		HEIGHT = dim.height / 2;
		setBounds(0, 0,WIDTH,HEIGHT);

		// Create the Scroll list for all the selectable markets (filled in teh createMarketsLists() method)
		marketsListsModel = new DefaultListModel();

		begin = new backgroundJButton("Rename Images");
		importBut = new backgroundJButton("Import Script");
		importBut.setBackground(dmcRed);
		begin.setBackground(dmcRed);
		//**********************************************************Menu Bar Settings
		menuBar = new BackgroundMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		historyMenu = new JMenu("History");
		menuBar.add(historyMenu);

		// Create the Datafile menu items
		createDataFile = new JMenuItem("Create Datafile");
		createDataFile.setMnemonic(KeyEvent.VK_C);
		createDataFile.addActionListener(new createDFListener());

		// Create Compile Menu Items
		compileFiles = new JMenuItem("Compile Folder");
		compileFiles.setMnemonic(KeyEvent.VK_H);
		compileFiles.addActionListener(new compileListener());

		// Create voice over menu item
		createCaptions = new JMenuItem("Create Captions");
		createCaptions.setMnemonic(KeyEvent.VK_V);
		createCaptions.addActionListener(new createVOListener());

	      // create the exit menu items
		exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.addActionListener(new exitListener());


		// create the update menu item
		updateImport = new JMenuItem("Update Import");
		updateImport.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e)
		{
		    updateScriptFile();
		}
		});

		settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic(KeyEvent.VK_S);

		filePathMenu = new JMenuItem("File Paths");
		filePathMenu.addActionListener(new ActionListener(){
		   public void actionPerformed(ActionEvent e)
		   {
		      openSettingsMenu();         
		   }
		});
		settingsMenu.add(filePathMenu);
		menuBar.add(settingsMenu);

		fileMenu.add(updateImport);
		fileMenu.add(createDataFile);
		fileMenu.add(createCaptions);
		fileMenu.add(compileFiles);
		fileMenu.add(exitItem);
		setJMenuBar(menuBar);
		//******************************************************* END MENU BAR

		//******************************************************* Load file paths and update GUI
		loadSettings();
		add(new UIPanel());
		setVisible(true);
		//*******************************************************


		// Temporarily obtain the Frame's JPane to adjust border settings
		JPanel tempPane = (JPanel)getContentPane();
		//tempPane.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

		// UpdateText() is called whenever something can affect the text.  This initializes the text.
		updateText();

		// Initializing variables
		companies = new ArrayList<String>();

		source = null;
		sourceLoc = null;
	}
	
	public static void main(String[] args)
	{
		imageRenamer programStart = new imageRenamer();
	}
	
   
	//----------------------------------------------------------------------BEGIN METHODS
		
	public void createMarketsLists()
	{
		File f = new File(defaultLogosPath);
      
		marketsListsModel.clear();  
        
	if(f.exists())
	{
		ArrayList<File> fileContents = new ArrayList<File>(Arrays.asList(f.listFiles()));
   		
 	for(int i = 0;i < fileContents.size();i++)
	{
		File marketFile = fileContents.get(i);		
    		if(marketFile.isDirectory())
		{
			marketsListsModel.addElement(marketFile.getName());
		}
	}
   		
	// create the listModel with a list of objects, and set selected index to 0 so there is no crashes on import
   		
 	if(marketsLists == null)
 	{
		JList marketsToAdd = new JList(marketsListsModel);
    		marketsToAdd.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e)
			{
				updateText();
			}
		}); 
		marketsLists = marketsToAdd;
         }else{
		marketsLists.setModel(marketsListsModel);
         }
		marketsLists.setSelectedIndex(0);
      }else{
		JOptionPane.showMessageDialog(new JFrame(),
		"Directory " + f + " not found. Adjust file path settings");
      }
}
	
   public void openSettingsMenu()
   {
      
      JFrame settingsWindow = new JFrame();
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
		settingsWindow.setTitle("File Path Settings");
		Toolkit toolkit =  Toolkit.getDefaultToolkit ();
 		Dimension dim = toolkit.getScreenSize();
		WIDTH = dim.width / 2;
		HEIGHT = dim.height / 2;
		settingsWindow.setBounds(0, 0,WIDTH,HEIGHT);
      		WindowAdapter windowAdapter = new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
               setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        };

      settingsWindow.addWindowListener(windowAdapter);
      settingsWindow.setUndecorated(true);
      
      JScrollPane sp = new JScrollPane(new filePathSettingsPane(settingsWindow));
      settingsWindow.add(sp);
      settingsWindow.setVisible(true);
		
   }
   
   public void loadSettings()
   { 
      // Load filepaths and populate the markets list ***********
      File confFolder = new File(configureFolder);
      File confPathFile = new File(configurePathFile);
      if(confFolder.exists() == false)
      {
         confFolder.mkdir();
         openSettingsMenu();
      }else{
         try{
            BufferedReader in = new BufferedReader(new FileReader(configurePathFile));
            defaultImportPath = in.readLine();
            defaultExportDFPath = in.readLine();
            defaultExportGFXPath = in.readLine();
            defaultLogosPath = in.readLine();
            backUpPath = in.readLine();
            defaultSubLogoFolder = in.readLine();
            createMarketsLists();
         }catch(IOException e){
            JOptionPane.showMessageDialog(this,"Failed to read path configuration save file.");
         }
      }
      createMarketsLists();
      //**********************************************************
      
      
      // Load top level domain lists to screen for logos
      File tldsFile = new File(configureTLDS);
      
   }
   
	public void updateText()
	{
		if(infoText != null)
		{
			infoText.setBody("<strong><p>Image Renaming Directory:</strong><br />" + defaultLogosPath +
			marketsLists.getSelectedValue() + "\\</p>");
		 if(scriptFile != null)
		 {
		    infoText.append(scriptFile.getName());
		 }else{
		    infoText.append("No import");
		 }
	}
	}
   
	public dmcEditorPane getTextBox()
	{
		return infoText;
	}
   
   public class JMenuItemPlus extends JMenuItem
   {
      private scriptObject pairedScript;
      private File scriptFile;
      
      public JMenuItemPlus(String labelIn, scriptObject scriptIn, File scriptFileIn)
      {
         super(labelIn);
         pairedScript = scriptIn;
         scriptFile = scriptFileIn;
      }
      
      public scriptObject getScriptObject()
      {
         return pairedScript;
      }
      
      public void setScriptObject(scriptObject scriptObjectIn)
      {
         pairedScript = scriptObjectIn;
      }
      
      public File getScriptFile()
      {
         return scriptFile;
      }
      
      public void setScriptFile(File scriptIn)
      {
         scriptFile = scriptIn;
      }
      
   }
   
   // ----------------------------------------------------------------- INNER CLASSES
   // This Panel contains the eastern border with marketslists and label
   class UIPanel extends JSplitPane
	{
		public UIPanel()
		{
			JPanel UILayout = new JPanel();
			JPanel UILayoutRight = new JPanel();
			
			UILayout.setLayout(new GridBagLayout());
			UILayoutRight.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			UILayout.setBackground(new Color(50,50,50));
			UILayoutRight.setBackground(new Color(50,50,50));
			setBackground(new Color(50,50,50));
			setForeground(new Color(50,50,50));		
			
			setDividerSize(5);
			setUI(new BasicSplitPaneUI() {
       			public BasicSplitPaneDivider createDefaultDivider() {
		        return new BasicSplitPaneDivider(this) {
		            @Override
		                public void paint(Graphics g) {
		                g.setColor(dmcBlue);
		                g.fillRect(0, 0, getSize().width, getSize().height);
		                    super.paint(g);
		                }
			    };
		  	  }
		        });
			
			//---------------------------------------------
			c.fill = GridBagConstraints.VERTICAL;
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 0.1;
			UILayout.add(new JLabel("Markets: "),c);
			//---------------------------------------------
			
			//---------------------------------------------
			c.insets = new Insets(0,10,0,10);  //top padding
			c.anchor = GridBagConstraints.LINE_START;
			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 0.1;
			c.weighty = 0.5;
			c.gridwidth = 2;
			//---------------------------------------------
			
			JScrollPane objectSelectionSP = new JScrollPane(marketsLists);
			
			//---------------------------------------------
			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 0.1;
			c.weighty = 0.9;
			c.fill = GridBagConstraints.BOTH;
			UILayout.add(objectSelectionSP,c);
			//---------------------------------------------
			
			//---------------------------------------------
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;

			//---------------------------------------------
			scriptCheck = new scriptChecker("Script to TXT");
         		scriptToTextButton = new backgroundJButton("Script to TXT");
			c.insets = new Insets(2,12,12,2);
			scriptToTextButton.setBackground(dmcRed);
        		scriptToTextButton.addActionListener(new createTextListener());
         
			c.gridx = 0;
			c.gridy = 6;
			c.weightx = 0.1;
			c.weighty = 0.1;

			c.insets = new Insets(2,12,12,2);  //top padding
			c.fill = GridBagConstraints.HORIZONTAL;
			UILayout.add(scriptToTextButton,c);
			//---------------------------------------------
			
			editButton = new backgroundJButton("Edit Import");
			editButton.setBackground(dmcRed);
			editButton.addActionListener(new editListener());

			infoText = new dmcEditorPane();
			infoText.setContentType("text/html");
		  	infoText.setEditable(false);
		   	infoText.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				  		System.out.println("Description: " + hle.getDescription() +
												 "Event Type: " + hle.getEventType());
	               if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
	                   System.out.println("LINK CLICKED" + hle.getURL());
	                   Desktop desktop = Desktop.getDesktop();
	                   try {
				String url = hle.getDescription();
				String httpHeader = "";
				if(url.contains("http://") == false && url.contains("www.") == false)
				{
					httpHeader = "http://";
				}
                         	String URIConversion = httpHeader + hle.getDescription();
                         	URIConversion = URIConversion.trim();
				URI website = new URI(URIConversion);
	                   	desktop.browse(website);
	                   } catch (Exception ex) {
	                       ex.printStackTrace();
	                   }
	               }
	           }
	       });

			JScrollPane scrollPane = new JScrollPane(infoText);
			
			importBut.addActionListener(new importListener());
			begin.addActionListener(new beginListener());
         		scriptCheck.setTextBox(infoText);
			
			//---------------------------------------------
			c.gridx = 1;
			c.gridy = 6;
			c.weightx = 0.1;
			//c.weighty = 0.1;
			c.insets = new Insets(2,12,12,12);  //top padding
			c.fill = GridBagConstraints.HORIZONTAL;
			UILayout.add(begin,c);
			//---------------------------------------------
			
			
			//---------------------------------------------
			c.gridx = 0;
			c.gridy = 7;
			c.weightx = 0.5;
			//c.weighty = 0.5;
			c.insets = new Insets(2,12,12,2);  //top padding
			c.fill = GridBagConstraints.HORIZONTAL;
			UILayout.add(importBut,c);
			//---------------------------------------------
			
			//---------------------------------------------
			c.gridx = 1;
			c.gridy = 7;
			c.weightx = 0.5;
			//c.weighty = 0.5;
			c.insets = new Insets(2,12,12,12);
			c.fill = GridBagConstraints.HORIZONTAL;
			UILayout.add(editButton,c);
			//---------------------------------------------
			
			
			//---------------------------------------------
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(5,5,5,5);  //top padding
			c.gridx = 3;
			c.gridy = 0;
			c.ipady = 250;
			c.gridheight = 8;
			c.gridwidth = 2;
			c.weightx = 1.0;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			UILayoutRight.add(scrollPane,c);
			//----------------------------------------------
			
			JScrollPane left = new JScrollPane(UILayout);
			JScrollPane right = new JScrollPane(UILayoutRight);
			setLeftComponent(left);
			setRightComponent(right);
		}
	}
		
	class importListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
         // Check if the imported file needs to be added to the history
         if(importedScript != null)
         {
            JMenuItemPlus historyAdd = new JMenuItemPlus(importedScript.getImportFileName(),importedScript,scriptFile);
            historyAdd.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e)
               {
                  JMenuItemPlus me = (JMenuItemPlus)e.getSource();
                  scriptObject temp = me.getScriptObject();
                  importedScript = temp;
                  scriptFile = me.getScriptFile();
                  infoText.append("<p>Import file switched to : " + temp.getImportFileName() + "</p>"); 
               }
            });
            historyMenu.add(historyAdd);
            
         }
         
			// create a new filechooser and get selected file
			fc = new JFileChooser(defaultImportPath);
			int returnVal = fc.showOpenDialog(imageRenamer.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                
                	scriptFile = fc.getSelectedFile();
                	System.out.println("Opening: " + scriptFile.getName() + ".");
               
                if(scriptFile.getName().endsWith(".doc") || scriptFile.getName().endsWith(".txt") )
                {
                  System.out.println("Import contains .doc");
                   try{
                     String document = "";
                     if(scriptFile.getName().endsWith(".doc")){
                        WordExtractor inWord = new WordExtractor(new FileInputStream(scriptFile));
                        document = inWord.getText();
                     }else{
                        FileInputStream fis = new FileInputStream(scriptFile);
                        int content;
               			while ((content = fis.read()) != -1) {
                           char select = (char)content;
                           document = " " + document + select;
               			}
                        System.out.println(document);
                        fis.close();
                        
                     }
                     try{
                        document = document.substring(document.indexOf("$#$#"));
                        System.out.println("Document is: " + document);
                     }catch(Exception f)
                     {
                        JOptionPane.showMessageDialog(new JFrame(),
                        "Document file missing delimiter: $#$#");
                     }
                     updateText();
                     importedScript = new scriptObject(document,infoText,scriptFile.getName());
                    
	             importedScript.setStringFilePath(defaultExportDFPath);
                     importedScript.setGraphicsPath(defaultExportGFXPath);
                     importedScript.setSlateTextPath(defaultSlateTextFilePath);
                     importedScript.setBackUpPath(backUpPath);
                     importedScript.setSubLogoFolder(defaultSubLogoFolder);
                     importedScriptArray.clear();
                   }catch(IOException f){

                   }
                }
               } else {
                  System.out.println("Open command cancelled by user.");
               }
		   }
	}
   
   class compileListener implements ActionListener{
      public void actionPerformed(ActionEvent e)
      {
         updateText();
         marketLogoPaths.clear();
         JFileChooser outPath = new JFileChooser(defaultImportPath);
	 String textForScript = null;
	 String spacer = "=================================================================\n";
         outPath.setDialogTitle("Select Directory to Compile");
   		outPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
   		int returnVal = outPath.showSaveDialog(new JFrame());
         if(returnVal == JFileChooser.APPROVE_OPTION)
         {
            importedScriptArray.clear();
   			File directorySelected = outPath.getSelectedFile();
            compilationDirectory = directorySelected.getAbsolutePath();
				File[] allFiles = directorySelected.listFiles();
				for(int i = 0;i < allFiles.length;i++)
				{
					File f = allFiles[i];
					if(f.isFile())
					{
						if(f.getName().endsWith(".doc") || f.getName().endsWith(".txt"))
                   {
                     System.out.println("Import contains .doc");
                      try{
                        String document = "";
                        if(f.getName().endsWith(".doc")){
                           WordExtractor inWord = new WordExtractor(new FileInputStream(f));
                           document = inWord.getText();
                        }else{
                           FileInputStream fis = new FileInputStream(f);
                           int content;
                  			while ((content = fis.read()) != -1) {
                              char select = (char)content;
                              document = " " + document + select;
                  			}
                           System.out.println(document);
                           fis.close();
                           
                        }
                                             
                        try{
                           document = document.substring(document.indexOf("$#$#"));
                        }catch(Exception g)
                        {
                           JOptionPane.showMessageDialog(new JFrame(),
                           "Document file missing delimiter: $#$#");
                        }
                        
                        scriptObject scriptToAdd = new scriptObject(document,infoText,f.getName());
         					scriptToAdd.setStringFilePath(defaultExportDFPath);
                        scriptToAdd.setGraphicsPath(defaultExportGFXPath);
                        scriptToAdd.setSlateTextPath(defaultSlateTextFilePath);
                        scriptToAdd.setBackUpPath(backUpPath);
                        scriptToAdd.setSubLogoFolder(defaultSubLogoFolder);
                        importedScriptArray.add(scriptToAdd);
                        System.out.println("Script object is: " + scriptToAdd.toString());
                      }catch(IOException g)
                      {
                        JOptionPane.showMessageDialog(imageRenamer.this, "Error Compiling");
                      }
                   }
                  } else {
                     System.out.println("Open command cancelled by user.");
                  }
                  importedScript = null;
				}	
         }
      } 
   }
   //##edit ##notepad - This listener opens the imported scriptfile with system extension default
	class editListener implements ActionListener
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
	
	public class exitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			System.exit(0);
		}
	}
   
   //##datafile ##df This listener creates a datafile by parsing the imported script
	public class createDFListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
		   if(importedScript != null)
         {
            importedScript.generateDataFile();
         }else if(importedScriptArray.size() > 0){
            for(int i = 0;i < importedScriptArray.size();i++)
            {
               scriptObject script = importedScriptArray.get(i);
               script.generateDataFile();
            }
         }else{
            JOptionPane.showMessageDialog(imageRenamer.this, "There is no file imported");
         }
		}
	}
   
   public class createVOListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if(scriptFile != null)
         {
            if(importedScript != null)
            {
               importedScript.generateVoiceCaptions();
            }
         }else{
            JOptionPane.showMessageDialog(imageRenamer.this, "There is no file imported");
         }
      }
   }
   
   class createTextListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if(importedScript != null)
         {
            importedScript.generateSlates();
         }else if(importedScriptArray.size() > 0){
            int counter = 0;
            int returnVal = -50;
            JFileChooser outPath = new JFileChooser(defaultSlateTextFilePath);
      		outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
            	outPath.setDialogTitle("Select Textfile Location for compilation:");
      		returnVal = outPath.showOpenDialog(new JFrame());
               
            for(int i = 0;i < importedScriptArray.size();i++)
            {
         		if(returnVal == JFileChooser.APPROVE_OPTION){
                  File slatePath = outPath.getSelectedFile();
         			String outString = slatePath.getPath();
                  counter = importedScriptArray.get(i).generateSlates(counter,outString);
               }else{
                  JOptionPane.showMessageDialog(imageRenamer.this, "Canceling Script to Text.");
               }
            }
         }else{
            JOptionPane.showMessageDialog(imageRenamer.this, "There is no file imported");
         }
      }
   }
   
	class beginListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
   		// Get the selected market and convert to a string, then branch directions
   		if(importedScript != null)
         {
            String selectedMarket = (String)marketsLists.getSelectedValue().toString();
      		
      		infoText.setBody("");
      		targetLocation = new File(defaultExportGFXPath + selectedMarket + "\\");
      			
      		sourceLoc = defaultLogosPath + selectedMarket + "\\" + defaultSubLogoFolder + "\\";
      
      		targetLocation = new File(defaultLogosPath + selectedMarket + "\\" + scriptFile.getName() + "\\");
      		System.out.println(targetLocation);
      		
            	updateText();	     
            	importedScript.renameImages(selectedMarket,defaultLogosPath);
         }else if(importedScriptArray.size() > 0){
         
            ArrayList<String> marketBuilder = new ArrayList<String>();
            infoText.setBody("");
            for(int i = 0;i < importedScriptArray.size();i++)
            {
               int returnVal = -50;

               
               if(marketLogoPaths.size() == 0)
               {
                  JFileChooser outPath = new JFileChooser(defaultLogosPath);
                  outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
                  outPath.setDialogTitle("Select Logo Location for " + importedScriptArray.get(i));
		  returnVal = outPath.showSaveDialog(new JFrame());
               
            	  if(returnVal == JFileChooser.APPROVE_OPTION){
                     String outString = outPath.getSelectedFile().getAbsolutePath();
                     String selectedMarket = outString.substring(outString.lastIndexOf("\\") + 1);
                     marketBuilder.add(selectedMarket);
                     System.out.println("Outstring is : " + selectedMarket);
                     importedScriptArray.get(i).renameImages(selectedMarket,defaultLogosPath);
                  }
              }else{
               importedScriptArray.get(i).renameImages(marketLogoPaths.get(i),defaultLogosPath);
              }
            }
            if(marketLogoPaths.size() == 0)
            {
               marketLogoPaths = marketBuilder;
            }
         }else{
            JOptionPane.showMessageDialog(imageRenamer.this, "There is no file imported");
         }
   		
		}
	}
   public void updateScriptFile()
   {
      if(scriptFile != null)
      {
         try{
            WordExtractor inWord = new WordExtractor(new FileInputStream(scriptFile));
            String document = inWord.getText();
                                 
            try{
               document = document.substring(document.indexOf("$#$#"));
            }catch(Exception f)
            {
               JOptionPane.showMessageDialog(new JFrame(),
               "Document file missing delimiter: $#$#");
            }
            importedScript = new scriptObject(document,infoText,scriptFile.getName());
            importedScript.setStringFilePath(defaultExportDFPath);
            importedScript.setGraphicsPath(defaultExportGFXPath);
            importedScript.setSlateTextPath(defaultSlateTextFilePath);
            importedScript.setBackUpPath(backUpPath);
            importedScript.setSubLogoFolder(defaultSubLogoFolder);
	    infoText.append("<p>Reimported : " + scriptFile.getName() + "</p>");
          }catch(IOException f)
          {
          
          }
      }else if(importedScriptArray.size() > 0 && compilationDirectory != null){
         updateText();
   		String textForScript = null;
   		String spacer = "=================================================================\n";
			File directorySelected = new File(compilationDirectory);
         		compilationDirectory = directorySelected.getAbsolutePath();
			File[] allFiles = directorySelected.listFiles();
        		importedScriptArray.clear();
			for(int i = 0;i < allFiles.length;i++)
			{
				File f = allFiles[i];
				if(f.isFile())
				{
					if(f.getName().endsWith(".doc"))
                {
                  System.out.println("Import contains .doc");
                   try{
                     WordExtractor inWord = new WordExtractor(new FileInputStream(f));
                     String document = inWord.getText();
                                          
                     try{
                        document = document.substring(document.indexOf("$#$#"));
                     }catch(Exception g)
                     {
                        JOptionPane.showMessageDialog(new JFrame(),
                        "Document file missing delimiter: $#$#");
                     }
                     
                     scriptObject scriptToAdd = new scriptObject(document,infoText,f.getName());
		     scriptToAdd.setStringFilePath(defaultExportDFPath);
                     scriptToAdd.setGraphicsPath(defaultExportGFXPath);
                     scriptToAdd.setSlateTextPath(defaultSlateTextFilePath);
                     scriptToAdd.setBackUpPath(backUpPath);
                     scriptToAdd.setSubLogoFolder(defaultSubLogoFolder);
                     importedScriptArray.add(scriptToAdd);
                     System.out.println("Script object is: " + scriptToAdd.toString());
                   }catch(IOException g)
                   {
                     JOptionPane.showMessageDialog(imageRenamer.this, "Error Compiling");
                   }
                }
               } else {
                  System.out.println("Open command cancelled by user.");
               }
               importedScript = null;
        }
      }else{
         JOptionPane.showMessageDialog(new JFrame(),
               "No File imported");
      }
	}
   
   class filePathSettingsPane extends JPanel
   {
      private String importPath;
      private String exportDFPath;
      private String exportGFXPath;
      private String logosPath;
      private String panelBackUpPath;
      private String subLogoFolder;
      private String slateTextFilePath;
      
      private JTextField importPathField;
      private JTextField exportDFPathField;
      private JTextField exportGFXPathField;
      private JTextField logosPathField;
      private JTextField panelBackUpPathField;
      private JTextField subFolderLogoField;
      private JTextField slateTextFilePathField;
      
      private JFrame parent;
      
      filePathSettingsPane(JFrame parentFrame)
      {
         Toolkit toolkit =  Toolkit.getDefaultToolkit ();
	 Dimension dim = toolkit.getScreenSize();
         
         JLabel label = new JLabel("IMPORT PATH: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
 
         importPath = defaultImportPath;
         exportDFPath = defaultExportDFPath;
         exportGFXPath = defaultExportGFXPath;
         logosPath = defaultLogosPath;
         panelBackUpPath = backUpPath;
         subLogoFolder = defaultSubLogoFolder;
         slateTextFilePath = defaultSlateTextFilePath;
         
         importPathField = new JTextField(importPath);
         exportDFPathField = new JTextField();
         exportGFXPathField = new JTextField();
         logosPathField = new JTextField();
         panelBackUpPathField = new JTextField();
         subFolderLogoField = new JTextField();
         slateTextFilePathField = new JTextField();
         
         parent = parentFrame;
         
         setLayout(new GridBagLayout());
	 GridBagConstraints c = new GridBagConstraints();


	 //--------------------------------------------- Initial Settings for Gridbag layout
	 c.fill = GridBagConstraints.NONE;
	 c.anchor = GridBagConstraints.LINE_START;
         c.insets = new Insets(20,20,20,20);  //top padding
         //---------------------------------------------
         
         //--------------------------------------------- First Line of GridBag
	 c.gridx = 0;
	 c.gridy = 0;
	 c.weightx = 0.1;
         c.weighty = .5;
         c.gridwidth = 1;
         c.gridheight = 1;
	 add(label,c);
         
         c.gridx = 1;
         c.gridy = 0;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         add(importPathField,c);
         System.out.println(importPathField.getText());
         //---------------------------------------------- End first line
         
         // Definition label for user
         label = new JLabel("This should be the root path of where script .doc files are located");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 1;
         c.weightx = 0.0;
         c.weighty = .01;
         add(label,c);         
         //---------------------------------------------- Second Line of GridBag
         c.gridx = 0;
	 c.gridy = 2;
	 c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Datafile Export Path: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 2;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         exportDFPathField.setText(exportDFPath);
         add(exportDFPathField,c);
         //---------------------------------------------- End Second Line of GridBag
         
         // Definition label for user
         label = new JLabel("This should be the root path of where datafiles are saved");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 3;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
         //---------------------------------------------- Third Line of GridBag
         c.gridx = 0;
	 c.gridy = 4;
	 c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Graphics Path: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 4;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         exportGFXPathField.setText(exportGFXPath);
         add(exportGFXPathField,c);
         //---------------------------------------------- End Third Line of GridBag
         
         // Definition label for user
         label = new JLabel("This should be the root path of where graphics are saved");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 5;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
          //---------------------------------------------- Fourth Line of GridBag
         c.gridx = 0;
	 c.gridy = 6;
	 c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Logos Path: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 6;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         logosPathField.setText(logosPath);
         add(logosPathField,c);
         //---------------------------------------------- End Fourth Line of GridBag
         
         // Definition label for user
         label = new JLabel("This should be the root path of where logos are stored");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 7;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
          //---------------------------------------------- Fifth Line of GridBag
         c.gridx = 0;
			c.gridy = 8;
			c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Backups Path: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 8;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         panelBackUpPathField.setText(panelBackUpPath);
         add(panelBackUpPathField,c);
         //---------------------------------------------- End Fifth Line of GridBag
         
         // Definition label for user
         label = new JLabel("This should be the root path of where backups are stored");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 9;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
         //---------------------------------------------- Sixth Line of GridBag
         c.gridx = 0;
	 c.gridy = 10;
	 c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Sub Logo Folder: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 10;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         subFolderLogoField.setText(subLogoFolder);
         add(subFolderLogoField,c);
         //---------------------------------------------- End Sixth Line of GridBag
         
          // Definition label for user
         label = new JLabel("This should be the folder where gifs are kepts in the specific market logo directory");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 11;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
         //---------------------------------------------- Seventh Line of GridBag
         c.gridx = 0;
	 c.gridy = 12;
	 c.weightx = 0.1;
         c.weighty = 0.1;
         c.gridwidth = 1;
         c.gridheight = 1;
         c.fill = GridBagConstraints.HORIZONTAL;
         label = new JLabel("Slate Text Root Folder: ");
         label.setFont(new Font("Helvetica", Font.BOLD, 14));
			
         add(label,c);
         
         c.gridx = 1;
         c.gridy = 12;
         c.weightx = .75;
         c.fill = GridBagConstraints.HORIZONTAL;
         slateTextFilePathField.setText(slateTextFilePath);
         add(slateTextFilePathField,c);
         //---------------------------------------------- End Seventh Line of GridBag
         
          // Definition label for user
         label = new JLabel("This should be the root foler where slate texts are saved");
         label.setFont(new Font("Helvetica", Font.BOLD, 12));
         c.gridwidth = 2;
         c.gridheight = 1;
         c.gridx = 0;
         c.gridy = 13;
         c.weightx = 0.0;
         c.weighty = 0.1;
         add(label,c);
         
         // Save and Cancel Buttons
         JButton saveButton = new JButton("Save");
         JButton cancelButton = new JButton("Cancel");
         
         c.gridwidth = 1;
         c.gridheight = 2;
         c.gridx = 0;
         c.gridy = 14;
         c.weightx = .5;
         c.weighty = .1;
         
         saveButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
               saveClicked();
            }
         });
         add(saveButton,c);
         
         c.gridx = 1;
         c.gridy = 14;
         
         cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
               parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));;
            }
         });
         add(cancelButton,c);
      
      }
      
      void saveClicked(){
         defaultImportPath = importPathField.getText().trim();
         defaultExportDFPath = exportDFPathField.getText().trim();
         defaultExportGFXPath = exportGFXPathField.getText().trim();
         defaultLogosPath = logosPathField.getText().trim();
         backUpPath = panelBackUpPathField.getText().trim();
         defaultSubLogoFolder = subFolderLogoField.getText().trim();
         
         try{
            BufferedWriter out = new BufferedWriter(new FileWriter(".\\configure\\filePaths.txt"));
            out.write(defaultImportPath + "\n");
            out.write(defaultExportDFPath + "\n");
            out.write(defaultExportGFXPath + "\n");
            out.write(defaultLogosPath + "\n");
            out.write(backUpPath + "\n");
            out.write(defaultSubLogoFolder + "\n");
            out.close();
            createMarketsLists();
         }catch(IOException e){
            JOptionPane.showMessageDialog(parent,
         "Failed to write configuration save file.");
         }
         
         parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));;
      }
   }
	
	public class BackgroundMenuBar extends JMenuBar
	{
		Color bgColor=dmcRed;

		public void setColor(Color color)
		{
			bgColor=color;
 		}

 		@Override
 		protected void paintComponent(Graphics g)
 		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			//g2d.setColor(bgColor);
			GradientPaint gp = new GradientPaint( 0, 0,Color.white, 0, getHeight() / 2 + getHeight()/4,dmcBlue,true);
			g2d.setPaint( gp ); //set gradient color to graphics2D object

			g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

			setOpaque( false );
 		}
	}
	
	class backgroundJButton extends JButton
	{
		Color bgColor= dmcGray;
		
		public void setColor(Color color)
		{
			bgColor = color;
		}
		
		private backgroundJButton(String sIn){
			super(sIn);
			System.out.println(getFont().toString());
			setFont(JButtonFont);
      			setContentAreaFilled(false);
      			setFocusPainted(false); // used for demonstration
         		Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED,dmcGray,dmcLightGray);
         		setBorder(border);
 	 	}

    	@Override
     	protected void paintComponent(Graphics g){
      	GradientPaint gp = new GradientPaint( 0, 0,dmcLightGray, 0, getHeight(),Color.white);
			Graphics2D g2 = (Graphics2D)g.create();
         if(getModel().isPressed() == false && getModel().isRollover() == false){
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
         }else if(getModel().isPressed()){
            gp = new GradientPaint(0,0,dmcLightGray.darker(),0,getHeight(),Color.white.darker());
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
         }else if(getModel().isRollover()){
            gp = new GradientPaint(0,0,dmcLightGray.brighter(),0,getHeight(),Color.white);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
         }

         super.paintComponent(g);
     }
	}
   //---------------------------------------------------------------END INNER CLASSES
}
