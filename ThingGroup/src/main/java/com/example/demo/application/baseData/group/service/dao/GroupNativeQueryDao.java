package com.example.demo.application.baseData.group.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Repository("groupNativeQueryDao")
public class GroupNativeQueryDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Transactional
	public Integer createGroupMapTable() {
		//
		String sql = "CREATE TABLE IF NOT EXISTS group_map("
				   + "group_name		varchar(128) DEFAULT NULL, "
				   + "group_info		LONGTEXT DEFAULT NULL, "
				   + "PRIMARY KEY PKEY (group_name));";	
		Integer result = entityManager.createNativeQuery(sql).executeUpdate();
	    
		return result;
	}
	
	@Transactional
	public Integer createGroupTreeTable() {
		//
		String sql = "CREATE TABLE IF NOT EXISTS group_tree("
				   + "root_name			varchar(128) DEFAULT NULL, "
				   + "group_tree		LONGTEXT DEFAULT NULL, "
				   + "PRIMARY KEY PKEY (root_name));";	
		Integer result = entityManager.createNativeQuery(sql).executeUpdate();
	    
		return result;
	}
}
