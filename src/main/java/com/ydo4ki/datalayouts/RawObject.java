package com.ydo4ki.datalayouts;

/**
 * @since 3/20/2025 2:51 PM
 * @author Sulphuris
 */
public interface RawObject {
	int fieldCount();
	
	Object get(int index);
}

class RawObjectImpl implements RawObject {
	Object[] data;
	
	RawObjectImpl(int fieldCount) {
		data = new Object[fieldCount];
	}
	
	@Override
	public int fieldCount() {
		return data.length;
	}
	
	@Override
	public Object get(int index) {
		return data[index];
	}
}