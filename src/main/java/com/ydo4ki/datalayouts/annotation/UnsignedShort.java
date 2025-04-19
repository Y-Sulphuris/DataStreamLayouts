package com.ydo4ki.datalayouts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying that an integer field should be treated as an unsigned short.
 * This annotation can be applied to integer fields to indicate that they should be
 * serialized as unsigned shorts (0-65535) rather than as full integers.
 * 
 * <p>When an integer is annotated with this annotation, it will be serialized as two bytes.
 * During deserialization, the bytes will be read as an unsigned value (0-65535) and stored in the
 * integer field.</p>
 * 
 * <p>This is useful for interoperability with other systems that use unsigned shorts,
 * or for reducing the size of serialized data when the values are known to be within the
 * unsigned short range.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Example {
 *     &#64;UnsignedShort
 *     private int mediumValue; // Will be serialized as two bytes
 * }
 * </pre>
 *
 * @since 1.1.0
 * @author Sulphuris
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UnsignedShort {
}
