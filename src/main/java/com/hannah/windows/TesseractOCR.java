package com.hannah.windows;

public class TesseractOCR {

	private final static String EXE_PATH = "D:\\\"Program Files\"\\Tesseract-OCR\\tesseract.exe";

	public static void recognize(String imagePath, String outTxtPath) {
		recognize(imagePath, outTxtPath, "eng");
	}

	public static void recognize(String imagePath, String outTxtPath, String language) {
		String command = EXE_PATH + " " + imagePath + " " + outTxtPath + " -l " + language;
		CmdUtil.callCmd(command);
	}

}
