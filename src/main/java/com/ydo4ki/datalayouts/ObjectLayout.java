package com.ydo4ki.datalayouts;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A layout implementation for Java objects that uses reflection and method handles for efficient
 * field access during serialization and deserialization.
 * 
 * <p>This class automatically discovers all non-static fields of a class and its superclasses,
 * creates layouts for each field, and uses method handles to get and set field values efficiently.
 * It also calculates the total size of the object if all fields have static sizes.</p>
 * 
 * <p>For object instantiation during deserialization, this class uses Objenesis to create instances
 * without calling constructors, allowing for efficient deserialization of objects.</p>
 *
 * @param <T> The type of object this layout represents
 * @since 1.0.0
 * @author Sulphuris
 */
class ObjectLayout<T> implements Layout.Of<T> {
	private final Class<T> clazz;
	private final Objenesis objenesis;
	private final MethodHandle[] getters;
	private final MethodHandle[] setters;
	private final Layout<?>[] fieldLayouts;
	private final OptionalInt sizeof;
	
	/**
	 * Returns the number of fields in this object layout.
	 *
	 * @return The number of fields
	 * @since 1.0.0
	 */
	public int fieldsCount() {
		return fieldLayouts.length;
	}
	
	/**
	 * Creates a new object layout for the specified class.
	 * This constructor discovers all non-static fields of the class and its superclasses,
	 * creates layouts for each field, and uses method handles to get and set field values.
	 *
	 * @param clazz The class to create a layout for
	 * @param lookup The method handles lookup to use for accessing fields
	 * @throws RuntimeException If an error occurs during layout creation
	 * @since 1.0.0
	 */
	ObjectLayout(Class<T> clazz, MethodHandles.Lookup lookup) {
		this.clazz = clazz;
		ArrayList<Field> fields = collectFields(clazz);
		try {
			this.getters = find(fields, lookup, MethodHandles.Lookup::unreflectGetter);
			this.setters = find(fields, lookup, MethodHandles.Lookup::unreflectSetter);
			this.objenesis = new ObjenesisStd();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		this.fieldLayouts = toLayouts(fields);
		
		
		this.sizeof = Layouts.totalSize(fieldLayouts);
	}
	
	/**
	 * Converts a list of fields to an array of layouts.
	 * This method creates a layout for each field based on its type and annotations.
	 *
	 * @param fields The fields to create layouts for
	 * @return An array of layouts for the fields
	 * @since 1.0.0
	 */
	private static Layout<?>[] toLayouts(ArrayList<Field> fields) {
		Layout<?>[] layouts = new Layout[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			Class<?> type = fields.get(i).getType();
			layouts[i] = Layout.of(Objects.requireNonNull(type), fields.get(i).getAnnotations());
		}
		return layouts;
	}
	
	/**
	 * Collects all non-static fields of a class and its superclasses.
	 * This method traverses the class hierarchy and collects all declared fields,
	 * excluding static fields.
	 *
	 * @param clazz The class to collect fields from
	 * @return A list of all non-static fields
	 * @throws NullPointerException If clazz is null
	 * @since 1.0.0
	 */
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
	
	/**
	 * Functional interface for unreflecting a field to a method handle.
	 * 
	 * @since 1.0.0
	 */
	@FunctionalInterface
	private interface Unreflector {
		/**
		 * Unreflects a field to a method handle.
		 *
		 * @param lookup The method handles lookup to use
		 * @param field The field to unreflect
		 * @return A method handle for the field
		 * @throws IllegalAccessException If access to the field is denied
		 * @since 1.0.0
		 */
		MethodHandle unreflect(MethodHandles.Lookup lookup, Field field) throws IllegalAccessException;
	}
	
	/**
	 * Creates an array of method handles for a list of fields using an unreflector.
	 * This method makes all fields accessible and creates method handles for them.
	 *
	 * @param fields The fields to create method handles for
	 * @param lookup The method handles lookup to use
	 * @param unreflector The unreflector to use for creating method handles
	 * @return An array of method handles for the fields
	 * @throws IllegalAccessException If access to a field is denied
	 * @since 1.0.0
	 */
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
	
	
	
	
	
	
	
	/**
	 * Writes an object to a data output stream.
	 * This method writes all fields of the object to the stream in the order they were discovered.
	 *
	 * @param x The object to write
	 * @param out The data output stream to write to
	 * @throws IOException If an I/O error occurs
	 * @throws RuntimeException If an error occurs during serialization
	 * @since 1.0.0
	 */
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
	
	/**
	 * Reads an object from a data input stream.
	 * This method creates a new instance of the object without calling its constructor,
	 * then reads all fields from the stream and sets them on the object.
	 *
	 * @param in The data input stream to read from
	 * @return The read object
	 * @throws IOException If an I/O error occurs
	 * @throws RuntimeException If an error occurs during deserialization
	 * @since 1.0.0
	 */
	@Override
	public T read(DataInput in) throws IOException {
		try {
			T newInstance = objenesis.newInstance(clazz);
			
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
	
	
	/**
	 * Writes a field value to a data output stream.
	 * This method uses the appropriate layout to write the field value to the stream.
	 *
	 * @param layout The layout to use for writing
	 * @param x The object containing the field
	 * @param getter The method handle for getting the field value
	 * @param out The data output stream to write to
	 * @throws Throwable If an error occurs during writing
	 * @since 1.0.0
	 */
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
	
	/**
	 * Reads a field value from a data input stream and sets it on an object.
	 * This method uses the appropriate layout to read the field value from the stream
	 * and set it on the object.
	 *
	 * @param layout The layout to use for reading
	 * @param x The object to set the field value on
	 * @param setter The method handle for setting the field value
	 * @param in The data input stream to read from
	 * @throws Throwable If an error occurs during reading
	 * @since 1.0.0
	 */
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
	
	
	/**
	 * Returns the size of this layout in bytes, or null if the size is dynamic.
	 * The size is calculated as the sum of the sizes of all fields.
	 * If any field has a dynamic size, the size of this layout is also dynamic.
	 *
	 * @return The size in bytes, or null if the size is dynamic
	 * @since 1.0.0
	 */
	@Override
	public OptionalInt size() {
		return sizeof;
	}
	
	/**
	 * Returns a string representation of this object layout.
	 *
	 * @return A string representation of this object layout
	 * @since 1.0.0
	 */
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
