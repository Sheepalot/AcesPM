package com.aces.application.models;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "workflow_element")
public class WorkflowElement {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public int id;

	@NotNull
	public String title;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	public WorkflowElement parent;
	
	@OneToMany
	@JoinColumn(name = "parent_id")
	public Collection<WorkflowElement> children;
	
	@OneToMany
	@JoinTable(name = "response_link",
    joinColumns = @JoinColumn(name = "elem_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "resp_id", referencedColumnName = "id"))
	public Collection<ResponseSet> responseSets;
	
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
	
	public Collection<ResponseSet> getResponseSets(){
		return responseSets;
	}
	
	public void setResponseSets(Collection<ResponseSet> responseSets){
		this.responseSets = responseSets;
	}
	
	public void setParent(WorkflowElement parent){
		this.parent = parent;
	}
}
