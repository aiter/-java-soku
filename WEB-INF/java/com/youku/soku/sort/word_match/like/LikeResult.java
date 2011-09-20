package com.youku.soku.sort.word_match.like;

import java.util.Arrays;

import com.youku.aword.Word;
import com.youku.soku.zhidaqu.v2.DictManager;

public class LikeResult {
	private Word program ;
	
	private Word person ;
	
	private Modifier modifier ;
	
	private Word date ;
	
	private Word version ;
	
	private Word episode ;
	
	private int number ;
	
	private String unknowWord;
	
	private boolean isValid = true;
	
	public String toString(){
		return "program:" + (program!=null?program.getValue():null) 
				+ (program!=null?Arrays.toString(DictManager.getElements(program)):"null") 
				+ "\t version:" + version 
				+ "\t episode:" + episode 
				+ "\t number:" + number 
				+ "\t person:" + person 
				+ "\t date:" + date 
				+ "\t modifier:" + modifier;
	}
	public Word getProgram() {
		return program;
	}

	public void setProgram(Word program) {
		this.program = program;
	}

	public Word getPerson() {
		return person;
	}

	public void setPerson(Word person) {
		this.person = person;
	}

	

	public Word getDate() {
		return date;
	}

	public void setDate(Word date) {
		this.date = date;
	}

	
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getUnknowWord() {
		return unknowWord;
	}
	public void setUnknowWord(String unknowWord) {
		this.unknowWord = unknowWord;
	}
	public Word getVersion() {
		return version;
	}
	public void setVersion(Word version) {
		this.version = version;
	}
	public Word getEpisode() {
		return episode;
	}
	public void setEpisode(Word episode) {
		this.episode = episode;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public Modifier getModifier() {
		return modifier;
	}
	public void setModifier(Modifier modifier) {
		this.modifier = modifier;
	}
	
	
}