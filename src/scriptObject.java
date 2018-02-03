import java.util.*;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.*;

public class scriptObject
{  
   private ArrayList<String> showCategoryOrder;
   private ArrayList<String> showCategoryNumOrder;
   private ArrayList<String> showCategoryAdNumOrder;
   
   private ArrayList<adObject> scriptAds;
   
   private boolean isViewImagable;
   
   private dmcEditorPane infoText;
   private int numberOfAds;

   private String backUpPath;
   private String slateTextPath;
   private String docDelimiter;
   private String market;
   private String stringFilePath;
   private String importFileName;
   private String graphicsPath; 
   private String subLogoFolder;
   
   
   // These are the tab distances for sluglines and datafile columns
   private final String TAB = "\t";
   private final String GIVEN_LOGO = "logo:";
   private final int VOICEOVER_MIN_LENGTH = 100;
   private final int AD_NUM_TAB_OFFSET = 3;
   private final int COMPANY_NAME_TAB_OFFSET = 2;
   private final int VO_DATAFILE_COLUMN = 19;
   private final int SWIFT_SUBCATEGORY = 20;
   
   public scriptObject(String documentIn, dmcEditorPane infoTextIn,String importFileNameIn)
   {
      // Initialize variables
      isViewImagable = false;
      showCategoryOrder = new ArrayList<String>();
      showCategoryNumOrder = new ArrayList<String>();
      showCategoryAdNumOrder = new ArrayList<String>();
      scriptAds = new ArrayList<adObject>();
      
      numberOfAds = 0;
      infoText = infoTextIn;
      
      docDelimiter = "$#$#";
      importFileName = importFileNameIn; 

      
      // These variables are necessary for Show Logo renaming
      String category = "";
      String categoryNum = "01";
      int currentAdNum = 1;
      
      while(documentIn.contains("\n"))
      {         
         String currentLine = documentIn.substring(0,documentIn.indexOf("\n") + 1);
         documentIn = documentIn.substring(documentIn.indexOf("\n") + 1);
         
         String scan = categoryScan(currentLine);
         
         // This block checks to see if the market has categories and assigns initial values
         if(scan.equals("unassigned") == false)
			{
				category = scan;
				categoryNum = "01";
				currentAdNum = 0;
			}
         
         if(currentLine.contains(TAB) && currentLine.length() > 0 && countTabs(currentLine) >= 5){
            
            if(currentLine.toLowerCase().contains("uploaded") == false)
            {
               System.out.println(currentLine);
               JOptionPane.showMessageDialog(new JFrame(), "Uploaded Not in ad# " + (numberOfAds + 1));
            }
            
            adObject currentAd = new adObject();
            
            // Set up the variables for the ad loop
            numberOfAds++;
            currentAdNum++;                     //currentAdNum is used to track show folder location
            currentAd.setccNumber(numberOfAds);
            
            // This section isolates the slugline and stores it in currentLine
            System.out.println(currentLine + "\n contains : " + countTabs(currentLine) + " tabs");
            currentLine = removeURLInfo(currentLine);
            
            currentAd.setSlugLine(currentLine);
            
            String currentSlate = "";
            String currentAdNumber = "";
            String currentCompanyName = "";
            
            // Substring to the line after the slug and parse to slate. documentIn.nextAd() essentially.
            currentLine = documentIn.substring(0,documentIn.indexOf("\n") + 1);
            documentIn = documentIn.substring(documentIn.indexOf("\n") + 1);
            
            while(currentLine.length() < 1)
            {
               currentLine = documentIn.substring(0,documentIn.indexOf("\n") + 1);
               documentIn = documentIn.substring(documentIn.indexOf("\n") + 1);
            }
            
            // Format slate for output. These parameters isolate slate lines before voice over text.
            while(currentLine.length() > 0 && currentLine.length() < VOICEOVER_MIN_LENGTH)
            {
               if(currentLine.toLowerCase().contains(GIVEN_LOGO))
               {
                  if(currentLine.contains(" "))
                  {
                     System.out.println("Adding logo link: " + currentLine);
                     currentLine.replace("\n","");
                     currentAd.addLogoLink(removeURLInfo(currentLine.substring(currentLine.indexOf(" ") + 1)));
                  }
               }
               
               String slateLine = currentLine.substring(0,currentLine.indexOf("\n"));
               
               // remove url meta text
               slateLine = removeURLInfo(slateLine);
               
               if(slateLine.length() > 1 && slateLine.toLowerCase().contains("eoe") == false &&
                  slateLine.toLowerCase().contains("logo") == false){
                  System.out.println("Adding slateline: " + slateLine);
                  currentSlate = currentSlate + slateLine + "\n";
               }
               
               currentLine = documentIn.substring(0,documentIn.indexOf("\n") + 1);
               documentIn = documentIn.substring(documentIn.indexOf("\n") + 1);
            }
            currentSlate = currentSlate.trim();                            // A little trim to tidy up loose ends
            currentAd.setSlate(currentSlate);
            
            
            // This parses the distance between slate and vo. Voice over text is always a single line with over
            // 100 characters.
            while(currentLine.length() < VOICEOVER_MIN_LENGTH)
            {
               currentLine = documentIn.substring(0,documentIn.indexOf("\n") + 1);
               documentIn = documentIn.substring(documentIn.indexOf("\n") + 1);
            }

            currentAd.setVoiceOver(removeURLInfo(currentLine));
            scriptAds.add(currentAd);
            
            // This next block only pertains to show specific logos
            if(currentAdNum > 10)
				{
					categoryNum = "02";
				}
				if(currentAdNum > 20)
				{
					categoryNum = "03";
				}
            
            System.out.println("Category : " + category + " in categoryNum: " + categoryNum);
            
            // Increment the showCategory numbers to match file directory
            showCategoryOrder.add(category);
				showCategoryNumOrder.add(categoryNum);
				showCategoryAdNumOrder.add(Integer.toString(currentAdNum));
            
         }
      }
      formatCompanyNames();
      for(int i = 0;i < scriptAds.size();i++)
      {
			infoText.append("<p>");
         infoText.append(scriptAds.get(i).toString());
			infoText.append("</p>");
      }
      checkForRepeats();
		printAdErrors();
      //infoText.insertComponent(new ImageViewer());
   }
  
  
   // the categoryScan method takes in a string from a script and checks to see if it's a category header
   // if it is, it is changed to the relevant category. Only concerns Springfield and Peoria 
   public String categoryScan(String sIn)
	{
		String goBack = "unassigned";
		
		if(sIn.contains("PROFESSIONAL CATEGORY") || sIn.contains("PROFESSIONAL/MANAGEMENT CATEGORY"))
		{
			goBack = "PROFESSIONAL";
		} else if(sIn.contains("HEALTHCARE CATEGORY") || sIn.contains("HEALTH CARE/NURSING CATEGORY"))
		{
			goBack = "HEALTHCARE";
		} else if(sIn.contains("SALES / MARKETING CATEGORY") || sIn.contains("SALES/MARKETING CATEGORY"))
		{
			goBack = "SALES";
		}else if(sIn.contains("SKILLS/TRADES CATEGORY") || sIn.contains("SKILLED TRADES CATEGORY"))
		{
			goBack = "SKILLS TRADES";
		}else if(sIn.contains("GENERAL CATEGORY"))
		{
			goBack = "GENERAL";
		}
		
		
		return goBack;
	}
   
