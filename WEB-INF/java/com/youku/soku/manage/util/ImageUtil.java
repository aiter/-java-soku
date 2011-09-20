package com.youku.soku.manage.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.youku.ftp.MovieImageFTP;
import com.youku.search.console.util.Wget;
import com.youku.soku.manage.common.FileUploadAction;

public class ImageUtil {

	public static String DEFAULT_NO_IMG_LOG = "0900641F464A911EDD00000000000000000000-0000-0000-0000-00009197BA80";

	public static String OLD_DEFAULT_NO_IMG_LOG = "0100641F464A92674312AC000000007CFC75D6-1816-BF85-F8D9-AD5E1B3DAD1B";
	
	public static boolean isNoImage(String url) {
		
		if(StringUtils.isBlank(url)) {
			return true;
		} else if(url.contains(DEFAULT_NO_IMG_LOG)){
			return true;
		} else if(url.contains(OLD_DEFAULT_NO_IMG_LOG)) {
			return true;
		} 
		return false;
	}
	
	
	public static boolean isYoukuImage(String url) {
		if (url == null) {
			return false;
		}
		Pattern p = Pattern.compile("^http://g\\d.ykimg.com/[\\w,-]+");
		Matcher m = p.matcher(url);

		return m.matches();
	}

	public static String getDisplayUrl(String dbUrl) {

		if (dbUrl == null || "".equals(dbUrl) || ("NA".equals(dbUrl))) {
			dbUrl = DEFAULT_NO_IMG_LOG;
		}

		dbUrl = dbUrl.replaceFirst("^http://g\\d.ykimg.com/", "");

		StringBuilder sb = new StringBuilder("http://g");
		sb.append(System.currentTimeMillis() % 4 + 1);
		sb.append(".ykimg.com/");
		sb.append(dbUrl);

		return sb.toString();
	}

	public static String getStoreUrl(String url) {
		if (url == null || url.equals("")) {
			return "NA";
		} else {
			url = url.replaceFirst("^http://g\\d.ykimg.com/", "");
			if (DEFAULT_NO_IMG_LOG.equals(url)) {
				return "NA";
			}
			return url;
		}
	}

