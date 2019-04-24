package com.xunye.zhibott.helper;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class HttpUtil {
	private static int CONNECTION_TIMEOUT_INT = 3000;
	private static int READ_TIMEOUT_INT = 3000;

	public static  void main(String args[]){
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "xyw");
		params.put("password", "xyw");
		params.put("phone", "13631505031");
		String res=sendPost(params,"http://192.168.2.242:8080/system/login/usershop");
		System.out.print(res);
	}


	public static String sendPost(Map<String, String> paramsMap, String URL) {
		String result = ""; // 获取服务器返回数据
		StringBuffer buff = new StringBuffer();  // 创建一个空StringBuffer存发给服务器的数据
		// 迭代Map拼接请求参数
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			buff.append(entry.getKey()).append('=').append(entry.getValue())
					.append('&');
		}
		if (buff != null) {
			buff.delete(buff.lastIndexOf("&"), buff.length());
			URL url = null;
			BufferedReader bufferReader = null;
			HttpURLConnection urlConn = null;
			try {
				url = new URL(URL);
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setDoInput(true); // 设置输入流采用字节流
				urlConn.setDoOutput(true);// 设置输出流采用字节流
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false); // 设置缓存
				urlConn.setRequestProperty("Charset", "utf-8");
				urlConn.setConnectTimeout(CONNECTION_TIMEOUT_INT);
				urlConn.setReadTimeout(READ_TIMEOUT_INT);

				urlConn.connect(); // 连接既往服务端发送消息

				DataOutputStream dop = new DataOutputStream(
						urlConn.getOutputStream());
				dop.write(buff.toString().getBytes("utf-8")); // 发送参数
				dop.flush(); // 发送，清空缓存
				dop.close(); // 关闭

				int statusCode = urlConn.getResponseCode();
				if (statusCode == 200) {

					// 下面开始做接收工作
					InputStreamReader inputStream = new InputStreamReader(
							urlConn.getInputStream());
					bufferReader = new BufferedReader(inputStream);
					String readLine = null;
					while ((readLine = bufferReader.readLine()) != null) {
						result += readLine;
					}
					inputStream.close();
				} else {
					InputStreamReader inputStream = new InputStreamReader(
							urlConn.getErrorStream());
					bufferReader = new BufferedReader(inputStream);
					String readLine = null;
					while ((readLine = bufferReader.readLine()) != null) {
						result += readLine;
					}
					inputStream.close();
				}
			} catch (MalformedURLException e) {
				System.out.println("post MalformedURLException===" + e.toString());
			} catch (IOException e) {
				System.out.println("post  IOException==" + e.toString());
			} finally {
				if (urlConn != null) {
					urlConn.disconnect();
				}

				if (bufferReader != null) {
					try {
						bufferReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}



	/**
	 * 通用Get請求
	 * @param paramsMap
	 * @param url
	 * @return
	 */
	public static String sendGet(Map<String, String> paramsMap, String url) {

		String getUrl;
		BufferedReader bufferReader = null;
		StringBuffer buff = new StringBuffer();
		buff.append("?");
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			buff.append(entry.getKey() + "=");
			buff.append(entry.getValue());
			buff.append("&");
		}
		getUrl = url + buff.toString();

		System.out.println("this request get url is " + getUrl);
		String resultStr = "";
		HttpURLConnection urlConnection = null;
		try {
			URL curl = new URL(getUrl);
			urlConnection = (HttpURLConnection) curl.openConnection();
			urlConnection
					.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(CONNECTION_TIMEOUT_INT);
			urlConnection.setReadTimeout(READ_TIMEOUT_INT);
			int statusCode = urlConnection.getResponseCode();
			if (statusCode == 200) {
				System.out.println("sendGet success");
				InputStreamReader inputStream = new InputStreamReader(
						urlConnection.getInputStream());
				bufferReader = new BufferedReader(inputStream);
				String readLine = null;
				while ((readLine = bufferReader.readLine()) != null) {
					resultStr += readLine;
				}
				inputStream.close();
			} else {
				InputStreamReader inputStream = new InputStreamReader(
						urlConnection.getErrorStream());
				bufferReader = new BufferedReader(inputStream);
				String readLine = null;
				while ((readLine = bufferReader.readLine()) != null) {
					resultStr += readLine;
				}
				inputStream.close();
			}
		} catch (MalformedURLException e) {
			System.out.println("post  MalformedURLException==" + e.toString());
		} catch (ProtocolException e) {
			System.out.println("post  ProtocolException==" + e.toString());
		} catch (IOException e) {
			System.out.println("post  IOException==" + e.toString());
		} finally {
			if (null != urlConnection)
				urlConnection.disconnect();
			if (null != bufferReader) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return resultStr;
	}
	
	
	/**
	 * 获取二维码所需要的url值
	 * @param json 携带的参数
	 * @param URL 微信二维码获取地址
	 * @return
	 */
	public static String sendQrcodePost(String json, String URL) {
		String result = ""; // 获取服务器返回数据
		System.out.println("json========="+json);
		if (json != null) {
			URL url = null;
			BufferedReader bufferReader = null;
			HttpURLConnection urlConn = null;
			try {
				url = new URL(URL);
				urlConn = (HttpURLConnection) url.openConnection();
				urlConn.setDoInput(true); // 设置输入流采用字节流
				urlConn.setDoOutput(true); // 设置输出流采用字节流
				urlConn.setRequestMethod("POST");
				urlConn.setUseCaches(false); // 设置缓存
				urlConn.setRequestProperty("Charset", "utf-8");
				urlConn.setConnectTimeout(CONNECTION_TIMEOUT_INT);
				urlConn.setReadTimeout(READ_TIMEOUT_INT);

				urlConn.connect(); // 连接既往服务端发送消息

				DataOutputStream dop = new DataOutputStream(
						urlConn.getOutputStream());
				dop.write(json.toString().getBytes("utf-8")); // 发送参数
				dop.flush(); // 发送，清空缓存
				dop.close(); // 关闭

				int statusCode = urlConn.getResponseCode();
				if (statusCode == 200) {

					// 下面开始做接收工作
					InputStreamReader inputStream = new InputStreamReader(
							urlConn.getInputStream());
					bufferReader = new BufferedReader(inputStream);
					String readLine = null;
					while ((readLine = bufferReader.readLine()) != null) {
						result += readLine;
					}
					inputStream.close();
				} else {
					System.out.println("sendPost ResponseCode Statuscode ==" + statusCode);
				}
			} catch (MalformedURLException e) {
				System.out.println("post MalformedURLException===" + e.toString());
			} catch (IOException e) {
				System.out.println("post  IOException==" + e.toString());
			} finally {
				if (urlConn != null) {
					urlConn.disconnect();
				}

				if (bufferReader != null) {
					try {
						bufferReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}
	

}
