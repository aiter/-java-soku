package com.youku.soku.manage.common;


/**
 * <p>
 * Manifest constants for the MailReader application.
 * </p>
 */
public final class Constants {

	// --- Tokens ----

	/**
	 * <p>
	 * The token representing a "cancel" request.
	 * </p>
	 */
	public static final String CANCEL = "cancel";

	/**
	 * <p>
	 * The token representing a "create" task.
	 * </p>
	 */
	public static final String CREATE = "Create";

	/**
	 * <p>
	 * The application scope attribute under which our user database is stored.
	 * </p>
	 */
	public static final String DATABASE_KEY = "database";

	/**
	 * <p>
	 * The token representing a "edit" task.
	 * </p>
	 */
	public static final String DELETE = "Delete";

	/**
	 * <p>
	 * The token representing a "edit" task.
	 * </p>
	 */
	public static final String EDIT = "Edit";
	
	/**
	 * <p>
	 * The token representing a "list" view
	 * </p>
	 */
	public static final String LIST = "list";
	
	
	/**
	 * <p>
	 * The token representing a "detail" view
	 * </p>
	 */
	public static final String DETAIL = "detail";
	
	/**
	 * <p>
	 * The token representing a "permission denied" view
	 * </p>
	 */
	public static final String DENIED = "denied";

	/**
	 * <p>
	 * The session scope attribute under which the User object for the currently
	 * logged in user is stored.
	 * </p>
	 */
	public static final String USER_KEY = "user";
	
	/**
	 * <p>
	 * The session scope attribute under which all the permission is stored
	 * </p>
	 */
	public static final String PERMISSION_MAP_KEY = "permission_map";
	
	/**
	 * <p>
	 * The session scope attribute under which the user permission is stored
	 * </p>
	 */
	public static final String USER_PERMISSION_KEY = "user_permission";

	
	/**
	 * <p>
	 * Name of field to associate with authentification errors.
	 * <p>
	 */
	public static final String PASSWORD_MISMATCH_FIELD = "password";

	// ---- Log Messages ----

	/**
	 * <p>
	 * Message to log if saving a user fails.
	 * </p>
	 */
	public static final String LOG_DATABASE_SAVE_ERROR = " Unexpected error when saving User: ";

	/**
	 * the token representing manage user permission
	 */
	public static final String MANAGE_USER_PERMISSION = "manage_user";
	
	/**
	 * the token representing manage item permission
	 */
	public static final String MANAGE_ITEM_PERMISSION = "manage_item";
	
	/**
	 * the token representing manage Video permission
	 */
	public static final String MANAGE_VIDEO_PERMISSION = "manage_video";
	
	/**
	 * the token representing manage Hotword permission
	 */
	public static final String MANAGE_HOTWORD_PERMISSION = "manage_hotword";
	
	/**
	 * the token representing manage Intervening keyword
	 */
	public static final String MANAGE_INTERVEN_KEYWORD = "manage_interven_keyword";
	
	/**
	 * the token representing manage Intervening Keyword Video
	 */
	public static final String MANAGE_INTERVEN_KEYWORD_VIDEO = "manage_interven_keyword_video";
	
	/**
	 * the token representing manage category of library database
	 */
	public static final String MANAGE_LIB_CATEGORY = "manage_lib_category";
	
	/**
	 * the token representing manage names of library database
	 */
	public static final String MANAGE_LIB_NAMES = "manage_lib_names";
	
	/**
	 * the token representing manage teleplay of library database
	 */
	public static final String MANAGE_LIB_TELEPLAY = "manage_lib_teleplay";
	
	/**
	 * the token representing manage movie of library database
	 */
	public static final String MANAGE_LIB_MOVIE = "manage_lib_movie";
	
	/**
	 * the token representing manage anime of library database
	 */
	public static final String MANAGE_LIB_ANIME = "manage_lib_anime";
	
	/**
	 * the token representing manage variety of library database
	 */
	public static final String MANAGE_LIB_VARIETY = "manage_lib_variety";
	
	/**
	 * the token representing manage person of library database
	 */
	public static final String MANAGE_LIB_PERSON = "manage_lib_person";
	
	/**
	 * the token representing delete video of library database
	 */
	public static final String DELETE_VIDEO = "delete_video";
	
	/**
	 * the token representing manage feedback of library database
	 */
	public static final String MANAGE_FEEDBACK = "manage_feedback";
	
	/**
	 * the token representing manage feedback of library database
	 */
	public static final String AUDIT_VIDOE = "audit_video";
	
	
	public static final String MANAGE_CORRECTION_PERMISSION = "manage_correction";
	
	public static final String MANAGE_NEW_TOP = "manage_new_top";
	
	public static final String MANAGE_SOKUFEEDBACK = "manage_new_soku_feedback";
	
	/**
	 * the token representing display on the index page
	 */
	public static final String INDEX_DISPLAY = "1";
	
	/**
	 * the token representing the default site id
	 */
	public static final int DEFAULT_SITE_ID = 100;
	
	/**
	 * the token representing the default site id
	 */
	public static final int INTEGRATED_SITE_ID = 100;
	
	/**
	 * the token representing the default site 
	 */
	public static final String INTEGRATED_SITE = "综合";
	