   public void checkForRepeats()
   {
      // Check for repeat ad numbers. This structure doesn't check numbers that have already been checked against
      for(int i = 0;i < scriptAds.size();i++)
      {
         for(int j = i;j < scriptAds.size();j++)
         {
            if(j + 1 >= scriptAds.size() == false)
            { 
               if(scriptAds.get(i).getAdNumber().toLowerCase().equals(scriptAds.get(j + 1).getAdNumber().toLowerCase()))
               {
                  // i + 1 and j + 2 are used to offset counting by 0
                  infoText.append("<p><strong><font size=\"4\">CC Num " + (scriptAds.get(i).getccNumber()) + " : " +
                                  scriptAds.get(i).getAdNumber() + " is a repeat with CC# " + (j + 2) + "\n" + importFileName + "</font></strong></p>");
               }
            }
         }
      }
   }
   
   // overloaded to check for back up folder repeats
   public void checkForRepeats(String selectedMarket)
   {
      System.out.println(backUpPath + selectedMarket);
   	File f = new File(backUpPath + selectedMarket);
   	ArrayList<File> fileContents = new ArrayList<File>(Arrays.asList(f.listFiles()));
   	
      for(int i = 0;i< scriptAds.size();i++)
   	{
         String currentFileName;
         long modified;
         Date modifiedDate;
         
         for(int j = 0;j < fileContents.size();j++)
         {
            currentFileName = fileContents.get(j).getName();
            
            if(currentFileName.contains("."))
            {
               currentFileName = currentFileName.substring(0,currentFileName.lastIndexOf("."));
            }
            if(scriptAds.get(i).getAdNumber().toLowerCase().equals(currentFileName.toLowerCase()))
            {
               modified = fileContents.get(j).lastModified();
               modifiedDate = new Date(modified);
              
               String dateToPrint = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(modifiedDate);
               dateToPrint = dateToPrint.substring(0,dateToPrint.indexOf(" "));
               
               infoText.append("<p>CC# " + scriptAds.get(i).getccNumber() 
                                 + " matches a backup of last modified : " + scriptAds.get(i).getAdNumber() 
                                 + " " + dateToPrint + "\nin " + importFileName + "</p>");
            }
         }
      }
   }
   
