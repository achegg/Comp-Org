package cs3421_emul;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class IODev {
	String ioreg;
	int ioindex = 0;
	Queue<String[]> schedule = new LinkedList<String[]>();
	String[] command = new String[4];
	
	public void dump () {
		System.out.println("IO Device: " + ioreg);
		System.out.println();
	}
	
	public void reset () {
		ioreg = "0x00";
	}
	
	public void load (File filename) {
		Scanner file = null;
		try {
			file = new Scanner(filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		while(file.hasNext()){
			command[0]=file.next();
			if(file.next().equals("write")) {
				command[1]="write";
				command[2]=file.next();
				command[3]=file.next();
			}
			else {
				command[1]="read";
				command[2]=file.next();
			}
			
			schedule.add(command);
		}
	}
	
	public void executeCommand (Memory mem) {
		if(schedule.peek()[1].equals("read")) {
			read(mem);
		}
		else if(schedule.peek()[1].equals("write")) {
			write(mem);
		}
	}
	
	public void read (Memory memory) {
		String address = schedule.remove()[2];
		ioreg = memory.mem[Integer.parseInt(address.substring(2),16)];
	}
	
	public void write (Memory memory) {
		String[] tofrom = schedule.remove();
		memory.mem[Integer.parseInt(tofrom[2].substring(2),16)] = tofrom[3];
	}
}
