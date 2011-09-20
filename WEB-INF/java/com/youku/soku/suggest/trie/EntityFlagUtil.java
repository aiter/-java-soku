package com.youku.soku.suggest.trie;

public class EntityFlagUtil {
	
	/**
	 * 设置TrieTree中的Entity的flag字段
	 * flag 类型  byte
	 * 初始值 0000 0000
	 * 右边第一位表示是否是直达区， xxxx xxx1(x表示0或1)表示是直达区 xxxx xxx0表示非直达区
	 * 右边第二位表示是否是人物,
	 * 右边第三位表示是否是电视剧剧名,
	 * 右边第四位表示是否是电视剧剧名  + 集数  
	 * 右边第五位表示是否是电影
	 * 
	 */
	
	public static byte DIRECTFLAG = 1; // 0000 0001
	
	public static byte PERSONFLAG = 2; // 0000 0010
	
	public static byte TELEPLAYFLAG = 4; // 0000 0100
	
	public static byte TELEPLAYORDERFLAG = 8; //0000 1000
	
	public static byte MOVIEODERFLAG = 16; //0001 0000
	
	public static boolean checkFlag(byte flag, byte base) {
		return (flag & base) == base;
	}
	
	public static byte setFlag(byte flag, byte base) {
		return (byte) (flag | base);
	}
	

	

	
	public static void main(String[] args) {
		byte b = 10;
		System.out.println(Integer.toBinaryString(b));
		
		System.out.println(Integer.toBinaryString(setFlag((byte)b, MOVIEODERFLAG)));
	}

}
