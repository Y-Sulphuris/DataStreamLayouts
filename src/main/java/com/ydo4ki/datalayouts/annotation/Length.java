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
