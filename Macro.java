
package org.automation.util;

import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.JOptionPane;

import java.net.*;
import java.io.*;
import java.util.*;

import java.lang.reflect.*;

public class Macro
{

	Robot myRobot = null;

	int autoDelay = 0;
	int increment =0;

	public Macro() {

		try
		{
			myRobot = new Robot();
		}
		catch (Exception e)
		{
		}
	}

	public Macro(String path) {

		try
		{
			myRobot = new Robot();
		}
		catch (Exception e)
		{
		}
		runInstructionsFile(path);

	}

	public void runInstructionsFile(String path) {

		String data = loadFile(path);

		StringTokenizer tokens = new StringTokenizer(data,"~");

		myRobot.setAutoWaitForIdle(false);

		while (tokens.hasMoreTokens())
		{
			String event = tokens.nextToken();
			runInstruction(event);	
		}

	}

	public void runInstruction(String event){

		if (!event.startsWith("--"))
		{

			if (event.startsWith("#"))   // Global Settings
			{
				autoDelay = Integer.parseInt(event.substring(event.indexOf(" ")+1,event.length()));
				myRobot.setAutoDelay(autoDelay);
			} 
			else if (event.startsWith("MM"))       /// Mouse Move
			{
				mouseMove(event);
			}
			else if (event.startsWith("MDC"))
			{
				mouseDoubleClick(event);
			}
			else if (event.startsWith("MC"))   // Mouse Clicked
			{
				mouseClick(event);
			}
			else if (event.startsWith("TYPE"))   // Type the Text
			{
				typeText(event);
			}
			else if (event.startsWith("WF"))    // Wait For
			{
				myRobot.delay(Integer.parseInt(event.substring(event.indexOf(" ")+1,event.length())));
			}
			else if (event.startsWith("ALT PRESS"))   // ALT Key Press
			{
				myRobot.keyPress(KeyEvent.VK_ALT);
			}
			else if (event.startsWith("ALT RELEASE"))   // ALT Key Release
			{
				myRobot.keyRelease(KeyEvent.VK_ALT);
			}
			else if (event.startsWith("SHIFT PRESS"))   // SHIFT Key Press
			{
				myRobot.keyPress(KeyEvent.VK_SHIFT);
			}
			else if (event.startsWith("SHIFT RELEASE"))   // SHIFT Key Release
			{
				myRobot.keyRelease(KeyEvent.VK_SHIFT);
			}
			else if (event.startsWith("CONTROL PRESS"))   // CONTROL Key Press
			{
				myRobot.keyPress(KeyEvent.VK_CONTROL);
			}
			else if (event.startsWith("LEFT"))
			{
				myRobot.keyPress(KeyEvent.VK_LEFT);
			}
			else if (event.startsWith("CONTROL RELEASE"))   // CONTROL Key Release
			{
				myRobot.keyRelease(KeyEvent.VK_CONTROL);
			}
			else if (event.startsWith("PAUSE"))   // PAUSE
			{
				JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.ERROR_MESSAGE); 
			}
			else if (event.startsWith("TAB +"))
			{
				int x = Integer.parseInt(event.substring(event.length()-1,event.length()));
				System.out.println("Tabbing here for "+x+" times");
				for (int i=0;i<increment;i+=x )
				{
					myRobot.keyPress(KeyEvent.VK_TAB);
					myRobot.keyRelease(KeyEvent.VK_TAB);
				}
				increment++;
			}
			else if (event.startsWith("PRINT "))
			{
				System.out.println(event.substring(event.indexOf(" ")+1,event.length()));
			}
			else if (event.startsWith("PRINTCOLOR"))
			{
				int x[] = giveIntArray(event.substring(event.indexOf(" ")+1,event.length()));
				System.out.println(myRobot.getPixelColor(x[0],x[1]));
			}
			else if (event.equals("EXIT"))
			{
				System.exit(0);
			}
			else if (event.startsWith("BEEP"))
			{
				if (event.length()==4)
				{
					beep(1);
				}
				else {
					beep (Integer.parseInt(event.substring(event.indexOf(" ")+1,event.length())));
				}
			}
			else if (event.startsWith("CHECKCOLOR"))
			{
				// CHECKCOLOR << x >> << y >> << red >> << blue >> << green >> << time >>
				System.out.println("Building...");
				int x[] = giveIntArray(event.substring(event.indexOf(" ")+1,event.length()));
				while(true) {
					Color c = myRobot.getPixelColor(x[0],x[1]);
					if (c.equals(new Color(x[2],x[3],x[4])))
					{
						break;
					}
					else
					{
						try
						{
							Thread.sleep(x[5]);
							System.out.println("Building...");
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else
			{
				try
				{
					myRobot.keyPress((char)KeyEvent.class.getField("VK_"+event).getInt("VK_"+event));
					myRobot.keyRelease((char)KeyEvent.class.getField("VK_"+event).getInt("VK_"+event));
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	private void beep(int x)
	{
		for (int i=0;i<x;i++)
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private int[] giveIntArray(String args){
		StringTokenizer st = new StringTokenizer(args,",");
		int x[] =new int[st.countTokens()];
		int i=0;
		while(st.hasMoreTokens())
		{
			x[i++]=Integer.parseInt(st.nextToken().trim());
		}
		return x;

	}

	public boolean checkForServerStart() throws Exception {

		URL url = new URL("https", "10.201.56.78", 9443, "login.do");

		URLConnection urlConnection = url.openConnection();

		urlConnection.setAllowUserInteraction(false);

		InputStream urlStream = url.openStream();
		String type = urlConnection.guessContentTypeFromStream(urlStream);
		System.out.println(type);
		if (type == null)
		    return false;
		if (type.compareTo("text/html") != 0) 
		    return true;

		return false;

	}


	public static void main(String[] args) throws Exception
	{
		Macro myMacro = new Macro();

//		myMacro.runInstructionsFile(args[0]);
		myMacro.runInstruction("#WF 2000");

		myMacro.runInstruction("ALT PRESS");
		myMacro.runInstruction("TAB");
		myMacro.runInstruction("ALT RELEASE");

		myMacro.runInstruction("CONTROL PRESS");
		myMacro.runInstruction("C");
		myMacro.runInstruction("CONTROL RELEASE");

		myMacro.runInstruction("ALT PRESS");
		myMacro.runInstruction("TAB");
		myMacro.runInstruction("ALT RELEASE");

		myMacro.runInstruction("CONTROL PRESS");
		myMacro.runInstruction("V");
		myMacro.runInstruction("CONTROL RELEASE");

//		for (int i=0;i<100 ;i++ )
//		{
//			myMacro.runInstruction("CHECKCOLOR 865,753,2,88,243,1000");
//			myMacro.runInstruction("MC 865,753");
//			myMacro.runInstruction("WF 2000");
//			myMacro.runInstruction("MC 740,664");
//			myMacro.runInstruction("WF 6000");
//			myMacro.runInstruction("ENTER");
//			myMacro.runInstruction("ENTER");
//		}


//
//		myMacro.runInstruction("TYPE //*AGR//05OCT//2//NONE//AAA//N//4027");

//		myMacro.runInstructionsFile("C:\\Documents and Settings\\venitspu\\Desktop\\OpenCommandLine.txt");
//		myMacro.runInstructionsFile("C:\\Documents and Settings\\venitspu\\Desktop\\BeepAfterBuild.txt");
//		myMacro.runInstructionsFile("C:\\Documents and Settings\\venitspu\\Desktop\\BeepAfterServerStart.txt");

//		while(myMacro.checkForServerStart()) {

//			myMacro.runInstructionsFile("C:\\Documents and Settings\\venitspu\\Desktop\\OpenPayment.txt");
//			break;
//		}


//		myMacro.runInstructionsFile("C:\\Documents and Settings\\pulavas\\Desktop\\OpenLocal.txt");

//		for (int i=0;i<53 ;i++ )
//		{
//			myMacro.runInstructionsFile("C:\\Documents and Settings\\venitspu\\Desktop\\OpenLocal.txt");
//		}
	
/*		Macro m = new Macro();
		m.runInstruction("WF 2000");
		m.runInstruction("MC 10,800");
		m = null;*/
	}

	private String loadFile(String path) {

		String str = null;

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(path));
			String temp = null;
			str = br.readLine()+"~";
			while((temp=br.readLine())!= null){
				str+=temp+"~";
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		

		return str;
	}

	private void mouseMove(String event){
		myRobot.mouseMove(Integer.parseInt(event.substring(event.indexOf(" ")+1,event.indexOf(","))), Integer.parseInt(event.substring(event.indexOf(",")+1,event.length())));
	}

	private void keyPress(String event) {
		myRobot.setAutoDelay(0);
		System.out.println(event);
		if (event.equals("*"))
		{
			myRobot.keyPress(KeyEvent.VK_SHIFT);
			myRobot.keyPress(KeyEvent.VK_8);
			myRobot.keyRelease(KeyEvent.VK_8);
			myRobot.keyRelease(KeyEvent.VK_SHIFT);
		}
		else {
			myRobot.keyPress(event.charAt(event.length()-1));
			myRobot.keyRelease(event.charAt(event.length()-1));
		}
		myRobot.setAutoDelay(autoDelay);
	}

	private void mouseClick(String event){
		myRobot.setAutoDelay(0);//
		mouseMove(event);
		myRobot.mousePress(InputEvent.BUTTON1_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_MASK);
		myRobot.setAutoDelay(autoDelay);
	}

	private void mouseDoubleClick(String event){
		myRobot.setAutoDelay(0);
		mouseMove(event);
		myRobot.mousePress(InputEvent.BUTTON1_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_MASK);
		myRobot.mousePress(InputEvent.BUTTON1_MASK);
		myRobot.mouseRelease(InputEvent.BUTTON1_MASK);
		myRobot.setAutoDelay(autoDelay);
	}

/*	private void alt(String event){
		myRobot.setAutoDelay(0);
		myRobot.keyPress(KeyEvent.VK_ALT);
		myRobot.keyPress(event.substring(event.indexOf(" ")+1,event.length()));
		myRobot.keyRelease(event.substring(event.indexOf(" ")+1,event.length()));
		myRobot.keyRelease(KeyEvent.VK_ALT);
		myRobot.setAutoDelay(autoDelay);
	}*/

	private void typeText(String event) {
		myRobot.setAutoDelay(0);
		for (int i=5;i<=event.length()-1 ;i++ )
		{
			keyPress(""+event.charAt(i));
		}
		myRobot.setAutoDelay(autoDelay);
	}

	private void printMouseLocation(String event) {
//		System.out.println(MouseInfo.getPointerInfo().getLocation());
	}
}