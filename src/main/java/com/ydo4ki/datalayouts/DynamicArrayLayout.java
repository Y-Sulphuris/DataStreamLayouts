package com.ydo4ki.datalayouts;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;

@SuppressWarnings({"rawtypes", "unchecked"})
class DynamicArrayLayout<T> implements ArrayLayout<T> {
	protected final Class<T> arrayType;
	protected final Layout elementLayout;
	
	DynamicArrayLayout(Class<T> arrayType, Layout elementLayout) {
		if (!arrayType.isArray()) throw new UnpureClassException(arrayType, "array expected");
		this.arrayType = arrayType;
		this.elementLayout = elementLayout;
	}
	
	@Override
	public void write(T array, DataOutput out) throws IOException {
		out.writeInt(Array.getLength(array));
		for (int i = 0, Len = Array.getLength(array); i < Len; i++) {
			elementLayout.asObjectLayout().write(Array.get(array, i), out);
		}
	}
	
	@Override
	public T read(DataInput in) throws IOException {
		return readArray(in, in.readInt());
	}
	
	protected final T readArray(DataInput in, int length) throws IOException {
		T array = (T) Array.newInstance(arrayType.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(array, i, elementLayout.asObjectLayout().read(in));
		}
		return array;
	}
	
	@Override
	public Integer size() {
		return null;
	}
	
	@Override
	public Layout<?> elementLayout() {
		return elementLayout;
	}
}
