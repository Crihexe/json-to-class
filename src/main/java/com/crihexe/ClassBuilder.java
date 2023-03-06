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
		generate(dir, className, getAttributes(obj));
		System.out.println("Done!");
	}
	
	private void generate(File dir, String className, HashMap<String, Object> attributes) {
		File file = new File(dir + File.separator + className + ".java");
		if(file.exists()) file.delete();
		try {
			System.out.println("Generating: " + className + ".java");
			file.createNewFile();
			PrintWriter pw = new PrintWriter(new FileWriter(file));
			
			pw.println("public class " + className + " {");
			for(Entry<String, Object> entry : attributes.entrySet()) {
				if(entry.getValue() instanceof JSONArray) {
					attributes = getAttributes(((JSONArray)entry.getValue()).getJSONObject(0));
					
					String recClassName = entry.getKey();
					recClassName = (""+recClassName.charAt(0)).toUpperCase() +  recClassName.substring(1, recClassName.length()-1);
					generate(dir, recClassName, attributes);
					pw.println("\tpublic " + recClassName + " " + entry.getKey() + "[];");
				} else pw.println("\tpublic " + entry.getValue().getClass().getSimpleName() + " " + entry.getKey() + ";");
			}
			pw.println("}");
			
			pw.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private HashMap<String, Object> getAttributes(JSONObject obj) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		for(String key : obj.keySet())
			attributes.put(key, obj.get(key));
		return attributes;
	}

}
 