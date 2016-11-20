package com.aces.application.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "response_set")
public class ResponseSet {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	public int id;

	@NotNull
	@Column(name="resp_type")
	public String type;
	
	public String options;
}
