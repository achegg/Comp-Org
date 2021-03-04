package cs3421_emul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Andrew Hegg
 * 9/22/19
 * CS3421 Assignment 1
 */

public class cs3421_emul {
	/**
	 * Main method for emulation, takes command line input in the form of a file name which is used to issue
	 * commands to the various devices that are in use through a given format
	 */
	public static void main(String[] args) {
		// creating objects for each of the devices that will be executing commands
		Clock simClock = new Clock();
		Memory simMemory = new Memory();
		CPU simCPU = new CPU();
		
		// read the file from the command line, throws an error if file does not exist
		File file = new File(args[0]);
		Scanner reader = null;
		
		try {
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		// initialization of various variables to be used when reading commands from the file
		String device;
		String command;
		int tickCount;
		int oldclock;
		int hexCount = 0;
		String[] bytearray;
		String address;
		int i;
		File bytedata;
		
		// read through the file and interpret commands for the devices using switch blocks for various commands
		// nested in if statements that tell which device to use
		while (reader.hasNext()){
			// variables that will be used to identify which device and command should be used next
			device = reader.next();
			command = reader.next();
			
			if(device.equals("clock")) {
				switch (command) {
				case "reset": 
					simClock.reset();
					break;
				case "tick": 
					tickCount = Integer.parseInt(reader.next());
					oldclock = simClock.counter;
					simClock.tick(tickCount);
					
					// for every clock tick, shifts the CPU registers by 1, fetches next byte from memory at PC location,
					// then increments PC by one
					for(i=oldclock;i<simClock.counter;i++) {
						simCPU.shift(simMemory);
					}
					break;
				case "dump":
					simClock.dump();
					break;
				// default only reached if none of the valid commands are recognized
				default: System.out.println("INVALID CLOCK COMMAND");
					break;
				}					
			}
			
			else if(device.equals("memory")) {
				switch (command) {
				case "create":
					simMemory.create(Integer.parseInt(reader.next().substring(2),16));
					break;
				case "reset": 
					simMemory.reset();
					break;
				case "dump":
					simMemory.dump(reader.next(), reader.next());
					break;
				case "set":
					address = reader.next();
					//command used to tell what type of set command should be used
					command = reader.next();
					// two possible input methods for set command: file version and list version are handled through if else statement
					if(command.equals("file")) {
						bytedata = new File(reader.next());
						simMemory.set(address,bytedata);
					}
					else {
						hexCount = Integer.parseInt(command.substring(2),16);
						bytearray = new String[hexCount];
						for(i=0;i<hexCount;i++) {
							bytearray[i] = reader.next();
						}
						simMemory.set(address,hexCount,bytearray);
					}
					break;
				// default only reached if none of the valid commands are recognized
				default: System.out.println("INVALID MEMORY COMMAND");
					break;
				}
			}
			
			else if(device.equals("cpu")) {
				switch (command) {
				case "reset":
					simCPU.reset();
					break;
				case "set":
					// next word in text file should be reg, which we don't need, so it can be moved to the command variable
					// to make room for the next string
					command = reader.next();
					simCPU.setreg(reader.next(), reader.next());
					break;
				case "dump":
					simCPU.dump();
					break;
				// default only reached if none of the valid commands are recognized
				default: System.out.println("INVALID CPU COMMAND");
					break;
				}
			}
		}
	}
}
