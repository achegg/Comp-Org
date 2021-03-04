package cs3421_emul;

import java.util.HashMap;

public class CPU {
	// use a hashmap so that register information can be accessed using the names of the individual registers as keys
	HashMap<String,String> registers = new HashMap<>();
	int wait = 0;
	String ins = "";
	String dest = null;
	String src = null;
	String tgt = null;
	String immediate = null;
	int target;
	int source;
	int result;
	String word1 = null;
	String word2 = null;
	int dMemspeed;
	
	public void reset() {
		// causes all CP registers to be zero
		registers.put("PC","0x00");
		registers.put("RA","0x00");
		registers.put("RB","0x00");
		registers.put("RC","0x00");
		registers.put("RD","0x00");
		registers.put("RE","0x00");
		registers.put("RF","0x00");
		registers.put("RG","0x00");
		registers.put("RH","0x00");
	}
	
	public String setreg(String reg, String hexbyte) {
		// set the value of the specified CPU register to a given hex byte
		String oldreg = registers.get(reg);
		if(reg.equals("PC")) {
			wait=0;
		}
		registers.put(reg,hexbyte);
		return oldreg;
	}
	
	public void dump() {
		// shows the value of all the CPU registers
		System.out.println("PC: " + registers.get("PC"));
		System.out.println("RA: " + registers.get("RA").toUpperCase());
		System.out.println("RB: " + registers.get("RB").toUpperCase());
		System.out.println("RC: " + registers.get("RC").toUpperCase());
		System.out.println("RD: " + registers.get("RD").toUpperCase());
		System.out.println("RE: " + registers.get("RE").toUpperCase());
		System.out.println("RF: " + registers.get("RF").toUpperCase());
		System.out.println("RG: " + registers.get("RG").toUpperCase());
		System.out.println("RH: " + registers.get("RH").toUpperCase());
		System.out.println();
	}
	
	/** 
	 * method used when a tick is issued and the device is not currently executing a command
	 * gets the next command pointed to by PC in iMemory
	 */
	public void fetch(InstructionMemory iMemory, Memory dMemory) {
		dMemspeed = dMemory.speed;
		decode(iMemory.imem[Integer.parseInt(registers.get("PC").substring(2),16)]);
	}
	
	public int decode(String command) {
		//this means there is no instruction at PC in imemory
		if(command.equals("0x00")) {
			return 0;
		}
		
		//convert from hex string to an int and back into binary to decode commands
		int val = Integer.parseInt(command, 16);
		String binary = String.format("%20s",Integer.toBinaryString(val)).replaceAll(" ","0");
		
		
		//divide binary into substrings to fit format for Entropy's instructions
		//(NNN DDD SSS TTT IIIIIIII)
		ins = binary.substring(0,3);
		
		
		//dest, src, and tgt need to be converted to the format of RX where X = character A-H
		//in order to properly access registers
		dest = binary.substring(3,6);
		dest = "R" + (char)(65 + Integer.parseInt(dest,2));
		
		src = binary.substring(6,9);
		src = "R" + (char)(65 + Integer.parseInt(src,2));
		
		tgt = binary.substring(9,12);
		tgt = "R" + (char)(65 + Integer.parseInt(tgt,2));
		
		immediate = binary.substring(12);
						
		
		//add takes 1 cycle
		if(ins.equals("000")) {
			wait = 1;
		}
		//addi takes 1 cycle
		else if(ins.equals("001")) {
			wait = 1;
		}
		//mul takes 2 cycles
		else if(ins.equals("010")) {
			wait = 2;
		}
		//inv takes 1 cycle
		else if(ins.equals("011")) {
			wait = 1;
		}
		//beq can take 1 or 2 cycles, 1 if values not equal, 2 if they are
		else if(ins.equals("100")) {
			wait = 1;
			//value will be changed in actual execution of command if found to be equal
		}
		//lw takes greater of mem speed or 1 cycle
		else if(ins.equals("101")) {
			wait = Math.max(dMemspeed, 1);
		}
		//sw takes the greater of the memory speed or 1 cycle
		else if(ins.equals("110")) {
			wait = Math.max(dMemspeed, 1);
		}
		//halt takes 1 cycle
		else if(ins.equals("111")) {
			wait = 1;
		}
		
		return wait;
	}
	
