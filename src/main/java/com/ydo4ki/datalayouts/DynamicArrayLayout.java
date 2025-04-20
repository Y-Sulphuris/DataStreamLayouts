package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.OptionalInt;

/**
 * A layout implementation for arrays with dynamic length.
 * This class provides serialization and deserialization support for arrays where the length
 * is determined at runtime. When writing an array, the length is written first as an integer,
 * followed by each element serialized using its layout.
 *
 * @param <T> The array type
 * @since 1.0.0
 * @author Sulphuris
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class DynamicArrayLayout<T> implements ArrayLayout<T> {
	/** The class of the array */
	protected final Class<T> arrayType;
	/** The layout for the array elements */
	protected final Layout elementLayout;
	
	/**
	 * Creates a new dynamic array layout with the specified element layout.
	 *
	 * @param arrayType The class of the array
	 * @param elementLayout The layout for the array elements
	 * @throws UnpureClassException If the provided class is not an array
	 * @since 1.0.0
	 */
	DynamicArrayLayout(Class<T> arrayType, Layout elementLayout) {
		if (!arrayType.isArray()) throw new UnpureClassException(arrayType, "array expected");
		this.arrayType = arrayType;
		this.elementLayout = elementLayout;
	}
	
	/**
	 * Converts this dynamic array layout to a static array layout with a fixed length.
	 *
	 * @param length The fixed length for the static array layout
	 * @return A static array layout with the specified length
	 * @since 1.0.0
	 */
	public StaticArrayLayout<T> toStaticLen(int length) {
		return new StaticArrayLayout<>(arrayType, elementLayout, length);
	}
	
	/**
	 * Writes an array to a data output stream.
	 * This method first writes the length of the array as an integer,
	 * then writes each element using its layout.
	 *
	 * @param array The array to write
	 * @param out The data output stream to write to
	 * @throws IOException If an I/O error occurs
	 * @since 1.0.0
	 */
	@Override
	public void write(T array, DataOutput out) throws IOException {
		out.writeInt(Array.getLength(array));
		for (int i = 0, Len = Array.getLength(array); i < Len; i++) {
			elementLayout.asObjectLayout().write(Array.get(array, i), out);
		}
	}
	
	/**
	 * Reads an array from a data input stream.
	 * This method first reads the length of the array as an integer,
	 * then reads each element using its layout.
	 *
	 * @param in The data input stream to read from
	 * @return The read array
	 * @throws IOException If an I/O error occurs
	 * @since 1.0.0
	 */
	@Override
	public T read(DataInput in) throws IOException {
		return readArray(in, in.readInt());
	}
	
	/**
	 * Reads an array of a specified length from a data input stream.
	 * This method creates a new array of the specified length and reads
	 * each element using its layout.
	 *
	 * @param in The data input stream to read from
	 * @param length The length of the array to read
	 * @return The read array
	 * @throws IOException If an I/O error occurs
	 * @since 1.0.0
	 */
	protected final T readArray(DataInput in, int length) throws IOException {
		T array = (T) Array.newInstance(arrayType.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, elementLayout.asObjectLayout().read(in));
		}
		return array;
	}
	
	/**
	 * Returns the size of this layout in bytes, or null if the size is dynamic.
	 * Since the length of the array is determined at runtime, this layout has a dynamic size.
	 *
	 * @return null, indicating a dynamic size
	 * @since 1.0.0
	 */
	@Override
	public OptionalInt size() {
		return OptionalInt.empty();
	}
	
	/**
	 * Returns the layout for the array elements.
	 *
	 * @return The layout for the array elements
	 * @since 1.0.0
	 */
	@Override
	public Layout<?> elementLayout() {
		return elementLayout;
	}
}
