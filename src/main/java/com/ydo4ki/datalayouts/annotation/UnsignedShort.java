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
