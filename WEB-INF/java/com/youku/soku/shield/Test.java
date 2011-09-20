package com.youku.soku.shield;

import java.util.concurrent.TimeUnit;

import com.youku.soku.shield.matcher.ContainHitMatcher;
import com.youku.soku.shield.matcher.HitMatcher;

public class Test {
	public static void main(String[] args) {
		
		ShieldWordsInfo wordsInfo = new ShieldWordsInfo();
		DataLoader loader = new DataLoader(wordsInfo);
		loader.init();
		System.out.println(Filter.Source.soku);
		ContainHitMatcher matcher = new ContainHitMatcher(wordsInfo);
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < 1000; i++) {
			matcher.match("1郑柔美2");
			matcher.match("苏维埃故事");
			matcher.match("头文字");
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Cost time " + (end - start));
	}
}
