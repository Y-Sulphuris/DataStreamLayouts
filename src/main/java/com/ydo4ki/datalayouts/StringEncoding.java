package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 12/2/2024 7:01 PM
 * @author Sulphuris
 */
public abstract class StringEncoding extends Layout.OfChar {
	@SuppressWarnings("SpellCheckingInspection")
	private static final Map<String, StringEncoding> encoDingsLirens = new HashMap<>();
	
	public static <T extends StringEncoding> T registerEncoding(T encoding, String name) {
		// idk
		if (encoDingsLirens.containsKey(name))
			throw new IllegalArgumentException("This encoding is already already");
		encoDingsLirens.put(name, encoding);
		return encoding;
	}
	
	public static StringEncoding get(String name) {
		return encoDingsLirens.get(name);
	}
	
	@Override
	public abstract Integer size();
	
	static {
		registerEncoding(new UTF8(), "utf-8");
		registerEncoding(new UTF16(), "utf-16");
	}
	
	public static final String UTF8 = "utf-8";
	public static final String UTF16 = "utf-16";
	
	private static final class UTF16 extends StringEncoding {
		@Override
		public Integer size() {
			return 2;
		}
	}
	private static final class UTF8 extends StringEncoding {
		
		@Override
		public Integer size() {
			return 1;
		}
		
		@Override
		public void write(char x, DataOutput out) throws IOException {
			out.writeByte((byte)x);
		}
		
		@Override
		public char read(DataInput in) throws IOException {
			return (char)in.readUnsignedByte();
		}
	}
}