	public static String storeImage(String url, String personFlag) throws Exception {
		String imageYoukuUrl = null;
		if (!isYoukuImage(url)) {
			byte[] bytes = Wget.get(url);

			boolean imageValid = false;
			try {
				imageValid = (ImageIO.read(new ByteArrayInputStream(bytes)) != null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Image img = ImageIO.read(new ByteArrayInputStream(bytes));
			byte[] newImage = null;
			if(FileUploadAction.PERSON_IMAGE_VALUE.equals(personFlag)) {
				newImage = ImageUtil.scaleImage(img, 128, 128, "");
			} else {
				newImage = ImageUtil.scaleImage(img, 128, 96, "");
			}

			if (imageValid) {
				imageYoukuUrl = MovieImageFTP.upload(newImage);
				if (imageYoukuUrl == null) {
					throw new RuntimeException("上传图像文件到ftp失败！");
				}
			} else {
				imageYoukuUrl = "NA";
			}
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
		/*out.close();
		Thread.sleep(1000);
		InputStream inputStream = new FileInputStream("/tmp/soku_image_tmp");
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        while (true) {
            int length = inputStream.read(buffer);
            if (length < 1) {
                break;
            }

            outputStream.write(buffer, 0, length);
        }*/
        
        return out.toByteArray();
	}
	
	public static Image getImage(String url) {

		try {
			byte[] bytes = Wget.get(url);
			Image img = ImageIO.read(new ByteArrayInputStream(bytes));
			
			System.out.println(bytes.length);
			/*if(bytes.length < 10240) {
				return null;
			}*/
			return img;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void saveImage(String path) throws Exception {
		int startPos = path.indexOf("com/");
		int endPos = path.length() - 1;
		if(startPos == -1) {
			return;
		}
		String fileName = path.substring(startPos + 4, endPos);		
		byte[] bytes = Wget.get(path);
		
		String fileDist = "/tmp/soku_image/" + fileName;
		System.out.println(fileDist);
		File f = new File(fileDist);
		f.createNewFile();
		OutputStream out = new FileOutputStream(f);
		out.write(bytes);
		
		scaleImage(getImage(path), 128, 96, "/tmp/soku_image_scale/" + fileName);
	}
	
	public static String uploadScaledImage(byte[] bytes) {
		String imageYoukuUrl = "";
		if(bytes != null) {
			imageYoukuUrl = MovieImageFTP.upload(bytes);
		} else {
			imageYoukuUrl = "NOT_CHANGE";
		}
		
		if (imageYoukuUrl == null) {
			throw new RuntimeException("上传图像文件到ftp失败！");
		}
		return imageYoukuUrl;
	}
	
	public static String changePic(String url) throws Exception{
		byte[] scaledImg = scaleImage(getImage(url), 128, 96, "");
		String resultUrl = uploadScaledImage(scaledImg);
		return getDisplayUrl(resultUrl);
	}

	public static void main(String[] args) {
		/*System.out.println(isYoukuImage(null));
		String url = getDisplayUrl("4A83C28F00000000000000000000-0000-0");
		System.out.println(isYoukuImage(url));

		String url1 = getDisplayUrl("0900641F464A73D1A200000000000000000000-0000-0000-0000-000010C52FF5");
		String url2 = getDisplayUrl(DEFAULT_VIDEO_LOGO);

		System.out.println(getStoreUrl(url1));
		System.out.println(getStoreUrl(url2));*/
		
		String imagePaths = "| http://g2.ykimg.com/0900641F464A93128E00000000000000000000-0000-0000-0000-0000BE2E0F2A |  http://g4.ykimg.com/0900641F4648A99DAC00000000000000000000-0000-0000-0000-00002DC1141D |  http://g1.ykimg.com/0900641F4649351FD000000000000000000000-0000-0000-0000-000064E1CC9E |  http://g1.ykimg.com/0900641F46489E91AD00000000000000000000-0000-0000-0000-0000EC3C3C6B |  http://g3.ykimg.com/0900641F4648BB143700000000000000000000-0000-0000-0000-0000385FD879 |  http://g3.ykimg.com/0900641F464A863B5500000000000000000000-0000-0000-0000-0000B16A7891 |  http://g4.ykimg.com/0900641F464951F43C00000000000000000000-0000-0000-0000-000051893617 |  http://g2.ykimg.com/0900641F464907860500000000000000000000-0000-0000-0000-00006ACD8E4E |  http://g3.ykimg.com/0900641F464A88767300000000000000000000-0000-0000-0000-0000554BB6D8 |  http://g3.ykimg.com/0900641F464847618400000000000000000000-0000-0000-0000-0000E90C76D9 |  http://g4.ykimg.com/0900641F4647CB7F3F00000000000000000000-0000-0000-0000-0000EC74C3D9 |  http://g1.ykimg.com/0900641F46491183F900000000000000000000-0000-0000-0000-00004265E4CF |  http://g2.ykimg.com/0900641F464818D1F800000000000000000000-0000-0000-0000-00000A030964 |  http://g2.ykimg.com/0900641F464A81D71100000000000000000000-0000-0000-0000-00006F4818E6 |  http://g4.ykimg.com/0900641F464A4960C700000000000000000000-0000-0000-0000-000054D4FC8E |  http://g1.ykimg.com/0900641F464902579700000000000000000000-0000-0000-0000-000065338D20 |  http://g1.ykimg.com/0900641F464A6A5EC600000000000000000000-0000-0000-0000-0000456DE274 |  http://g2.ykimg.com/0900641F464899D24A00000000000000000000-0000-0000-0000-0000282332A9 |  http://g4.ykimg.com/0900641F4648978D3700000000000000000000-0000-0000-0000-00000E39ABCC |  http://g4.ykimg.com/0900641F464A86DDE000000000000000000000-0000-0000-0000-0000010CA733 |  http://g3.ykimg.com/0900641F464A63C1D600000000000000000000-0000-0000-0000-0000062CC339 |  http://g2.ykimg.com/0900641F464926716800000000000000000000-0000-0000-0000-0000643640E4 |  http://g3.ykimg.com/0900641F46480C66A900000000000000000000-0000-0000-0000-00005F6B9086 |  http://g1.ykimg.com/0900641F46489C811C00000000000000000000-0000-0000-0000-00002AD89C64 |  http://g4.ykimg.com/0900641F4648306AFD00000000000000000000-0000-0000-0000-0000EE99D25D |  http://g2.ykimg.com/0900641F4648BBDF4100000000000000000000-0000-0000-0000-000018F67310 |  http://g4.ykimg.com/0900641F4647EC3D3D00000000000000000000-0000-0000-0000-000097B70AD4 |  http://g3.ykimg.com/0900641F4649A7572800000000000000000000-0000-0000-0000-0000777DB37C |  http://g4.ykimg.com/0900641F464A4A747400000000000000000000-0000-0000-0000-000084E84173 |  http://g3.ykimg.com/0900641F464A8DC12500000000000000000000-0000-0000-0000-0000848ABC36 |  http://g4.ykimg.com/0900641F464A852E2700000000000000000000-0000-0000-0000-0000B1A37DE6 |  http://g3.ykimg.com/0900641F46485F66C500000000000000000000-0000-0000-0000-0000F2AE06A3 |  http://g3.ykimg.com/0900641F46488C5BE100000000000000000000-0000-0000-0000-00005E49AD34 |  http://g4.ykimg.com/0900641F464A967CBA00000000000000000000-0000-0000-0000-0000DAFF0487 |  http://g4.ykimg.com/0900641F464A51D01000000000000000000000-0000-0000-0000-000023B9D158 |  http://g4.ykimg.com/0900641F4649B23F2B00000000000000000000-0000-0000-0000-00005C8B67C3 |  http://g3.ykimg.com/0900641F464A9023B300000000000000000000-0000-0000-0000-000096623775 |  http://g2.ykimg.com/0900641F4648E5972000000000000000000000-0000-0000-0000-000009D94551 |  http://g2.ykimg.com/0900641F46481A5C8800000000000000000000-0000-0000-0000-0000C528087D |  http://g1.ykimg.com/0100641F4646F61DCD684F0042A3A1F23B4AF8-65EA-4FB7-78B4-2CC358DFBCE5 |  http://g3.ykimg.com/0900641F464891C14200000000000000000000-0000-0000-0000-0000AF5A4B2C |  http://g4.ykimg.com/0900641F464A6ED00300000000000000000000-0000-0000-0000-00003E17B6F2 |  http://g4.ykimg.com/0900641F46493EA86400000000000000000000-0000-0000-0000-0000C5F4998F |  http://g2.ykimg.com/0900641F464A4B802200000000000000000000-0000-0000-0000-0000836BAC9B |  http://g3.ykimg.com/0900641F46494AA38200000000000000000000-0000-0000-0000-000017813CBE |  http://g2.ykimg.com/0900641F4647FFC2A500000000000000000000-0000-0000-0000-0000CA9E3E22 |  http://g2.ykimg.com/0900641F464994B52C00000000000000000000-0000-0000-0000-0000C4C73C29 |  http://g3.ykimg.com/0100641F464B0878E424430114EAAE1F1F8A85-D58D-3F45-470C-3EF3723A7676 |  http://g2.ykimg.com/0900641F4648FDB99D00000000000000000000-0000-0000-0000-0000B2DC7D7F |  http://g4.ykimg.com/0900641F4648436A6800000000000000000000-0000-0000-0000-0000EA300DC6 |  http://g1.ykimg.com/0900641F46496C787D00000000000000000000-0000-0000-0000-0000EEDC3BE5 |  http://g3.ykimg.com/0100011F46478DA93FEEA9000006E6B72F85FB-AE6F-BE2F-6E6C-89A09496057E |  http://g2.ykimg.com/0900641F4649819A5000000000000000000000-0000-0000-0000-0000A46743B2 |  http://g4.ykimg.com/0900641F4648B0041500000000000000000000-0000-0000-0000-0000E94DF9CB |  http://g1.ykimg.com/0900641F464A7CEB8500000000000000000000-0000-0000-0000-0000C42A1310 |  http://g1.ykimg.com/0900641F4648FEE72800000000000000000000-0000-0000-0000-0000BCF85D1B |  http://g4.ykimg.com/0900641F46496B38CC00000000000000000000-0000-0000-0000-0000FB48E18B |  http://g3.ykimg.com/0900641F464A90DFA300000000000000000000-0000-0000-0000-000012CF45E5 |  http://g3.ykimg.com/0900641F4649AC54DF00000000000000000000-0000-0000-0000-0000772C2C3E |  http://g1.ykimg.com/0900641F464963B5AE00000000000000000000-0000-0000-0000-0000B539CB22 |  http://g4.ykimg.com/0100641F4646F8A7C6E9280042AFA3CB923994-6A2C-00D1-DBC0-0ECF27A06CE0 |  http://g4.ykimg.com/0900641F46490F832B00000000000000000000-0000-0000-0000-0000ADC6A57B |  http://g4.ykimg.com/0900641F464950FC2600000000000000000000-0000-0000-0000-0000583A4E2E |  http://g1.ykimg.com/0900641F464A64AF9700000000000000000000-0000-0000-0000-000039B8F1F4 |  http://g2.ykimg.com/0900641F46484DC11100000000000000000000-0000-0000-0000-00009BEE025B |  http://g3.ykimg.com/0900641F4648E8D8F200000000000000000000-0000-0000-0000-0000ED1E39DB |  http://g1.ykimg.com/0100641F464B81D650A6BB002AA5F52B34093A-F29B-E28A-C354-C43388F864E4 |  http://g3.ykimg.com/0900641F464A81CD5000000000000000000000-0000-0000-0000-0000D77C16B3 |  http://g1.ykimg.com/0900641F4649AABD0300000000000000000000-0000-0000-0000-00003BB6C043 |  http://g2.ykimg.com/0900641F46482577AE00000000000000000000-0000-0000-0000-00006CFAC4EB |  http://g4.ykimg.com/0900641F4648839CB600000000000000000000-0000-0000-0000-000095770BC8 |  http://g4.ykimg.com/0100641F464B6F1B24E67A0049FC5DE4DD75E3-3551-9613-8AD5-47656CCF5AD3 |  http://g4.ykimg.com/0900641F46494B4CF600000000000000000000-0000-0000-0000-00006A703487 |  http://g4.ykimg.com/0900641F464A632E6D00000000000000000000-0000-0000-0000-000024A9917D |  http://g1.ykimg.com/0900641F464A4D2EC000000000000000000000-0000-0000-0000-00007F447B97 |  http://g1.ykimg.com/0900641F4648A8943500000000000000000000-0000-0000-0000-000039F70EF3 |  http://g2.ykimg.com/0900641F464801042400000000000000000000-0000-0000-0000-00009049CEA4 |  http://g4.ykimg.com/0900641F464847EAE300000000000000000000-0000-0000-0000-0000F6DB621F |  http://g3.ykimg.com/0900641F464885843E00000000000000000000-0000-0000-0000-0000D0A8B59F |  http://g2.ykimg.com/0900641F464A572CC900000000000000000000-0000-0000-0000-000089E6EE19 |  http://g3.ykimg.com/0900641F4648D09DD300000000000000000000-0000-0000-0000-0000A6AAF003 |  http://g2.ykimg.com/0900641F464900C26F00000000000000000000-0000-0000-0000-000025836FC0 |  http://g3.ykimg.com/0900641F4647DEE78300000000000000000000-0000-0000-0000-0000ABA4D974 |  http://g3.ykimg.com/0900641F46499AD66B00000000000000000000-0000-0000-0000-0000CE2356BF |  http://g4.ykimg.com/0900641F4648CC0C2700000000000000000000-0000-0000-0000-0000C5F85A1F |  http://g1.ykimg.com/0900641F464A677B9800000000000000000000-0000-0000-0000-00008D425FB3 |  http://g1.ykimg.com/0900641F4648E8450100000000000000000000-0000-0000-0000-000009E5431A |  http://g3.ykimg.com/0900641F4648FF319D00000000000000000000-0000-0000-0000-0000612555CD |  http://g1.ykimg.com/0900641F4648C6BFC100000000000000000000-0000-0000-0000-0000B631243A |  http://g2.ykimg.com/0900641F464A5BC13C00000000000000000000-0000-0000-0000-0000E1EDE38E |  http://g1.ykimg.com/0900641F4647E9850600000000000000000000-0000-0000-0000-0000B76C1451 |  http://g1.ykimg.com/0900641F464923A31300000000000000000000-0000-0000-0000-00001DEFF846 |  http://g2.ykimg.com/0900641F464947CA1A00000000000000000000-0000-0000-0000-00000181960F |  http://g1.ykimg.com/0100641F464B2F388BCF75007C5E2075C4BC48-6F29-B0C3-6A3A-A34C654E2098 |  http://g1.ykimg.com/0900641F46486CA4DA00000000000000000000-0000-0000-0000-0000D50E86E2 |  http://g4.ykimg.com/0900641F46491060CB00000000000000000000-0000-0000-0000-0000F1F99D57 |  http://g3.ykimg.com/0900641F4647EFE24500000000000000000000-0000-0000-0000-0000260D7A1F |  http://g1.ykimg.com/0900641F464890A7C000000000000000000000-0000-0000-0000-00004E15DB2C |  http://g1.ykimg.com/0900641F46494841D000000000000000000000-0000-0000-0000-0000666C1D64 |  http://g1.ykimg.com/0900641F4648D3799300000000000000000000-0000-0000-0000-00000C7CFF24 |";
		
		File htmlFile = new File("/tmp/test_scalimage.html");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
			bw.write("<html><body><table>");
			int i =0;
			String[] ss = imagePaths.split("[|]");
			System.out.println(Arrays.toString(ss));
			for(String s : ss) {
				i++;
				System.out.println(s);
				saveImage(s);
				
				byte[] scaledImg = scaleImage(getImage(s), 128, 96, "");
					
				boolean imageValid = false;
				try {
					imageValid = (ImageIO.read(new ByteArrayInputStream(scaledImg)) != null);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String url = "";
				try {
					//url = uploadScaledImage(scaledImg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println(getDisplayUrl(url));
				bw.write("<tr><td>");
				bw.write("<img width='128' height='96' src=\"soku_image/" + url + "\" />");
				bw.write("</td>");
				bw.write("<td>");
				bw.write("<img src=\"" + getDisplayUrl(url)	 + "\" />");
				bw.write("</td>");
				bw.write("</tr>\n");
			}
			bw.write("</body></html>");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
