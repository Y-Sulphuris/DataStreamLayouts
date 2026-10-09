/*
 * MIT License
 *
 * Copyright (c) 2024-2026 Sulphuris
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.OptionalInt;

/**
 * A layout implementation for skipping a specified number of bytes in a data stream.
 * This class is used for padding or alignment purposes, or for skipping over unused data
 * in a binary format. When writing, it writes zeros for the specified number of bytes.
 * When reading, it skips the specified number of bytes.
 *
 * @since 1.1.0
 * @author Sulphuris
 */
final class SkipLayout implements Layout.Of<Void> {
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
	public OptionalInt size() {
		return OptionalInt.of(bytes);
	}
}
