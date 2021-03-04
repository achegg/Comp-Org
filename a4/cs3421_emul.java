package cs3421_emul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Andrew Hegg
 * 11/3/19
 * CS3421 Assignment 2
 */

public class cs3421_emul {

	/**
	 * Main method for emulation, takes command line input in the form of a file name which is used to issue
	 * commands to the various devices that are in use through a given format
	 */
	public static void main(String[] args) {
		// creating objects for each of the devices that will be executing commands

		Clock simClock = new Clock();
		Memory simDataMemory = new Memory();
		CPU simCPU = new CPU();
		InstructionMemory simIMemory = new InstructionMemory();
		Cache simCache = new Cache();
		
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
		String[] Ibytearray;
		String address;
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
					
					//uses a loop to do 1 tick at a time so that the cpu waiting state is checked every single tick
					for(int i=0; i<tickCount; i++) {
						//commands will only be executed if the cpu is done waiting
						//for the previous command to finish
						if(simCPU.wait == 0) {
							if(simCache.enabled) {
								simCPU.cachedexecute(simDataMemory,simIMemory,simCache);
							}
							else {
								simCPU.execute(simDataMemory,simIMemory);
							}
							simCPU.fetch(simIMemory, simDataMemory, simCache);
						}
												
						//clock tick incremented and integer for waiting is decreased by 1
						simClock.tick(1);
						//TC will stop if halt command is issued
						if(simCPU.wait>=0) {
						simCPU.setreg("TC",String.valueOf(Integer.parseInt(simCPU.registers.get("TC"))+1));
						}
						simCPU.wait--;
						
						//since the tick is issued after the first if statement, there is the case where the last tick
						//is the one where wait=0, so a command should be executed
						if(simCPU.wait == 0 && i+1==tickCount) {
							if(simCache.enabled) {
								simCPU.cachedexecute(simDataMemory,simIMemory,simCache);
							}
							else {
								simCPU.execute(simDataMemory,simIMemory);
							}
							
							simCPU.fetch(simIMemory, simDataMemory, simCache);
						}
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
					simDataMemory.create(Integer.parseInt(reader.next().substring(2),16));
					break;
				case "reset": 
					simDataMemory.reset();
					break;
				case "dump":
					simDataMemory.dump(reader.next(), reader.next());
					break;
				case "set":
					address = reader.next();
					//command used to tell what type of set command should be used
					command = reader.next();
					// two possible input methods for set command: file version and list version are handled through if else statement
					if(command.equals("file")) {
						bytedata = new File(reader.next());
						simDataMemory.set(address,bytedata);
					}
					else {
						hexCount = Integer.parseInt(command.substring(2),16);
						bytearray = new String[hexCount];
						for(int i=0;i<hexCount;i++) {
							bytearray[i] = reader.next();
						}
						simDataMemory.set(address,hexCount,bytearray);
					}
					break;
				// default only reached if none of the valid commands are recognized
				default: System.out.println("INVALID DATA MEMORY COMMAND");
					break;
				}
			}
			
			else if(device.equals("imemory")) {
				switch (command) {
				case "create":
					simIMemory.create(Integer.parseInt(reader.next().substring(2),16));
					break;
				case "reset": 
					simIMemory.reset();
					break;
				case "dump":
					simIMemory.dump(reader.next(), reader.next());
					break;
				case "set":
					address = reader.next();
					//command used to tell what type of set command should be used
					command = reader.next();
					// two possible input methods for set command: file version and list version are handled through if else statement
					if(command.equals("file")) {
						bytedata = new File(reader.next());
						simIMemory.set(address,bytedata);
					}
					else {
						hexCount = Integer.parseInt(command.substring(2),16);
						Ibytearray = new String[hexCount];
						for(int i=0;i<hexCount;i++) {
							Ibytearray[i] = reader.next();
						}
						simIMemory.set(address,hexCount,Ibytearray);
					}
					break;
				// default only reached if none of the valid commands are recognized
				default: System.out.println("INVALID INSTRUCTION MEMORY COMMAND");
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
			
			else if(device.equals("cache")) {
				switch (command) {
				case "reset":
					simCache.reset();
					break;
				case "on":
					simCache.on();
					simCPU.cacheEnabled = true;
					break;
				case "off":
					simCache.off();
					simCPU.cacheEnabled = false;
					break;
				case "dump":
					simCache.dump();
					break;
				}				
			}
		}
	}
}
