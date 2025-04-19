package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;

/**
 * The core interface for defining data layouts that can be serialized to and deserialized from binary streams.
 * A Layout represents a blueprint for how data of type T should be structured when written to or read from
 * a binary stream. This interface provides methods for creating layouts for various data types, including
 * primitives, objects, and arrays.
 * 
 * <p>Layouts can be static (fixed size) or dynamic (variable size). Static layouts have a known size in bytes,
 * while dynamic layouts may have a size that depends on the actual data.</p>
 * 
 * <p>The library provides built-in layouts for all Java primitive types and common objects like String and UUID.</p>
 *
 * @param <T> The type of data this layout represents
 * @since 1.0.0
 * @author Sulphuris
 */
@SuppressWarnings("ClassEscapesDefinedScope") // :)
public interface Layout<T> {
	
	/**
	 * Creates a layout for a non-primitive class type with optional annotations.
	 * This method uses method handles to access the fields of the class.
	 *
	 * @param <T> The type of data this layout represents
	 * @param clazz The class to create a layout for
	 * @param lookup The method handles lookup to use for accessing fields
	 * @param annotations Optional annotations to modify the layout behavior
	 * @return A layout for the specified class
	 * @throws UnpureClassException If the class is primitive (use {@link #of(Class, Annotation...)} instead)
	 * @since 1.0.0
	 */
	static <T> Of<T> of(Class<T> clazz, MethodHandles.Lookup lookup, Annotation... annotations) {
		if (clazz.isPrimitive()) // so it's not null
			throw new UnpureClassException(clazz, "Use ::of(Class) instead");
		return Layouts.applyAnnotations(Layouts.get(clazz, lookup), annotations, clazz).asObjectLayout(); // wow
	}
	
	/**
	 * Creates a layout for any class type with optional annotations.
	 * For primitive types, this method creates a primitive layout.
	 * For non-primitive types, it delegates to {@link #of(Class, MethodHandles.Lookup, Annotation...)}.
	 *
	 * @param <T> The type of data this layout represents
	 * @param clazz The class to create a layout for
	 * @param annotations Optional annotations to modify the layout behavior
	 * @return A layout for the specified class
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	static <T> Layout<T> of(Class<T> clazz, Annotation... annotations) {
		if (clazz.isPrimitive()) {
			return Layouts.applyAnnotations((Layout<T>)Layouts.primitiveLayout(clazz), annotations, clazz);
		}
		return of(clazz, MethodHandles.publicLookup(), annotations);
	}
	
	/**
	 * Creates a raw object layout from an array of class types.
	 * This method creates a layout for each class type and combines them into a raw object layout.
	 *
	 * @param params The class types to include in the raw object
	 * @return A layout for a raw object containing the specified types
	 * @since 1.2.0
	 */
	static Layout.Of<RawObject> ofRaw(Class<?>... params) {
		final int len = params.length;
		Layout<?>[] layouts = new Layout[len];
		for (int i = 0; i < len; i++) {
			layouts[i] = Layout.of(params[i]);
		}
		return ofRaw(layouts);
	}
	
	/**
	 * Creates a raw object layout from an array of layouts.
	 * This method combines multiple layouts into a single raw object layout.
	 *
	 * @param params The layouts to include in the raw object
	 * @return A layout for a raw object containing the specified layouts
	 * @since 1.2.0
	 */
	@SuppressWarnings("unchecked")
	static Layout.Of<RawObject> ofRaw(Layout<?>... params) {
		int size = Layouts.sum(params, Layout::size);
		return new Layout.Of<RawObject>() {
			@Override
			public Integer size() {
				return size;
			}
			
			@Override
			public void write(RawObject x, DataOutput out) throws IOException {
				for (int i = 0; i < params.length; i++) {
					((Layout.Of<Object>)params[i].asObjectLayout()).write(x.get(i), out);
				}
			}
			
			@Override
			public RawObject read(DataInput in) throws IOException {
				RawObjectImpl obj = new RawObjectImpl(params.length);
				for (int i = 0; i < params.length; i++) {
					obj.data[i] = ((Layout.Of<Object>)params[i].asObjectLayout()).read(in);
				}
				return obj;
			}
		};
	}
	
