package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Abstract base class for string character encodings.
 * This class provides functionality for encoding and decoding characters in strings
 * when serializing and deserializing them. Different implementations can support
 * various character encodings such as UTF-8 and UTF-16.
 * 
 * <p>String encodings are registered by name and can be retrieved using the {@link #get(String)}
 * method. The library provides built-in encodings for UTF-8 and UTF-16.</p>
 *
 * @since 1.0.0
 * @author Sulphuris
 */
public abstract class StringEncoding extends Layout.OfChar {
	@SuppressWarnings("SpellCheckingInspection")
	private static final Map<String, StringEncoding> encoDingsLirens = new HashMap<>();
	
	/**
	 * Registers a string encoding with the specified name.
	 * This method adds the encoding to the registry so it can be retrieved later using {@link #get(String)}.
	 *
	 * @param <T> The type of string encoding
	 * @param encoding The encoding to register
	 * @param name The name to register the encoding under
	 * @return The registered encoding
	 * @throws IllegalArgumentException If an encoding with the specified name is already registered
	 * @since 1.0.0
	 */
	public static <T extends StringEncoding> T registerEncoding(T encoding, String name) {
		// idk
		if (encoDingsLirens.containsKey(name))
			throw new IllegalArgumentException("This encoding is already already");
		encoDingsLirens.put(name, encoding);
		return encoding;
	}
	
	/**
	 * Retrieves a string encoding by name.
	 * This method returns the encoding registered with the specified name, or null if no such encoding exists.
	 *
	 * @param name The name of the encoding to retrieve
	 * @return The encoding with the specified name, or null if no such encoding exists
	 * @since 1.0.0
	 */
	public static StringEncoding get(String name) {
		return encoDingsLirens.get(name);
	}
	
	/**
	 * Returns the size of this encoding in bytes.
	 * This is the number of bytes used to represent a single character in this encoding.
	 *
	 * @return The size of this encoding in bytes
	 * @since 1.0.0
	 */
	@Override
	public abstract OptionalInt size();
	
	static {
		registerEncoding(new UTF8(), "utf-8");
		registerEncoding(new UTF16(), "utf-16");
	}
	
	/** The name of the UTF-8 encoding */
	public static final String UTF8 = "utf-8";
	/** The name of the UTF-16 encoding */
	public static final String UTF16 = "utf-16";
	
	/**
	 * Implementation of StringEncoding for UTF-16 encoding.
	 * This encoding uses 2 bytes per character.
	 *
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	private static final class UTF16 extends StringEncoding {
		/**
		 * Returns the size of this encoding in bytes.
		 * UTF-16 uses 2 bytes per character.
		 *
		 * @return 2, the number of bytes used per character
		 * @since 1.0.0
		 */
		@Override
		public OptionalInt size() {
			return OptionalInt.of(2);
		}
	}
	
	/**
	 * Implementation of StringEncoding for UTF-8 encoding.
	 * This implementation is simplified and uses 1 byte per character, which is only
	 * suitable for ASCII characters. A full UTF-8 implementation would use variable-length
	 * encoding (1-4 bytes per character).
	 *
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	private static final class UTF8 extends StringEncoding {
		/**
		 * Returns the size of this encoding in bytes.
		 * This simplified UTF-8 implementation uses 1 byte per character.
		 *
		 * @return 1, the number of bytes used per character
		 * @since 1.0.0
		 */
		@Override
		public OptionalInt size() {
			return OptionalInt.of(1);
		}
		
		/**
		 * Writes a character to a data output stream using UTF-8 encoding.
		 * This simplified implementation only writes the lower 8 bits of the character,
		 * which is only suitable for ASCII characters.
		 *
		 * @param x The character to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		@Override
		public void write(char x, DataOutput out) throws IOException {
			out.writeByte((byte)x);
		}
		
		/**
		 * Reads a character from a data input stream using UTF-8 encoding.
		 * This simplified implementation reads a single byte and converts it to a character,
		 * which is only suitable for ASCII characters.
		 *
		 * @param in The data input stream to read from
		 * @return The read character
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		@Override
		public char read(DataInput in) throws IOException {
			return (char)in.readUnsignedByte();
		}
	}
}
