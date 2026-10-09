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
 * Exception thrown when a class cannot be represented as a layout.
 * This exception is thrown when a class is not suitable for serialization or deserialization
 * using the DataStreamLayouts library. This can happen for various reasons, such as:
 * <ul>
 *   <li>Using a primitive class where an object class is expected</li>
 *   <li>Using an abstract class or interface where a concrete class is expected</li>
 *   <li>Using a non-array class where an array class is expected</li>
 * </ul>
 *
 * @since 1.0.0
 * @author Sulphuris
 */
public class UnpureClassException extends RuntimeException {
	/**
	 * Creates a new exception with a message that includes the class name and details.
	 *
	 * @param clazz The class that cannot be represented as a layout
	 * @param details Additional details about why the class cannot be represented as a layout
	 * @since 1.0.0
	 */
	public UnpureClassException(Class<?> clazz, String details) {
		super(clazz + " cannot be present as layout: " + details);
	}
	
	/**
	 * Creates a new exception with a message that includes the class name.
	 *
	 * @param clazz The class that cannot be represented as a layout
	 * @since 1.0.0
	 */
	public UnpureClassException(Class<?> clazz) {
		super(clazz + " cannot be present as layout");
	}
}
