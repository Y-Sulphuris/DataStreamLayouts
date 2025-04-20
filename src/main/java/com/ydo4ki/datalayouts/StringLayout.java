package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.OptionalInt;

/**
 * Abstract base class for layouts that represent strings.
 * This class provides common functionality for string serialization and deserialization,
 * with different implementations for dynamic and static length strings.
 * 
 * <p>String layouts handle the encoding and decoding of string data using a specified
 * {@link StringEncoding}. They can be configured to use different encodings, fixed lengths,
 * or null termination.</p>
 *
 * @since 1.0.0
 * @author Sulphuris
 */
public abstract class StringLayout implements Layout.Of<String> {
	/** The encoding to use for string serialization and deserialization */
	protected final StringEncoding encoding;
	
	/**
	 * Creates a new string layout with the specified encoding.
	 *
	 * @param encoding The encoding to use for string serialization and deserialization
	 * @since 1.0.0
	 */
	public StringLayout(StringEncoding encoding) {
		this.encoding = encoding;
	}
	
	/**
	 * Creates a new string layout with the specified encoding, preserving other settings.
	 *
	 * @param encoding The new encoding to use
	 * @return A new string layout with the specified encoding
	 * @since 1.0.0
	 */
	public abstract StringLayout updateEncoding(StringEncoding encoding);
	
	/**
	 * Returns the size of this layout in bytes, or null if the size is dynamic.
	 * Since the length of strings is generally determined at runtime, this layout has a dynamic size.
	 *
	 * @return null, indicating a dynamic size
	 * @since 1.0.0
	 */
	@Override
	public OptionalInt size() {
		return OptionalInt.empty();
	}
	
	/**
	 * Converts this string layout to a static length string layout.
	 *
	 * @param length The fixed length for the static string layout
	 * @return A static string layout with the specified length
	 * @since 1.0.0
	 */
	public abstract StringLayout toStaticLen(int length);
	
	/**
	 * Implementation of StringLayout for strings with dynamic length.
	 * This class provides serialization and deserialization support for strings where the length
	 * is determined at runtime. When writing a string, the length can be written first as an integer,
	 * or the string can be null-terminated.
	 *
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	static final class DynamicStringLayout extends StringLayout {
		/** Whether the string is null-terminated */
		private final boolean nullTerminated;
		
		/**
		 * Returns whether this layout uses null termination.
		 *
		 * @return true if the layout uses null termination, false otherwise
		 * @since 1.0.0
		 */
		public boolean isNullTerminated() {
			return nullTerminated;
		}
		
		/**
		 * Creates a new dynamic string layout with the specified encoding and null termination setting.
		 *
		 * @param encoding The encoding to use for string serialization and deserialization
		 * @param nullTerminated Whether the string is null-terminated
		 * @since 1.0.0
		 */
		public DynamicStringLayout(StringEncoding encoding, boolean nullTerminated) {
			super(encoding);
			this.nullTerminated = nullTerminated;
		}
		
		/**
		 * Creates a new dynamic string layout with the specified encoding, preserving the null termination setting.
		 *
		 * @param encoding The new encoding to use
		 * @return A new dynamic string layout with the specified encoding
		 * @since 1.0.0
		 */
		public StringLayout updateEncoding(StringEncoding encoding) {
			return new DynamicStringLayout(encoding, nullTerminated);
		}
		
		/**
		 * Converts this dynamic string layout to a static length string layout.
		 *
		 * @param length The fixed length for the static string layout
		 * @return A static string layout with the specified length
		 * @since 1.0.0
		 */
		@Override
		public StringLayout toStaticLen(int length) {
			return new StaticStringLayout(encoding, length);
		}
		
		/**
		 * Creates a new dynamic string layout with the specified null termination setting, preserving the encoding.
		 *
		 * @param nullTerminated The new null termination setting
		 * @return A new dynamic string layout with the specified null termination setting
		 * @since 1.0.0
		 */
		public StringLayout updateNullTerminated(boolean nullTerminated) {
			return new DynamicStringLayout(encoding, nullTerminated);
		}
		
		/**
		 * Writes a string to a data output stream.
		 * If the layout is not null-terminated, the length of the string is written first as an integer.
		 * Then each character is written using the specified encoding.
		 * If the layout is null-terminated, a null character is written at the end.
		 *
		 * @param x The string to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		@Override
		public void write(String x, DataOutput out) throws IOException {
			if (!nullTerminated) out.writeInt(x.length());
			for (char c : x.toCharArray()) {
				encoding.write(c, out);
			}
			if (nullTerminated) encoding.write('\0', out);
		}
		
		/**
		 * Reads a string from a data input stream.
		 * If the layout is null-terminated, characters are read until a null character is encountered.
		 * Otherwise, the length of the string is read first as an integer, then that many characters are read.
		 *
		 * @param in The data input stream to read from
		 * @return The read string
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
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
	
	/**
	 * Implementation of StringLayout for strings with a fixed length.
	 * This class provides serialization and deserialization support for strings with a fixed length.
	 * When writing a string, if it is shorter than the fixed length, it is padded with null characters.
	 * If it is longer, it is truncated.
	 *
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	static final class StaticStringLayout extends StringLayout {
		/** The fixed length of the string */
		private final int length;
		
		/**
		 * Creates a new static string layout with the specified encoding and fixed length.
		 *
		 * @param encoding The encoding to use for string serialization and deserialization
		 * @param length The fixed length of the string
		 * @since 1.0.0
		 */
		public StaticStringLayout(StringEncoding encoding, int length) {
			super(encoding);
			this.length = length;
		}
		
		/**
		 * Creates a new static string layout with the specified encoding, preserving the fixed length.
		 *
		 * @param encoding The new encoding to use
		 * @return A new static string layout with the specified encoding
		 * @since 1.0.0
		 */
		@Override
		public StringLayout updateEncoding(StringEncoding encoding) {
			return new StaticStringLayout(encoding, length);
		}
		
		/**
		 * Converts this static string layout to a static length string layout with a different length.
		 *
		 * @param length The new fixed length for the static string layout
		 * @return A static string layout with the specified length
		 * @since 1.0.0
		 */
		@Override
		public StringLayout toStaticLen(int length) {
			return updateLength(length);
		}
		
		/**
		 * Creates a new static string layout with the specified fixed length, preserving the encoding.
		 *
		 * @param length The new fixed length
		 * @return A new static string layout with the specified fixed length
		 * @since 1.0.0
		 */
		public StringLayout updateLength(int length) {
			return new StaticStringLayout(encoding, length);
		}
		
		/**
		 * Writes a string to a data output stream.
		 * If the string is shorter than the fixed length, it is padded with null characters.
		 * If it is longer, it is truncated.
		 *
		 * @param x The string to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
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
		
		/**
		 * Reads a string from a data input stream.
		 * This method reads exactly the fixed number of characters, then trims any trailing null characters.
		 *
		 * @param in The data input stream to read from
		 * @return The read string
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
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
