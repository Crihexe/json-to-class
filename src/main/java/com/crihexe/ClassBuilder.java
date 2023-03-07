package com.crihexe;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClassBuilder {
	
	public String mainClassName;
	
	public ClassBuilder(String mainClassName) {
		this.mainClassName = mainClassName;
	}
	
	public void build(JSONObject obj, String className, File dir) {
		if(!dir.exists()) dir.mkdirs();
		generateObject(dir, className, obj);
		System.out.println("Done!");
	}
	
	private Pair<Boolean, Integer> generateObject(File dir, String className, JSONObject obj) {
		HashMap<String, Object> attributes = getAttributes(obj);
		
		int hash = HashGenerator.hash(attributes.keySet());
		
		System.out.println("Hash: " + hash + " <- " + className);
		
		Pair<Boolean, Integer> pair = new Pair<Boolean, Integer>(HashGenerator.add(hash, className), hash);
		if(!pair.first) return pair;
		
		File file = new File(dir + File.separator + className + ".java");
		if(file.exists()) file.delete();
		try {
			System.out.println("Generating: " + className + ".java");
			file.createNewFile();
			
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			
			pw.println("public class " + className + " {");
			for(Entry<String, Object> entry : attributes.entrySet()) {
				String recClassName = entry.getKey();
				if(entry.getValue() instanceof JSONArray) {
					JSONArray array = (JSONArray)entry.getValue();
					//recClassName = generateArray(dir, entry.getKey(), (JSONArray)entry.getValue());
					
					if(array.length() <= 0) {
						recClassName = "Object";
					} else {
						recClassName = (""+recClassName.charAt(0)).toUpperCase() +  recClassName.substring(1, recClassName.length());
						//generateObject(dir, recClassName, array.getJSONObject(0));
						Pair<Boolean, Integer> result = generateObject(dir, recClassName, array.getJSONObject(0));
						if(!result.first)
							recClassName = HashGenerator.get(result.second);
					}
					
					pw.println("\tpublic List<" + recClassName + "> " + entry.getKey() + ";");
				} else if(entry.getValue() instanceof JSONObject) {
					recClassName = (""+recClassName.charAt(0)).toUpperCase() +  recClassName.substring(1, recClassName.length());
					Pair<Boolean, Integer> result = generateObject(dir, recClassName, (JSONObject)entry.getValue());
					if(!result.first)
						pw.println("\tpublic " + HashGenerator.get(result.second) + " " + entry.getKey() + ";");
					else
						pw.println("\tpublic " + recClassName + " " + entry.getKey() + ";");
						
				} else {
					recClassName = entry.getValue().getClass().getSimpleName();
					if(recClassName.equals("Null"))
						pw.println("//\tpublic " + entry.getValue().getClass().getSimpleName() + " " + entry.getKey() + ";  // TODO Unknow type");
					else
						pw.println("\tpublic " + entry.getValue().getClass().getSimpleName() + " " + entry.getKey() + ";");
				}
			}
			pw.println("}");
			
			pw.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return new Pair<Boolean, Integer>(true, 0);
	}
	
	private String generateArray(File dir, String name, JSONArray array) {
		if(array.length() == 0) return "List<Object>";
		
		String output = "List<";
		
		boolean isObject = false;
		boolean generatedArray = false;
		String listType = null;
		boolean validList = true;
		int generatedObjects = 0;
		
		for(int i = 0; i < array.length(); i++) {
			Object o = array.get(i);
			if(o instanceof JSONObject) {
				JSONObject jsonObj = (JSONObject) o;
				Pair<Boolean, Integer> result = generateObject(dir, name + i, jsonObj);	
				// first: 
				//		true   se è stato creato un nuovo oggetto
				//		false  se esiste già
				generatedObjects += result.first ? 1 : 0;
				if(!isObject) {
					if(generatedObjects >= 2) {
						isObject = true;
					}
					if(generatedArray) {
						isObject = true;
					}
					
				}
			} else if(o instanceof JSONArray) {
				JSONArray jsonArr = (JSONArray) o;
				String arrayOutput = generateArray(dir, name + i, jsonArr);
				if(listType == null) listType = arrayOutput;
				if(!arrayOutput.equals(listType)) validList = false;
				generatedArray = true;
				if(!isObject) {
					if(generatedObjects > 0) {
						isObject = true;
					}
				}
				if(i == array.length()-1) {
					if(validList) output += listType; else output += "List<Object>";
				}
			} else {
				isObject = true;
			}
		}
		
		if(isObject) output += "Object";
		
		return output + ">";
	}
	
	private HashMap<String, Object> getAttributes(JSONObject obj) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for(String key : obj.keySet())
			attributes.put(key, obj.get(key));
		return attributes;
	}

}
 