package com.youku.search.console.vo;

public class TeleplaySpideVO {
	/** The value for the id field */
    private int id;
                                          
    /** The value for the status field */
    private int status = 0;
      
    /** The value for the name field */
    private String name;
      
    /** The value for the deeltime field */
    private String createtime;
   

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	
}
