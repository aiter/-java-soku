package com.youku.soku.manage.common;

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.youku.ftp.MovieImageFTP;
import com.youku.soku.manage.util.ImageUtil;

public class FileUploadAction extends BaseActionSupport {
	
	private Logger logger = Logger.getLogger(this.getClass());

	private File file;

	private String contentType;

	private String fileName;

	private static final int BUFFER_SIZE = 10 * 1024;
	
	private String filePath;
	
	private String imageUrl;
	
	private String personImage;
	
	public static final String PERSON_IMAGE_VALUE = "person_image";
	
	public String getFilePath() {
		return filePath;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	private Logger log = Logger.getLogger(this.getClass());

	public File getUpload() {
		return file;
	}

	public void setUpload(File file) {
		this.file = file;
	}

	public String getUploadContentType() {
		return contentType;
	}

	public void setUploadContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUploadFileName() {
		return fileName;
	}

	public void setUploadFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

	public String getPersonImage() {
		return personImage;
	}

	public void setPersonImage(String personImage) {
		this.personImage = personImage;
	}

	private void copy(File src, File dest) {
		try {
			InputStream in = null;
			OutputStream out = null;

			try {
				in = new BufferedInputStream(new FileInputStream(src),
						BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dest),
						BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private String getExtension(String fileName) {
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos);
	}
	
	private String uploadImage(File file) throws Exception{
		try {
			InputStream in = null;
			
			try {
				boolean imageValid = false;
				
				try {
					imageValid = (ImageIO.read(file) != null);
				} catch (Exception e) {
					log.info("Image IO Error, upload raw bytes!");
					String imageUrl = null;
					if(file != null) {
						imageUrl = MovieImageFTP.upload(FileUtils.readFileToByteArray(file));
						log.info("Image Path: " + imageUrl);
					}
					if (imageUrl == null) {
						throw new RuntimeException("上传图像文件到ftp失败！");
					}
					return imageUrl;
				}
				
				if(imageValid) {
					in = new FileInputStream(file);
					long length = file.length();
					
					if(length > Integer.MAX_VALUE) {
						return null;
					}
					
					byte[] bytes = new byte[(int) length];
					
					int offSet = 0;
					int numRead = 0;
					
					while(offSet < bytes.length && (numRead = in.read(bytes, offSet, bytes.length - offSet)) > 0) {
						offSet += numRead;
					}
					
					if(offSet < bytes.length) {
						throw new IOException("Could not complete read file");
					}
					
					Image img = ImageIO.read(new ByteArrayInputStream(bytes));
					byte[] newImage = null;
					if(PERSON_IMAGE_VALUE.equals(getPersonImage())) {
						newImage = ImageUtil.scaleImage(img, 128, 128, "");
					} else {
						newImage = ImageUtil.scaleImage(img, 128, 96, "");
					}
					String imageYoukuUrl = MovieImageFTP.upload(newImage);
					if (imageYoukuUrl == null) {
						throw new RuntimeException("上传图像文件到ftp失败！");
					}
					return imageYoukuUrl;
				} else {
					return null;
				} 
				
			}  finally {
				if(in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String execute() {
		String url = ImageUtil.getDisplayUrl("NA");
		try {
			
			if (getUpload() != null) {
				System.out.println("Uploadimge a image");
				log.debug("File upload Action execute");
				String fileName = new Date().getTime()
						+ getExtension(getUploadFileName());
				log.debug("upload file name is: " + fileName);
				File uploadDir = new File(ServletActionContext
						.getServletContext().getRealPath("/uploads"));
				if (!uploadDir.exists()) {
					uploadDir.mkdir();
				}
				log.debug("FileUploadAction save dir is: "
						+ uploadDir.getPath());
				File file = new File(uploadDir.getPath() + "/" + fileName);
				String contextPath = ServletActionContext.getServletContext()
						.getContextPath();
				log.debug("FileUploadAction contextPath is: " + contextPath);
				if ("/".equals(contextPath)) {
					setFilePath("/uploads/" + fileName);
				} else {
					setFilePath(contextPath + "/uploads/" + fileName);
				}
				copy(getUpload(), file);
				String imageftpurl = uploadImage(file);
				url = ImageUtil.getDisplayUrl(imageftpurl);
				//url = getFilePath();
			} else {
				String imageUrl = getImageUrl();
				if(ImageUtil.isYoukuImage(imageUrl)) {
					System.out.println("Image is youku image");
					url = imageUrl;
				} else {
					System.out.println("fetch image from other site");
					String imageftpurl = ImageUtil.storeImage(imageUrl, getPersonImage());
					url = ImageUtil.getDisplayUrl(imageftpurl);
				}
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.print("{\"filePath\":\"" + url + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
