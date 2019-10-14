package com.chip8emu.core;

public interface IScreen {
	public void clearScreen();
	public void updateScreen();	
	public boolean drawByte(int x, int y, short pattern);
	
	public static final int SCREEN_WIDTH = 64;
	public static final int SCREEN_HEIGHT = 32;
	public static final boolean WHITE_PIXEL = false;
	public static final boolean BLACK_PIXEL = !WHITE_PIXEL;
}
