package com.example.demo.application.baseData.group.service.dao.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="group_map")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMapDto {
	//
	@Id
	@Column(name="group_name")
	private String groupName;
	
	@Column(name="group_info")
	private String groupInfo;
}