	/**
	 * the token representing the youku site id
	 */
	public static final int YOUKU_SITE_ID = 14;
	
	/**
	 * the token representing the category of teleplay
	 */
	public static final int CATEGORY_TELEPLAY = 1;
	
	/**
	 * the token representing the category of movie
	 */
	public static final int CATEGORY_MOVIE = 2;
	
	/**
	 * the token representing the category of variety
	 */
	public static final int CATEGORY_VARIETY = 3;
	
	/**
	 * the token representing the category of music
	 */
	public static final int CATEGORY_MUSIC = 4;
	
	/**
	 * the token representing the category of anime
	 */
	public static final int CATEGORY_ANIME = 5;
	
	/**
	 * the token representing the falg of blocked
	 */
	public static final int BLOCKED = 1;
	
	/**
	 * the token representing the falg of unblocked
	 */
	public static final int UNBLOCKED = 0;

	public static final int VIDEO_SERIAL = 0;

	public static final int TELEPLAY_CATE_ID = 1;

	public static final String TELEPLAY_STR = "电视剧";

	public static final int MOVIE_CATE_ID = 2;

	public static final String MOVIE_STR = "电影";

	public static final int VARIETY_CATE_ID = 3;

	public static final String VARIETY_STR = "综艺";

	public static final int ANIME_CATE_ID = 5;

	public static final String ANIME_STR = "动漫";
	
	public static final String PERSON_STR = "人物";
	
	public static final int PERSON_CATE_ID = 6;
	
	public static final String COOKIE_REMEMBER_ME = "soku.cookie.rememberme";
	
	public static final String COOKIE_SPLITER = "==";
	
	
	public static final String UPDATE_VERSIONNAME = "versionname";
	
	public static final String UPDATE_ORDER = "order";
	
	/**
	 * the token representing manage top word
	 */
	public static final String MANAGE_TOP_WORD = "manage_top_word";
	
	
	/**
	 * the token representing block top word
	 */
	public static final String BLOCK_TOP_WORD = "block_top_word";
	
	/**
	 * the token representing manage protocol site
	 */
	public static final String MANAGE_PROTOCOL_SITE = "manage_protocol_site";
	
	/**
	 * the token representing manage site weight
	 */
	public static final String MANAGE_SITE_WEIGHT = "manage_site_weight";
	
	/**
	 * the token representing manage major term of library database
	 */
	public static final String MANAGE_MAJOR_TERM = "manage_major_term";
	
	/**
	 * the token representing manage index pic of library database
	 */
	public static final String MANAGE_INDEX_PIC = "manage_index_pic";
	
	/**
	 * the toke representing manage shielding system
	 */
	public static final String MANAGE_SHIELDING_SYSTEM = "manage_shielding_system";
	
	/**
	 * the toke representing manage shielding system keyword
	 */
	public static final String MANAGE_SHIELDING_SYSTEM_KEYWORD = "manage_shielding_system_keyword";
	
	
	/**
	 * the toke representing manage shielding system site
	 */
	public static final String MANAGE_SHIELDING_SYSTEM_SITE = "manage_shielding_system_site";
	
	/**
	 * the toke representing manage shielding system mail
	 */
	public static final String MANAGE_SHIELDING_SYSTEM_MAIL = "manage_shielding_system_mail";
	
	public static final String[] SHIELDING_AUTHS = {MANAGE_SHIELDING_SYSTEM, MANAGE_SHIELDING_SYSTEM_KEYWORD, MANAGE_SHIELDING_SYSTEM_SITE, MANAGE_SHIELDING_SYSTEM_MAIL};
	
	public static final String SHIELD_USER_KEY = "shield";
	
	/**
	 * the toke representing manage knowledge column
	 */
	public static final String MANAGE_KNOWLEDGE_COLUMN = "manage_knowledge_column";
	
	public static final String MANAGE_FORWARD_WORD = "manage_foward_word";	
	
	public static final String NO_IMG_STR = "0900641F46495A535700000000000000000000-0000-0000-0000-00004914A959";
	
	public static final String USER_OPERATION_CHANGE_SERIES = "change series";
	
	public static final String USER_OPERATION_DELETE_SERIES = "delete series";
	
	public static final String USER_OPERATION_DELETE = "delete";
	
	public static final String USER_OPERATION_ADD = "add";
	
	public static final String USER_OPERATION_UPDATE = "update";
	
	public static int CONCERN_FLAG = 1;  
	public static int NOT_CONCERN_FLAG = 0;
	
	public static int EPISODE_LOG_NOTHANDLED = 0;	
	public static int EPISODE_LOG_HANDLEDSUCCESS = 1;	
	public static int EPISODE_LOG_HANDLEDFAIL = 2;
	
	public static int EPISODE_LOG_EDITOR_ADDED = 3;
	
	public static int VIEW_ORDER_NOT_YOUKU = 2;
	
	public static String ADD_URL_SOURCE = "";
	
	public static String AUTO_SEARCH_FLAG = "100";
	

	public static final int KU6_SITE_ID = 10;
	
	public static String[] TOP_SITES = {"土豆网", "56网", "新浪网", "搜狐", "激动网", "CNTV", "优酷网", "乐视网", "奇艺网", "天线视频"};
}