	/**
	 * Binds a layout to a class type.
	 * This method registers the layout for the specified class type so that it can be used
	 * for serialization and deserialization of instances of that class.
	 *
	 * @param <T> The type of data this layout represents
	 * @param clazz The class to bind the layout to
	 * @param layout The layout to bind
	 * @since 1.0.0
	 */
	static <T> void bindTo(Class<T> clazz, Layout.Of<T> layout) {
		Layouts.bind(clazz, layout);
	}
	
	/**
	 * Binds a layout to a class type virtually.
	 * This method registers the layout for the specified class type and its subclasses.
	 *
	 * @param <T> The type of data this layout represents
	 * @param clazz The class to bind the layout to
	 * @param layout The layout to bind
	 * @since 1.0.0
	 */
	static <T> void bindToVirtual(Class<T> clazz, Layout.Of<T> layout) {
		Layouts.bindVirtual(clazz, layout); // jk
	}
	
	/**
	 * Binds an annotation pragma to an annotation type for a specific class type.
	 * This method registers a handler for an annotation that can modify layouts.
	 *
	 * @param <A> The annotation type
	 * @param <T> The class type the annotation can be applied to
	 * @param <L> The layout type
	 * @param annotationType The annotation class
	 * @param applicableTo The class the annotation can be applied to
	 * @param pragma The annotation pragma handler
	 * @since 1.0.0
	 */
	static <A extends Annotation, T, L extends Layout<T>> void bindAnnotationPragma(Class<A> annotationType, Class<T> applicableTo, AnnotationPragma<A, T, L> pragma) {
		Layouts.registerAnnotation(annotationType, applicableTo, pragma);
	}
	
	/** Layout for byte values */
	Layout.OfByte    ofByte    = new OfByte();
	/** Layout for boolean values */
	Layout.OfBoolean ofBoolean = new OfBoolean();
	/** Layout for short values */
	Layout.OfShort   ofShort   = new OfShort();
	/** Layout for char values */
	Layout.OfChar    ofChar    = new OfChar();
	/** Layout for int values */
	Layout.OfInt     ofInt     = new OfInt();
	/** Layout for float values */
	Layout.OfFloat   ofFloat   = new OfFloat();
	/** Layout for long values */
	Layout.OfLong    ofLong    = new OfLong();
	/** Layout for double values */
	Layout.OfDouble  ofDouble  = new OfDouble();
	
	/** Layout for String values using UTF-16 encoding */
	Layout.Of<String>ofString  = new StringLayout.DynamicStringLayout(StringEncoding.get(StringEncoding.UTF16), false);
	
	/**
	 * Creates a layout that skips a specified number of bytes.
	 * This is useful for padding or skipping over unused data in a binary format.
	 *
	 * @param bytes The number of bytes to skip
	 * @return A layout that skips the specified number of bytes
	 * @since 1.1.0
	 */
	static Layout.Of<Void>  skip(int bytes) {
		return new SkipLayout(bytes);
	}
	
	
	/**
	 * Returns the size of this layout in bytes, or null if the size is dynamic.
	 *
	 * @return The size in bytes, or null if the size is dynamic
	 * @since 1.0.0
	 */
	Integer size();
	
	/**
	 * Converts this layout to an object layout.
	 *
	 * @return This layout as an object layout
	 * @since 1.0.0
	 */
	Layout.Of<T> asObjectLayout();
	
	
	/**
	 * Determines if a layout has a static (fixed) size.
	 *
	 * @param layout The layout to check
	 * @return true if the layout has a static size, false otherwise
	 * @since 1.0.0
	 */
	static boolean isStatic(Layout<?> layout) {
		return layout.size() != null;
	}
	
	
	
	
	
	
	
	
	
	
	// impl details
	
