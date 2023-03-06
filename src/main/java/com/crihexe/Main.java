package com.crihexe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;

public class Main {
	
	public Main(String mainClassName, File jsonFile) {
		StringBuilder json = new StringBuilder();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
			String line;
			while((line = reader.readLine()) != null)
				json.append(line);
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		init(mainClassName, json.toString(), jsonFile.getName().replaceFirst("[.][^.]+$", ""));
	}
	
	public void init(String mainClassName, String json, String projectName) {
		JSONObject obj = new JSONObject(json);
		
		ClassBuilder builder = new ClassBuilder(mainClassName);
		
		builder.build(obj, mainClassName, new File(".\\output\\" + projectName));
	}

	public static void main(String[] args) {
		if(args.length < 2) {
			System.out.println("Invalid params!\nUsage: mainClassName jsonFilePath");
			return;
		}
		
		File jsonFile = new File(args[1]);
		if(new File(args[1]).exists()) new Main(args[0], jsonFile);
		else System.out.println("File not found!\nUsage: mainClassName jsonFilePath");
	}

}
