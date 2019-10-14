package com.chip8emu.core;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.EOFException;


public class CPU {
	
	private static boolean debug_mode = false;
	private static final int TAILLE_MEMOIRE =  4096;
	private static final int ADRESSE_DEBUT  =  512;
	private static final int NB_REGISTER    =  16;
	private static final int NB_MAX_JUMP    =  16;
	public  static final int NB_MAX_KEY    =  16;

	private Instruction instructionSet = new Instruction();
	private short memory[] = new short[TAILLE_MEMOIRE];
	private short register_V[] = new short[NB_REGISTER];
	private int addressRegisterI;
	private int[] stack = new int[NB_MAX_JUMP];
	private short  stackPointer;
	private short delayTimerDT;
	private short soundTimerST;
	private int programCounter;
	private boolean bigEndian = true;
	private IScreen myScreen;
	private boolean keyboard[] = new boolean[NB_MAX_KEY];
	
	public CPU(IScreen screen)
	{
		myScreen =  screen;
	}
	
	public void pressKey(int numKey)
	{
		if(numKey<NB_MAX_KEY) keyboard[numKey]=true;
	}
	
	public void releaseKey(int numKey)
	{
		if(numKey<NB_MAX_KEY) keyboard[numKey]=false;
	}
	
	public void reset_CPU()
	{
		for(int i = 0; i < TAILLE_MEMOIRE; i++)
		{
			memory[i++] = 0;
		}
		
		for(int i = 0; i < NB_REGISTER; i++)
		{
			register_V[i] = 0;
		}	

		for(int i = 0; i < NB_MAX_JUMP; i++)
		{
			stack[i] = 0;
		}
		for(int i = 0; i < NB_MAX_KEY; i++)
		{
			keyboard[i] = false;
		}	
		
		addressRegisterI=0;
		stackPointer = 0;
		delayTimerDT = 0;
		soundTimerST = 0;
		programCounter= ADRESSE_DEBUT;
		loadFont();
		myScreen.clearScreen();
	}
	
	public void decompter()
	{
		if(delayTimerDT>0)
			delayTimerDT--;
		
		if(soundTimerST>0)
			soundTimerST--;

	}
	
	public void printState()
	{
		String out = "";
		for(int i = 0;i<16;i++)
			out += ("V"+i+"= 0x" + String.format("%04x", register_V[i]) +" ");
		out+="\n";
		out +=("PC= 0x"+String.format("%04x", programCounter));
		out+="\n";
		for(int i = 0;i<16;i++)
			out +=("stack["+i+"]= 0x" + String.format("%04x", stack[i]) +" ");
		out+="\n";
		out +=("sp= 0x"+String.format("%04x", stackPointer));
		out+="\n";
		out +=("I= 0x"+String.format("%04x", addressRegisterI));
		out+="\n";
		out+="\n";
		
		System.out.println(out);
		
	}
	
