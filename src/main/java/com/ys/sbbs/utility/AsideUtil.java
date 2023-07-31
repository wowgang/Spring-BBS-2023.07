package com.ys.sbbs.utility;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ys.sbbs.controller.UserController;

@Service
public class AsideUtil {
	// 이렇게 하면 프로그램에서 안보임
	// 웹에서의 위치로 날씨 보여짐
	@Value("${roadAddrKey}") private String roadAddrKey;
	@Value("${kakaoApiKey}") private String kakaoApiKey;
	@Value("${openWeatherApiKey}") private String openWeatherApiKey;
	
	public String getTodayQuote(String filename) {
		String result = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename), 1024); // BufferedReader로 readline함
			int index = (int) Math.floor(Math.random() * 100); // 한줄 단위로 100을곱해서 0-99까지 해당한 인덱스 읽기
			for (int i=0; i<=index; i++) // 처음부터 인덱스까지 읽고
				result = br.readLine();	// result에 overwrite하기 랜덤넘버로 지정한 값이 읽혀지는것임
			br.close();
		} catch (Exception e) {	// throw하면 미룬거라 userController에서 exception이 또 떠서 여기서 try catch한것임
			e.printStackTrace();
		}
		return result;
	}
	
	public String squareImage(String profilePath, String fname) {
		String newFname = null;
		try {
			File file = new File(profilePath + fname);
			BufferedImage buffer = ImageIO.read(file);
			int width = buffer.getWidth();
			int height = buffer.getHeight();
			int size = width, x = 0, y = 0;
			if (width > height) {
				size = height;
				x = (width - size) / 2;
			} else if (width < height) {
				size = width;
				y = (height - size) / 2;
			}
			
			String now = LocalDateTime.now().toString().substring(0,22).replaceAll("[-T:.]", "");
			int idx = fname.lastIndexOf('.');
			String format = fname.substring(idx+1);
			newFname = now + fname.substring(idx);
			
			BufferedImage dest = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = dest.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(buffer, 0, 0, size, size, x, y, x + size, y + size, null);
			g.dispose();
			
			File dstFile = new File(profilePath + newFname);
			OutputStream os = new FileOutputStream(dstFile);
			ImageIO.write(dest, format, os);
			os.close();
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFname;
	}
	
	// 행안부 도로명주소 API
	public String getRoadAddr(String place) {
		String apiUrl = "https://www.juso.go.kr/addrlink/addrLinkApiJsonp.do";
		String roadAddr = null;
		try {
			String keyword = URLEncoder.encode(place, "utf-8");
			apiUrl += "?confmKey=" + roadAddrKey
					+ "&currentPage=1&countPerPage=10"
					+ "&keyword=" + keyword + "&resultType=json";
			URL url = new URL(apiUrl);	// 엑세스
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result.substring(1, result.length()-1)); // type casting
			JSONObject results = (JSONObject) obj.get("results");
			JSONArray juso = (JSONArray) results.get("juso");
			JSONObject jusoItem = (JSONObject) juso.get(0);
			roadAddr = (String) jusoItem.get("roadAddr");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roadAddr;
	}
	
	// 카카오 로컬 API - 위도, 경도
	public List<String> getGeoCode(String roadAddr) {
		List<String> list = new ArrayList<>();
		String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json";
		
		try {
			String keyword = URLEncoder.encode(roadAddr, "utf-8");
			apiUrl += "?query=" + keyword;
			URL url = new URL(apiUrl);
			// 헤더 설정
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Authorization", "KakaoAK " + kakaoApiKey);
			conn.setDoInput(true);
			
			// 응답 결과 확인
			int responseCode = conn.getResponseCode();
//			System.out.println("responseCode: " + responseCode);
			
			// 데이터 수신
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);	// 괄호없어서 ({이거 아니고 { 이렇게 시작해서
			JSONArray documents = (JSONArray) obj.get("documents");
			JSONObject localItem = (JSONObject) documents.get(0);
			list.add((String) localItem.get("x")); // 경도
			list.add((String) localItem.get("y")); // 위도
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
	// OpenWeather API
	public String getWheather(String lon, String lat) {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
		apiUrl += "?lat=" + lat + "&lon=" +  lon + "&appid=" + openWeatherApiKey 
				+ "&units=metric&lang=kr";
		List<String> list = new ArrayList<>();
		String weatherStr = null;
		try {
			URL url = new URL(apiUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));

			String line = null, result = "";
			while ((line = br.readLine()) != null)
				result += line;
			br.close();
			
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(result);
			JSONArray weather = (JSONArray) obj.get("weather");
			JSONObject weatherItem = (JSONObject) weather.get(0);
			String desc = (String) weatherItem.get("description");
			String iconCode = (String) weatherItem.get("icon");
			System.out.println(weatherItem);
			
			JSONObject main = (JSONObject) obj.get("main");
			double temp = (Double) main.get("temp");
			String tempStr = String.format("%.1f", temp);
			String iconUrl = "http://api.openweathermap.org/img/w/" + iconCode + ".png";
			weatherStr = "<img src=\"" + iconUrl + "\" height=\"28\">" + desc + ","
					+ " 온도: " + tempStr + "&#8451";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return weatherStr;
	}
}

