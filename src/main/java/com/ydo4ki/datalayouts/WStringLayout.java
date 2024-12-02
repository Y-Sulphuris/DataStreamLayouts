package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @since 12/2/2024 1:21 AM
 * @author Sulphuris
 */
class WStringLayout implements Layout.Of<String> {
	@Override
	public void write(String x, DataOutput out) throws IOException {
		out.writeInt(x.length());
		for (char c : x.toCharArray()) {
			out.writeChar(c);
		}
	}
	
	@Override
	public String read(DataInput in) throws IOException {
		int len = in.readInt();
		char[] data = new char[len];
		for (int i = 0; i < data.length; i++) {
			data[i] = in.readChar();
		}
		return String.valueOf(data);
	}
	
	@Override
	public Integer size() {
		return null;
	}
}
