package com.example.demo.application.systemInterface.imdgInterface.model;

import com.example.demo.application.systemInterface.imdgInterface.service.InMemoryEventListener;

public class InMemoryInstane {
	//
	private String name;               // Data 객체 이름
	
	private Integer backupCount;		// sync backup
	
	private Boolean readBackupData;
	
	private String lockName;
	
	private InMemoryEventListener eventListener;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBackupCount() {
		return backupCount;
	}

	public void setBackupCount(Integer backupCount) {
		this.backupCount = backupCount;
	}

	public Boolean getReadBackupData() {
		return readBackupData;
	}

	public void setReadBackupData(Boolean readBackupData) {
		this.readBackupData = readBackupData;
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public InMemoryEventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(InMemoryEventListener eventListener) {
		this.eventListener = eventListener;
	}

	@Override
	public String toString() {
		return "InMemoryInstane [name=" + name + ", backupCount=" + backupCount + ", readBackupData=" + readBackupData
				+ ", lockName=" + lockName + ", eventListener=" + eventListener + "]";
	}
}