import java.util.*;
import javax.swing.*;
import java.util.regex.*;
import java.text.DateFormat;
import java.io.*;
import java.net.*;

public class adObject
{
   private int ccNumber;
   private String adNumber;
   private String companyName;
   private String voiceOver;
   private String position;
   private String category;
   private String stateCode;
   private String startDate;
   private String endDate;
   private String slate;
   private String slugLineOriginal;
   private boolean confidential;
   private ArrayList<String> logoLinks;
   private ArrayList<String> imageLinks;
   private ArrayList<String> slugLine;
   private ArrayList<String> errors;
   private String[] tlds = {".com",".org",".biz",".net",".int",".edu",".gov",".mil",".arpa",
                                      ".aero",".bike",".wiki",".us"};
   private String[] blacklist = {"gmail.com","yahoo.com","hotmail.com","mindspring.net","jobpostingtoday.com","aol.com",
                                 "applitrack.com","ymail.com","careerbuilder.com","outlook.com"};
   private ImageViewer imgViewer;
   
   public adObject(String adNumberIn, String companyNameIn, String voiceOverIn)
   {
      adNumber = adNumberIn;
      companyName = companyNameIn;
      voiceOver = voiceOverIn;
      ccNumber = 0;
      position = "";
      category = "";
      stateCode = "";
      startDate = "";
      endDate = "";
      slate = "";
      imgViewer = new ImageViewer();
      confidential = false;
      imageLinks = new ArrayList<String>();
      errors = new ArrayList<String>();
      slugLine = new ArrayList<String>();
      logoLinks = new ArrayList<String>();
   }
   
   public adObject()
   {
      ccNumber = 0;
      adNumber = "";
      companyName = "";
      voiceOver = "";
      position = "";
      category = "";
      stateCode = "";
      startDate = "";
      endDate = "";
      slate = "";
      imgViewer = new ImageViewer();
      confidential = false;
		imageLinks = new ArrayList<String>();
      errors = new ArrayList<String>();
      slugLine = new ArrayList<String>();
      logoLinks = new ArrayList<String>();
   }
   
   public String getAdNumber()
   {
      return adNumber;
   }
   
   public void setAdNumber(String adNumberIn)
   {
      adNumber = adNumberIn;
   }
   
   public String getCategory()
   {
      return category;
   }
   
   public void setCategory(String categoryIn)
   {
      category = categoryIn;
   }
   
   public int getccNumber()
   {
      return ccNumber;
   }
   
   public void setccNumber(int ccNumberIn)
   {
      ccNumber = ccNumberIn;
   }
   
   public String getCompanyName()
   {
      return companyName;
   }
   
   public void setCompanyName(String companyNameIn)
   {
      companyName = companyNameIn;
   }
   
   public boolean isConfidential()
   {
      if(confidential){
         return true;
      }
      return false;
   }
   
   public void setConfidential(boolean setting)
   {
      confidential = setting;
   }
   
   public String getEndDate()
   {
      return endDate;
   }
   
   public void setEndDate(String endDateIn)
   {
      endDate = endDateIn;  
   }
   
   public ArrayList<String> getErrors()
   {
      return errors;
   }
   
   public void addError(String errorIn)
   {
      errors.add(errorIn);
   }
   
   public void addLogoLink(String linkIn)
   {
      if(logoLinks.contains(linkIn) == false)
      {
         logoLinks.add(linkIn);
      }
   }
   
   public ArrayList<String> getImageLinks()
   {
      return imageLinks;
   }
   
   public void setImageLinks(ArrayList<String> imageLinksIn)
   {
      imageLinks = imageLinksIn;
   }
   
   public ImageViewer getImageViewer()
   {
      return imgViewer;
   }
   
   public void setImageViewer(ImageViewer ImageViewerIn)
   {
      imgViewer = ImageViewerIn;
   }
   
   public ArrayList<String> getLogoLinks()
   {
      return logoLinks;
   }
   
   public void setLogoLinks(ArrayList<String> logoLinksIn)
   {
      logoLinks = logoLinksIn;
   }
   
   public String getPosition()
   {
      return position;
   }
   
   public void setPosition(String positionIn)
   {
      position = positionIn;
   }
   
   public String getSlate()
   {
      return slate;
   }
   
   public void setSlate(String slateIn)
   {
      slate = slateIn;
   }
   
   public String getStartDate()
   {
      return startDate;
   }
   
   public void setStartDate(String startDateIn)
   {
      startDate = startDateIn;
   }
   
   public String getVoiceOver()
   {
      return voiceOver;
   }
   
   public String getStateCode()
   {
      return stateCode;
   }
   
   public void setStateCode(String stateCodeIn)
   {
      stateCode = stateCodeIn;
   }
   
   public void setVoiceOver(String voiceOverIn)
   {
      voiceOver = voiceOverIn;
   }
   
   public ArrayList<String> getSlugLine()
   {
      return slugLine;
   }
   
   public String getSlugLineOriginal()
   {
      return slugLineOriginal;
   }
   
   public void setSlugLine(ArrayList<String> slugLineIn)
   {
      slugLine = slugLineIn;
   }
   
