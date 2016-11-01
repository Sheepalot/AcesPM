package com.aces.application.models;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "workflow_element")
public class WorkflowElement {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public int id;

	public String title;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	public WorkflowElement parent;
	
	@OneToMany
	@JoinColumn(name = "parent_id")
	public Collection<WorkflowElement> children;
	
	/**
	 * Convert the workflow into a node to be json'd up and sent back down to the client
	 * @param currentNode
	 * @return
	 */
	public Node buildNodeView(){
		Node currentNode = new Node(title, id);
		children.forEach(child -> currentNode.addNode(child.buildNodeView()));
		return currentNode;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
