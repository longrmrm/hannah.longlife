package com.hannah.common.file;

import com.hannah.common.util.FileUtil;

import java.io.*;
import java.util.*;

/**
 * Operate lite ini files. Lite Ini file has many sections, and every section is
 * a string list.<br>
 * For example:<br>
 * [sectionA]<br>
 * StringA1<br>
 * StringA2<br>
 * [sectionB]<br>
 * StringB1<br>
 * StringB2<br>
 * ...
 * @author longrm
 * @date 2012-5-24
 */
public class LiteIni implements FileLoader {

	private Map<String, List<String>> sectionMap = new HashMap<String, List<String>>();

	public Map<String, List<String>> getSectionMap() {
		return sectionMap;
	}

	public void setSectionMap(Map<String, List<String>> sectionMap) {
		this.sectionMap = sectionMap;
	}

	public List<String> getSectionContext(String sectionName) {
		return sectionMap.get(sectionName);
	}

	public void load(File file) throws IOException {
		load(new FileInputStream(file));
	}

	public void load(InputStream inStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
		load(br);
		br.close();
	}

	public void load(BufferedReader br) throws IOException {
		sectionMap.clear();
		String section = null;
		List<String> list = null;
		// read every line
		String line = br.readLine();
		while (line != null) {
			if (line.matches(FileConstants.SECTION_REGEX)) {
				if (section != null)
					sectionMap.put(section, list);
				section = FileConstants.getSectionName(line);
				list = new ArrayList<String>();
				line = br.readLine();
				continue;
			}
			// add line string into list, ignore the front lines and all
			// comments
			if (section != null && !line.trim().equals("")
					&& !line.startsWith(FileConstants.COMMENT_PREFIX))
				list.add(line);
			line = br.readLine();
		}
		if (section != null)
			sectionMap.put(section, list);
	}

	public void addSection(String sectionName) {
		if (sectionMap.get(sectionName) == null)
			sectionMap.put(sectionName, new ArrayList<String>());
	}

	public void addSection(String sectionName, List<String> context) {
		sectionMap.put(sectionName, context);
	}

	public void addSectionLine(String sectionName, String line) {
		if (sectionMap.get(sectionName) == null)
			addSection(sectionName);
		
		List<String> context = sectionMap.get(sectionName);
		for (String tmpLine : context) {
			// check the record whether it has been existed
			if (tmpLine.equals(line))
				return;
		}
		context.add(line);
	}

	public void addSectionLines(String sectionName, List<String> lines) {
		for (String line : lines) {
			addSectionLine(sectionName, line);
		}
	}

	public void deleteSection(String sectionName) {
		sectionMap.remove(sectionName);
	}

	public void deleteSectionLine(String sectionName, String line) {
		List<String> context = sectionMap.get(sectionName);
		if (context == null)
			return;
		context.remove(line);
	}

	public void deleteSectionLines(String sectionName, List<String> lines) {
		for (String line : lines) {
			deleteSectionLine(sectionName, line);
		}
	}

	public void store(OutputStream out, String comments) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		store(bw, comments);
		bw.close();
	}

	public void store(BufferedWriter bw, String comments) throws IOException {
		if (comments != null) {
			FileUtil.writeText(bw, comments, FileConstants.COMMENT_PREFIX);
			bw.newLine();
		}
		bw.write(FileConstants.COMMENT_PREFIX + new Date().toString());
		bw.newLine();
		Iterator<String> it = sectionMap.keySet().iterator();
		while (it.hasNext()) {
			String sectionName = it.next();
			bw.write("[" + sectionName + "]");
			bw.newLine();
			List<String> context = sectionMap.get(sectionName);
			for (String line : context) {
				bw.write(line);
				bw.newLine();
			}
		}
		bw.flush();
	}

}
