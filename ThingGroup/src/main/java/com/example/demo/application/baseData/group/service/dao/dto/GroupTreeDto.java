package com.example.demo.application.baseData.group.service.dao.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="group_tree")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTreeDto {
	//
	@Id
	@Column(name="root_name")
	private String rootName;
	
	@Column(name="group_tree")
	private String groupTree;
}