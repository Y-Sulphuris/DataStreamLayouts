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
