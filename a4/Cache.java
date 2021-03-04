package cs3421_emul;

public class Cache {
	boolean enabled = false;
	String[] cdata = new String[8];
	String[] cflags = new String[8];
	String CLO;
	boolean valid = false;
	boolean written = false;
	
	public void reset() {
		//disables cache, sets CLO to zero, and data to be invalid
		enabled = false;
		CLO = "0x00";
		for (int i = 0; i < 8; i++) {
			cflags[i]="I";
		}
		
	}

	public boolean on() {
		enabled = true;
		return enabled;
	}

	public boolean off() {
		enabled = false;
		return enabled;
		
	}

	public void dump() {
		System.out.print("CLO        : " + CLO);
		System.out.println();
		
		System.out.print("cache data :");
		for(int i = 0; i < 8; i++) {
			System.out.print(" " + cdata[i]);
		}
		System.out.println();
		
		System.out.print("Flags      :");
		for(int i=0; i < 8; i++) {
			System.out.print("   " + cflags[i] + " ");
		}
		System.out.println();
		
		System.out.println();
	}
	
	public boolean validdata() {
		valid = false;
		for(int i = 0; i < 8; i++) {
			if (cflags[i].equals("V")) {
				valid = true;
			}
		}
		return valid;
	}
		
	public int cread(String tgt, Memory datamem){
		int lineoffset = (int) Math.floor(Integer.parseInt(tgt,16)/8);
		if(tgt.equals("FF")) {
			for(int i = 0; i < 8; i++) {
				cflags[i] = "I";
			}
		}
		else if(lineoffset==Integer.valueOf(CLO.substring(2),16) && validdata()) {
			//CACHE HIT
			return Integer.parseInt(cdata[Integer.parseInt(tgt,16)-lineoffset*8].substring(2),16);
		}
		else {
			//CACHE MISS
			for(int i = 0; i < 8; i++) {
				if(Integer.parseInt(datamem.mem[lineoffset*8+i].substring(2),16)<16) {
					cdata[i] = "0x0" + datamem.mem[lineoffset*8 + i].substring(2);
				}
				else {
					cdata[i] = "0x" + datamem.mem[lineoffset*8 + i].substring(2);
				}
				cflags[i] = "V";
			}
			if(lineoffset<16) {CLO="0x0" + Integer.toHexString(lineoffset).toUpperCase();}
			else {CLO="0x"+Integer.toHexString(lineoffset).toUpperCase();}
			return Integer.parseInt(cdata[Integer.parseInt(tgt,16)-lineoffset*8].substring(2),16);
		}
		return 0;
	}
	
	public boolean cstore(String tgt, String src, Memory datamem) {
		int lineoffset;
		if(src.equals("FF")) {
			dump();
			//FORCE FLUSH, should take 5 ticks to write to dmemory
			flush(datamem);
			return false;
		}
		else {
			lineoffset = (int) Math.floor(Integer.parseInt(tgt,16)/8);
			if(lineoffset == Integer.parseInt(CLO.substring(2),16) || !validdata()) {
				//CACHE HIT CONDITION, takes 1 tick to insert into cache
				cdata[Integer.parseInt(tgt,16) - Integer.parseInt(CLO.substring(2),16)*8] = "0x"+src;
				cflags[Integer.parseInt(tgt,16) - Integer.parseInt(CLO.substring(2),16)*8] = "W";
				return true;
			}
			else {
				//CACHE MISS CONDITION, takes 5 ticks to insert into dmemory
				flush(datamem);
				if(lineoffset<16) {CLO = "0x0" + Integer.toHexString(lineoffset).toUpperCase();}
				else {CLO = "0x" + Integer.toHexString(lineoffset).toUpperCase();}
				cdata[Integer.parseInt(tgt,16) - Integer.parseInt(CLO.substring(2),16)*8] = "0x"+src;
				cflags[Integer.parseInt(tgt,16) - Integer.parseInt(CLO.substring(2),16)*8] = "W";
				return false;
			}
		}
	}
	
	public void flush(Memory dMemory) {
		int offset = Integer.parseInt(CLO.substring(2),16) * 8;
		
		
		for(int i = 0; i < 8; i++) {
			if (cflags[i].equals("W")) {
				dMemory.mem[offset+i] = cdata[i];
				cflags[i] = "V";
			}
		}
	}

	public boolean writeHit(String tgt) {
		int lineoffset = (int) Math.floor(Integer.parseInt(tgt.substring(2),16)/8);
		if(lineoffset == Integer.parseInt(CLO.substring(2),16) || !validdata()) {
			//CACHE HIT CONDITION, takes 1 tick to insert into cache
			return true;
		}
		return false;
	}

	public boolean readHit(String tgt) {
		int lineoffset = (int) Math.floor(Integer.parseInt(tgt.substring(2),16)/8);
		if(lineoffset==Integer.valueOf(CLO.substring(2),16) && validdata()) {
			//CACHE HIT
			return true;
		}
		return false;
	}
	
	
}
