package com.hannah.windows;

import com.hannah.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class CmdUtil {

	public static void callCmd(String command) {
		try {
			Process child = Runtime.getRuntime().exec("cmd.exe /c start " + command);
			InputStream in = child.getInputStream();
			int c;
			while ((c = in.read()) != -1) {
			}
			in.close();
			try {
				child.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean exec(List<String> commandList) throws IOException {
		File batFile = new File("D:\\" + UUID.randomUUID() + ".bat");
		boolean flag = exec(batFile, commandList);
		// 删除临时文件
		batFile.delete();
		return flag;
	}

	public static boolean exec(File batFile, List<String> commandList) throws IOException {
		if (createBatFile(batFile, commandList)) {
			String command = batFile.getPath();
			callCmd(command);
			return true;
		} else
			return false;
	}

	public static boolean createBatFile(File batFile, List<String> commandList) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String command : commandList)
			sb.append(command + "\n");

		sb.append("exit");
		if (batFile.exists() || batFile.createNewFile()) {
			FileUtil.writeText(batFile, sb.toString());
			return true;
		} else
			return false;
	}

}
