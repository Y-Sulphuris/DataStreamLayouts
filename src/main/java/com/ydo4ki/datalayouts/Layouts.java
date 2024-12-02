package com.ydo4ki.datalayouts;

import com.ydo4ki.datalayouts.annotations.Encoding;
import com.ydo4ki.datalayouts.annotations.NullTerminated;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since 12/2/2024 6:05 PM
 * @author Sulphuris
 */
class Layouts {
	private Layouts() throws InstantiationException {
		throw new InstantiationException();
	}
	
	
	private static final Map<Class<?>, Layout.Of<?>> layouts = new HashMap<>();
	private static final Map<Class<?>, Layout.Of<?>> virtualLayouts = new HashMap<>();
	private static final Map<Class<? extends Annotation>, AnnotationPragma<?,?,?>> annotations = new HashMap<>();
	
	static <T> Layout<T> applyAnnotations(Layout<T> layout, Annotation[] annotations, Class<T> clazz) {
		for (Annotation annotation : annotations) {
			@SuppressWarnings("unchecked")
			AnnotationPragma<Annotation,T,Layout<T>> pragma = (AnnotationPragma<Annotation,T,Layout<T>>) getPragma(annotation.annotationType());
			
			if (pragma == null)
				continue;
			layout = pragma.getLayout(layout, annotation, clazz);
		}
		return layout;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> Layout.Of<T> getLayoutIfExists(Class<T> clazz) {
		Layout.Of<T> layout = (Layout.Of<T>)layouts.get(Objects.requireNonNull(clazz));
		if (layout != null) return layout;
		
		return (Layout.Of<T>) findLayoutForVirtual(clazz);
	}
	
	private static Layout.Of<?> findLayoutForVirtual(Class<?> clazz) {
		Layout.Of<?> layout = virtualLayouts.get(clazz);
		if (layout != null) return layout;
		
		if (clazz.getSuperclass() != null) {
			layout = findLayoutForVirtual(clazz.getSuperclass());
			if (layout != null) return layout;
		}
		
		for (Class<?> anInterface : clazz.getInterfaces()) {
			layout = findLayoutForVirtual(anInterface);
			if (layout != null) return layout;
		}
		return layout;
	}
	
	static <T> Layout.Of<T> get(Class<T> clazz, MethodHandles.Lookup lookup) {
		Layout.Of<T> layout = getLayoutIfExists(clazz);
		if (layout == null) {
			if (clazz.getName().startsWith("java."))
				throw new IllegalArgumentException("Unbound class from stdlib: " + clazz);
			if (clazz.isArray()) {
				layout = new DynamicArrayLayout<>(clazz, Layout.of(clazz.getComponentType()));
			} else {
				layout = new ClassLayout<>(clazz, lookup);
			}
			bind(clazz, layout);
		}
		return layout;
	}
	
	static <T> void bind(Class<T> clazz, Layout.Of<T> layout) {
		if (layouts.containsKey(clazz))
			throw new IllegalArgumentException(clazz + " already has a layout");
		if (!clazz.isPrimitive() && !clazz.isArray()) if (clazz.isInterface() || (clazz.getModifiers() & Modifier.ABSTRACT) != 0)
			throw new UnpureClassException(clazz, "not finished classes are not allowed");
		layouts.put(clazz, layout);
	}
	static <T> void bindVirtual(Class<T> clazz, Layout.Of<T> layout) {
		if (virtualLayouts.containsKey(clazz))
			throw new IllegalArgumentException(clazz + " already has a virtual layout");
		virtualLayouts.put(clazz, layout);
	}
	
	// func type = Layout<T>(Class<?> targetType, T annotationItself)
	static <A extends Annotation> void registerAnnotation(Class<A> annotationType, AnnotationPragma<A,?,?> pragma) {
		annotations.put(annotationType, pragma);
	}
	
	@SuppressWarnings("unchecked")
	static <A extends Annotation> AnnotationPragma<A,?,?> getPragma(Class<A> annotationType) {
		return (AnnotationPragma<A,?,?>) annotations.get(annotationType);
	}
	
	static Layout<?> primitiveLayout(Class<?> clazz) {
		assert clazz.isPrimitive();
		if (clazz == byte.class)    return Layout.ofByte;
		if (clazz == boolean.class) return Layout.ofBoolean;
		if (clazz == short.class)   return Layout.ofShort;
		if (clazz == char.class)    return Layout.ofChar;
		if (clazz == int.class)     return Layout.ofInt;
		if (clazz == float.class)   return Layout.ofFloat;
		if (clazz == long.class)    return Layout.ofLong;
		if (clazz == double.class)  return Layout.ofDouble;
		throw new AssertionError();
	}
	
	
	
	
	static {
		Layout.bindTo(Boolean.class, Layout.ofBoolean.asObjectLayout());
		Layout.bindTo(Byte.class, Layout.ofByte.asObjectLayout());
		Layout.bindTo(Short.class, Layout.ofShort.asObjectLayout());
		Layout.bindTo(Character.class, Layout.ofChar.asObjectLayout());
		Layout.bindTo(Integer.class, Layout.ofInt.asObjectLayout());
		Layout.bindTo(Float.class, Layout.ofFloat.asObjectLayout());
		Layout.bindTo(Long.class, Layout.ofLong.asObjectLayout());
		Layout.bindTo(Double.class, Layout.ofDouble.asObjectLayout());
		
		Layout.bindTo(String.class, Layout.ofString);
	}
	
	static {
		// well now it starts working
		Layout.bindAnnotationPragma(Encoding.class, Layouts::getEncodingLayout);
		Layout.bindAnnotationPragma(NullTerminated.class, Layouts::getNullTerminatedLayout);
	}
	
	private static StringLayout getEncodingLayout(StringLayout l, Encoding encoding, Class<String> cls) {
		//noinspection DataFlowIssue
		if (!(l instanceof StringLayout) || cls != String.class)
			throw new IllegalArgumentException("Invalid class or invalid string layout or invalid idk what, encoding is unsupported here anyways");
		return l.updateEncoding(StringEncoding.get(encoding.value()));
	}
	private static StringLayout getNullTerminatedLayout(StringLayout l, NullTerminated nullTerminated, Class<String> cls) {
		//noinspection DataFlowIssue
		if (!(l instanceof StringLayout) || cls != String.class)
			throw new IllegalArgumentException("Invalid class or invalid string layout or invalid idk what, null-terminated is unsupported here anyways");
		return l.updateNullTerminated(nullTerminated.value());
	}
}
