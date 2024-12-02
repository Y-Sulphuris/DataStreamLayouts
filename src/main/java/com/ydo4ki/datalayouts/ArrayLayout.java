package com.ydo4ki.datalayouts;

/**
 * @since 12/2/2024 12:24 AM
 * @author Sulphuris
 */
public interface ArrayLayout<T> extends Layout.Of<T> {
	Layout<?> elementLayout();
}

