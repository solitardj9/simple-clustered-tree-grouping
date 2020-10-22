package com.example.demo.application.baseData.group.service.data;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayeredGroup implements Serializable {

	private static final long serialVersionUID = -2187634062298171904L;

	private String groupName;
	
	private String parentGroupName;
	
	private String rootGroupName;
	
	private List<LayeredGroup> childGroups;
}