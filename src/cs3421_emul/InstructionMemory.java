package cs3421_emul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InstructionMemory {
	String[] imem;
	public void create(int size) {
		// creates imemory device of specified size in bytes
		imem = new String[size];
	}
	
	public void reset() {
		// causes all allocated imemory to be set to zero
		for (int i=0; i<imem.length; i++) {
			imem[i] = ("0x00");
		}
	}
	
	public void dump(String address, String hexcount) {
		// shows contents of memory starting at address <address> and continuing for <hexcount> bytes
		System.out.println("Addr       0     1     2     3     4     5     6     7");
		int addr = Integer.parseInt(address.substring(2), 16);
		int count = Integer.parseInt(hexcount.substring(2), 16);
		int dif = 0;
				
		// if the address is not a multiple of 8, this will print the nearest multiple of 8 before the address
		// in hex followed by blank spaces until the address is reached
		if(addr%8 != 0) {
			dif = (addr) - (addr%8);
			System.out.print("0x" + String.format("%1$04X",dif) + " ");
			for(int i=0; i<addr%8; i++) {
				System.out.print("     "+" ");
			}
		}
		// if the address is a multiple of 8, formats the address to have 0x with leading zeros and prints the 4 digit hex number
		else {
			System.out.print("0x" + String.format("%1$04X", addr) + " ");
		}
		
		// loop that will print the individual contents of imemory
		for(int i=0; i<count; i++) {	
			
			System.out.print(imem[addr + i]);
					
			// starts a new line whenever the address reaches a multiple of 8
			if((addr+i+1) % 8 == 0) {
				System.out.println();
				System.out.print("0x" + String.format("%1$04X",addr+i+1) + " ");
			}
			else {
				System.out.print(" ");
			}
		}
		System.out.println();
	}
	
	// set method for when the hex values are given in list form,
		// uses a for loop to store the bytes in the imem array
	public void set(String address, int count, String[] hexbytes) {
			
		int addr = Integer.parseInt(address.substring(2), 16);
			
		for(int i=0; i<count; i++){
			imem[addr+i] = hexbytes[i];
		}
	}
		
		// set method for when the hex values are given in a data file,
		// uses a while loop to add every byte in the file into the imem array
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
			imem[addr++] = file.next();
		}
	}
}
