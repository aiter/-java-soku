package com.youku.soku.newext.info;

import java.io.Serializable;

public class TypeConstant implements Serializable {

	private static final long serialVersionUID = 1L;

	public static class NameType implements Serializable {
		private static final long serialVersionUID = 1L;

		public static final int TELEPLAY = 1;// 电视剧
		public static final int MOVIE = 2;// 电影
		public static final int VARIETY = 3;// 综艺
		public static final int MUSIC = 4;// 音乐
		public static final int ANIME = 5;// 动漫
		public static final int PERSON = 6;// 人物
	}

	public static class RoleType implements Serializable {
		private static final long serialVersionUID = 1L;

		public static final int XXXXXXX = -99;

		public static final int DIRECTOR = 1;
		public static final int PERFORMER = 2;
		public static final int SINGER = 3;
		public static final int WRITER = 4;
		public static final int COMPOSEMUSIC = 5;
		public static final int SCENARIST = 6;
		public static final int PRODUCER = 7;
		public static final int HOST = 8;
		public static final int DUBBING = 9;
		public static final int GUEST = 10;
	}
	
	public static class ProgrammeState implements Serializable {

		/**
		 * normal deleted blocked checking paycheck limited check
		 */
		private static final long serialVersionUID = 911147762062541833L;
		public static final String NORMAL="normal";
	}
}
