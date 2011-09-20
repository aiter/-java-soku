package com.youku.top;

public class DickResult {
	String keyword;
	boolean hasRight = false;
	String year;
	int series_id;
	int subject_id;

	public int getSubject_id() {
		return subject_id;
	}

	public void setSubject_id(int subjectId) {
		subject_id = subjectId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public boolean isHasRight() {
		return hasRight;
	}

	public void setHasRight(boolean hasRight) {
		this.hasRight = hasRight;
	}

	public DickResult(String keyword, boolean hasRight, String year,
			int seriesId, int subjectId) {
		super();
		this.keyword = keyword;
		this.hasRight = hasRight;
		this.year = year;
		series_id = seriesId;
		subject_id = subjectId;
	}

	public DickResult(String keyword, boolean hasRight, String year,
			int seriesId) {
		super();
		this.keyword = keyword;
		this.hasRight = hasRight;
		this.year = year;
		series_id = seriesId;
	}

	public int getSeries_id() {
		return series_id;
	}

	public void setSeries_id(int seriesId) {
		series_id = seriesId;
	}

	public DickResult(String keyword, boolean hasRight) {
		super();
		this.keyword = keyword;
		this.hasRight = hasRight;
		this.year = "";
	}

	public DickResult(String keyword, boolean hasRight, String year) {
		super();
		this.keyword = keyword;
		this.hasRight = hasRight;
		this.year = year;
	}

	public DickResult() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
		result = prime * result + series_id;
		result = prime * result + subject_id;
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
		final DickResult other = (DickResult) obj;
		if (keyword == null) {
			if (other.keyword != null)
				return false;
		} else if (!keyword.equals(other.keyword))
			return false;
		if (series_id != other.series_id)
			return false;
		if (subject_id != other.subject_id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DickResult [hasRight=" + hasRight + ", keyword=" + keyword
				+ ", year=" + year + "]";
	}

}
