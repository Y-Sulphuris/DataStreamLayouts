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
