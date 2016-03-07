package com.forddirect.jsonic;

public interface ValueGenerator<E> {
	Object getValue(E object) throws Exception;
}
