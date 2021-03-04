package cs3421_emul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {
	String[] mem;
	public void create(int size) {
		// creates memory device of specified size in bytes
		mem = new String[size];
	}
	
	public void reset() {
		// causes all allocated memory to be set to zero
		for (int i=0; i<mem.length; i++) {
			mem[i] = ("0x00");
		}
	}
	
	public void dump(String address, String hexcount) {
		// shows contents of memory starting at address <address> and continuing for <hexcount> bytes
		System.out.println("Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F");
		int addr = Integer.parseInt(address.substring(2), 16);
		int count = Integer.parseInt(hexcount.substring(2), 16);
		int dif = 0;
		
		// if the address is not a multiple of 16, this will print the nearest multiple of 16 before the address
		// in hex followed by blank spaces until the address is reached
		if(addr%16 != 0) {
			dif = (addr) - (addr%16);
			System.out.print("0x" + String.format("%1$04X",dif) + " ");
			for(int i=0; i<addr%16; i++) {
				System.out.print("  "+" ");
			}
		}
		// if the address is a multiple of 16, formats the address to have 0x with leading zeros and prints the 4 digit hex number
		else {
			System.out.print("0x" + String.format("%1$04X", addr) + " ");
		}
		
		// loop that will print the individual bytes stored in memory
		for(int i=0; i<count; i++) {	
			
			System.out.print(String.format("%1$02X",Integer.parseInt(mem[addr + i].substring(2),16)));
			
			// starts a new line whenever the address reaches a multiple of 16
			if((addr+i+1) % 16 == 0) {
				System.out.println();
				System.out.print("0x" + String.format("%1$04X",addr+i+1) + " ");
			}
			else {
				System.out.print(" ");
				if(i==count-1) {
					System.out.println();
				}
			}
		}
		System.out.println();
	}
	
	
	// set method for when the hex values are given in list form,
	// uses a for loop to store the bytes in the mem array
	public void set(String address, int count, String[] hexbytes) {
		
		int addr = Integer.parseInt(address.substring(2), 16);
		
		for(int i=0; i<count; i++){
			mem[addr+i] = hexbytes[i];
		}
	}
	
	// set method for when the hex values are given in a data file,
	// uses a while loop to add every byte in the file into the mem array
	public void set(String address, File fileName) {
		
		// creates a scanner of the file, throws an exception if the file does not exist
		Scanner file = null;
		try {
			file = new Scanner(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int addr = Integer.parseInt(address.substring(2), 16);
		
		while(file.hasNext()){
			mem[addr++] = file.next();
		}
	}
}
