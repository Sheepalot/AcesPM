package com.aces.application.models;

import java.util.ArrayList;

public class Node {
	public String text;
	public int nodeId;
	public ArrayList<Node> nodes;
	
	public Node(String title, int nodeId){
		this.text = title;
		this.nodeId = nodeId;
	}
	
	public void addNode(Node child){
		if(nodes==null){
			nodes = new ArrayList<Node>();
		}
		nodes.add(child);
	}
}
