package com.hannah.http.google;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hannah.http.util.HttpRequestUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoogleQuery {

	public GoogleQuery() throws UnsupportedEncodingException {
		String query = URLEncoder.encode("中国人", "UTF-8");
		// String?query?=?URLEncoder.encode("AOP示例",?"UTF-8");??
		for (int i = 0; i < 3; i++) {
			makeQuery("http://ajax.googleapis.com/ajax/services/search/web?start=" + i * 8 + "&rsz=large&v=1.0&q="
					+ query);
		}

	}

	private void makeQuery(String httpUrl) {
		try {
			HttpResponse response = HttpRequestUtil.sendGetRequest(new DefaultHttpClient(), httpUrl);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.err.println("Method?failed:?" + response.getStatusLine());
			}
			String content = EntityUtils.toString(response.getEntity());
			// JSONObject json = JSONObject.fromObject(response);

			System.out.println(content);
			JSONObject json = JSON.parseObject(content);
			System.out.println("Total?results?=?"
					+ json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount"));
			JSONArray ja = json.getJSONObject("responseData").getJSONArray("results");

			System.out.println("?Results:");
			for (int i = 0; i < ja.size(); i++) {
				JSONObject j = ja.getJSONObject(i);
				System.out.println(j.getString("titleNoFormatting"));
				System.out.println(j.getString("url"));
				System.out.println(j.get("content"));
			}
		} catch (Exception e) {
			System.err.println("Something?went?wrong...");
			e.printStackTrace();
		}
		System.out.println("--------------------------------------------");
	}

	public static void main(String args[]) {
		try {
			new GoogleQuery();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}