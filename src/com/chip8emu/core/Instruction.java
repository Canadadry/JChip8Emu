package com.chip8emu.core;

public class Instruction {
	
	public class InstructionSpec
	{
		public  class Arg
		{
			public int pos;
			public int size;
			public boolean register = false;
			public Arg(int pos, int size,boolean isRegister)
			{
				this.pos=pos;
				this.size=size;
				this.register= isRegister;
			}
		}

		public static final int MAX_ARG = 3;
		
		public int bitMask;
		public int id;
		public String hexaName; 
		public String MenmoName;
		public Arg[] argTab;
		
		public InstructionSpec(int bitMask, int id, String hexaName, String MenmoName)
		{
			this.bitMask   = bitMask;
			this.id        = id;
			this.hexaName  = hexaName;
			this.MenmoName = MenmoName;
			this.argTab = new Arg[0];
		}
		
		public InstructionSpec(int bitMask, int id, String hexaName, String MenmoName, int arg0pos, int arg0size,boolean isRegister0)
		{
			this.bitMask   = bitMask;
			this.id        = id;
			this.hexaName  = hexaName;
			this.MenmoName = MenmoName;
			this.argTab = new Arg[1];
			argTab[0] = new Arg(arg0pos,arg0size,isRegister0);
		}
		
		public InstructionSpec(int bitMask, int id, String hexaName, String MenmoName, int arg0pos, int arg0size,boolean isRegister0, int arg1pos, int arg1size,boolean isRegister1)
		{
			this.bitMask   = bitMask;
			this.id        = id;
			this.hexaName  = hexaName;
			this.MenmoName = MenmoName;
			this.argTab = new Arg[2];
			argTab[0] = new Arg(arg0pos,arg0size,isRegister0);
			argTab[1] = new Arg(arg1pos,arg1size,isRegister1);
		}
		
		public InstructionSpec(int bitMask, int id, String hexaName, String MenmoName, int arg0pos, int arg0size,boolean isRegister0, int arg1pos, int arg1size,boolean isRegister1, int arg2pos, int arg2size,boolean isRegister2)
		{
			this.bitMask   = bitMask;
			this.id        = id;
			this.hexaName  = hexaName;
			this.MenmoName = MenmoName;
			this.argTab = new Arg[3];
			argTab[0] = new Arg(arg0pos,arg0size,isRegister0);
			argTab[1] = new Arg(arg1pos,arg1size,isRegister1);
			argTab[2] = new Arg(arg2pos,arg2size,isRegister2);
		}          
	}
	
