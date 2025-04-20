package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.OptionalInt;

/**
 * A layout implementation for arrays with a fixed length.
 * This class extends {@link DynamicArrayLayout} but enforces a specific array length
 * during serialization and deserialization. If the provided array is shorter than
 * the specified length, it will be padded. If it's longer, it will be truncated.
 *
 * @param <T> The array type
 * @since 1.0.0
 * @author Sulphuris
 */
@SuppressWarnings({"rawtypes", "unchecked"})
final class StaticArrayLayout<T> extends DynamicArrayLayout<T> {
	private final int length;
	
	/**
	 * Creates a new static array layout with the specified element layout and fixed length.
	 *
	 * @param arrayType The class of the array
	 * @param elementLayout The layout for the array elements
	 * @param length The fixed length of the array
	 * @since 1.0.0
	 */
	StaticArrayLayout(Class<T> arrayType, Layout elementLayout, int length) {
		super(arrayType, elementLayout);
		this.length = length;
	}
	
	/**
	 * Writes an array to a data output stream.
	 * If the array is shorter than the specified length, it will be padded.
	 * If it's longer, it will be truncated.
	 *
	 * @param array The array to write
	 * @param out The data output stream to write to
	 * @throws IOException If an I/O error occurs
	 * @since 1.0.0
	 */
	@Override
	public void write(T array, DataOutput out) throws IOException {
		array = copyOf(array, length);
		for (int i = 0, Len = Array.getLength(array); i < Len; i++) {
			elementLayout.asObjectLayout().write(Array.get(array, i), out);
		}
	}
	
	/**
	 * Creates a copy of the array with the specified length.
	 * If the new length is greater than the original length, the extra elements will be initialized to their default values.
	 * If the new length is less than the original length, the array will be truncated.
	 *
	 * @param array The array to copy
	 * @param newLength The length of the new array
	 * @return A new array with the specified length
	 * @param <T> The array type
	 * @since 1.0.0
	 */
	private static <T> T copyOf(T array, int newLength) {
		int length = Array.getLength(array);
		if (length == newLength) return array;
		T newArray = (T)Array.newInstance(array.getClass().getComponentType(), newLength);
		//noinspection SuspiciousSystemArraycopy
		System.arraycopy(array, 0, newArray, 0, Math.min(length, newLength));
		return newArray;
	}
	
	/**
	 * Reads an array from a data input stream.
	 * The array will have the fixed length specified in the constructor.
	 *
	 * @param in The data input stream to read from
	 * @return The read array
	 * @throws IOException If an I/O error occurs
	 * @since 1.0.0
	 */
	@Override
	public T read(DataInput in) throws IOException {
		return readArray(in, length);
	}
	
	/**
	 * Returns the size of this layout in bytes, or null if the size is dynamic.
	 * The size is calculated as the element size multiplied by the fixed length.
	 * If the element size is dynamic, the size of this layout is also dynamic.
	 *
	 * @return The size in bytes, or null if the size is dynamic
	 * @since 1.0.0
	 */
	@Override
	public OptionalInt size() {
		OptionalInt elementSize = elementLayout().size();
		if (!elementSize.isPresent()) return OptionalInt.empty();
		return OptionalInt.of(elementSize.getAsInt() * length);
	}
}
