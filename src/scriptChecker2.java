import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.BufferStrategy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class scriptChecker2{

	private boolean imported;
	private ArrayList<String> dfLines;
	private dmcEditorPane infoText;

	private int strategyCreated;
	
	private JFrame frame;
	private JFileChooser fc;
	private File saveLocation;
	private File scriptFile;
	
	private String scriptingLog = "\\\\Quickbooks\\scripting\\00 Logs\\";
	private String productionLog = "Z:\\Data\\00 Logs\\";
	
	public scriptChecker2(File scriptIn,File saveLocationIn, imageRenamer frameIn)
	{
		scriptFile = scriptIn;
		saveLocation = saveLocationIn;
		frame = frameIn;
		infoText = frameIn.getTextBox();
		
		infoText.append("\n DFGenerator Constructed Successfully!");
		runCheck();
		
	 	try{
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet();
		   HSSFRow row;
		   HSSFCell cell;
			CreationHelper createHelper = wb.getCreationHelper();
			CellStyle cellStyle = wb.createCellStyle();	
			cellStyle.setDataFormat(
	  						createHelper.createDataFormat().getFormat("M/D/YY"));
			infoText.append("\n DataFormater Constructed, Preparing Excel Write");				
			for(int i = 0;i < dfLines.size();i++)
			{
				row = sheet.createRow(i);
				int colCount = 0;
				String cellCut = dfLines.get(i);
				
				while(cellCut.contains("\t"))
				{
					cell = row.createCell(colCount);
					String cellInput = cellCut.substring(0,cellCut.indexOf("\t") + 1);
					System.out.println(cellCut);
					cellCut = cellCut.substring(cellCut.indexOf("\t") + 1);
					System.out.println("Cell input: " + cellInput);
					cellInput = cellInput.trim();
					
					if(colCount == 2 || colCount == 3)
					{
						cell.setCellValue(new Date(cellInput));
						cell.setCellStyle(cellStyle);
					}else{
						cell.setCellValue(cellInput);
					}
					colCount++;
				}
            if(cellCut.length() >= 2)
            {
               cell = row.createCell(colCount);
               String cellInput = cellCut.substring(0);
               cellInput = cellInput.trim();
               System.out.println("Cell input: " + cellInput);
               cell.setCellValue(cellInput);
            }
            /*
				cell = row.createCell(colCount);
				cell.setCellValue("");
				cell = row.createCell(colCount);
				cell.setCellValue("Uploaded");
            */
			}
			FileOutputStream out = new FileOutputStream(saveLocation.getAbsolutePath() + ".xls");
			wb.write(out);
			out.close();
			infoText.append("\n Data File Created Successfully!");
		 }catch(IOException f){
		 	JOptionPane.showMessageDialog(frame, "There is no file imported");
		 }

	}
	
	public void runCheck()
	{
		try {
			 BufferedReader in = new BufferedReader(new FileReader(scriptFile));			// load buffer with file
		    String str;																				// iterator string
			 dfLines = new ArrayList();
			 
		    while ((str = in.readLine()) != null) {											// While file still has Next()
				 
             if(str.contains("COMPLETION CHECKLIST"))
             {
               break;
             }
		       if(str.contains("Uploaded") || str.contains("Uplaoded") || str.contains("Uploadde"))														// First slug Updated occurence.
				 {
				 	dfLines.add(str);
			    }
			 }
		    in.close();
			 infoText.append("\n File Parsed Successfully!");
		} catch (IOException e) {
		}
	}
}