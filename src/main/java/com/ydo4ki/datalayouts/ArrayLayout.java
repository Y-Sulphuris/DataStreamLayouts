package com.ydo4ki.datalayouts;
import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Array;

/**
 * @since 12/2/2024 12:24 AM
 * @author Sulphuris
 */
interface ArrayLayout<T> extends Layout.Of<T> {

}
/*
final class DynamicArrayLayout<T> implements ArrayLayout<T> {
	@Override
	public void write(T array, DataOutput out) {
	
	}
	
	@Override
	public T read(DataInput in) {
	
	}
}

final class StaticArrayLayout<T> implements ArrayLayout<T> {
	private final int length;
	
	StaticArrayLayout(int length) {
		this.length = length;
	}
	
	@Override
	public void write(T array, DataOutput out) {
	
	}
	
	@Override
	public T read(DataInput in) {
	
	}
}*/