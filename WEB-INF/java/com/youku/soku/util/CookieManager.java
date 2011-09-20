/**
 * 
 */
package com.youku.soku.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youku.search.util.StringUtil;


public class CookieManager {
	
	public static final String COOKIEKEY = "uc";
	
	private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    
    private String cookies = null;
    private char[] chars = null;
    
    public CookieManager(HttpServletRequest req,HttpServletResponse res){
    	request = req;
    	response = res;
    	cookies = request.getHeader("Cookie");
    	 if (cookies != null) {
             chars = cookies.toCharArray();
    	 }
    }
    
    public String getUniqCookie()
    {
    	String cookie = getCookie(COOKIEKEY);
    	if (cookie == null)
    		cookie = createUniqCookie();
    	return cookie;
    }
    
    public String getCookie(String key)
    {
    	String cookie = null;
        if (chars != null) {
            char[] keys = key.toCharArray();
            int keystart = 0;
            int keyend = 0;
            int valuestart = 0;
            int valueend = 0;

            for (int i = 0; i < chars.length - keys.length; i++) {
                // 32=' '
                if (chars[i] == 32) {
                    keystart = i + 1;

                }
                // 32='='
                if (chars[i] == 61) {
                    keyend = i;

                    if (keyend - keystart == keys.length) {
                        for (int j = keystart; j < keyend; j++) {
                            if (chars[j] != keys[j - keystart]) {
                                break;
                            }
                            if (j == keyend - 1) {
                                // get cookie value
                                valuestart = i + 1;
                                for (int k = valuestart; k < chars.length; k++) {
                                    // 59=';'
                                    if (chars[k] == 59) {
                                        valueend = k;
                                        return new String(chars, valuestart,
                                                valueend - valuestart);
                                    }
                                    if (k == chars.length - 1) {
                                        valueend = k + 1;
                                        return new String(chars, valuestart,
                                                valueend - valuestart);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    continue;
                }

            }

        }
        return cookie;
    }
    
    public String createUniqCookie() {
        // 时间串+随机三个字符
        String cookie = System.currentTimeMillis() + StringUtil.randomString(3);
        Cookie c = new Cookie(COOKIEKEY, cookie);
        c.setDomain(".soku.com");
        c.setPath("/");
        c.setMaxAge(365 * 24 * 3600);
        response.addCookie(c);
        return cookie;
    }
    

}
