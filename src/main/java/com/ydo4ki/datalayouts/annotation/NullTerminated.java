package com.ydo4ki.datalayouts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @since 12/2/2024 7:04 PM
 * @author Sulphuris
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NullTerminated {
	boolean value() default true;
}
