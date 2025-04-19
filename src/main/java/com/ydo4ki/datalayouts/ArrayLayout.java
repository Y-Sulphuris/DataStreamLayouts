package com.ydo4ki.datalayouts;

/**
 * Interface for layouts that represent arrays.
 * This interface extends {@link Layout.Of} to provide additional functionality specific to arrays,
 * such as accessing the layout of the array elements.
 * 
 * <p>Array layouts are used for serializing and deserializing arrays of any type.
 * They handle both primitive and object arrays, and can be either static (fixed length)
 * or dynamic (variable length).</p>
 *
 * @param <T> The array type
 * @since 1.0.0
 * @author Sulphuris
 */
public interface ArrayLayout<T> extends Layout.Of<T> {
	/**
	 * Returns the layout for the array elements.
	 * This layout is used to serialize and deserialize individual elements of the array.
	 *
	 * @return The layout for the array elements
	 * @since 1.0.0
	 */
	Layout<?> elementLayout();
}
