package com.aces.application.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Question {
	public String title;
	public ArrayList<String> anwsers = new ArrayList<String>();
	public LinkedHashMap<String, Integer> results = new LinkedHashMap<String, Integer>();
	
	public Question(String title) {
		this.title = title;
	}
}
