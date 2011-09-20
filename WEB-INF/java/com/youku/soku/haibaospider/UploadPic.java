package com.youku.soku.haibaospider;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.youku.ftp.MovieImageFTP;
import com.youku.search.console.util.Wget;

public class UploadPic {
	
	public static String uploadHaibao(byte[] bytes){
		Image img;
		String imageYoukuUrl =null;
		try {
			img = ImageIO.read(new ByteArrayInputStream(bytes));
			if(null==img) return null;
			byte[] newImage = scaleImage(img, 120, 170, "");
			imageYoukuUrl = MovieImageFTP.upload(newImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageYoukuUrl;
	}
	
	public static byte[] scaleImage(Image src, int widthdist, int heightdist, String distFile) throws Exception {
		
		if(src == null) {
			return null;
		}
			
		BufferedImage tag = new BufferedImage((int) widthdist,
				(int) heightdist, BufferedImage.TYPE_INT_RGB);
		/*
		 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
		 */
		tag.getGraphics().drawImage(
				src
						.getScaledInstance(widthdist, heightdist,
								Image.SCALE_SMOOTH), 0, 0, null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(tag);
        
        return out.toByteArray();
	}
	
	public static String uploadHaibao(String url){
		byte[] bytes = null;
		try {
			bytes = Wget.get(url);
			int block = 0;
			while(null==bytes&&block<2){
				SpiderUtils.sleep();
				block++;
				bytes = Wget.get(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(null!=bytes){
			return uploadHaibao(bytes);
		}
		return null;
	}
	
	public static void main(String[]  args) throws Exception{
		String url = "http://img0.hao123.com/data/6dc3efd9ca76ad77172a9315190b3f7f";
		byte[] bytes = Wget.get(url);
		if(null!=bytes)
			System.out.println(uploadHaibao(bytes));
		else
			System.out.println("--------");
	}
}
