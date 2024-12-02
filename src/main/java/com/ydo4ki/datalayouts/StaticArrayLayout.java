package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;

@SuppressWarnings({"rawtypes", "unchecked"})
final class StaticArrayLayout<T> extends DynamicArrayLayout<T> {
	private final int length;
	
	StaticArrayLayout(Class<T> arrayType, Layout elementLayout, int length) {
		super(arrayType, elementLayout);
		this.length = length;
	}
	
	@Override
	public void write(T array, DataOutput out) throws IOException {
		super.write(copyOf(array, length), out);
	}
	
	private static <T> T copyOf(T array, int newLength) {
		int length = Array.getLength(array);
		if (length == newLength) return array;
		T newArray = (T)Array.newInstance(array.getClass().getComponentType(), newLength);
		//noinspection SuspiciousSystemArraycopy
		System.arraycopy(array, 0, newArray, 0, Math.min(length, newLength));
		return newArray;
	}
	
	@Override
	public T read(DataInput in) throws IOException {
		return readArray(in, length);
	}
	
	@Override
	public Integer size() {
		Integer elementSize = elementLayout().size();
		if (elementSize == null) return null;
		return elementSize * length;
	}
}
