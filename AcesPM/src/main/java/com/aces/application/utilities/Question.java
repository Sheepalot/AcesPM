package com.aces.application.utilities;

import java.util.HashMap;
import java.util.TreeMap;

public class Question {
	public String title;
	public TreeMap<String, Integer> anwsers = new TreeMap<String, Integer>();
	
	public Question(String title) {
		this.title = title;
	}
}
