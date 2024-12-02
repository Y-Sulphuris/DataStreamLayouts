package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringLayout implements Layout.Of<String> {
	@Override
	public void write(String x, DataOutput out) throws IOException {
		out.writeInt(x.length());
		for (char c : x.toCharArray()) {
			out.writeByte((byte)c);
		}
	}
	
	@Override
	public String read(DataInput in) throws IOException {
		int len = in.readInt();
		char[] data = new char[len];
		for (int i = 0; i < data.length; i++) {
			data[i] = (char)in.readUnsignedByte();
		}
		return String.valueOf(data);
	}
	
	@Override
	public Integer size() {
		return null;
	}
}
