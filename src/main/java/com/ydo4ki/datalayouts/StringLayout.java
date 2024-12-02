package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringLayout implements Layout.Of<String> {
	private final StringEncoding encoding;
	private final boolean nullTerminated;
	
	public StringLayout(StringEncoding encoding, boolean nullTerminated) {
		this.encoding = encoding;
		this.nullTerminated = nullTerminated;
	}
	
	public StringLayout updateEncoding(StringEncoding encoding) {
		return new StringLayout(encoding, nullTerminated);
	}
	public StringLayout updateNullTerminated(boolean nullTerminated) {
		return new StringLayout(encoding, nullTerminated);
	}
	
	@Override
	public void write(String x, DataOutput out) throws IOException {
		if (!nullTerminated) out.writeInt(x.length());
		for (char c : x.toCharArray()) {
			encoding.write(c, out);
		}
		if (nullTerminated) encoding.write('\0', out);
	}
	
	@Override
	public String read(DataInput in) throws IOException {
		if (nullTerminated) {
			StringBuilder builder = new StringBuilder();
			char ch = encoding.read(in);
			while (ch != '\0') {
				builder.append(ch);
				ch = encoding.read(in);
			}
			return builder.toString();
		} else {
			int len = in.readInt();
			char[] data = new char[len];
			for (int i = 0; i < data.length; i++) {
				data[i] = encoding.read(in);
			}
			return String.valueOf(data);
		}
	}
	
	@Override
	public Integer size() {
		return null;
	}
}
