/**
 * 
 */
package com.youku.search.index.db;

import org.apache.lucene.document.Document;

/**
 * @author 1verge
 *
 */
public class AssembleDoc {
	
	public AssembleDoc(Document document){
		this.doc = document;
	}
	public AssembleDoc(Document document,float boost){
		this.doc = document;
		this.boost = boost;
	}
	
	private Document doc;
	private float boost;
	public Document getDoc() {
		return doc;
	}
	public void setDoc(Document doc) {
		this.doc = doc;
	}
	public float getBoost() {
		return boost;
	}
	public void setBoost(float boost) {
		this.boost = boost;
	}
	public String get(String key){
		if (doc !=null)
			return doc.get(key);
		return null;
	}
	
}
