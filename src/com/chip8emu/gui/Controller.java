package com.chip8emu.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.chip8emu.core.CPU;
import com.chip8emu.core.IScreen;

public class Controller implements Runnable, KeyListener {

	public CPU myCPU       = null;
	public IScreen myScreen = null;
	private Thread myThread = new Thread(this);
	private final int fps = 60; 
	private final int speedCPU = 5;
	private boolean run =true;
	private boolean pause = true;
	private String currentFilename;
	private Hashtable<Integer,Integer> keyCode = new Hashtable<Integer,Integer>(CPU.NB_MAX_KEY);

	public Controller(IScreen screen)
	{
		myScreen = screen;
		myCPU = new CPU(myScreen);
		initKeyBoard();
		myThread.start();
	}
	
	public String currentFilename() 
	{
		return currentFilename;
	}
	
	public void play()
	{
		pause = false;
	}
	
	public void pause()
	{
		pause = true;
	}
	
	public boolean isPaused()
	{
		return pause;
	}
	
	public void reset()
	{
		pause = true;
		myCPU.reset_CPU();
		myScreen.clearScreen();
	}
	
	public void restart()
	{
		pause = true;
		myCPU.loadRom(currentFilename);
		pause = false;
	}
	
	public void step() {
		myCPU.step();
	}
	
	public void loadROM()
	{
		pause = true;
		String filename = openFile();
		if(filename != null)
		{
			currentFilename = filename;
			myCPU.loadRom(filename);
		}
	}
	
	

	private void initKeyBoard()
	{
		int key = 0;
		keyCode.put( KeyEvent.VK_A , key++); 
		keyCode.put( KeyEvent.VK_Z , key++); 
		keyCode.put( KeyEvent.VK_E , key++); 
		keyCode.put( KeyEvent.VK_R , key++); 
		keyCode.put( KeyEvent.VK_Q , key++);  
		keyCode.put( KeyEvent.VK_S , key++); 
		keyCode.put( KeyEvent.VK_D , key++); 
		keyCode.put( KeyEvent.VK_F , key++); 
		keyCode.put( KeyEvent.VK_W , key++);  
		keyCode.put( KeyEvent.VK_X , key++); 
		keyCode.put( KeyEvent.VK_C , key++); 
		keyCode.put( KeyEvent.VK_V , key++); 
		keyCode.put( KeyEvent.VK_T , key++);  
		keyCode.put( KeyEvent.VK_Y , key++); 
		keyCode.put( KeyEvent.VK_U , key++); 
		keyCode.put( KeyEvent.VK_I , key++); 
	}
	
    private String openFile() 
    {
        JFileChooser jfc=new JFileChooser(".");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result=jfc.showOpenDialog(null);

        if (result == JFileChooser.CANCEL_OPTION) return null;
        File fichier =jfc.getSelectedFile();
        if (fichier==null||fichier.getName().equals("" ))
        {
	        JOptionPane.showMessageDialog(null,"Nom de fichier invalide",
	        "Nom de fichier invalide",JOptionPane.ERROR_MESSAGE);
	        return null;
        }
        return fichier.getAbsolutePath();
    }

	@Override
	public void run() {
		while(run)
		{	
			if(pause == false)
			{
				for(int i= 0;i<speedCPU;i++)
				{
					myCPU.step();
				}
			}

			myScreen.updateScreen();
			
			try {
				Thread.sleep((int)(1000.0/(double)fps));
			} catch (Exception e) {}
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		Integer ret = keyCode.get(e.getKeyCode());
		if(ret != null)
		{
			//System.out.println("press key : "+ret);
			myCPU.pressKey(ret.intValue());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Integer ret = keyCode.get(e.getKeyCode());
		if(ret != null)
		{
			//System.out.println("release key : "+ret);
			myCPU.releaseKey(ret.intValue());
		}
		
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
