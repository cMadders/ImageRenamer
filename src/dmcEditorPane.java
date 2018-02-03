import java.util.*;
import javax.swing.*;

public class dmcEditorPane extends JTextPane
{
	private String body = "";
	private String htmlStart = "<html>";
	private String htmlEnd = "</html>";
	private String pStart = "<p>";
	private String pEnd = "</p>";
	
	public dmcEditorPane()
	{
	
	}
	
	public String getBody()
	{
		return body;
	}
	
	public void setBody(String bodyIn)
	{
		body = bodyIn;
		updatePane();
	}
	
	public void append(String sIn)
	{
		body += sIn;
		updatePane();
	}
	
	public void updatePane()
	{
		setText(htmlStart + pStart + body + pEnd + htmlEnd);
	}
	
}