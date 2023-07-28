package com.ys.sbbs.utility;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.ys.sbbs.controller.UserController;

@Service
public class AsideUtil {
	
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
		
		
		return null;
	}
	
	// 카카오 로컬 API - 위도, 경도
	public List<String> getGeoCode(String roadAddr) {
		List<String> list = new ArrayList<>();
		
		
		return list;
		
	}
	
	// OpenWeather API
	public String getWheather(List<String> geoCode) {
		
		
		return "가상의 날씨";
	}
}

