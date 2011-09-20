package com.youku.soku.pos_analysis.entity;

public class CategoryClickVO{
	int categoryId;
	String category_name;
	int click_nums;
	int rate;
	
	public CategoryClickVO(int categoryId, String categoryName, int clickNums) {
		super();
		this.categoryId = categoryId;
		category_name = categoryName;
		click_nums = clickNums;
	}
	
	public CategoryClickVO() {
		super();
	}
	
	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String categoryName) {
		category_name = categoryName;
	}
	public int getClick_nums() {
		return click_nums;
	}
	public void setClick_nums(int clickNums) {
		click_nums = clickNums;
	}
	@Override
	public String toString() {
		return "CategoryClickVO [categoryId=" + categoryId + ", category_name="
				+ category_name + ", click_nums=" + click_nums + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + categoryId;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryClickVO other = (CategoryClickVO) obj;
		if (categoryId != other.categoryId)
			return false;
		return true;
	}
	
	
}
