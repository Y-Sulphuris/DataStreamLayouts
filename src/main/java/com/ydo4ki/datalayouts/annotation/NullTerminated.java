package com.ydo4ki.datalayouts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying that strings should be null-terminated.
 * This annotation can be applied to string fields to indicate that they should be
 * serialized as null-terminated strings, rather than with a length prefix.
 * 
 * <p>When a string is null-terminated, it is serialized by writing each character
 * followed by a null character (0) at the end. When deserializing, characters are
 * read until a null character is encountered.</p>
 * 
 * <p>This format is commonly used in C/C++ and other languages where strings are
 * represented as null-terminated character arrays.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Example {
 *     &#64;NullTerminated
 *     private String cStyleString;
 *     
 *     &#64;NullTerminated(false)
 *     private String normalString; // Same as not using the annotation
 * }
 * </pre>
 *
 * @since 1.0.0
 * @author Sulphuris
 * @see com.ydo4ki.datalayouts.StringLayout.DynamicStringLayout
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NullTerminated {
	/**
	 * Whether the string should be null-terminated.
	 * If true, the string will be serialized as a null-terminated string.
	 * If false, the string will be serialized with a length prefix.
	 *
	 * @return true if the string should be null-terminated, false otherwise
	 * @since 1.0.0
	 */
	boolean value() default true;
}
