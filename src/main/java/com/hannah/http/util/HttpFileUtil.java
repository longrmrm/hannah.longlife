package com.hannah.http.util;

import com.hannah.common.util.FileUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author longrm
 * @date 2012-4-2
 */
public class HttpFileUtil {

	/**
	 * download file from url by http get mode
	 * @param filepath
	 * @param httpUrl
	 * @param headers
	 * @param params
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static File downloadFileByHttpGet(String filepath, String httpUrl, Map<String, String> headers,
			Map<String, String> params, String encoding) throws IOException {
		HttpResponse response = HttpRequestUtil.sendGetRequest(new DefaultHttpClient(), httpUrl, headers, params,
				encoding);
		return downloadFile(filepath, response);
	}

	/**
	 * download file from response
	 * @param filepath
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static File downloadFile(String filepath, HttpResponse response) throws IOException {
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			File file = FileUtil.createNewFile(filepath);
			if (file != null) {
				FileOutputStream outputStream = new FileOutputStream(file);
				response.getEntity().writeTo(outputStream);
				outputStream.close();
				EntityUtils.consume(response.getEntity());
			}
			return file;
		} else
			return null;
	}

	public static File downloadFileByHttpGet(String filepath, String httpUrl, Map<String, String> headers)
			throws IOException {
		return downloadFileByHttpGet(filepath, httpUrl, headers, null, null);
	}

	public static File downloadFileByHttpGet(String filepath, String httpUrl) throws IOException {
		return downloadFileByHttpGet(filepath, httpUrl, null, null, null);
	}

	/**
	 * download file from url by http post mode
	 * @param client
	 * @param filepath
	 * @param httpUrl
	 * @param headers
	 * @param params
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static File downloadFileByHttpPost(HttpClient client, String filepath, String httpUrl,
			Map<String, String> headers, Map<String, String> params, String encoding) throws IOException {
		HttpResponse response = HttpRequestUtil.sendPostRequest(client, httpUrl, headers, params, encoding);
		return downloadFile(filepath, response);
	}

	public static File downloadFileByHttpPost(String filepath, String httpUrl, Map<String, String> headers,
			Map<String, String> params, String encoding) throws IOException {
		return downloadFileByHttpPost(new DefaultHttpClient(), filepath, httpUrl, headers, params, encoding);
	}

	/**
	 * fetch checkcode
	 * @param response
	 * @return
	 * @throws IOException
	 */
	public static String requestCheckcode(HttpResponse response) throws IOException {
		InputStream is = response.getEntity().getContent();
		BufferedImage bi = ImageIO.read(is);
		String checkCode = (String) JOptionPane.showInputDialog(null, "请输入验证码：", "验证码", JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(bi), null, null);
		EntityUtils.consume(response.getEntity());
		return checkCode;
	}

	/**
	 * fetch checkcode
	 * @param response
	 * @param length 输入length位后自动确定
	 * @return
	 * @throws IOException
	 */
	public static String requestCheckcode(HttpResponse response, final int length) throws IOException {
		InputStream is = response.getEntity().getContent();
		BufferedImage bi = ImageIO.read(is);
		EntityUtils.consume(response.getEntity());

		final JTextField tf = new JTextField(16);
		Object[] message = { "请输入验证码：", tf };
		JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION,
				new ImageIcon(bi));
		final JDialog dialog = pane.createDialog(null, "验证码");
		tf.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				if (tf.getText().length() == length)
					dialog.dispose();
			}
		});
		dialog.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				tf.requestFocus();
			}
		});
		dialog.setVisible(true);
		return tf.getText();
	}

}
