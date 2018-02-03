import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;

public class scriptError
{
	int adNumber;
	private ArrayList<String> slateInfo;
	private ArrayList<String> error;
	private ArrayList<String> vo;
	
	public scriptError()
	{
		adNumber = 0;
	}
	
	public scriptError(int adNum, ArrayList<String> slateInfoIn)
	{
		adNumber = adNum;
		slateInfo = slateInfoIn;
		error = new ArrayList<String>();
		vo = new ArrayList<String>();
	}
	
	public int getAdNum()
	{
		return adNumber;
	}
	
	public void addError(String errorIn)
	{
		error.add(errorIn);
	}
	
	public ArrayList<String> getErrors()
	{
		return error;
	}
	
	public ArrayList<String> getVo()
	{
		return vo;
	}
	
	public ArrayList<String> getSlateInfo()
	{
		return slateInfo;
	}
	
	public void setVO(String voIn)
	{
		vo = new ArrayList<String>();
	
		while(voIn.length() >= 50)
		{
			vo.add(voIn.substring(0,50));
			voIn = voIn.substring(50);
		} 
		vo.add(voIn);
	}
}