package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @since 12/3/2024 2:21 AM
 * @author Sulphuris
 */
class SkipLayout implements Layout.Of<Void> {
	private final int bytes;
	private final byte[] writeBytes;
	
	SkipLayout(int bytes) {
		this.bytes = bytes;
		this.writeBytes = new byte[bytes];
	}
	
	@Override
	public void write(Void x, DataOutput out) throws IOException {
		out.write(writeBytes);
	}
	
	@Override
	public Void read(DataInput in) throws IOException {
		in.skipBytes(bytes);
		return null;
	}
	
	
	@Override
	public Integer size() {
		return bytes;
	}
}