   public void formatCompanyNames()
   {
      for(int i = 0;i < scriptAds.size();i++)
      {
         scriptAds.get(i).setCompanyName(removeCharacters(scriptAds.get(i).getCompanyName().toLowerCase()));
      }
   }
   
   public void generateDataFile()
   {
      int returnVal = -50;
      
      if(importFileName.toLowerCase().contains("swift"))
      {
         generateSwiftDatafile();
         return;
      }
      
		JFileChooser outPath = new JFileChooser(stringFilePath);
      outPath.setDialogTitle("Select Datafile Location for " + importFileName);
		returnVal = outPath.showSaveDialog(new JFrame());
      
		if(returnVal == JFileChooser.APPROVE_OPTION){
      {
         try{
   			HSSFWorkbook wb = new HSSFWorkbook();
   			HSSFSheet sheet = wb.createSheet();
   		   HSSFRow row;
   		   HSSFCell cell;
   			CreationHelper createHelper = wb.getCreationHelper();
   			CellStyle cellStyle = wb.createCellStyle();	
   			cellStyle.setDataFormat(
   	  						createHelper.createDataFormat().getFormat("M/D/YY"));
   			infoText.append("<p><strong>DataFormater Constructed, Preparing Excel Write</strong></p>");				
   			for(int i = 0;i < scriptAds.size();i++)
   			{
   				row = sheet.createRow(i);
   				int colCount = 0;
   				//String cellCut = scriptAds.get(i).getSlugLineOriginal();
   				ArrayList<String> adRow = scriptAds.get(i).getSlugLine();
   				for(int j = 0;j < adRow.size();j++)
   				{
   					cell = row.createCell(colCount);
   					String cellInput = adRow.get(j);
   					System.out.println("Cell input: " + cellInput);
   					cellInput = cellInput.trim();
   					
   					if(colCount == 2 || colCount == 3)
   					{
                     try{
   						   cell.setCellValue(new Date(cellInput));
   						   cell.setCellStyle(cellStyle);
                        cell.setCellValue(cellInput);
                     }catch(Exception e){
                        System.out.println("Error parsing ad date in ad# : " + i + " " + cellInput);
                        infoText.append("<p>Error parsing ad date in ad#: " + i + "\nin " + importFileName + "</p>"); 
                     } 
   					}else{
   						cell.setCellValue(cellInput);
   					}
   					colCount++;
   				}
               cell = row.createCell(VO_DATAFILE_COLUMN);
               cell.setCellValue(scriptAds.get(i).getVoiceOver());
   			}
            
            FileOutputStream out = null;
            
            if(outPath.getSelectedFile().getAbsolutePath().endsWith(".xls") == false)
            {
   			   out = new FileOutputStream(outPath.getSelectedFile().getAbsolutePath() + ".xls");
            }else{
               out = new FileOutputStream(outPath.getSelectedFile().getAbsolutePath());
            }
   			wb.write(out);
            out.close();
   			infoText.append("<p><strong>Data File for " + importFileName + " Created Successfully!</strong></p>");
   		 }catch(IOException f){
   		 	JOptionPane.showMessageDialog(new JFrame(), "There is no file imported");
   		 }
        }
      }
   }
   
