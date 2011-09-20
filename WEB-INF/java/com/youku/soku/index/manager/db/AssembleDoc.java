/**
 * 
 */
package com.youku.soku.index.manager.db;

import org.apache.lucene.document.Document;

/**
 * @author 1verge
 *
 */
public class AssembleDoc {
	
	
	
	private Document doc;
	private float boost;
	private int  id ;
	
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
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}
