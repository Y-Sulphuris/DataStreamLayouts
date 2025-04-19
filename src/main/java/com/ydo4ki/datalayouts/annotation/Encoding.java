package com.ydo4ki.datalayouts.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying the character encoding to use for string fields.
 * This annotation can be applied to string fields to specify which character encoding
 * should be used when serializing and deserializing the string.
 * 
 * <p>The value of this annotation should be the name of a registered string encoding,
 * such as "utf-8" or "utf-16". The encoding must be registered with the
 * {@link com.ydo4ki.datalayouts.StringEncoding#registerEncoding} method.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * public class Example {
 *     &#64;Encoding("utf-8")
 *     private String text;
 * }
 * </pre>
 *
 * @since 1.0.0
 * @author Sulphuris
 * @see com.ydo4ki.datalayouts.StringEncoding
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Encoding {
	/**
	 * The name of the character encoding to use.
	 * This should be the name of a registered string encoding,
	 * such as "utf-8" or "utf-16".
	 *
	 * @return The name of the character encoding
	 * @since 1.0.0
	 */
	String value();
}
