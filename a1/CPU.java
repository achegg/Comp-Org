package cs3421_emul;

import java.util.HashMap;

public class CPU {
	// use a hashmap so that register information can be accessed using the names of the individual registers as keys
	HashMap<String,String> registers = new HashMap<>();

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
		registers.put(reg,hexbyte);
		return reg;
	}
	
	public void dump() {
		// shows the value of all the CPU registers
		System.out.println("PC: " + registers.get("PC"));
		System.out.println("RA: " + registers.get("RA"));
		System.out.println("RB: " + registers.get("RB"));
		System.out.println("RC: " + registers.get("RC"));
		System.out.println("RD: " + registers.get("RD"));
		System.out.println("RE: " + registers.get("RE"));
		System.out.println("RF: " + registers.get("RF"));
		System.out.println("RG: " + registers.get("RG"));
		System.out.println("RH: " + registers.get("RH"));
		System.out.println();
	}
	
	/** 
	 * method used when a tick is issued to this device to shift all registers down by one, fetch next byte from memory,
	 * and increment program counter.
	 * takes a memory device as input so that RA knows where to get its next byte from
	 */
	public void shift(Memory memory) {
		setreg("RH",registers.get("RG"));
		setreg("RG",registers.get("RF"));
		setreg("RF",registers.get("RE"));
		setreg("RE",registers.get("RD"));
		setreg("RD",registers.get("RC"));
		setreg("RC",registers.get("RB"));
		setreg("RB",registers.get("RA"));
		// RA comes from the byte that is located at the memory address of the program counter
		setreg("RA",memory.mem[Integer.parseInt(registers.get("PC").substring(2),16)]);
		// PC is incremented by 1 every shift, if statement for when PC<16 and needs a leading 0
		if(Integer.parseInt(registers.get("PC").substring(2),16)+1 < 16) {
			setreg("PC","0x0" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
		}
		else {
			setreg("PC","0x" + Integer.toHexString((Integer.parseInt(registers.get("PC").substring(2),16)+1)));
		}
	}
}
