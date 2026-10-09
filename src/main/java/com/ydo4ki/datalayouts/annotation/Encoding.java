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
