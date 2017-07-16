package com.aces.application.utilities;

import java.util.LinkedHashMap;

public class Question {
	public String title;
	public LinkedHashMap<String, Integer> anwsers = new LinkedHashMap<String, Integer>();
	
	public Question(String title) {
		this.title = title;
	}
}