   public void generateSlates()
   {
      int returnVal = -50;
		JFileChooser outPath = new JFileChooser(slateTextPath);
		outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
        outPath.setDialogTitle("Select Textfile Location for " + importFileName);
		returnVal = outPath.showOpenDialog(new JFrame());
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			
         File slatePath = outPath.getSelectedFile();
			String outString = slatePath.getPath();
			BufferedWriter out;
			
			for(int i = 0;i < scriptAds.size();i++)
			{
				try{
					if(i<9)
					{
						out = new BufferedWriter(new FileWriter(outString + "\\0" + (i + 1) + ".txt"));
					}else{
						out = new BufferedWriter(new FileWriter(outString + "\\" + (i + 1) + ".txt"));
					}
               System.out.println("Writing slate #: " + i + " \n" + scriptAds.get(i).getSlate() + "\n");
               out.write(scriptAds.get(i).getSlate());
               out.close();
				}catch(IOException e){
				}
			}
         infoText.append("<strong><p>" + scriptAds.size() + " slates created for" + importFileName + "</p></strong>");
         
		}
   
   }
   
   public int generateSlates(int start, String outString)
   {
      BufferedWriter out;
		for(int i = 0;i < scriptAds.size();i++)
		{
			try{
				if(start<9)
				{
					out = new BufferedWriter(new FileWriter(outString + "\\0" + (start + 1) + ".txt"));
				}else{
					out = new BufferedWriter(new FileWriter(outString + "\\" + (start + 1) + ".txt"));
				}
            System.out.println("Writing slate #: " + start + " \n" + scriptAds.get(i).getSlate() + "\n");
            out.write(scriptAds.get(i).getSlate());
            out.close();
			}catch(IOException e){
			}
         start++;
		}
      infoText.append("<strong><p>" + scriptAds.size() + " slates created for" + importFileName + "</p></strong>");
      return start;
   
   }
   
   public void generateVoiceCaptions()
   {
      int returnVal = -50;
		JFileChooser outPath = new JFileChooser(slateTextPath);
		outPath.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);

		returnVal = outPath.showOpenDialog(new JFrame());
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			
         File slatePath = outPath.getSelectedFile();
			String outString = slatePath.getPath();
			BufferedWriter out;
			
			for(int i = 0;i < scriptAds.size();i++)
			{
				try{
					if(i<9)
					{
						out = new BufferedWriter(new FileWriter(outString + "\\" + scriptAds.get(i).getAdNumber() + ".txt"));
					}else{
						out = new BufferedWriter(new FileWriter(outString + "\\" + scriptAds.get(i).getAdNumber() + ".txt"));
					}
               System.out.println("Writing slate #: " + i + " \n" + scriptAds.get(i).getSlate() + "\n");
               out.write(scriptAds.get(i).getVoiceOver());
               out.close();
				}catch(IOException e){
				}
			}
         infoText.append("<strong><p>" + scriptAds.size() + " VO captions created" + "</p></strong>");
         
		}
   }
   
   public void generateSwiftDatafile()
   {
      ArrayList<HSSFWorkbook> outBooks = new ArrayList<HSSFWorkbook>();
      HSSFWorkbook wbCO1 = new HSSFWorkbook();
      HSSFWorkbook wbCO2 = new HSSFWorkbook();
      HSSFWorkbook wbCO3 = new HSSFWorkbook();
      HSSFWorkbook wbCO4 = new HSSFWorkbook();
      HSSFWorkbook wbCO5 = new HSSFWorkbook();
      HSSFWorkbook wbNV = new HSSFWorkbook();
      HSSFWorkbook wbNCCG = new HSSFWorkbook();
      HSSFWorkbook wbNCPC = new HSSFWorkbook();
      
      ArrayList<HSSFWorkbook> allBooks = new ArrayList<HSSFWorkbook>();
      allBooks.add(wbCO1);
      allBooks.add(wbCO2);
      allBooks.add(wbCO3);
      allBooks.add(wbCO4);
      allBooks.add(wbCO5);
      allBooks.add(wbNV);
      allBooks.add(wbNCCG);
      allBooks.add(wbNCPC);
      
      HSSFSheet shCO1 = wbCO1.createSheet();
      HSSFSheet shCO2 = wbCO2.createSheet();
      HSSFSheet shCO3 = wbCO3.createSheet();
      HSSFSheet shCO4 = wbCO4.createSheet();
      HSSFSheet shCO5 = wbCO5.createSheet();
      HSSFSheet shNV = wbNV.createSheet();
      HSSFSheet shNCCG = wbNCCG.createSheet(); 
      HSSFSheet shNCPC = wbNCPC.createSheet(); 
      
      int returnVal = -50;
      CellStyle cellStyle = null;
      
		infoText.append("<p><strong>DataFormater Constructed, Preparing Excel Write</strong></p>");		
      
		JFileChooser outPath = new JFileChooser(stringFilePath);
      outPath.setDialogTitle("Select Datafile Save Location & Filename");
		returnVal = outPath.showSaveDialog(new JFrame());
      
		if(returnVal == JFileChooser.APPROVE_OPTION){
         try{
   		   HSSFRow row = null;
   		   HSSFCell cell = null;
            			
   			for(int i = 0;i < scriptAds.size();i++){
   				int colCount = 0;
   				ArrayList<String> adRow = scriptAds.get(i).getSlugLine();
               String subcategories = "";
               ArrayList<HSSFSheet> categorySheets = new ArrayList<HSSFSheet>();
               
               if(SWIFT_SUBCATEGORY >= adRow.size())
               {
                  JOptionPane.showMessageDialog(new JFrame(), "Swift Column Error in Ad: " + (i + 1) + "\nNot Enough Tabs" +
                                                (adRow.size()) + "OUT OF " + SWIFT_SUBCATEGORY + "\n Datafile Write failed.");
                  break;
               }else{
                  subcategories = adRow.get(SWIFT_SUBCATEGORY);
               }
               
               if(subcategories.contains("1"))
               {
                  categorySheets.add(shCO1);
               }
               if(subcategories.contains("2"))
               {
                  categorySheets.add(shCO2);
               }
               if(subcategories.contains("3"))
               {
                  categorySheets.add(shCO3);
               }
               if(subcategories.contains("4"))
               {
                  categorySheets.add(shCO4);
               }
               if(subcategories.contains("5"))
               {
                  categorySheets.add(shCO5);
               }
               if(subcategories.toLowerCase().contains("nv"))
               {
                  categorySheets.add(shNV);
               }
               if(subcategories.toLowerCase().contains("nccg"))
               {
                  categorySheets.add(shNCCG);
               }
               if(subcategories.toLowerCase().contains("ncpc"))
               {
                  categorySheets.add(shNCPC);
               }
               
               for(int k = 0;k < categorySheets.size();k++){
                  
                  row = categorySheets.get(k).createRow(categorySheets.get(k).getLastRowNum() + 1);
      				
                  HSSFWorkbook wb = categorySheets.get(k).getWorkbook();
                  
                  CreationHelper createHelper = wb.getCreationHelper();
            		cellStyle = wb.createCellStyle();	
            		cellStyle.setDataFormat(
              						createHelper.createDataFormat().getFormat("M/D/YY"));
                  for(int j = 0;j < adRow.size();j++)
      				{
      					cell = row.createCell(colCount);
      					String cellInput = adRow.get(j);
      					cellInput = cellInput.trim();
      					
      					if(colCount == 2 || colCount == 3)
      					{
                        try{
      						   cell.setCellValue(new Date(cellInput));
      						   cell.setCellStyle(cellStyle);
                           cell.setCellValue(cellInput);
                        }catch(Exception e){
                           System.out.println("Error parsing ad date in ad# : " + i + " " + cellInput);
                           infoText.append("<p>Error parsing ad date in ad#: " + i + "\nin " + importFileName + "</p>"); 
                        } 
      					}else{
      						cell.setCellValue(cellInput);
      					}
      					colCount++;
                   }
                   colCount = 0;
                 }
               if(cell != null)
               {
                  cell = row.createCell(VO_DATAFILE_COLUMN);
                  cell.setCellValue(scriptAds.get(i).getVoiceOver());
               }
            }
            FileOutputStream out = null;
            HSSFWorkbook wb = null;
            
            
            for(HSSFWorkbook currentBook : allBooks)
            {
               
               HSSFSheet firstSheet = currentBook.getSheetAt(0);
               firstSheet.shiftRows(1,firstSheet.getLastRowNum() + 1,-1);
            } 
            
            for(int j = 1;j <= allBooks.size();j++)
            {
               String fileEnder = "";
               if(j == 1){
                  fileEnder = "CO1";
                  wb = wbCO1;
               }else if(j == 2){
                  fileEnder = "CO2";
                  wb = wbCO2;
               }else if(j == 3){
                  fileEnder = "CO3";
                  wb = wbCO3;
               }else if(j == 4){
                  fileEnder = "CO4";
                  wb = wbCO4;
               }else if(j == 5){
                  fileEnder = "CO5";
                  wb = wbCO5;
               }else if (j == 6){
                  fileEnder = "nv";
                  wb = wbNV;
               }else if (j == 7){
                  fileEnder = "nccg";
                  wb = wbNCCG;
               }else if (j == 8){
                  fileEnder = "ncpc";
                  wb = wbNCPC;
               }
               
               if(outPath.getSelectedFile().getAbsolutePath().endsWith(".xls") == false)
               {
      			   out = new FileOutputStream(outPath.getSelectedFile().getAbsolutePath() + fileEnder + ".xls");
               }else{
                  out = new FileOutputStream(outPath.getSelectedFile().getAbsolutePath() + fileEnder);
               }
               if(wb.getSheetAt(0).getLastRowNum() > 0)
               {
      			   wb.write(out);
               }
               out.close();
               
               File f = new File(outPath.getSelectedFile().getAbsolutePath() + fileEnder + ".xls");
               if(f.length() == 0)
               {
                  f.delete();
               }
            }
   			infoText.append("<p><strong>Data File for " + importFileName + "Created Successfully!</strong></p>");
   		 }catch(IOException f){
   		 	JOptionPane.showMessageDialog(new JFrame(), "There is no file imported");
   		 }
      }
   }
   
   public void renameImages(String marketIn,String logoPath)
   {
      boolean anyErrors = false;
      market = marketIn;
      File source;
      File source2;
      File source3;
      String sourceLoc = logoPath + market + "\\" + subLogoFolder + "\\";
      
      checkForRepeats(market);

      File destination = new File(logoPath + market + "\\" + importFileName);
      
      // Clear folder if it already exists
      if(destination.exists() == false)
			{
				destination.mkdir();
			}else{
				File[] files = destination.listFiles();
   			if(files!=null) { //some JVMs return null for empty dirs
       			for(File f: files) {
                  if(f.getName().endsWith(".gif") || f.getName().endsWith(".png") || f.getName().endsWith(".jpg"))
                  {    
             		   f.delete();
                     System.out.println("Deleting file: " + f.getAbsolutePath());
                  }
            	}
        		}
			}
      
		for(int i = 0;i < scriptAds.size();i++)
		{
			// Using apache
			source = new File(sourceLoc + scriptAds.get(i).getCompanyName() + ".gif");
         source2 = new File(sourceLoc + "\\twitter\\" + scriptAds.get(i).getCompanyName() + ".png");
         source3 = new File(sourceLoc + "\\facebook\\" + scriptAds.get(i).getCompanyName() + ".jpg");
         
			String fDestSTR = logoPath + market + "\\" + importFileName + "\\" + scriptAds.get(i).getAdNumber() + ".gif";
         String fDestSTR2 = logoPath + market + "\\" + importFileName + "\\" + scriptAds.get(i).getAdNumber() + ".png";
         String fDestSTR3 = logoPath + market + "\\" + importFileName + "\\" + scriptAds.get(i).getAdNumber() + ".jpg";
			System.out.println("destination: " + fDestSTR);
			try{
				File fDest = new File(fDestSTR);
				FileUtils.copyFile(source, fDest);
            
            File fDest2 = new File(fDestSTR2);
				FileUtils.copyFile(source2, fDest2);
            
            File fDest3 = new File(fDestSTR3);
				FileUtils.copyFile(source3, fDest3);
			}catch(IOException e)
			{
            ArrayList<String> possibleLogos = scriptAds.get(i).getLogoLinks();
            ArrayList<String> possibleImages = scriptAds.get(i).getImageLinks();
            
            anyErrors = true;
				infoText.append("<p>" + scriptAds.get(i).getCompanyName() 
                              + " needs a logo made.\n ad#: " + (i + 1) + "\n" + importFileName + "</p>");
            
            if(possibleLogos.size() > 0 && scriptAds.get(i).isConfidential() == false)
            {
              for (String linkURL : possibleLogos)
              {
                  infoText.append("    <a href=\'" + linkURL + "'>" + linkURL + "</a>");
              }
            }else if(scriptAds.get(i).isConfidential() == false)
            {
               scriptAds.get(i).parseLogoLinks();
               possibleLogos = scriptAds.get(i).getLogoLinks();
               for(String linkURL : possibleLogos)
               {
                  infoText.append("    <a href='" + linkURL + "'>" + linkURL + "</a>");
               }     
            }
            
			}
         
		}
      if(anyErrors == false)
      {
         infoText.append("<strong><p>" + "All logos renamed successfully.</p></strong>");
      }
   }
   
   // This method checks to see if a string is a number
   public boolean isNumber(String sIn)
   {
      int parseNumber = -1;
      
      try{
         parseNumber = Integer.parseInt(sIn);
      }catch(Exception e){
         return false;
      }
      
      return true;
   }
   
	public void printAdErrors()
	{
		for(int i = 0;i < scriptAds.size();i++)
		{
			adObject ad = scriptAds.get(i);
			ad.checkForErrors();
			ArrayList<String> errors = ad.getErrors();
			if(errors != null){
				for(int j = 0;j < errors.size();j++)
				{
					infoText.append(errors.get(j));
				}
			}
		}
	}
	
   public void printLogoLinks(ArrayList<String> logoLinksIn)
   {
      for(int i = 0;i < logoLinksIn.size();i++)
      {
         // contains html link for future conversion to JTextPane instead of JTextAreas
         infoText.append("<a href=\"" + logoLinksIn.get(i) + "\">" + logoLinksIn.get(i) + "</a>\n");
      } 
   }
   
   public String removeCharacters(String s)
   {
      if(s.contains(" "))
      {
         s = s.replace(" ","");  
      }
      
      if(s.contains("?"))
      {
         s = s.replace("?","");
      }
      
      if(s.contains("-"))
      {
         s = s.replace("-","");
      }
       if(s.contains(","))
      {
         s = s.replace(",","");
      }
       if(s.contains("!"))
      {
         s = s.replace("!","");
      }
       if(s.contains("'"))
      {
         s = s.replace("'","");
      }
       if(s.contains("("))
      {
         s = s.replace("(","");
      }
       if(s.contains(")"))
      {
         s = s.replace(")","");
      }
       if(s.contains("’"))
      {
         s = s.replace("’","");
      }
       if(s.contains("’"))
      {
         s = s.replace("’","");
      }
      if(s.contains("-"))
      {
         s = s.replace("-","");
      }
      if(s.contains("\\"))
      {
         s = s.replace("\\","");
      }
      if(s.contains("/"))
      {
         s = s.replace("/","");
      }
      if(s.contains("-"))
      {
         s = s.replace("-","");
      }
      if(s.contains("@"))
      {
         s = s.replace("@","");
      }
      if(s.contains("."))
      {
         s = s.replace(".","");
      }
      return s;
   }
   
   public boolean isImageViewable()
   {
      return isViewImagable;
   }
   
   public void setImageViewable(boolean viewableIn)
   {
      isViewImagable = viewableIn;
   }
   
   public int countTabs(String sIn)
   {
      int tabs = 0;
      
      while(sIn.contains(TAB))
      {
         tabs++;
         sIn = sIn.substring(sIn.indexOf(TAB) + 1);
      }
      return tabs;
   }
   
   public String getBackUpPath()
   {
      return backUpPath;
   }
   public void setBackUpPath(String pathIn)
   {
      backUpPath = pathIn;
   }
   
   public String getGraphicsPath()
   {
      return graphicsPath;
   }
   
   public void setGraphicsPath(String pathIn)
   {
      graphicsPath = pathIn;
   }
   
   public String getImportFileName()
   {
      return importFileName;
   }
   
   public void setImportFileName(String importFileNameIn)
   {
      importFileName = importFileNameIn;
   }
   
   public String getSlateTextPath()
   {
      return slateTextPath;
   }
   
   public void setSlateTextPath(String slatePathIn)
   {
      slateTextPath = slatePathIn;
   }
   
   public String getSubLogoFolder()
   {
      return subLogoFolder;
   }
   
   public void setSubLogoFolder(String sIn)
   {
      subLogoFolder = sIn;
   }
   
   public String getDocDelimiter()
   {
      return docDelimiter;
   }
   
   public void setDocDelimiter(String delimitIn)
   {
      docDelimiter = delimitIn;
   }
   
   public String getMarket()
   {
      return market;
   }
   
   public void setMarket(String marketIn)
   {
      market = marketIn;
   }
   
   public void setStringFilePath(String filePathIn)
   {
      stringFilePath = filePathIn;
   }
   
   public String getStringFilePath()
   {
      return stringFilePath;
   }
   
   public ArrayList<String> getVoiceOverText()
   {
      ArrayList<String> voiceOvers = new ArrayList<String>();
      
      for(int i = 0;i < scriptAds.size();i++)
      {
         voiceOvers.add(scriptAds.get(i).getVoiceOver());
      }
      return voiceOvers;
   }
   
   public ArrayList<String> getSlates()
   {
      ArrayList<String> slates = new ArrayList<String>();
      
      for(int i = 0;i < scriptAds.size();i++)
      {
         slates.add(scriptAds.get(i).getSlate());
      }
      return slates;
   }
   
   public ArrayList<String> getCompanyNames()
   {
      ArrayList<String> companyNames = new ArrayList<String>();
      
      for(int i = 0;i < scriptAds.size();i++)
      {
         companyNames.add(scriptAds.get(i).getCompanyName());
      }
      return companyNames;
   }
   
   public String removeURLInfo(String sIn)
   {
      String beginning = "";
      String end = "";
      
      if(sIn.contains("mailto"))
      {
         System.out.println("beginning line is " + sIn);
         beginning = sIn.substring(0,sIn.indexOf("mailto") - 2);
         end = sIn.substring(sIn.indexOf("mailto") - 1);
         end = sIn.substring(sIn.indexOf(")") + 1);
         beginning = beginning + end;
      }else{
         return sIn;
      }
      beginning = beginning.trim();
      return beginning;
   }
   
   public String toString(){
      return importFileName;
   }
}