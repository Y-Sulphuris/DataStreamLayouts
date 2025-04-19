package com.ydo4ki.datalayouts;

/**
 * Interface for raw objects that contain an array of arbitrary objects.
 * Raw objects are used to represent structured data without defining a specific class.
 * They can be used to store and retrieve data in a generic way, similar to a tuple or record.
 * 
 * <p>Raw objects are created by the {@link Layout#ofRaw} methods and can be used to
 * serialize and deserialize structured data without defining a specific class.</p>
 *
 * @since 1.2.0
 * @author Sulphuris
 */
public interface RawObject {
	/**
	 * Returns the number of fields in this raw object.
	 *
	 * @return The number of fields
	 * @since 1.2.0
	 */
	int fieldCount();
	
	/**
	 * Returns the value of the field at the specified index.
	 *
	 * @param index The index of the field
	 * @return The value of the field
	 * @throws IndexOutOfBoundsException If the index is out of range
	 * @since 1.2.0
	 */
	Object get(int index);
}

/**
 * Implementation of the RawObject interface.
 * This class stores the field values in an array and provides methods to access them.
 * 
 * <p>This class is used internally by the {@link Layout#ofRaw} methods to create raw objects.</p>
 *
 * @since 1.2.0
 * @author Sulphuris
 */
class RawObjectImpl implements RawObject {
	/** The array of field values */
	Object[] data;
	
	/**
	 * Creates a new raw object with the specified number of fields.
	 * All fields are initially set to null.
	 *
	 * @param fieldCount The number of fields
	 * @since 1.2.0
	 */
	RawObjectImpl(int fieldCount) {
		data = new Object[fieldCount];
	}
	
	/**
	 * Returns the number of fields in this raw object.
	 *
	 * @return The number of fields
	 * @since 1.2.0
	 */
	@Override
	public int fieldCount() {
		return data.length;
	}
	
	/**
	 * Returns the value of the field at the specified index.
	 *
	 * @param index The index of the field
	 * @return The value of the field
	 * @throws IndexOutOfBoundsException If the index is out of range
	 * @since 1.2.0
	 */
	@Override
	public Object get(int index) {
		return data[index];
	}
}