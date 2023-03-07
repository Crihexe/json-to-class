package com.crihexe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HashGenerator {
	
	public static HashMap<Integer, String> hashList = new HashMap<Integer, String>();
	
	public static int hash(Set<String> set) {
		int hash = 17;
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext())
			hash = hash * 31 * iterator.next().hashCode();
		System.out.println("hash " + hash);
		return hash;
	}
	
	public static String get(int hash) {
		return hashList.get(hash);
	}
	
	public static boolean add(int hash, String className) {
		if(hashList.containsKey(hash)) return false;
		hashList.put(hash, className);
		return true;
	}
	
}
