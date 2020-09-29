package com.hannah.swing.util;

public interface InvokeHandler<T> {

	void before();

	T execute() throws Exception;

	void after();

	void success(T result);

	void failure(Exception exception);

}
