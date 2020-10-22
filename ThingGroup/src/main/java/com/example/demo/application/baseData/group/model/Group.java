package com.example.demo.application.baseData.group.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group implements Serializable {

	private static final long serialVersionUID = -3114041073436636856L;
	
	private String groupName;
	
	private String parentGroupName;
	
	private String rootGroupName;
}