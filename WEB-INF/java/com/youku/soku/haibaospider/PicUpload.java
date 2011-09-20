package com.youku.soku.haibaospider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.youku.search.util.DataFormat;
import com.youku.top.test.Info;

public class PicUpload {
	public static void write2Excel(OutputStream os, List<Info> list) {
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(os);
			WritableSheet sheet = null;
			WritableFont writefont = new WritableFont(WritableFont.ARIAL, 14,
					WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.GREEN);
			WritableCellFormat writecellfont = new WritableCellFormat(writefont);
			WritableFont writefontkeyword = new WritableFont(
					WritableFont.ARIAL, 12, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat writecellfontkeyword = new WritableCellFormat(
					writefontkeyword);
			int i = 0;
			sheet = workbook.createSheet("海报", i);
			sheet.addCell(new jxl.write.Label(0, 0, "ID", writecellfont));
			sheet.addCell(new jxl.write.Label(1, 0, "图片", writecellfont));
			int j = 1;
			int k = 0;
			for (Info rv : list) {

				System.out.println(++k + "," + rv.getContent_id() + " , "
						+ rv.getUrl());

				sheet.addCell(new jxl.write.Number(0, j, rv.getContent_id(),
						writecellfontkeyword));
				sheet.addHyperlink(new WritableHyperlink(1, j, new URL(rv
						.getUrl())));
				j++;
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Info> upload(int c, List<File> files) {
		List<Info> pics = new ArrayList<Info>();
		String name = null;
		byte[] bytes = null;
		String pic = null;
		int id = 0;
		Info info = null;
		int k = 0;
		for (File file : files) {
			name = file.getName();
			name = StringUtils.substringBefore(name, ".");
			if (StringUtils.isBlank(name)) {
				System.err.println("=====" + file.getPath());
				continue;
			}
			id = DataFormat.parseInt(name.trim());
			if (id < 1) {
				System.err.println("=====" + file.getPath());
				continue;
			}
			if (ids.contains(id))
				continue;
			try {
				bytes = FileUtils.readFileToByteArray(file);
				pic = UploadPic.uploadHaibao(bytes);
				if (StringUtils.isBlank(pic)) {
					System.err.println("=====" + file.getPath());
					continue;
				}
				info = new Info();
				info.setContent_id(id);
				info.setUrl("http://g" + (new Random().nextInt(4) + 1)
						+ ".ykimg.com/" + pic);
				System.out.println(++k + "," + id + " , " + pic + ",c:" + c);
				pics.add(info);
			} catch (Exception e) {
				System.err.println("=====" + file.getPath());
				continue;
			}
		}

		return pics;
	}

	private static void uploadpic(int i, List<File> teleplay, List<File> movie,
			String haibao) {
		List<Info> teleplays = new ArrayList<Info>();
		if (null != teleplay) {
			System.out.println(" size:" + teleplay.size());
			teleplays = upload(i, teleplay);
			System.out.println(" upload size:" + teleplays.size());
		}
		List<Info> movies = new ArrayList<Info>();
		if (null != movie) {
			System.out.println(" size:" + movie.size());
			movies = upload(i, movie);
			System.out.println(" upload size:" + movies.size());
		}

		try {
			if (teleplays.size() > 0)
				write2Excel(new FileOutputStream(haibao), teleplays);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Set<Integer> ids = null;

	public static Set<Integer> readFile() {
		Set<Integer> set = new HashSet<Integer>();
		try {
			List list = FileUtils.readLines(new File("D:\\xin\\t.txt"));
			for (int i = 0; i < list.size(); i++) {
				set.add(DataFormat.parseInt(list.get(i)));
			}
			System.out.print(list.size() + "," + set.size());
			return set;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return set;
	}

	public static void main(String[] args) {
		ids = readFile();

		String t = "D:\\xin\\pic";
		File[] ts = new File(t).listFiles();

		int size = ts.length;
		int thread_count = size / 100 + 1;

		List<File> filelist = new ArrayList<File>();
		for (int i = 0; i < size; i++) {
			filelist.add(ts[i]);
		}

		for (int i = 0; i < thread_count; i++) {
			int offset = (i + 1) * 100;
			if (offset > size)
				offset = size;
			run(i, filelist.subList(i * 100, offset), null, "D:\\xin\\haibao"
					+ i + ".xls");
		}

	}

	public static void run(final int c, final List<File> files,
			List<File> movies, final String path) {
		new Thread(new Runnable() {
			public void run() {
				uploadpic(c, files, null, path);
			}
		}).start();
	}

}
