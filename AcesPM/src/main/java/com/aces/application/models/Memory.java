package com.aces.application.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.util.SerializationUtils;

import com.aces.application.utilities.RunningAuditManager;

@Entity
public class Memory {
	
	@Id
	public int id;
	
	@Lob
    @Column(name = "memory", length = Integer.MAX_VALUE - 1)
	private byte[] memory;
	
    public RunningAuditManager getManager() {
        return (RunningAuditManager) SerializationUtils.deserialize(memory);
    }

    public void setManager(RunningAuditManager manager) {
        this.memory = SerializationUtils.serialize((Serializable) manager);
    }
}
