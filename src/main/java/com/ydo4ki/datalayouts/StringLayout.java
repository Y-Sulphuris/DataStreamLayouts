package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;


public abstract class StringLayout implements Layout.Of<String> {
	protected final StringEncoding encoding;
	
	public StringLayout(StringEncoding encoding) {
		this.encoding = encoding;
	}
	
	public abstract StringLayout updateEncoding(StringEncoding encoding);
	
	
	@Override
	public Integer size() {
		return null;
	}
	
	public abstract StringLayout toStaticLen(int length);
	
	static final class DynamicStringLayout extends StringLayout {
		private final boolean nullTerminated;
		
		public boolean isNullTerminated() {
			return nullTerminated;
		}
		
		public DynamicStringLayout(StringEncoding encoding, boolean nullTerminated) {
			super(encoding);
			this.nullTerminated = nullTerminated;
		}
		
		public StringLayout updateEncoding(StringEncoding encoding) {
			return new DynamicStringLayout(encoding, nullTerminated);
		}
		
		@Override
		public StringLayout toStaticLen(int length) {
			return new StaticStringLayout(encoding, length);
		}
		
		public StringLayout updateNullTerminated(boolean nullTerminated) {
			return new DynamicStringLayout(encoding, nullTerminated);
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
	}
	
	static final class StaticStringLayout extends StringLayout {
		private final int length;
		
		public StaticStringLayout(StringEncoding encoding, int length) {
			super(encoding);
			this.length = length;
		}
		
		@Override
		public StringLayout updateEncoding(StringEncoding encoding) {
			return new StaticStringLayout(encoding, length);
		}
		
		@Override
		public StringLayout toStaticLen(int length) {
			return updateLength(length);
		}
		
		public StringLayout updateLength(int length) {
			return new StaticStringLayout(encoding, length);
		}
		
		@Override
		public void write(String x, DataOutput out) throws IOException {
			int actualLen = Math.min(x.length(), length);
			int i;
			for (i = 0; i < actualLen; i++) {
				encoding.write(x.charAt(i), out);
			}
			for (; i < length; i++) {
				encoding.write('\0', out);
			}
		}
		
		@Override
		public String read(DataInput in) throws IOException {
			char[] data = new char[length];
			int lastSymbol = -1;
			for (int i = 0; i < length; i++) {
				char ch = encoding.read(in);
				data[i] = ch;
				if (ch != '\0') lastSymbol = i;
			}
			data = Arrays.copyOf(data, lastSymbol + 1);
			return String.valueOf(data);
		}
	}
}
