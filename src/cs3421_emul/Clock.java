package cs3421_emul;

public class Clock {
	// initially the clock tick will always be 0
	int counter = 0;
	
	public void reset() {
		// sets the counter to zero
		counter = 0;
	}
	
	public void tick(int tickCount) {
		// takes given positive integer indicating how many clock ticks should be issued to attached devices
		counter += tickCount; 
	}
	
	public void dump() {
		// shows "Clock: " followed by the value of the internal clock in decimal
		System.out.println("Clock: " + counter);
		System.out.println();
	}
}
