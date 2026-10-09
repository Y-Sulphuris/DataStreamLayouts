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