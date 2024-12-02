package com.ydo4ki.datalayouts;

import java.lang.annotation.Annotation;

/**
 * @since 12/2/2024 5:53 PM
 * @author Sulphuris
 */
public interface AnnotationPragma<A extends Annotation, T, L extends Layout<T>> {
	L getLayout(L currentLayout, A annotation, Class<T> fieldType);
}
