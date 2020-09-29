package com.hannah.common.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileLoader {

	void load(File file) throws IOException;

	void load(InputStream inStream) throws IOException;

}
