package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;

/**
 * @since 12/1/2024 10:19 PM
 * @author Sulphuris
 */
@SuppressWarnings("ClassEscapesDefinedScope") // :)
public interface Layout<T> {
	
	static <T> Of<T> of(Class<T> clazz, MethodHandles.Lookup lookup, Annotation... annotations) {
		if (clazz.isPrimitive()) // so it's not null
			throw new UnpureClassException(clazz, "Use ::of(Class) instead");
		return Layouts.applyAnnotations(Layouts.get(clazz, lookup), annotations, clazz).asObjectLayout(); // wow
	}
	
	@SuppressWarnings("unchecked")
	static <T> Layout<T> of(Class<T> clazz, Annotation... annotations) {
		if (clazz.isPrimitive()) {
			return Layouts.applyAnnotations((Layout<T>)Layouts.primitiveLayout(clazz), annotations, clazz);
		}
		return of(clazz, MethodHandles.publicLookup(), annotations);
	}
	
	
	
	static <T> void bindTo(Class<T> clazz, Layout.Of<T> layout) {
		Layouts.bind(clazz, layout);
	}
	
	static <T> void bindToVirtual(Class<T> clazz, Layout.Of<T> layout) {
		Layouts.bindVirtual(clazz, layout); // jk
	}
	
	static <A extends Annotation, T, L extends Layout<T>> void bindAnnotationPragma(Class<A> annotationType, AnnotationPragma<A, T, L> pragma) {
		Layouts.registerAnnotation(annotationType, pragma);
	}
	
	Layout.OfByte    ofByte    = new OfByte();
	Layout.OfBoolean ofBoolean = new OfBoolean();
	Layout.OfShort   ofShort   = new OfShort();
	Layout.OfChar    ofChar    = new OfChar();
	Layout.OfInt     ofInt     = new OfInt();
	Layout.OfFloat   ofFloat   = new OfFloat();
	Layout.OfLong    ofLong    = new OfLong();
	Layout.OfDouble  ofDouble  = new OfDouble();
	
	Layout.Of<String>ofString  = new StringLayout(StringEncoding.get(StringEncoding.UTF16), false);
	
	
	// nullable (null means unknown or dynamic size)
	Integer size();
	
	Layout.Of<T> asObjectLayout();
	
	
	static boolean isStatic(Layout<?> layout) {
		return layout.size() != null;
	}
	
	
	
	
	
	
	
	
	
	
	// impl details
	
	// now every external class which wants to implement Layout has to implements this
	// (I also could've just make it as an abstract class, but it's boring)
	interface Of<T> extends Layout<T> {
		
		void write(T x, DataOutput out) throws IOException;
		
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
	
	class OfByte implements Layout<Byte> {
		OfByte() {
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
		
		public void write(byte x, DataOutput out) throws IOException {
			out.writeByte(x);
		}
		
		public byte read(DataInput in) throws IOException {
			return in.readByte();
		}
	}
	
	class OfBoolean implements Layout<Boolean> {
		OfBoolean() {
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
		
		public void write(boolean x, DataOutput out) throws IOException {
			out.writeBoolean(x);
		}
		
		public boolean read(DataInput in) throws IOException {
			return in.readBoolean();
		}
	}
	
	class OfShort implements Layout<Short> {
		OfShort() {
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
		
		public void write(short x, DataOutput out) throws IOException {
			out.writeShort(x);
		}
		
		public short read(DataInput in) throws IOException {
			return in.readShort();
		}
	}
	
	class OfChar implements Layout<Character> {
		OfChar() {
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
		
		public void write(char x, DataOutput out) throws IOException {
			out.writeChar(x);
		}
		
		public char read(DataInput in) throws IOException {
			return in.readChar();
		}
	}
	
	class OfInt implements Layout<Integer> {
		OfInt() {
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
		
		public void write(int x, DataOutput out) throws IOException {
			out.writeInt(x);
		}
		
		public int read(DataInput in) throws IOException {
			return in.readInt();
		}
	}
	
	class OfFloat implements Layout<Float> {
		OfFloat() {
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
		
		public void write(float x, DataOutput out) throws IOException {
			out.writeFloat(x);
		}
		
		public float read(DataInput in) throws IOException {
			return in.readFloat();
		}
	}
	
	class OfLong implements Layout<Long> {
		OfLong() {
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
		
		public void write(long x, DataOutput out) throws IOException {
			out.writeLong(x);
		}
		
		public long read(DataInput in) throws IOException {
			return in.readLong();
		}
	}
	
	class OfDouble implements Layout<Double> {
		OfDouble() {
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
		
		public void write(double x, DataOutput out) throws IOException {
			out.writeDouble(x);
		}
		
		public double read(DataInput in) throws IOException {
			return in.readDouble();
		}
	}
	
	Local __local();
}

// sealed interface lifehack
class Local {}