package com.ydo4ki.datalayouts;

import com.ydo4ki.datalayouts.annotations.Encoding;

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
			layouts[i] = Layout.of(Objects.requireNonNull(type), fields.get(i).getAnnotations());
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
		if (layout instanceof Layout.OfBoolean) {
			((OfBoolean) layout).write((boolean)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfByte) {
			((OfByte) layout).write((byte)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfShort) {
			((OfShort) layout).write((short)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfChar) {
			((OfChar) layout).write((char)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfFloat) {
			((OfFloat) layout).write((float)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfInt) {
			((OfInt) layout).write((int)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfLong) {
			((OfLong) layout).write((long)getter.invoke(x), out);
		} else if (layout instanceof Layout.OfDouble) {
			((OfDouble) layout).write((double)getter.invoke(x), out);
		} else if (layout instanceof Layout.Of) {
			//noinspection rawtypes,unchecked,bruh
			((Layout.Of)layout).write(getter.invoke(x), out);
		}
	}
	
	private static void read(Layout<?> layout, Object x, MethodHandle setter, DataInput in) throws Throwable {
		if (layout instanceof Layout.OfBoolean) {
			setter.invoke(x, ((OfBoolean) layout).read(in));
		} else if (layout instanceof Layout.OfByte) {
			setter.invoke(x, ((OfByte) layout).read(in));
		} else if (layout instanceof Layout.OfShort) {
			setter.invoke(x, ((OfShort) layout).read(in));
		} else if (layout instanceof Layout.OfChar) {
			setter.invoke(x, ((OfChar) layout).read(in));
		} else if (layout instanceof Layout.OfFloat) {
			setter.invoke(x, ((OfFloat) layout).read(in));
		} else if (layout instanceof Layout.OfInt) {
			setter.invoke(x, ((OfInt) layout).read(in));
		} else if (layout instanceof Layout.OfLong) {
			setter.invoke(x, ((OfLong) layout).read(in));
		} else if (layout instanceof Layout.OfDouble) {
			setter.invoke(x, ((OfDouble) layout).read(in));
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
	
	
	
}
