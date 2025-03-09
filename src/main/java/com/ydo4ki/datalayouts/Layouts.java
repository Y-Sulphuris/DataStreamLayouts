package com.ydo4ki.datalayouts;

import com.ydo4ki.datalayouts.annotation.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Modifier;
import java.util.*;

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
	private static final Map<AnnotatedFieldType, AnnotationPragma<?,?,?>> annotations = new HashMap<>();
	private static final Set<Class<? extends Annotation>> registeredAnnotations = new HashSet<>();
	
	static <T> Layout<T> applyAnnotations(Layout<T> layout, Annotation[] annotations, Class<T> clazz) {
		for (Annotation annotation : annotations) {
			@SuppressWarnings("unchecked")
			AnnotationPragma<Annotation,T,Layout<T>> pragma = (AnnotationPragma<Annotation,T,Layout<T>>) getPragma(annotation.annotationType(), clazz);
			
			if (pragma == null) {
				if (registeredAnnotations.contains(annotation.annotationType())) {
					throw new IllegalArgumentException("Invalid annotation " + annotation + " for " + clazz.getCanonicalName() + " field");
				}
				continue;
			}
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
	
	static synchronized <T> Layout.Of<T> get(Class<T> clazz, MethodHandles.Lookup lookup) {
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
	static <A extends Annotation> void registerAnnotation(Class<A> annotationType, Class<?> fieldType, AnnotationPragma<A,?,?> pragma) {
		AnnotatedFieldType type = new AnnotatedFieldType(annotationType, fieldType);
		
		if (annotations.containsKey(type))
			throw new IllegalArgumentException("This annotation is already registered");
		annotations.put(type, pragma);
		registeredAnnotations.add(annotationType);
	}
	
	@SuppressWarnings("unchecked")
	static <A extends Annotation> AnnotationPragma<A,?,?> getPragma(Class<A> annotationType, Class<?> fieldType) {
		return (AnnotationPragma<A,?,?>) annotations.get(new AnnotatedFieldType(annotationType, fieldType));
	}
	
	private static final class AnnotatedFieldType {
		final Class<? extends Annotation> annotationType;
		final Class<?> fieldType;
		final int hash;
		
		private AnnotatedFieldType(Class<? extends Annotation> annotationType, Class<?> fieldType) {
			this.annotationType = annotationType;
			this.fieldType = fieldType;
			this.hash = Objects.hash(annotationType, fieldType);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			AnnotatedFieldType that = (AnnotatedFieldType) o;
			return Objects.equals(annotationType, that.annotationType) && Objects.equals(fieldType, that.fieldType);
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
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
	
	
	
	private static final class UUIDLayout implements Layout.Of<UUID> {
		
		@Override
		public void write(UUID x, DataOutput out) throws IOException {
			out.writeLong(x.getMostSignificantBits());
			out.writeLong(x.getLeastSignificantBits());
		}
		
		@Override
		public UUID read(DataInput in) throws IOException {
			return new UUID(in.readLong(), in.readLong());
		}
		
		@Override
		public Integer size() {
			return 16;
		}
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
		Layout.bindTo(UUID.class, new UUIDLayout());
	}
	
	static {
		// well now it starts working
		Layout.bindAnnotationPragma(Encoding.class, String.class, Layouts::getEncodingLayout);
		Layout.bindAnnotationPragma(NullTerminated.class, String.class, Layouts::getNullTerminatedLayout);
		Layout.bindAnnotationPragma(Length.class, String.class, Layouts::getLengthLayout);
		Layout.bindAnnotationPragma(UnsignedByte.class, int.class, Layouts::getUnsignedByteLayout);
		Layout.bindAnnotationPragma(UnsignedShort.class, int.class, Layouts::getUnsignedShortLayout);
	}
	
	private static StringLayout getEncodingLayout(StringLayout l, Encoding encoding, Class<String> cls) {
		//noinspection DataFlowIssue
		if (!(l instanceof StringLayout))
			throw new IllegalArgumentException("Incomparable annotation: " + encoding);
		return l.updateEncoding(StringEncoding.get(encoding.value()));
	}
	private static StringLayout getNullTerminatedLayout(StringLayout l, NullTerminated nullTerminated, Class<String> cls) {
		if (!(l instanceof StringLayout.DynamicStringLayout))
			throw new IllegalArgumentException("Incomparable annotation: " + nullTerminated);
		return ((StringLayout.DynamicStringLayout)l).updateNullTerminated(nullTerminated.value());
	}
	private static StringLayout getLengthLayout(StringLayout l, Length length, Class<String> cls) {
		if (l instanceof StringLayout.DynamicStringLayout && !((StringLayout.DynamicStringLayout) l).isNullTerminated()) {
			return l.toStaticLen(length.value());
		}
		if (!(l instanceof StringLayout.StaticStringLayout))
			throw new IllegalArgumentException("Incomparable annotation: " + length);
		return ((StringLayout.StaticStringLayout)l).updateLength(length.value());
	}
	
	private static Layout<Integer> getUnsignedByteLayout(Layout<Integer> l, UnsignedByte annotation, Class<Integer> cls) {
		if (l instanceof Layout.OfInt) return new Layout.OfInt() {
			@Override
			public int read(DataInput in) throws IOException {
				return in.readUnsignedByte();
			}
			
			@Override
			public void write(int x, DataOutput out) throws IOException {
				out.writeByte(x);
			}
		};
		throw new IllegalArgumentException("Incomparable annotation: " + annotation);
	}
	
	private static Layout<Integer> getUnsignedShortLayout(Layout<Integer> l, UnsignedShort annotation, Class<Integer> cls) {
		if (l instanceof Layout.OfInt) return new Layout.OfInt() {
			@Override
			public int read(DataInput in) throws IOException {
				return in.readUnsignedShort();
			}
			
			@Override
			public void write(int x, DataOutput out) throws IOException {
				out.writeShort(x);
			}
		};
		throw new IllegalArgumentException("Incomparable annotation: " + annotation);
	}
}
