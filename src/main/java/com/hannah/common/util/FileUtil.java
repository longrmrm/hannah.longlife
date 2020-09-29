package com.hannah.common.util;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * file tools
 * @author longrm
 * @date 2012-3-31
 */
public class FileUtil {

	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index == -1)
			return null;
		return fileName.substring(index + 1);
	}

	public static boolean isImage(String fileName) {
		String extension = getFileExtension(fileName.toLowerCase());
		return "jpg".equals(extension) || "jpeg".equals(extension) || "gif".equals(extension)
				|| "png".equals(extension) || "bmp".equals(extension) || "icon".equals(extension);
	}

	/**
	 * when run as a jar, get execute file directory
	 * @return
	 */
	public static String getExecuteDirectoryPath() {
		return getExecuteDirectoryPath(null);
	}

	public static String getExecuteDirectoryPath(Class<?> cls) {
		if (cls == null)
			cls = FileUtil.class;
		URL url = cls.getProtectionDomain().getCodeSource().getLocation();
		// transform to utf-8, support chinese
		try {
			String path = URLDecoder.decode(url.getPath(), "UTF-8");
			int index = path.lastIndexOf("/");
			if (index == -1)
				return null;
			return path.substring(0, index);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * format file size: B(bytes)、KB(k bytes)、MB(m bytes)、GB(g bytes)
	 * @param fileLength
	 * @return
	 */
	public static String formatFileSize(long fileLength) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeStr = "";
		if (fileLength < 1024)
			fileSizeStr = df.format((double) fileLength) + "B";
		else if (fileLength < 1048576)
			fileSizeStr = df.format((double) fileLength / 1024) + "KB";
		else if (fileLength < 1073741824)
			fileSizeStr = df.format((double) fileLength / 1048576) + "MB";
		else
			fileSizeStr = df.format((double) fileLength / 1073741824) + "GB";
		return fileSizeStr;
	}

	public static File createNewFile(String filepath) throws IOException {
		return createNewFile(new File(filepath));
	}

	/**
	 * create a new blank file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File createNewFile(File file) throws IOException {
		File pareFile = file.getParentFile();
		if (pareFile.exists() || pareFile.mkdirs()) {
			File newFile = file;
			if (newFile.exists() || newFile.createNewFile())
				return newFile;
		}
		return null;
	}

	/**
	 * recursively remove directory and all sub directories
	 * @param file
	 * @return
	 */
	public static boolean removeDirectories(File file) {
		if (!file.exists())
			return true;
		// recursively traversal all sub files
		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (File subFile : subFiles) {
				if (!removeDirectories(subFile))
					return false;
			}
		}
		// it is file or it is empty
		return file.delete();
	}

	/**
	 * recursively remove directory and all sub directories which matches
	 * regular expression
	 * @param file
	 * @param regex
	 * @return
	 */
	public static boolean removeDirectories(File file, String regex) {
		if (!file.exists())
			return true;
		// recursively traversal all sub files
		if (file.isDirectory()) {
			if (file.getName().matches(regex)) {
				if (!removeDirectories(file))
					return false;
			} else {
				File[] subFiles = file.listFiles();
				for (File subFile : subFiles) {
					if (!removeDirectories(subFile, regex))
						return false;
				}
			}
		}
		return true;
	}

	public static boolean deleteFiles(File file) {
		return deleteFiles(file, ".*");
	}

	/**
	 * recursively delete all sub files which matches regular expression
	 * @param file
	 * @param regex
	 * @return
	 */
	public static boolean deleteFiles(File file, String regex) {
		if (!file.exists())
			return true;
		// recursively traversal all sub files
		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (File subFile : subFiles) {
				if (!deleteFiles(subFile, regex))
					return false;
			}
		} else if (file.getName().matches(regex))
			return file.delete();
		return true;
	}

	/**
	 * recursively rename all sub files while matches filterRegex, replace each
	 * substring of filename that matches replaceRegex with the given
	 * replacement.
	 * @param file
	 * @param filterRegex
	 * @param replaceRegex
	 * @param replacement
	 * @return
	 */
	public static boolean renameFiles(File file, String filterRegex, String replaceRegex, String replacement) {
		if (!file.exists())
			return true;
		// recursively traversal all sub files
		if (file.isDirectory()) {
			File[] subFiles = file.listFiles();
			for (File subFile : subFiles) {
				if (!renameFiles(subFile, filterRegex, replaceRegex, replacement))
					return false;
			}
		} else if (file.getName().matches(filterRegex)) {
			String newFileName = file.getName().replaceAll(replaceRegex, replacement);
			String directory = file.getParent();
			return file.renameTo(new File(directory + File.separator + newFileName));
		}
		return true;
	}

	public static boolean renameFiles(File file, String regex, String replacement) {
		return renameFiles(file, ".*", regex, replacement);
	}

	/**
	 * copy files from src to dst
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void copyFiles(File src, File dst) throws IOException {
		if (!src.exists())
			return;
		else if (src.isDirectory() && !dst.isFile()) {
			if (dst.exists() || dst.mkdirs()) {
				File[] subSrcs = src.listFiles();
				for (File subSrc : subSrcs) {
					File subDst = new File(dst.getPath() + File.separator + subSrc.getName());
					copyFiles(subSrc, subDst);
				}
			}
		} else if (src.isFile() && !dst.isDirectory()) {
			if (dst.exists() || (dst = createNewFile(dst.getPath())) != null)
				StreamUtil.copyThenClose(new FileInputStream(src), new FileOutputStream(dst));
		} else
			return;
	}

	/**
	 * Use system command to copy file
	 * @param src
	 * @param dst
	 * @param isMove move or copy
	 */
	public static void copyFileByCommand(String src, String dst, boolean isMove) {
		File file = new File(dst).getParentFile();
		if (!file.exists() && !file.mkdirs())
			return;

		String[] cmd = null;
		if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
			cmd = new String[5];
			cmd[0] = "cmd";
			cmd[1] = "/c";
			cmd[2] = isMove ? "move" : "copy";
			cmd[3] = src;
			cmd[4] = dst;
		} else {
			cmd = new String[3];
			cmd[0] = isMove ? "mv" : "cp";
			cmd[1] = src;
			cmd[2] = dst;
		}

		InputStream in = null;
		Process process = null;
		try {
			process = new ProcessBuilder(cmd).start();
			in = process.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				process.destroy();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * output stream into file
	 * @param file
	 * @param input
	 * @throws IOException
	 */
	public static void writeFile(File file, InputStream input) throws IOException {
		StreamUtil.copyThenClose(input, new FileOutputStream(file));
	}

	/**
	 * read bytes from file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		byte[] buf = new byte[(int) file.length()];
		input.read(buf);
		input.close();
		// byte[] buf = StreamUtil.getBytes(input);
		return buf;
	}

	/**
	 * read text from file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readText(File file) throws IOException {
		boolean isUtf = isUtf(file);
		byte[] bytes = readBytes(file);
		return new String(bytes, isUtf ? "UTF-8" : "GBK");
	}

	public static byte[] readBytes(File file, long beginIndex) throws IOException {
		return readBytes(file, beginIndex, Long.MAX_VALUE);
	}

	/**
	 * read segment bytes from file by random access
	 * @param file
	 * @param beginIndex the beginning index, inclusive.
	 * @param endIndex the ending index, exclusive.
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytes(File file, long beginIndex, long endIndex) throws IOException {
		// get randomAccessFile and seek to the beginIndex
		RandomAccessFile randomFile = new RandomAccessFile(file, "r");
		randomFile.seek(beginIndex);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		long length = endIndex - beginIndex;
		while (length > 0) {
			// create a buffer array to store read stream
			byte buf[] = null;
			if (StreamUtil.DEFAULT_BUFFER_SIZE > length)
				buf = new byte[(int) length];
			else
				buf = new byte[StreamUtil.DEFAULT_BUFFER_SIZE];
			// read from randomFile and write to output stream
			int bytesRead = randomFile.read(buf);
			if (bytesRead != -1)
				output.write(buf, 0, bytesRead);
			else
				break;

			length = length - buf.length;
		}
		// close input and output stream
		output.flush();
		output.close();
		randomFile.close();
		return output.toByteArray();
	}

	/**
	 * write bytes into file
	 * @param file
	 * @param append
	 * @param bytes
	 * @throws IOException
	 */
	public static void writeBytes(File file, boolean append, byte[] bytes) throws IOException {
		if (bytes.length == 0)
			return;

		FileOutputStream output = new FileOutputStream(file, append);
		output.write(bytes);
		output.flush();
		output.close();
	}

	public static void writeText(File file, String text) throws IOException {
		writeText(file, text, null);
	}

	public static void writeText(File file, String text, String charsetName) throws IOException {
		byte[] bytes = charsetName == null ? text.getBytes() : text.getBytes(charsetName);
		writeBytes(file, false, bytes);
	}

	/**
	 * write text, make every line starts with prefix
	 * @param bw
	 * @param text
	 * @param prefix
	 * @throws IOException
	 * @see Properties
	 */
	public static void writeText(BufferedWriter bw, String text, String prefix) throws IOException {
		int len = text.length();
		int current = 0;
		int last = 0;
		while (current < len) {
			char c = text.charAt(current);
			if (c == '\n' || c == '\r') {
				if (last != current)
					bw.write(prefix + text.substring(last, current));
				bw.newLine();
				last = current + 1;
			}
			current++;
		}
		if (last != current)
			bw.write(prefix + text.substring(last, current));
		bw.flush();
	}

	/**
	 * get the unique file path by add a serial number. e.g. directory[1],
	 * file[1].jpg
	 * @param filePath
	 * @return
	 */
	public static String getUniqueFilePath(String filePath) {
		int id = 1;
		File file = new File(filePath);
		while (file.exists()) {
			if (file.isDirectory()) {
				if (id == 1)
					filePath += "[" + (id++) + "]";
				else
					filePath = filePath.substring(0, filePath.lastIndexOf("[") + 1) + (id++) + "]";
			} else {
				int pointIndex = filePath.indexOf(".");
				String suffix = null;
				if (pointIndex != -1) {
					suffix = filePath.substring(pointIndex);
					filePath = filePath.substring(0, pointIndex);
				}
				if (id == 1)
					filePath += "[" + (id++) + "]";
				else
					filePath = filePath.substring(0, filePath.lastIndexOf("[") + 1) + (id++) + "]";
				filePath += suffix;
			}
			file = new File(filePath);
		}
		return filePath;
	}

	public static boolean isUtf(File file) throws IOException {
		return isUtf(file, 1024);
	}

	/**
	 * judge whether the file is UTF-8 charset
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static boolean isUtf(File file, int readSize) throws IOException {
		readSize = file.length() < readSize ? (int) file.length() : readSize;
		FileInputStream fis = new FileInputStream(file);
		byte[] bbuf = new byte[readSize];
		int status = 0;
		int errorCount = 0;
		int readNum = fis.read(bbuf);
		if (readNum != -1) {
			for (int i = 0; i < readNum; i++) {
				byte b = bbuf[i];
				switch (status) {
				case 0:
					if (b >= 0 && b <= (byte) 0x7F)// 一个字节的首字节范围
						;
					else if (b >= (byte) 0xC0 && b <= (byte) 0xDF)// 两个字节的首字节范围
						status = 2;// 跳到状态2
					else if (b >= (byte) 0xE0 && b <= (byte) 0XEF)// 三个字节的首字节范围
						status = 4;// 跳到状态4
					else if (b >= (byte) 0xF0 && b <= (byte) 0xF7)// 四个字节的首字节范围
						status = 7;// 跳到状态7
					else
						errorCount++;
					break;
				case 1:
					break;
				case 2:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF) {// 两个字节的第二个字节范围
						status = 0;
					} else {
						errorCount += 2;
						status = 0;
					}
					break;
				case 3:
					break;
				case 4:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF)// 三个字节的第二个字节的范围
						status = 5;
					else {
						errorCount += 2;
						status = 0;
					}
					break;
				case 5:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF) {// 三个字节的第三个字节的范围
						status = 0;
					} else {
						errorCount += 3;
						status = 0;
					}
					break;
				case 7:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF) {// 四个字节的第二个字节的范围
						status = 8;
					} else {
						errorCount += 2;
						status = 0;
					}
					break;
				case 8:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF) {// 四个字节的第三个字节的范围
						status = 9;
					} else {
						errorCount += 3;
						status = 0;
					}
					break;
				case 9:
					if (b >= (byte) 0x80 && b <= (byte) 0xBF) {// 四个字节的第四个字节的范围
						status = 0;
					} else {
						errorCount++;
						status = 0;
					}
					break;
				default:
					break;
				}
			}
		}
		fis.close();

		if (errorCount == 0)
			return true;
		return false;
	}

}