	public InstructionSpec[] instructionSet = {
		new InstructionSpec(0xFFFF,0x00E0," 00E0 "," CLS  "),
		new InstructionSpec(0xFFFF,0x00EE," 00EE "," RET  "),
		new InstructionSpec(0xF000,0x0000," 0NNN "," SYS  ",2,3,false),
		new InstructionSpec(0xF000,0x1000," 1NNN "," JMP  ",2,3,false),
		new InstructionSpec(0xF000,0x2000," 2NNN "," CALL ",2,3,false),
		new InstructionSpec(0xF000,0x3000," 3XNN "," SE   ",2,1,true ,1,2,false),
		new InstructionSpec(0xF000,0x4000," 4XNN "," SNE  ",2,1,true ,1,2,false),
		new InstructionSpec(0xF00F,0x5000," 5XY0 "," SE   ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF000,0x6000," 6XNN "," LD   ",2,1,true ,1,2,false),
		new InstructionSpec(0xF000,0x7000," 7XNN "," ADD  ",2,1,true ,1,2,false),
		new InstructionSpec(0xF00F,0x8000," 8XY0 "," LD   ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8001," 8XY1 "," OR   ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8002," 8XY2 "," AND  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8003," 8XY3 "," XOR  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8004," 8XY4 "," ADD  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8005," 8XY5 "," SUB  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8006," 8XY6 "," SHR  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x8007," 8XY7 "," SUBN ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x800E," 8XYE "," SHL  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF00F,0x9000," 9XY0 "," SNE  ",2,1,true ,1,1,true ),
		new InstructionSpec(0xF000,0xA000," ANNN "," LD   ",2,3,false),
		new InstructionSpec(0xF000,0xB000," BNNN "," JMP  ",2,3,false),
		new InstructionSpec(0xF000,0xC000," CXNN "," RND  ",2,1,true ,1,2,false),
		new InstructionSpec(0xF000,0xD000," DXYN "," DRW  ",2,1,true ,1,1,true ,0,1,false),
		new InstructionSpec(0xF0FF,0xE09E," EX9E "," SKP  ",2,1,true ),
		new InstructionSpec(0xF0FF,0xE0A1," EXA1 "," SKNP ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF007," FX07 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF00A," FX0A "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF015," FX15 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF018," FX18 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF01E," FX1E "," ADD  ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF029," FX29 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF033," FX33 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF055," FX55 "," LD   ",2,1,true ),
		new InstructionSpec(0xF0FF,0xF065," FX65 "," LD   ",2,1,true )
	};
	
	public static final int lenght = 35;
	
	public static final int _00E0 =  0; /* 00E0 */
	public static final int _00EE =  1; /* 00EE */
	public static final int _0NNN =  2; /* 0NNN */
	public static final int _1NNN =  3; /* 1NNN */
	public static final int _2NNN =  4; /* 2NNN */
	public static final int _3XNN =  5; /* 3XNN */
	public static final int _4XNN =  6; /* 4XNN */
	public static final int _5XY0 =  7; /* 5XY0 */
	public static final int _6XNN =  8; /* 6XNN */
	public static final int _7XNN =  9; /* 7XNN */
	public static final int _8XY0 = 10; /* 8XY0 */
	public static final int _8XY1 = 11; /* 8XY1 */
	public static final int _8XY2 = 12; /* 8XY2 */
	public static final int _8XY3 = 13; /* 8XY3 */
	public static final int _8XY4 = 14; /* 8XY4 */
	public static final int _8XY5 = 15; /* 8XY5 */
	public static final int _8XY6 = 16; /* 8XY6 */
	public static final int _8XY7 = 17; /* 8XY7 */
	public static final int _8XYE = 18; /* 8XYE */
	public static final int _9XY0 = 19; /* 9XY0 */
	public static final int _ANNN = 20; /* ANNN */
	public static final int _BNNN = 21; /* BNNN */
	public static final int _CXNN = 22; /* CXNN */
	public static final int _DXYN = 23; /* DXYN */
	public static final int _EX9E = 24; /* EX9E */
	public static final int _EXA1 = 25; /* EXA1 */
	public static final int _FX07 = 26; /* FX07 */
	public static final int _FX0A = 27; /* FX0A */
	public static final int _FX15 = 28; /* FX15 */
	public static final int _FX18 = 29; /* FX18 */
	public static final int _FX1E = 30; /* FX1E */
	public static final int _FX29 = 31; /* FX29 */
	public static final int _FX33 = 32; /* FX33 */
	public static final int _FX55 = 33; /* FX55 */
	public static final int _FX65 = 34; /* FX65 */
	
	public boolean opcodeIsAction(int opcode, int action)
	{
		boolean result = false;
		if(action < Instruction.lenght)
		{
			result = ((instructionSet[action].bitMask & opcode) == instructionSet[action].id);
		}
		return result;
	}
	
	public int getSubByte(int bytes, int num) // bytes is considered as uint16
	{
		return (bytes & ((0xF)<<(num*4)))>>(num*4);
	}
	
	public String getSubValue(int bytes, int pos,int size)
	{
		String value = "";
		for(int i=0;i<size;i++)
		{
			value+= String.format("%x", getSubByte(bytes, pos-i));
		}
		return value;
	}
	
	public String decode(int opcode)
	{
		boolean opcodefound = false;
		String ligne = "" + String.format("%04x", opcode) + " : ";
		for(int i=0;i<Instruction.lenght;i++)
		{
			if(opcodeIsAction(opcode, i))
			{
				opcodefound = true;
				ligne += instructionSet[i].MenmoName;
				for(int j = 0;j<instructionSet[i].argTab.length;j++)
				{
					if(instructionSet[i].argTab[j].register)
					{
						ligne+="V";
					}
					else
					{
						ligne+="0x";
					}
					ligne += getSubValue(opcode, instructionSet[i].argTab[j].pos,instructionSet[i].argTab[j].size);
					if((j+1)<instructionSet[i].argTab.length)
					{
						ligne += " , ";
					}
				}
			}
		}
		
		if(opcodefound == false)
		{
			ligne += "NOP";
		}

		return ligne;

	}
}
