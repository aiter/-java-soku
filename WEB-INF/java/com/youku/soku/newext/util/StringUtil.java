package com.youku.soku.newext.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
//import com.youku.soku.util.Constant;

import org.apache.commons.lang.StringUtils;
public class StringUtil {
	
	public static Set<String> getDistinctName(String... string) {
		Set<String> returnSet=new HashSet<String>();
		for(String eleInput: string){
			if(eleInput!=null && StringUtils.trimToEmpty(eleInput).length()>0){
				returnSet.add(StringUtils.trimToEmpty(eleInput).toLowerCase());
			}
			
		}
		
		return returnSet;
	}

    public static int parseInt(String s, int defaultValue) {

        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
        }

        return defaultValue;
    }

    public static int parseInt(String s, int defaultValue, int min) {

        int result = parseInt(s, defaultValue);
        return Math.max(min, result);
    }

    public static int parseInt(String s, int defaultValue, int min, int max) {

        int result = parseInt(s, defaultValue, min);
        return Math.min(max, result);
    }

    public static String urlEncode(String s, String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlEncode(String s, String enc, String failValue) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (Exception e) {
            return failValue;
        }
    }

    public static String urlDecode(String s, String enc) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String s, String enc, String failValue) {
        try {
            return URLDecoder.decode(s, enc);
        } catch (Exception e) {
            return failValue;
        }
    }

    public static String toAscii(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            if (chars[i] >= 32 & chars[i] <= 126) {
                builder.append(chars[i]);
            } else {
                final String tmp = "000" + Integer.toHexString(chars[i]);
                builder.append("\\u");
                builder.append(tmp.substring(tmp.length() - 4));
            }
        }

        return builder.toString();
    }

    public static String filterNull(String s) {
        return s == null ? "" : s;
    }

    /**
     * 把换行符替换成空格，把tab替换成空格，把连续的空格替换成一个空格，把其余的控制字符删除掉，把首尾的空格删除掉
     */
    public static String filterNonetext(String s) {
        if (s == null) {
            return s;
        }

        Charset charset = Charset.forName("UTF-8");
        byte[] bytes = s.getBytes(charset);

        boolean previousSpace = true;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b > ' ') {
                outputStream.write(b);
                previousSpace = false;

            } else if (b == ' ') {
                if (previousSpace) {
                    continue;
                } else {
                    outputStream.write(b);
                    previousSpace = true;
                }

            } else if (b == '\t' || b == '\n' || b == '\r') {
                if (previousSpace) {
                    continue;
                } else {
                    outputStream.write(' ');
                    previousSpace = true;
                }

            } else {
                continue;
            }
        }

        byte[] result = outputStream.toByteArray();
        int lastIndex = result.length - 1;
        while (lastIndex >= 0 && result[lastIndex] == ' ') {
            lastIndex--;
        }

        if (lastIndex >= 0) {
            return new String(result, 0, lastIndex + 1);
        }

        return "";
    }

    /**
     * 字符串编码转换
     */
    public static String conv(String s, String inEncoding, String outEncoding) {
        if (s == null) {
            return null;
        }

        try {
            return new String(s.getBytes(inEncoding), outEncoding);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

   
    
    /**
     * 全角转半角
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
          if (c[i] == '\u3000') {
            c[i] = ' ';
          } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
            c[i] = (char) (c[i] - 65248);
          }
        }
        String returnString = new String(c);
        return returnString;
    }
    
    public static boolean isNotNull(String s) {
		if (null != s && s.trim().length() > 0)
			return true;
		return false;
	}
    
    public static File getFileFromBytes(byte[] b, String outputFile){
        BufferedOutputStream stream = null;
        File file = null;
        try{
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e){
            e.printStackTrace();
        } finally{
            if (stream != null){
                try{
                    stream.close();
                } catch(IOException e1){
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }
    
    public static File getFileFromStr(String src, String outputFile){
    	File file = new File(outputFile);
    	if(file.exists())
    		file.delete();
    	BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "utf-8"));
			bw.write(src);
			bw.flush();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (bw != null) {
				try {
					bw.close();
				} catch (Exception ie) {
				}
			}
		}
        return new File(outputFile);
    }
    
    public static String[] stringToArr(String strs,String split){
		if(isNotNull(strs)){
			return strs.split(split);
		}
		return null;
	}
    
    private static Random randGen = new Random();
    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
            + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    
    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }
    public static String arrToString(String[] arr,String split){
		if(null!=arr&&arr.length>0){
			StringBuilder strs = new StringBuilder();
			for(int i=0;i<arr.length;i++){
				if(isNotNull(arr[i])){
					strs.append(arr[i]);
					if(i!=arr.length-1)
						strs.append("|");
				}
			}
			return strs.toString();
		}
		return null;
	}
    
    
    public static List<String> parseArr2List(String[] arr) {
		List<String> alias = new ArrayList<String>();
		if (null != arr) {
			for (String a : arr) {
				if (!StringUtils.isBlank(a)){
						alias.add(a.trim());
				}
			}
		}
		if (alias.size() > 0)
			return alias;
		else
			return null;
	}

	/**
	 * @param site
	 * @return
	 */
	public static Set<Integer> parseSite(String site) {
		if(site==null || site.trim().length()==0){
			return null;
		}
		Set<Integer> tmpSet = new HashSet<Integer>();
		String [] sites = site.split(",");
		if(sites!=null && sites.length>0){
			for (String siteId : sites) {
				int tmp = parseInt(siteId, 0);
				if(tmp!=0){
					tmpSet.add(tmp);
				}
			}
		}
		return tmpSet;
	}
	
	 public static void main(String[] args) throws Exception {
//	    	String[] arr = new String[]{"a","ddf","feg"};
//	    	System.out.println(arrToString(arr, "|"));
		 
		 String site = "";
		 
		 Set<Integer> set = parseSite(site);
		 if(set!=null){
		 System.out.println(Arrays.toString(set.toArray()));
		 }
	    }
}
