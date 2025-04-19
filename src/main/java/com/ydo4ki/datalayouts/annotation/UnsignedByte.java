package com.ydo4ki.datalayouts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying that an integer field should be treated as an unsigned byte.
 * This annotation can be applied to integer fields to indicate that they should be
 * serialized as unsigned bytes (0-255) rather than as full integers.
 * 
 * <p>When an integer is annotated with this annotation, it will be serialized as a single byte.
 * During deserialization, the byte will be read as an unsigned value (0-255) and stored in the
 * integer field.</p>
 * 
 * <p>This is useful for interoperability with other systems that use unsigned bytes,
 * or for reducing the size of serialized data when the values are known to be small.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Example {
 *     &#64;UnsignedByte
 *     private int smallValue; // Will be serialized as a single byte
 * }
 * </pre>
 *
 * @since 1.1.0
 * @author Sulphuris
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnsignedByte {
}
