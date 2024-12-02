package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

// this must be writable to a stream itself btw
/**
 * @since 12/1/2024 10:43 PM
 * @author Sulphuris
 */
class ClassLayout<T> implements Layout.Of<T> {
	private final Class<T> clazz;
	private final MethodHandle constructor;
	private final MethodHandle[] getters;
	private final MethodHandle[] setters;
	private final Layout<?>[] fieldLayouts;
	private final Integer sizeof;
	
	public int fieldsCount() {
		return fieldLayouts.length;
	}
	
	ClassLayout(Class<T> clazz, MethodHandles.Lookup lookup) {
		this.clazz = clazz;
		ArrayList<Field> fields = collectFields(clazz);
		try {
			this.getters = find(fields, lookup, MethodHandles.Lookup::unreflectGetter);
			this.setters = find(fields, lookup, MethodHandles.Lookup::unreflectSetter);
			this.constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class)); // wow i didn't expect that already
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		this.fieldLayouts = toLayouts(fields);
		
		Integer size = 0;
		for (Layout<?> fieldLayout : fieldLayouts) {
			Integer fieldSize = fieldLayout.size();
			if (fieldSize == null) {
				size = null;
				break;
			}
			size += fieldSize;
		}
		
		this.sizeof = size;
	}
	
	private static Layout<?>[] toLayouts(ArrayList<Field> fields) {
		Layout<?>[] layouts = new Layout[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			Class<?> type = fields.get(i).getType();
			layouts[i] = Layout.of(Objects.requireNonNull(type));
		}
		return layouts;
	}
	
	private static ArrayList<Field> collectFields(Class<?> clazz) {
		ArrayList<Field> fields = new ArrayList<>();
		Objects.requireNonNull(clazz, "clazz is null");
		while (clazz != Object.class && clazz != null) {
			for (Field declaredField : clazz.getDeclaredFields()) {
				if ((declaredField.getModifiers() & Modifier.STATIC) != 0)
					continue;
				fields.add(declaredField);
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}
	
	@FunctionalInterface
	private interface Unreflector {
		MethodHandle unreflect(MethodHandles.Lookup lookup, Field field) throws IllegalAccessException;
	}
	
	private static MethodHandle[] find(ArrayList<Field> fields, MethodHandles.Lookup lookup, Unreflector unreflector) throws IllegalAccessException {
		MethodHandle[] getters = new MethodHandle[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			// hm should i try to set accessible flag to true first
			// probably not
			fields.get(i).setAccessible(true);
			getters[i] = unreflector.unreflect(lookup, fields.get(i)); // BRUH SO WHY I STILL CAN'T MAKE FIELDS @Stable
		}
		return getters;
	}
	
	
	
	
	
	
	private static final Map<Class<?>, Layout.Of<?>> layouts = new HashMap<>();
	private static final Map<Class<?>, Layout.Of<?>> virtualLayouts = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	private static <T> Layout.Of<T> getLayoutIfExists(Class<T> clazz) {
		Layout.Of<T> layout = (Layout.Of<T>)layouts.get(Objects.requireNonNull(clazz));
		if (layout != null) return layout;
		
		return (Of<T>) findLayoutForVirtual(clazz);
	}
	
	private static Layout.Of<?> findLayoutForVirtual(Class<?> clazz) {
		Layout.Of<?> layout = virtualLayouts.get(clazz);
		if (layout != null) return layout;
		
		layout = findLayoutForVirtual(clazz);
		if (layout != null) return layout;
		
		for (Class<?> anInterface : clazz.getInterfaces()) {
			layout = findLayoutForVirtual(anInterface);
			if (layout != null) return layout;
		}
		return layout;
	}
	
	
	
	static <T> Layout.Of<T> get(Class<T> clazz, MethodHandles.Lookup lookup) {
		Layout.Of<T> layout = getLayoutIfExists(clazz);
		if (layout == null) {
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
		if (clazz.isInterface() || (clazz.getModifiers() & Modifier.ABSTRACT) != 0)
			throw new UnpureClassException(clazz, "not finished classes are not allowed");
		layouts.put(clazz, layout);
	}
	static <T> void bindVirtual(Class<T> clazz, Layout.Of<T> layout) {
		if (virtualLayouts.containsKey(clazz))
			throw new IllegalArgumentException(clazz + " already has a layout");
		virtualLayouts.put(clazz, layout);
	}
	
	
	@Override
	public void write(T x, DataOutput out) throws IOException {
		for (int i = 0, Len = fieldsCount(); i < Len; i++) {
			try {
				write(fieldLayouts[i], x, getters[i], out);
			} catch (IOException | RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				throw new RuntimeException(fieldLayouts[i].toString(), e); // I have no idea
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T read(DataInput in) throws IOException {
		try {
			T newInstance = (T) constructor.invoke();
			
			for (int i = 0, Len = fieldsCount(); i < Len; i++) {
				read(fieldLayouts[i], newInstance, setters[i], in);
			}
			
			return newInstance;
		} catch (IOException | RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e); // i still have no idea
		}
	}
	
	
	private static void write(Layout<?> layout, Object x, MethodHandle getter, DataOutput out) throws Throwable {
		if (layout == Layout.ofBoolean) {
			Layout.ofBoolean.write((boolean)getter.invoke(x), out);
		} else if (layout == Layout.ofByte) {
			Layout.ofByte.write((byte)getter.invoke(x), out);
		} else if (layout == Layout.ofShort) {
			Layout.ofShort.write((short)getter.invoke(x), out);
		} else if (layout == Layout.ofChar) {
			Layout.ofChar.write((char)getter.invoke(x), out);
		} else if (layout == Layout.ofFloat) {
			Layout.ofFloat.write((float)getter.invoke(x), out);
		} else if (layout == Layout.ofInt) {
			Layout.ofInt.write((int)getter.invoke(x), out);
		} else if (layout == Layout.ofLong) {
			Layout.ofLong.write((long)getter.invoke(x), out);
		} else if (layout == Layout.ofDouble) {
			Layout.ofDouble.write((double)getter.invoke(x), out);
		} else if (layout instanceof Layout.Of) {
			//noinspection rawtypes,unchecked,bruh
			((Layout.Of)layout).write(getter.invoke(x), out);
		}
	}
	
	private static void read(Layout<?> layout, Object x, MethodHandle setter, DataInput in) throws Throwable {
		if (layout == Layout.ofBoolean) {
			setter.invoke(x, Layout.ofBoolean.read(in));
		} else if (layout == Layout.ofByte) {
			setter.invoke(x, Layout.ofByte.read(in));
		} else if (layout == Layout.ofShort) {
			setter.invoke(x, Layout.ofShort.read(in));
		} else if (layout == Layout.ofChar) {
			setter.invoke(x, Layout.ofChar.read(in));
		} else if (layout == Layout.ofFloat) {
			setter.invoke(x, Layout.ofFloat.read(in));
		} else if (layout == Layout.ofInt) {
			setter.invoke(x, Layout.ofInt.read(in));
		} else if (layout == Layout.ofLong) {
			setter.invoke(x, Layout.ofLong.read(in));
		} else if (layout == Layout.ofDouble) {
			setter.invoke(x, Layout.ofDouble.read(in));
		} else if (layout instanceof Layout.Of) {
			//noinspection rawtypes,bruh
			setter.invoke(x, ((Layout.Of)layout).read(in));
		}
	}
	
	
	
	@Override
	public Integer size() {
		return sizeof;
	}
	
	@Override
	public String toString() {
		return "ClassLayout{" +
				"clazz=" + clazz +
				", getters=" + Arrays.toString(getters) +
				", setters=" + Arrays.toString(setters) +
				", fieldLayouts=" + Arrays.toString(fieldLayouts) +
				", sizeof=" + sizeof +
				'}';
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
}