	public void step()
	{
		int opcode = getOpCode();
		if(debug_mode)
		{
			System.out.println("executing : "+ String.format("%04x", 0xFFFF&opcode)+"\n");
		}
		
		System.out.println("PC : "+String.format("%04x", 0xFFFF&(programCounter-2))+" Executing : "+instructionSet.decode(opcode));
		
		if(instructionSet.opcodeIsAction(opcode,Instruction._00E0))
		{
			myScreen.clearScreen();
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._00EE))
		{
			if(stackPointer > 0)
			{
				if(stackPointer>=NB_MAX_JUMP)
					stackPointer = NB_MAX_JUMP-1;
				
				programCounter = stack[stackPointer];
				stackPointer--;
				
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._0NNN))
		{
			if(debug_mode)
			{
				System.out.println("opcode ONNN not implemented!\n");
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._1NNN))
		{
			int addr = 	   getSubByte(opcode, 0) 
						| (getSubByte(opcode, 1) << 4)
						| (getSubByte(opcode, 2) << 8);
			programCounter = (addr&0xFFF);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._2NNN))
		{
			int addr = 	   getSubByte(opcode, 0) 
						| (getSubByte(opcode, 1) << 4)
						| (getSubByte(opcode, 2) << 8);
			if(stackPointer>=(NB_MAX_JUMP-1))
			{
				System.out.println("stackOverFlow at : "+ programCounter + "\n");
			}
			else{
				stackPointer++;
				stack[stackPointer] = programCounter;
			}
			programCounter = (addr&0xFFF);		
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._3XNN))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int NN = 	   getSubByte(opcode, 0)
						| (getSubByte(opcode, 1) << 4);
			if(register_V[X] == NN)
			{
				programCounter+=2;
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._4XNN))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int NN = 	   getSubByte(opcode, 0)
						| (getSubByte(opcode, 1) << 4);
			if(register_V[X] != NN)
			{
				programCounter+=2;
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._5XY0))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			if(register_V[X] == register_V[Y])
			{
				programCounter+=2;
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._6XNN))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int NN = 	   getSubByte(opcode, 0)
						| (getSubByte(opcode, 1) << 4);
			register_V[X] = (short) NN;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._7XNN))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int NN = 	   getSubByte(opcode, 0)
						| (getSubByte(opcode, 1) << 4);
			register_V[X] += (short) NN;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY0))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			register_V[X] = register_V[Y];
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY1))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			register_V[X] = (short) (register_V[X] | register_V[Y]);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY2))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			register_V[X] = (short) (register_V[X] & register_V[Y]);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY3))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			register_V[X] = (short) (register_V[X] ^ register_V[Y]);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY4))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			if((register_V[X] + register_V[Y]) > 0xFF)
			{
				register_V[0xF] = 1;
			}
			else
			{
				register_V[0xF] = 0;
			}
			register_V[X] += register_V[Y];
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY5))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			if((register_V[Y] > register_V[X]))
			{
				register_V[0xF] = 0;
			}
			else
			{
				register_V[0xF] = 1;
			}
			register_V[X] = (short)(register_V[X] - register_V[Y]);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY6))
		{
			int X = 	   getSubByte(opcode, 2) ;
			//int Y = 	   getSubByte(opcode, 1) ;
			register_V[0xF] = (short) (register_V[X]&0x1);
			register_V[X] = (short) (register_V[X] >> 1);
			
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XY7))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			if((register_V[X] > register_V[Y]))
			{
				register_V[0xF] = 0;
			}
			else
			{
				register_V[0xF] = 1;
			}
			register_V[X] = (short)(register_V[Y] - register_V[X]);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._8XYE))
		{
			int X = 	   getSubByte(opcode, 2) ;
			//int Y = 	   getSubByte(opcode, 1) ;
			register_V[0xF] = (short) ((register_V[X]>>7)&0x1);
			register_V[X] = (short) (register_V[X] << 1);
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._9XY0))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			if(register_V[X] != register_V[Y])
			{
				programCounter+=2;
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._ANNN))
		{
			int addr = 	   getSubByte(opcode, 0) 
			| (getSubByte(opcode, 1) << 4)
			| (getSubByte(opcode, 2) << 8);
			
			addressRegisterI = (addr&0xFFF);

		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._BNNN))
		{
			int addr = 	   getSubByte(opcode, 0) 
			| (getSubByte(opcode, 1) << 4)
			| (getSubByte(opcode, 2) << 8);
			
			programCounter = (addr&0xFFF) + register_V[0];
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._CXNN))
		{
			int X = 	   getSubByte(opcode, 2);
			int NN = 	   getSubByte(opcode, 0)
			| (getSubByte(opcode, 1) << 4);
			
			register_V[X] = (short) Math.ceil(0.5+(Math.random()*NN));
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._DXYN))
		{
			int X = 	   getSubByte(opcode, 2) ;
			int Y = 	   getSubByte(opcode, 1) ;
			int N = 	   getSubByte(opcode, 0) ;
			for(int i = 0;i<N;i++)
			{
				myScreen.drawByte(register_V[X], register_V[Y]+i, memory[addressRegisterI+i]);
			}
			//System.out.println("draw not implemented yet!\n");
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._EX9E))
		{
			int X = 	   getSubByte(opcode, 2) ;
			if(register_V[X] < NB_MAX_KEY)
			{
				if(keyboard[register_V[X]] == true)
				{
					programCounter+=2;
				}
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._EXA1))
		{
			int X = 	   getSubByte(opcode, 2) ;
			if(register_V[X] < NB_MAX_KEY)
			{
				if(keyboard[register_V[X]] == false)
				{
					programCounter+=2;
				}
			}
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX07))
		{
			int X = 	   getSubByte(opcode, 2);
			
			register_V[X] = delayTimerDT;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX0A))
		{
			System.out.println("key not implemented yet!\n");
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX15))
		{
			int X = 	   getSubByte(opcode, 2);
			
			delayTimerDT = register_V[X];
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX18))
		{
			int X = 	   getSubByte(opcode, 2);
			
			soundTimerST = register_V[X];
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX1E))
		{
			int X = 	   getSubByte(opcode, 2) ;
			if((register_V[X] + addressRegisterI) > 0xFF)
			{
				register_V[0xF] = 1;
			}
			else
			{
				register_V[0xF] = 0;
			}
			addressRegisterI += register_V[X] ;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX29))
		{
			int X = 	   getSubByte(opcode, 2) ;
			
			addressRegisterI = register_V[X]*5 ;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX33))
		{
			int X = 	   getSubByte(opcode, 2) ;
			
			memory[addressRegisterI] = (short) (register_V[X]/100) ;
			memory[addressRegisterI+1] = (short) ((int)(register_V[X]/10 ) - (int)(register_V[X]/100)*10)  ;
			memory[addressRegisterI+1] = (short) ((register_V[X]) - (int)(register_V[X]/10)*10)  ;
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX55))
		{
			int X = 	   getSubByte(opcode, 2) ;
			
            for(int i=0;i<=X;i++)
            {
                memory[addressRegisterI+i]=register_V[i];
            }
		}
		else if(instructionSet.opcodeIsAction(opcode,Instruction._FX65))
		{
			int X = 	   getSubByte(opcode, 2) ;
			
            for(int i=0;i<=X;i++)
            {
                register_V[i] = memory[addressRegisterI+i];
            }
		}
		else{
			System.out.println("error : opcode unknow (" + opcode +  ")\n");
		}
		
		decompter();
		if(debug_mode)
		{
			printState();
		}
	}
	
	public int getSubByte(int bytes, int num) // bytes is considered as uint16
	{
		return (bytes & ((0xF)<<(num*4)))>>(num*4);
	}
	
	public int getOpCode()
	{
		int result = 0;
		
		if(bigEndian)	result =  (int) (((int)(memory[programCounter  ])<<8) | memory[programCounter+1]);
		else			result =  (int) (((int)(memory[programCounter+1])<<8) | memory[programCounter  ]);	
		
		programCounter += 2;
		return result;
	}
	
	public void loadRom(String filename)
	{
		reset_CPU();
		try {

			DataInputStream file = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
			int i = 0;
			int add = ADRESSE_DEBUT;
			
			while((i = file.readUnsignedByte()) != -1)
			{
				//System.out.println("0x"+String.format("%x", i&0xFF));
				memory[add++] = (short) (i&0xFF);
			}
			
		} catch (EOFException e) {
			// do nothing 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void  loadFont() {
		int i = 0x0;
        
        //0 SPRITE
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        //1 SPRITE
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("11111000", 2);
        memory[i++] = (short) Integer.parseInt("11111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        //2 SPRITE
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("00000111", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        memory[i++] = (short) Integer.parseInt("00011100", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("01110000", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        //3 SPRITE
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("00000111", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        memory[i++] = (short) Integer.parseInt("00001111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        //4 SPRITE
        memory[i++] = (short) Integer.parseInt("00011110", 2);
        memory[i++] = (short) Integer.parseInt("00111110", 2);
        memory[i++] = (short) Integer.parseInt("11100110", 2);
        memory[i++] = (short) Integer.parseInt("11000110", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("01111111", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        //5 SPRITE
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("01111000", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("00001111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        //6 SPRITE
        memory[i++] = (short) Integer.parseInt("00001111", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("01111000", 2);
        memory[i++] = (short) Integer.parseInt("11110000", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        //7 SPRITE
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("00001110", 2);
        memory[i++] = (short) Integer.parseInt("00011100", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        memory[i++] = (short) Integer.parseInt("00111000", 2);
        //8 SPRITE
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11000011", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        //9 SPRITE
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("01111111", 2);
        memory[i++] = (short) Integer.parseInt("00001111", 2);
        memory[i++] = (short) Integer.parseInt("00011110", 2);
        memory[i++] = (short) Integer.parseInt("0011110", 2);
        memory[i++] = (short) Integer.parseInt("1111000", 2);
        //A SPRITE
        memory[i++] = (short) Integer.parseInt("00111100", 2);
        memory[i++] = (short) Integer.parseInt("01100110", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        //B SPRITE
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        //C SPRITE
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100111", 2);
        memory[i++] = (short) Integer.parseInt("01111110", 2);
        //D SPRITE
        memory[i++] = (short) Integer.parseInt("11111100", 2);
        memory[i++] = (short) Integer.parseInt("11100110", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100011", 2);
        memory[i++] = (short) Integer.parseInt("11100110", 2);
        memory[i++] = (short) Integer.parseInt("11111100", 2);
        //E SPRITE
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100001", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100001", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        //F SPRITE
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11111111", 2);
        memory[i++] = (short) Integer.parseInt("11100001", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11111110", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
        memory[i++] = (short) Integer.parseInt("11100000", 2);
	}
}
