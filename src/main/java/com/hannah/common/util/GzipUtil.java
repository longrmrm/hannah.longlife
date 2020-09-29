package com.hannah.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author longrm
 * @date 2013-7-28
 */
public class GzipUtil {

	/**
	 * unzip inputstream
	 * @param in
	 * @param charset
	 * @return
	 */
	public static String unZip(InputStream in, String charset) {
		try {
			GZIPInputStream gInputStream = new GZIPInputStream(in);
			byte[] bytes = StreamUtil.getBytes(gInputStream);
			return new String(bytes, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
