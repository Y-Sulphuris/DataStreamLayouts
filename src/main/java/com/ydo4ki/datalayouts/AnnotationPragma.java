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

import java.lang.annotation.Annotation;

/**
 * Interface for annotation pragmas that can modify layouts based on annotations.
 * Annotation pragmas are used to customize the behavior of layouts based on annotations
 * applied to fields or classes. They provide a way to extend the functionality of layouts
 * without modifying the layout classes themselves.
 * 
 * <p>For example, an annotation pragma might modify a string layout to use a specific encoding,
 * or an array layout to have a fixed length. Annotation pragmas are registered with the
 * {@link Layout#bindAnnotationPragma} method and are applied automatically when layouts are created.</p>
 *
 * @param <A> The annotation type
 * @param <T> The field type the annotation can be applied to
 * @param <L> The layout type
 * @since 1.0.0
 * @author Sulphuris
 */
public interface AnnotationPragma<A extends Annotation, T, L extends Layout<T>> {
	/**
	 * Modifies a layout based on an annotation.
	 * This method is called when a layout is created for a field or class that has
	 * the annotation this pragma is registered for. It should return a new layout
	 * that incorporates the behavior specified by the annotation.
	 *
	 * @param currentLayout The current layout
	 * @param annotation The annotation to apply
	 * @param fieldType The type of the field or class
	 * @return A new layout that incorporates the behavior specified by the annotation
	 * @since 1.0.0
	 */
	L getLayout(L currentLayout, A annotation, Class<T> fieldType);
}
