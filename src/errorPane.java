import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;


public class errorPane extends JComponent
{
	private dmcEditorPane slate;
	private dmcEditorPane vo;
	
	public errorPane()
	{
		GridLayout layout = new GridLayout(2,0);
		layout.setVgap(10);
		layout.setHgap(10);
		setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
		
		setLayout(layout);
		slate = new dmcEditorPane();
      slate.setContentType("text/html");
		slate.setEditable(false);
      JScrollPane slateSP = new JScrollPane(slate);
      
		vo = new dmcEditorPane();
		vo.setContentType("text/html");
		vo.setEditable(false);
		vo.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
		slate.setBorder(BorderFactory.createMatteBorder(2,2,2,2,Color.black));
		
		add(slateSP);
		add(vo);
		
		setVisible(true);
	}
	
	public dmcEditorPane getSlate()
	{
		return slate;
	}
	
	public dmcEditorPane getVO()
	{
		return vo;
	}
	
}
