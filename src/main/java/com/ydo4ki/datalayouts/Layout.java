package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * @since 12/1/2024 10:19 PM
 * @author Sulphuris
 */
@SuppressWarnings("ClassEscapesDefinedScope") // :)
public interface Layout {
	
	static <T> Of<T> of(Class<T> clazz, MethodHandles.Lookup lookup) {
		if (clazz.isPrimitive()) // so it's not null
			throw new UnpureClassException(clazz, "Use ::of<PrimitiveName> instead");
		return ClassLayout.get(clazz, lookup);
	}
	
	static Layout of(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (clazz == byte.class) return ofByte;
			if (clazz == boolean.class) return ofBoolean;
			if (clazz == short.class) return ofShort;
			if (clazz == char.class) return ofChar;
			if (clazz == int.class) return ofInt;
			if (clazz == float.class) return ofFloat;
			if (clazz == long.class) return ofLong;
			if (clazz == double.class) return ofDouble;
		}
		if (clazz.isArray()) {
			throw new UnpureClassException(clazz, "todo");
		}
		return of(clazz, MethodHandles.publicLookup());
	}
	
	
	static <T> void bindToClass(Class<T> clazz, Layout.Of<T> layout) {
		ClassLayout.bind(clazz, layout);
	}
	
	Layout.OfByte    ofByte    = new OfByte();
	Layout.OfBoolean ofBoolean = new OfBoolean();
	Layout.OfShort   ofShort   = new OfShort();
	Layout.OfChar    ofChar    = new OfChar();
	Layout.OfInt     ofInt     = new OfInt();
	Layout.OfFloat   ofFloat   = new OfFloat();
	Layout.OfLong    ofLong    = new OfLong();
	Layout.OfDouble  ofDouble  = new OfDouble();
	
	
	
	int size();
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// impl details
	
	// now every external class which wants to implement Layout has to implements this
	// (I also could've just make it as an abstract class, but it's boring)
	interface Of<T> extends Layout {
		
		void write(T x, DataOutput out) throws IOException;
		
		T read(DataInput in) throws IOException;
		
		@Override
		default Local __local() {
			return null;
		}
	}
	
	final class OfByte implements Layout {
		OfByte() {
		}
		
		@Override
		public int size() {
			return 1;
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
	
	final class OfBoolean implements Layout {
		OfBoolean() {
		}
		
		@Override
		public int size() {
			return 1;
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
	
	final class OfShort implements Layout {
		OfShort() {
		}
		
		@Override
		public int size() {
			return 2;
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
	
	final class OfChar implements Layout {
		OfChar() {
		}
		
		@Override
		public int size() {
			return 2;
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
	
	final class OfInt implements Layout {
		OfInt() {
		}
		
		@Override
		public int size() {
			return 4;
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
	
	final class OfFloat implements Layout {
		OfFloat() {
		}
		
		@Override
		public int size() {
			return 4;
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
	
	final class OfLong implements Layout {
		OfLong() {
		}
		
		@Override
		public int size() {
			return 8;
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
	
	final class OfDouble implements Layout {
		OfDouble() {
		}
		
		@Override
		public int size() {
			return 8;
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