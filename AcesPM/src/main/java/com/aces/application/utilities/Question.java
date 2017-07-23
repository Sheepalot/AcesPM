package com.aces.application.utilities;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Question implements Serializable{
	public String title;
	public ArrayList<String> anwsers = new ArrayList<String>();
	public LinkedHashMap<String, Integer> results = new LinkedHashMap<String, Integer>();
	
	public Question(String title) {
		this.title = title;
	}
	
	/**
	 * Nasty servers-side HTML building but I can't be bothered to do this in the template
	 * @return
	 */
	public String buildSummary(){
		StringBuffer summary = new StringBuffer();
		double sum = results.values().stream().mapToInt(i -> i).sum();
		DecimalFormat df = new DecimalFormat("#.##");
		results.entrySet().forEach(e ->{
			summary.append("<p><strong>"+e.getKey()+"</strong>: "+df.format((e.getValue()/sum)*100)+"%</p>");
		});
		return summary.toString();
	}
}
