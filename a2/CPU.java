package cs3421_emul;

import java.util.HashMap;

public class CPU {
	// use a hashmap so that register information can be accessed using the names of the individual registers as keys
	HashMap<String,String> registers = new HashMap<>();
	int wait = 0;
	String ins = null;
	String dest = null;
	String src = null;
	String tgt = null;
	String immediate = null;
	
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
		System.out.println("RA: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RA").substring(2),16)));
		System.out.println("RB: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RB").substring(2),16)));
		System.out.println("RC: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RC").substring(2),16)));
		System.out.println("RD: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RD").substring(2),16)));
		System.out.println("RE: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RE").substring(2),16)));
		System.out.println("RF: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RF").substring(2),16)));
		System.out.println("RG: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RG").substring(2),16)));
		System.out.println("RH: 0x" + String.format("%1$02X",Integer.parseInt(registers.get("RH").substring(2),16)));
		System.out.println();
	}
	
	/** 
	 * method used when a tick is issued and the device is not currently executing a command
	 * gets the next command pointed to by PC in iMemory
	 */
	public void fetch(InstructionMemory iMemory) {
		
		decode(iMemory.imem[Integer.parseInt(registers.get("PC").substring(2),16)]);
	}
	
	public int decode(String command) {
		//this means there is no instruction at PC in imemory
		if(command.equals("0x00")) {
			return 0;
		}
		
		//convert from hex string to an int and back into binary to decode commands
		int val = Integer.parseInt(command, 16);
		String binary = Integer.toBinaryString(val);
		
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
		
		//lw command, takes 5 ticks
		if(ins.equals("101")) {
			wait = 5;
		}
		//sw command, takes 5 ticks
		else if(ins.equals("110")){
			wait = 5;
		}
		return wait;
	}
	
	//returns true if an instruction was executed, false if not
	// takes dMem as an argument in order to edit the contents of the data
	public boolean execute(Memory dMem) {
		if(ins == null) {
			return false;
		}
		
		//lw command
		else if(ins.equals("101")) {
			//load the data memory value, indicated by the address stored in register tgt, into the register dest
			setreg(dest, dMem.mem[Integer.parseInt(registers.get(tgt).substring(2),16)]);
			
			
			//increment PC by 1
			if(Integer.parseInt(registers.get("PC").substring(2),16)+1 < 16) {
				setreg("PC","0x0" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
			}
			else {
				setreg("PC","0x" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
			}
			return true;
		}
		
		//sw command
		else if(ins.equals("110")) {
			//store the contents of register src into data memory at address pointed to by tgt
			dMem.mem[Integer.parseInt(registers.get(tgt).substring(2),16)] = registers.get(src);
			
			//increment PC by 1
			if(Integer.parseInt(registers.get("PC").substring(2),16)+1 < 16) {
				setreg("PC","0x0" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
			}
			else {
				setreg("PC","0x" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
			}
			return true;
		}
		
		return false;
		
	}
}