	/**
	 * Interface for object layouts that can read and write objects of type T.
	 * This is the main interface for implementing custom layouts.
	 *
	 * @param <T> The type of data this layout represents
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	interface Of<T> extends Layout<T> {
		
		/**
		 * Writes an object to a data output stream.
		 *
		 * @param x The object to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		void write(T x, DataOutput out) throws IOException;
		
		/**
		 * Reads an object from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read object
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		T read(DataInput in) throws IOException;
		
		@Override
		default Of<T> asObjectLayout() {
			return this;
		}
		
		@Override
		default Local __local() {
			return null;
		}
	}
	
	/**
	 * Layout implementation for byte values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfByte implements Layout<Byte> {
		public OfByte() {
		}
		
		@Override
		public Integer size() {
			return 1;
		}
		
		// it's a singleton anyway so doesn't matter
		private final Of<Byte> object = new Of<Byte>() {
			@Override
			public void write(Byte x, DataOutput out) throws IOException {
				OfByte.this.write(x, out);
			}
			
			@Override
			public Byte read(DataInput in) throws IOException {
				return OfByte.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 1;
			}
		};
		
		@Override
		public Of<Byte> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a byte value to a data output stream.
		 *
		 * @param x The byte value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(byte x, DataOutput out) throws IOException {
			out.writeByte(x);
		}
		
		/**
		 * Reads a byte value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read byte value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public byte read(DataInput in) throws IOException {
			return in.readByte();
		}
	}
	
	/**
	 * Layout implementation for boolean values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfBoolean implements Layout<Boolean> {
		public OfBoolean() {
		}
		
		@Override
		public Integer size() {
			return 1;
		}
		
		private final Of<Boolean> object = new Of<Boolean>() {
			@Override
			public void write(Boolean x, DataOutput out) throws IOException {
				OfBoolean.this.write(x, out);
			}
			
			@Override
			public Boolean read(DataInput in) throws IOException {
				return OfBoolean.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 1;
			}
		};
		
		@Override
		public Of<Boolean> asObjectLayout() {
			return object;
		}
		
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a boolean value to a data output stream.
		 *
		 * @param x The boolean value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(boolean x, DataOutput out) throws IOException {
			out.writeBoolean(x);
		}
		
		/**
		 * Reads a boolean value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read boolean value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public boolean read(DataInput in) throws IOException {
			return in.readBoolean();
		}
	}
	
	/**
	 * Layout implementation for short values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfShort implements Layout<Short> {
		public OfShort() {
		}
		
		@Override
		public Integer size() {
			return 2;
		}
		
		private final Of<Short> object = new Of<Short>() {
			@Override
			public void write(Short x, DataOutput out) throws IOException {
				OfShort.this.write(x, out);
			}
			
			@Override
			public Short read(DataInput in) throws IOException {
				return OfShort.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 2;
			}
		};
		
		@Override
		public Of<Short> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a short value to a data output stream.
		 *
		 * @param x The short value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(short x, DataOutput out) throws IOException {
			out.writeShort(x);
		}
		
		/**
		 * Reads a short value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read short value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public short read(DataInput in) throws IOException {
			return in.readShort();
		}
	}
	
	/**
	 * Layout implementation for char values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfChar implements Layout<Character> {
		public OfChar() {
		}
		
		@Override
		public Integer size() {
			return 2;
		}
		
		private final Of<Character> object = new Of<Character>() {
			@Override
			public void write(Character x, DataOutput out) throws IOException {
				OfChar.this.write(x, out);
			}
			
			@Override
			public Character read(DataInput in) throws IOException {
				return OfChar.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 2;
			}
		};
		
		@Override
		public Of<Character> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a char value to a data output stream.
		 *
		 * @param x The char value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(char x, DataOutput out) throws IOException {
			out.writeChar(x);
		}
		
		/**
		 * Reads a char value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read char value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public char read(DataInput in) throws IOException {
			return in.readChar();
		}
	}
	
	/**
	 * Layout implementation for int values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfInt implements Layout<Integer> {
		public OfInt() {
		}
		
		@Override
		public Integer size() {
			return 4;
		}
		
		private final Of<Integer> object = new Of<Integer>() {
			@Override
			public void write(Integer x, DataOutput out) throws IOException {
				OfInt.this.write(x, out);
			}
			
			@Override
			public Integer read(DataInput in) throws IOException {
				return OfInt.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 4;
			}
		};
		
		@Override
		public Of<Integer> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes an int value to a data output stream.
		 *
		 * @param x The int value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(int x, DataOutput out) throws IOException {
			out.writeInt(x);
		}
		
		/**
		 * Reads an int value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read int value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public int read(DataInput in) throws IOException {
			return in.readInt();
		}
	}
	
	/**
	 * Layout implementation for float values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfFloat implements Layout<Float> {
		public OfFloat() {
		}
		
		@Override
		public Integer size() {
			return 4;
		}
		
		private final Of<Float> object = new Of<Float>() {
			@Override
			public void write(Float x, DataOutput out) throws IOException {
				OfFloat.this.write(x, out);
			}
			
			@Override
			public Float read(DataInput in) throws IOException {
				return OfFloat.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 4;
			}
		};
		
		
		@Override
		public Of<Float> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a float value to a data output stream.
		 *
		 * @param x The float value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(float x, DataOutput out) throws IOException {
			out.writeFloat(x);
		}
		
		/**
		 * Reads a float value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read float value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public float read(DataInput in) throws IOException {
			return in.readFloat();
		}
	}
	
	/**
	 * Layout implementation for long values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfLong implements Layout<Long> {
		public OfLong() {
		}
		
		@Override
		public Integer size() {
			return 8;
		}
		
		private final Of<Long> object = new Of<Long>() {
			@Override
			public void write(Long x, DataOutput out) throws IOException {
				OfLong.this.write(x, out);
			}
			
			@Override
			public Long read(DataInput in) throws IOException {
				return OfLong.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 8;
			}
		};
		
		
		@Override
		public Of<Long> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a long value to a data output stream.
		 *
		 * @param x The long value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(long x, DataOutput out) throws IOException {
			out.writeLong(x);
		}
		
		/**
		 * Reads a long value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read long value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public long read(DataInput in) throws IOException {
			return in.readLong();
		}
	}
	
	/**
	 * Layout implementation for double values.
	 * 
	 * @since 1.0.0
	 * @author Sulphuris
	 */
	class OfDouble implements Layout<Double> {
		public OfDouble() {
		}
		
		@Override
		public Integer size() {
			return 8;
		}
		
		private final Of<Double> object = new Of<Double>() {
			@Override
			public void write(Double x, DataOutput out) throws IOException {
				OfDouble.this.write(x, out);
			}
			
			@Override
			public Double read(DataInput in) throws IOException {
				return OfDouble.this.read(in);
			}
			
			@Override
			public Integer size() {
				return 8;
			}
		};
		
		@Override
		public Of<Double> asObjectLayout() {
			return object;
		}
		
		@Override
		public Local __local() {
			return null;
		}
		
		/**
		 * Writes a double value to a data output stream.
		 *
		 * @param x The double value to write
		 * @param out The data output stream to write to
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public void write(double x, DataOutput out) throws IOException {
			out.writeDouble(x);
		}
		
		/**
		 * Reads a double value from a data input stream.
		 *
		 * @param in The data input stream to read from
		 * @return The read double value
		 * @throws IOException If an I/O error occurs
		 * @since 1.0.0
		 */
		public double read(DataInput in) throws IOException {
			return in.readDouble();
		}
	}
	
	Local __local();
}

