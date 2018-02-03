import java.util.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO; 
import java.net.*;
import java.awt.*;
import java.util.regex.*;
import java.awt.Image; 
import java.awt.image.BufferedImage; 
import javax.imageio.ImageIO; 
import java.awt.Graphics; 
import java.awt.Graphics2D; 


public class ImageViewer extends JInternalFrame
{
   private ArrayList<String> imageLinks;
   private ArrayList<String> linkURLs;
   private ArrayList<BufferedImage> bufferedImages;
   private int currentImage;
   private ImagePanel imagePane;
   
	public ImageViewer()
	{  
      currentImage = 0;
      
      imageLinks = new ArrayList<String>();
      linkURLs = new ArrayList<String>();
      bufferedImages = new ArrayList<BufferedImage>();
      
      GridBagConstraints c = new GridBagConstraints();
		JPanel mainPane = new JPanel();
      imagePane = new ImagePanel();
		setSize(250,400);
		mainPane.setLayout(new GridBagLayout());
		
      JButton previousButton = new JButton("Previous");
		JButton nextButton = new JButton("Next");
		JButton saveButton = new JButton("Save");
      
      //c.insets = new Insets(0,10,0,10);  //top padding
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = .9;
		c.gridwidth = 3;
      //imagePane.add(new JButton("aswesomesessss"),c);
      mainPane.add(imagePane,c);
      
      c.gridy = 1;
      c.weightx = .4;
      c.weighty = .1;
      c.gridwidth = 1;
      
		mainPane.add(previousButton,c);
      
      c.gridx = 1;
      c.weightx = .2;
      
		mainPane.add(saveButton,c);
      
      c.gridx = 2;
      c.weightx = .4;
      
		mainPane.add(nextButton,c);
		add(mainPane);
		setVisible(true);
		
	}
   //infoText.append("<img src=\"http://" + linkURL + possibleImages.get(j) + "\"</img>");

   // Create Images must be called after parseLogoImages()
   public void createImages()
   {
      for(int i = 0;i < linkURLs.size();i++)
      {
         for(int j = 0;j < imageLinks.size();j++)
         {
            try{
               URL url = new URL("http://" + linkURLs.get(i) + imageLinks.get(j));
               try{
                  Image image = ImageIO.read(url); 
                  BufferedImage cpimg=bufferImage(image); 
                  bufferedImages.add(cpimg);
                  /*
                  File f1 = new File("test.png"); 
                  ImageIO.write(cpimg, "png", f1); 
                  */
               } catch(IOException e){
                 System.out.println(e);
               }
            }catch (MalformedURLException mue) {
               System.out.println("Ouch - a MalformedURLException happened.");
               mue.printStackTrace();
               System.exit(1);

            }
         }
      }
      displayImage();
   }
   
   public void displayImage()
   {
      imagePane = new ImagePanel(bufferedImages.get(currentImage));
   }
   
	public void parseLogoImage(String urlIn)
   {
      URL imageURL = null;
      InputStream is = null;
      DataInputStream dis;
      String htmlLine;
      String imgTag = "<img src=\"";
      Pattern srcPattern = Pattern.compile("/src=\"\\w+\\W+\"/");
      		
      try{
         System.out.println("URL IN: " + urlIn);
         if(urlIn.contains("http") == false){
            imageURL = new URL("http://" + urlIn);
         }else{
            imageURL = new URL(urlIn);
         }
         is = imageURL.openStream();
         dis = new DataInputStream(new BufferedInputStream(is));
         while ((htmlLine = dis.readLine()) != null) {
				String extension = "";
    			if(htmlLine.contains(imgTag))
				{        
					if(htmlLine.contains(".gif"))
	            {
						extension = ".gif";
	            }else if(htmlLine.contains(".png")){
						extension = ".png";
					}else if(htmlLine.contains(".jpeg")){
						extension = ".jpeg";
					}else if(htmlLine.contains(".targa")){
						extension = ".targa";
					}else if(htmlLine.contains(".jpg")){
						extension = ".jpg";
					}else if(htmlLine.contains(".PNG")){
						extension = ".PNG";
					}else if(htmlLine.contains(".JPEG")){
						extension = ".JPEG";
					}else if(htmlLine.contains(".TARGA")){
						extension = ".TARGA";
					}else if(htmlLine.contains(".JPG")){
						extension = ".JPG";
					}
					String assembledPath = htmlLine.substring(htmlLine.indexOf(imgTag) + imgTag.length());
               
					if(extension != "") //&& assembledPath.contains(">") == false)
					{
						assembledPath = assembledPath.substring(0,assembledPath.indexOf(extension) + extension.length());
                  Matcher matcher = srcPattern.matcher(assembledPath);
                  if (matcher.find()) {
                      System.out.println("Matched" + matcher.group(0)); //prints /{item}/
                      if(imageLinks.contains(assembledPath) == false)
                      {
                        System.out.println("Adding " + assembledPath);
                        
                      }
                  } else {
                      //System.out.println("Match not found");
                  }
   					imageLinks.add(assembledPath);	
   				}
                  
					
               
				}
            //System.out.println(s);
         }
         
      } catch (MalformedURLException mue) {

         System.out.println("Ouch - a MalformedURLException happened.");
         mue.printStackTrace();
         System.exit(1);

      } catch (IOException ioe) {

         System.out.println("Oops- an IOException happened.");
         //ioe.printStackTrace();
         try{
            URLConnection uc = imageURL.openConnection();
            uc.addRequestProperty("User-Agent", 
               "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
      
            uc.connect();
            is=uc.getInputStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((htmlLine = dis.readLine()) != null) { 
               if(htmlLine.contains(".gif"))
               {
                  System.out.println("Reading user-agent only");
                  System.out.println("Contains gif");
                  System.out.println(htmlLine);
               }
            }
         }catch(IOException innerException){
         
         }
         
         //System.exit(1);

      } finally {

         //---------------------------------//
         // Step 6:  Close the InputStream  //
         //---------------------------------//
         /*
         try {
         } catch (IOException ioe) {
            // just going to ignore this one
         }
         */

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
   
   public ArrayList<String> getLinkURLs()
   {
      return linkURLs;
   }
   
   public void setLinkURLs(ArrayList<String> urlsIn)
   {
      linkURLs = urlsIn;
   }
	/*
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
	}
	*/
   public static BufferedImage bufferImage(Image image) { 
      return bufferImage(image,BufferedImage.TYPE_INT_RGB); 
   }
   public static BufferedImage bufferImage(Image image, int type) { 
      BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type); 
      Graphics2D g = bufferedImage.createGraphics(); 
      g.drawImage(image, null, null); 
      return bufferedImage; 
   } 
   
   class ImagePanel extends JPanel
   {
      /*the default image to use*/
      BufferedImage imageFile;;
      
      public ImagePanel()
      {
         super();
      }
      
      public ImagePanel(BufferedImage image)
      {
         super();
         this.imageFile = image;
      }
      
      public void paintComponent(Graphics g)
      {
         /*Draw image on the panel*/
         super.paintComponent(g);
      
         if (imageFile != null)
         g.drawImage(imageFile, 0, 0, getWidth(), getHeight(), this);
      }
   }
}