package com.youku.highlight;

public class AnalyzerHighLighterResult {
	
	private String originalText ; //还原后的字符串
	private String lightText ;		//高亮后的字符串
	
	public String getOriginalText() {
		return originalText;
	}
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}
	public String getLightText() {
		return lightText;
	}
	public void setLightText(String lightText) {
		this.lightText = lightText;
	}
	
	public String toString(){
		return "originalText=" + originalText + "\n"
				+ "lightText=" + lightText;
	}
	
}
