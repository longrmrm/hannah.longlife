package com.hannah.common.file;

import com.hannah.common.util.FileUtil;
import com.hannah.common.util.StringUtil;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Operate ini files. Ini file has many sections, and every section has many
 * key/value sets.
 * @author longrm
 * @date 2012-5-24
 */
public class Ini implements FileLoader {

	private boolean isUtf = false;

	private Map<String, Map<String, String>> sectionMap = new HashMap<String, Map<String, String>>();

	public boolean isUtf() {
		return isUtf;
	}

	public void setUtf(boolean isUtf) {
		this.isUtf = isUtf;
	}

	public Map<String, Map<String, String>> getSectionMap() {
		return sectionMap;
	}

	public void setSectionMap(Map<String, Map<String, String>> sectionMap) {
		this.sectionMap = sectionMap;
	}

	public Map<String, String> getSectionContext(String sectionName) {
		return sectionMap.get(sectionName);
	}

	public void load(File file) throws IOException {
		isUtf = FileUtil.isUtf(file);
		load(new FileInputStream(file));
	}

	public void load(InputStream inStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inStream, isUtf ? "UTF-8" : "GBK"));
		load(br);
		br.close();
	}

	public void load(BufferedReader br) throws IOException {
		sectionMap.clear();
		String section = null;
		Map<String, String> map = null;
		// read every line
		String line = br.readLine();
		while (line != null) {
			if (line.matches(FileConstants.SECTION_REGEX)) {
				if (section != null)
					sectionMap.put(section, map);
				section = FileConstants.getSectionName(line);
				map = new HashMap<String, String>();
				line = br.readLine();
				continue;
			}
			// add line string into list, ignore the front lines and all
			// comments
			if (section != null && !line.trim().equals("")
					&& !line.startsWith(FileConstants.COMMENT_PREFIX)) {
				int index = line.indexOf(FileConstants.EQUAL_KEY);
				if (index > 0)
					map.put(line.substring(0, index), line.substring(index + FileConstants.EQUAL_KEY.length()));
			}
			line = br.readLine();
		}
		if (section != null)
			sectionMap.put(section, map);
	}

	public void addSection(String sectionName) {
		if (sectionMap.get(sectionName) == null)
			sectionMap.put(sectionName, new HashMap<String, String>());
	}

	public void addSection(String sectionName, Map<String, String> context) {
		sectionMap.put(sectionName, context);
	}

	public void addSectionProperty(String sectionName, String key, String value) {
		if (sectionMap.get(sectionName) == null)
			addSection(sectionName);
		Map<String, String> context = sectionMap.get(sectionName);
		context.put(key, value);
	}

	public void addSectionProperties(String sectionName, Map<String, String> properties) {
		if (sectionMap.get(sectionName) == null)
			addSection(sectionName);
		Map<String, String> context = sectionMap.get(sectionName);
		context.putAll(properties);
	}

	public void deleteSection(String sectionName) {
		sectionMap.remove(sectionName);
	}

	public void deleteSectionProperty(String sectionName, String key) {
		Map<String, String> context = sectionMap.get(sectionName);
		if (context == null)
			return;
		context.remove(key);
	}

	public void deleteSectionProperties(String sectionName, Map<String, String> properties) {
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			deleteSectionProperty(sectionName, it.next());
		}
	}

	public void save(File file) throws IOException {
		save(new FileOutputStream(file), "");
	}

	public void save(OutputStream out, String comments) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		save(bw, comments);
		bw.close();
	}

	public void save(BufferedWriter bw, String comments) throws IOException {
		if (!StringUtil.isNull(comments)) {
			FileUtil.writeText(bw, comments, FileConstants.COMMENT_PREFIX);
			bw.newLine();
		}
		bw.write(FileConstants.COMMENT_PREFIX + new Date().toString());
		bw.newLine();
		Iterator<String> it = sectionMap.keySet().iterator();
		while (it.hasNext()) {
			String sectionName = it.next();
			bw.newLine();
			bw.write("[" + sectionName + "]");
			bw.newLine();
			Map<String, String> context = sectionMap.get(sectionName);
			Iterator<String> keyIt = context.keySet().iterator();
			while (keyIt.hasNext()) {
				String key = keyIt.next();
				bw.write(key + FileConstants.EQUAL_KEY + context.get(key));
				bw.newLine();
			}
		}
		bw.flush();
	}

}
