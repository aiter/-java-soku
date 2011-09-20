package com.youku.soku.suggest.data.serialize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.youku.soku.suggest.data.LibraryData;
import com.youku.soku.suggest.data.loader.LibraryDataLoader;
import com.youku.soku.suggest.trie.TrieTree;
import com.youku.soku.suggest.trie.TrieTree.TrieNode;

public class ObjectSaverAndLoader {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private static final String DEFAULT_FILE_NAME = "library_data";
	
	private File file;
	
	private File dir;
	
	public ObjectSaverAndLoader(String dir, String fileName) {
		this.dir = new File(dir);
		this.file = new File(this.dir, fileName);
	}
	
	public ObjectSaverAndLoader() {
		this(System.getProperty("java.io.tmpdir"), DEFAULT_FILE_NAME);
	}
	
	public ObjectSaverAndLoader(String dir) {
		this(dir, DEFAULT_FILE_NAME);
	}
	
	public boolean save(LibraryData data) {
		log.info("Save Library Data into file: " + file.getAbsolutePath());
		ObjectOutputStream out = null;
		
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(data);
			
			log.info("Complete Save Library Data into file: " + file.getAbsoluteFile());
			log.info("Size of Library Data is: " + file.length());
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(out != null) {
				try{
					out.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean save(TrieTree data) {
		log.info("Save Library Data into file: " + file.getAbsolutePath());
		ObjectOutputStream out = null;
		
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(data);
			
			log.info("Complete Save Library Data into file: " + file.getAbsoluteFile());
			log.info("Size of Library Data is: " + file.length());
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(out != null) {
				try{
					out.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean save(TrieNode data) {
		log.info("Save Library Data into file: " + file.getAbsolutePath());
		ObjectOutputStream out = null;
		
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(data);
			
			log.info("Complete Save Library Data into file: " + file.getAbsoluteFile());
			log.info("Size of Library Data is: " + file.length());
			return true;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if(out != null) {
				try{
					out.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public LibraryData loader() {
		
		
		
		log.info("Begin to read Library Data file......");
		ObjectInputStream in = null;
		
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			LibraryData data = (LibraryData) in.readObject();
			log.info("Complete loading Library Data file......");
			return data;
		} catch(Exception e) {
			e.printStackTrace();			
			log.info("Library Data file not exist, create a new one");
			LibraryData data = new LibraryData();
			LibraryDataLoader loader = new LibraryDataLoader();
			loader.loadData(data);			
			
			log.info("Complete load library data, Save data to local disk ...");
			ObjectSaverAndLoader osl = new ObjectSaverAndLoader();
			osl.save(data);			
			return data;
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
