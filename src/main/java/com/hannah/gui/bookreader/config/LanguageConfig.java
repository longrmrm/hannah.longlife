package com.hannah.gui.bookreader.config;

import com.hannah.common.file.AbstractConfig;

import java.io.File;
import java.util.Map;

public class LanguageConfig extends AbstractConfig {

	private Map<String, String> langTrans;

	@Override
	protected String getFilePath() {
		return Constant.CURRENT_PATH + File.separator + Constant.LANGUAGE_FILE;
	}

	public void setLanguage(String language) {
		langTrans = getSection(language);
	}

	public String getTrans(String key) {
		String tran = langTrans.get(key);
		return tran == null ? key : tran;
	}

}