   // This overload parses a slugline by tabs
   public void setSlugLine(String slugLineIn)
   {
      int incrementer = 1;
      String segment = "";
      slugLineOriginal = slugLineIn;
      
      while(slugLineIn.contains("\t"))
      {
         segment = slugLineIn.substring(0,slugLineIn.indexOf("\t"));
         
         switch(incrementer)
         {  
            case 2:
               if(segment.toLowerCase().equals("confidential") == true){
                 confidential = true;
               }
               slugLine.add(segment.trim());
               break;
            case 3:
               startDate = segment.trim();
               slugLine.add(startDate);
               break;
            case 4:
               endDate = segment.trim();
               slugLine.add(endDate);
               break;
            case 5:
               adNumber = segment.trim();
               slugLine.add(adNumber);
               break;
            case 6:
               category = segment.trim();
               slugLine.add(category);
               break;
            case 7:
               position = segment.trim();
               slugLine.add(position);
               break;
            case 8:
               companyName = segment.trim();
               slugLine.add(companyName);
               break;
            case 10:
               if(segment.toLowerCase().trim().equals("uploaded") == false)
                  alertUser("Ad #: " + ccNumber + " is not tabbed correctly");
               slugLine.add(segment.trim());
               break;
            case 11:
               stateCode = segment.trim();
               slugLine.add(stateCode);
               break;
            default:
               slugLine.add(segment.trim());
               
         }
         slugLineIn = slugLineIn.substring(slugLineIn.indexOf("\t") + 1);
         incrementer++;
      }
      slugLine.add(slugLineIn);
   }
   
   public void alertUser(String messageIn)
   {
      JOptionPane.showMessageDialog(new JFrame(), messageIn);
   }
   
   public enum slugColumns{
      CCNUM(1),LOGO(2),STARTDATE(3),ENDDATE(4),ADNUMBER(5),CATEGORY(6), POSITION(7), COMPANY(8),UPLOADED(11),STATECODE(12);
      private int value;
      
      private slugColumns(int valueIn)
      {
         value = valueIn; 
      }
   }
   
   public void checkForErrors()
   {
      // Check for start and end date conflicts *************************************
      try{
         Date start = new Date(startDate);
         Date end = new Date(endDate);
      
         if(start.after(end))
         {
            errors.add("<p><strong>Start date is later than end date in ad# " + ccNumber + "</strong></p>");
				
         }
      }catch(Exception e){
         JOptionPane.showMessageDialog(new JFrame(), "There a tabbing/date formatting issue with ad#: " + ccNumber);
      }
      // ****************************************************************************
      
   }
   
   // This method is called when no logo was explicitly declared before an ad's slate.
   // parseLogoLinks takes lines in a slate and checks for top level domains to add to logoLinks array
   public void parseLogoLinks()
   {
      String tempSlate = slate;
		boolean doLastLine = false;
      
      while(tempSlate.contains("\n") || doLastLine == false)
      {
			String currentLine = "";
			
			if(tempSlate.contains("\n")){
         	currentLine = tempSlate.substring(0,tempSlate.indexOf("\n"));
         	tempSlate = tempSlate.substring(tempSlate.indexOf("\n") + 1);
			}else{
				currentLine = tempSlate;
				doLastLine = true;
			}
         
         // Iterate through the list of top level domains to look for
         for(String tld : tlds)
         {
            // If it is not an email
            if(currentLine.contains(tld) && currentLine.contains("@") == false)
            {
               if(logoLinks.contains(currentLine) == false)
               {
                  currentLine = currentLine.trim();
                  if(logoLinks.contains(currentLine.toLowerCase()) == false)
                  {
                     logoLinks.add(currentLine.toLowerCase());
                  }
               }
            // if it is an email
            }else if(currentLine.contains(tld) && currentLine.contains("@") == true)
            {
               currentLine = currentLine.substring(currentLine.indexOf("@") + 1);
               if(logoLinks.contains(currentLine) == false)
               {
                  boolean blacklisted = false;
                  
                  // Check against list of mass email services
                  for(String blacklister : blacklist)
                  {
                     if(currentLine.equals(blacklister))
                     {
                        blacklisted = true;
                     }  
                  }
                  if(blacklisted == false)
                  {
                     currentLine = currentLine.trim();
                     if(logoLinks.contains(currentLine.toLowerCase()) == false)
                     {
                        logoLinks.add(currentLine.toLowerCase());
                     }
                  }
               }
            }
         }
      }
   }
   
   // populateViewer() requires the parseLogoLinks method to be called beforehand.
   public void populateViewer()
   {
      for(int i = 0;i < logoLinks.size();i++)
      {
         imgViewer.parseLogoImage(logoLinks.get(i));
      }
      imgViewer.setLinkURLs(logoLinks);
      imgViewer.createImages();
   }
   
   public String toString()
   {
      String rString = "";
      
      rString += "<strong><font size=\"5\">CC Num: </font></strong>" + ccNumber + "<br />";
      rString += "<strong>Ad Num: </strong>" + adNumber + "<br />";
      rString += "<strong>Company Name: </strong>" + companyName + "<br />";
      rString += "<strong>Postion: </strong>" + position + "<br />";
      rString += "<strong>Category: </strong>" + category + "<br />";
      rString += "<strong>Live Dates: </strong>" + startDate + "<strong> | </strong>" + endDate + "<br />";
      return rString;
   }
   
}