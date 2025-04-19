package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * A layout implementation for skipping a specified number of bytes in a data stream.
 * This class is used for padding or alignment purposes, or for skipping over unused data
 * in a binary format. When writing, it writes zeros for the specified number of bytes.
 * When reading, it skips the specified number of bytes.
 *
 * @since 1.1.0
 * @author Sulphuris
 */
class SkipLayout implements Layout.Of<Void> {
	/** The number of bytes to skip */
	private final int bytes;
	/** A byte array of zeros to write when skipping */
	private final byte[] writeBytes;
	
	/**
	 * Creates a new skip layout with the specified number of bytes to skip.
	 *
	 * @param bytes The number of bytes to skip
	 * @since 1.1.0
	 */
	SkipLayout(int bytes) {
		this.bytes = bytes;
		this.writeBytes = new byte[bytes];
	}
	
	/**
	 * Writes zeros for the specified number of bytes to a data output stream.
	 * The input parameter is ignored since this layout does not represent any data.
	 *
	 * @param x The input parameter (ignored)
	 * @param out The data output stream to write to
	 * @throws IOException If an I/O error occurs
	 * @since 1.1.0
	 */
	@Override
	public void write(Void x, DataOutput out) throws IOException {
		out.write(writeBytes);
	}
	
	/**
	 * Skips the specified number of bytes from a data input stream.
	 * This method always returns null since this layout does not represent any data.
	 *
	 * @param in The data input stream to read from
	 * @return null
	 * @throws IOException If an I/O error occurs
	 * @since 1.1.0
	 */
	@Override
	public Void read(DataInput in) throws IOException {
		in.skipBytes(bytes);
		return null;
	}
	
	/**
	 * Returns the size of this layout in bytes.
	 * This is the number of bytes that will be skipped.
	 *
	 * @return The number of bytes to skip
	 * @since 1.1.0
	 */
	@Override
	public Integer size() {
		return bytes;
	}
}
