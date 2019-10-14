package com.chip8emu.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.chip8emu.core.IScreen;

@SuppressWarnings("serial")
public class Screen extends JPanel implements IScreen{

	public final int PIXEL_SIZE = 10;
	public boolean pixels[] = new boolean[SCREEN_WIDTH*SCREEN_HEIGHT];
	private boolean needUpdate =true;
	private BufferedImage img;

	public Screen()
	{
		setPreferredSize(new Dimension(PIXEL_SIZE*SCREEN_WIDTH, PIXEL_SIZE*SCREEN_HEIGHT));
		img = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);	
		clearScreen();
	}


	public void clearScreen()
	{
		initPixel();
		Graphics g = img.getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		needUpdate =true;
	}

	public void updateScreen()
	{
		if(needUpdate)
			repaint();
	}
	
	public boolean drawByte(int x, int y, short pattern)
	{
		if(y>=SCREEN_HEIGHT) return false;
		final int s_pos = x+y*SCREEN_WIDTH;
		needUpdate = true;
		boolean ret = true;
		for(int i = 0;i<8;i++)
		{
			if((x+i)>=SCREEN_WIDTH)
			{
				ret = false;
				break;
			}
			else
			{
				int mask = (0x1<<(7-i));
				pixels[s_pos+i] = pixels[s_pos+i] ^ ( (pattern &  mask)==mask);
				if(pixels[s_pos+i] == WHITE_PIXEL)
					img.setRGB(x+i, y,Color.white.getRGB());
				else if(pixels[s_pos+i] == BLACK_PIXEL)
					img.setRGB(x+i, y,Color.black.getRGB());
			} 
		}
		return ret;
	}

	public void paintComponent(Graphics g)
	{
		updateScreen(g);
		needUpdate =false;
	} 

	private void updateScreen(Graphics g)
	{
		g.drawImage(img, 0, 0,PIXEL_SIZE*SCREEN_WIDTH, PIXEL_SIZE*SCREEN_HEIGHT, null);
	}

	private void initPixel()
	{
		for(int i =0;i<(SCREEN_WIDTH*SCREEN_HEIGHT);i++)
		{
			pixels[i] = BLACK_PIXEL;
		}
	}
}