	//returns true if an instruction was executed, false if not
	// takes dMem as an argument in order to edit the contents of the data
	public boolean execute(Memory dMem, InstructionMemory iMem) {
		switch(ins) {
		
		//add command
		case "000":
			//add tgt and src register words, storing result in destination register, treats values as 8 bit 2's comp
			target = Integer.parseInt(registers.get(tgt).substring(2),16);
			source = Integer.parseInt(registers.get(src).substring(2),16);
			target = (int) Long.parseLong(String.format("%8s",Integer.toBinaryString(target)).replaceAll(" ", "0"),2);
			source = (int) Long.parseLong(String.format("%8s",Integer.toBinaryString(source)).replaceAll(" ", "0"),2);
			
			result = target + source;
			if(result>=16) {
				setreg(dest, "0x"+Integer.toHexString(result));
			}
			else {
				setreg(dest, "0x0"+Integer.toHexString(result));
			}
			
			//increment PC by 1
			incPC();
			return true;
			
		//addi command
		case "001":
			//add src register and immediate value words, storing the result in the destination register
			source = Integer.parseInt(registers.get(src).substring(2),16);
			source = (int) Long.parseLong(String.format("%8s",Integer.toBinaryString(source)).replaceAll(" ", "0"),2);
			target = (int) Long.parseLong(immediate,2);
			result = target + source;
			if(result>=16) {
				setreg(dest, "0x"+Integer.toHexString(result));
			}
			else {
				setreg(dest, "0x0"+Integer.toHexString(result));
			}

			//increment PC
			incPC();
			return true;
			
		//mul command
		case "010":
			//mutiply upper and lower 4 bits of src reg word, stores in dest
			source = Integer.parseInt(registers.get(src).substring(2),16);
			word1 = String.format("%8s",Integer.toBinaryString(source)).replaceAll(" ","0");
			result = Integer.parseInt(word1.substring(0,4),2) * Integer.parseInt(word1.substring(4),2);
			if(result<16) {
				setreg(dest, "0x0"+Integer.toHexString(result));
			}
			else {
				setreg(dest, "0x"+Integer.toHexString(result));
			}
			
			//increment PC
			incPC();
			return true;
	
		//inv command
		case "011":
			//invert all bits in the src register word, stores result in the dest register
			source = Integer.parseInt(registers.get(src).substring(2),16);
			result = ~source;
			if(result<0) {
				result = 256 + result;
			}
			if(result<16) {
				setreg(dest, "0x0"+Integer.toHexString(result));
			}
			else {
				setreg(dest, "0x"+Integer.toHexString(result));
			}
			
			//increment PC
			incPC();
			return true;
			
		//beq command
		case "100":
			//if words in tgt and src are equal, assign PC to immediate-specified imemory addresss, otherwise increment PC
			if(registers.get(src).equals(registers.get(tgt))) {
				//if equal, takes 2 ticks instead of 1 and set PC to immediate value
				wait=wait+1;
				if(Integer.parseInt(immediate,2)<16) {
					setreg("PC","0x0" + Integer.toHexString(Integer.parseInt(immediate,2)));
				}
				else {
					setreg("PC","0x" + Integer.toHexString(Integer.parseInt(immediate,2)));
				}
			}
			else {
				//if not equal, increment PC
				incPC();
			}
			return true;
			
		//lw command
		case "101":
			//load the data memory value, indicated by the address stored in register tgt, into the register dest
			setreg(dest, dMem.mem[Integer.parseInt(registers.get(tgt).substring(2),16)]);
			
			
			//increment PC by 1
			incPC();
			return true;
			
		//sw commmand
		case "110":
			//store the contents of register src into data memory at address pointed to by tgt
			dMem.mem[Integer.parseInt(registers.get(tgt).substring(2),16)] = registers.get(src);
			
			//increment PC by 1
			incPC();
			return true;
		
		//halt command
		case "111":
			//halts execution of processor after incrementing PC
			incPC();
			wait = -1;
		default:
			return false;
		}
	}
	
	
	//helper method to increment program counter by 1 after an instruction is executed
	public void incPC() {
		if(Integer.parseInt(registers.get("PC").substring(2),16)+1 < 16) {
			setreg("PC","0x0" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)).toUpperCase());
		}
		else {
			setreg("PC","0x" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)).toUpperCase());
		}
	}
}
