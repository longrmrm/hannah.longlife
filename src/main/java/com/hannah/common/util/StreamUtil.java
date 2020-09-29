package com.hannah.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public final static int DEFAULT_BUFFER_SIZE = 1024 * 2;

	/**
	 * copy data from inputstream to outputstream
	 * @param out
	 * @param in
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException {
		copy(input, output, DEFAULT_BUFFER_SIZE);
	}

	public static void copy(InputStream input, OutputStream output, int bufferSize)
			throws IOException {
		byte buf[] = new byte[bufferSize];
		for (int bytesRead = input.read(buf); bytesRead != -1; bytesRead = input.read(buf))
			output.write(buf, 0, bytesRead);
		output.flush();
	}

	public static void copyThenClose(InputStream input, OutputStream output) throws IOException {
		copy(input, output);
		input.close();
		output.close();
	}

	public static byte[] getBytes(InputStream input) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		copy(input, result);
		result.close();
		return result.toByteArray();
	}
	
}
