package com.ydo4ki.datalayouts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying a fixed length for arrays and strings.
 * This annotation can be applied to array and string fields to specify a fixed length
 * that should be used when serializing and deserializing the field.
 * 
 * <p>For arrays, this annotation causes the array to be serialized with exactly the specified
 * number of elements. If the actual array is shorter, it will be padded with default values.
 * If it's longer, it will be truncated.</p>
 * 
 * <p>For strings, this annotation causes the string to be serialized with exactly the specified
 * number of characters. If the actual string is shorter, it will be padded with null characters.
 * If it's longer, it will be truncated.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Example {
 *     &#64;Length(10)
 *     private String fixedLengthString;
 *     
 *     &#64;Length(5)
 *     private int[] fixedLengthArray;
 * }
 * </pre>
 *
 * @since 1.0.0
 * @author Sulphuris
 * @see com.ydo4ki.datalayouts.StaticArrayLayout
 * @see com.ydo4ki.datalayouts.StringLayout.StaticStringLayout
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Length {
	/**
	 * The fixed length to use for the array or string.
	 * For arrays, this is the number of elements.
	 * For strings, this is the number of characters.
	 *
	 * @return The fixed length
	 * @since 1.0.0
	 */
	int value();
}
