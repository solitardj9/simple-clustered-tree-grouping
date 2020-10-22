package com.example.demo.application.baseData.group.service;

import java.util.Map;
import java.util.Set;

import com.example.demo.application.baseData.group.model.Group;
import com.scalified.tree.TreeNode;

public interface GroupManager {
	//
	public Boolean addGroup(String groupName, String parentGroupName);
	
	public Group describeGroup(String groupName);
	
	public Set<String> listAllGroupNames();
	
	public Set<Group> listAllGroups();
	
	public Set<String> listParentGroupNames(String groupName);
	
	public Set<Group> listParentGroups(String groupName);
	
	public Set<String> listChildGroupNames(String groupName);
	
	public Set<Group> listChildGroups(String groupName);
	
	public Set<String> listChainedChildGroupNames(String groupName);
	
	public Set<Group> listChainedChildGroups(String groupName);
	
	public Boolean deleteGroup(String groupName);
	
	public Map<String, TreeNode<String>> getGroupTree(String rootGroupName);
	
	public String backupGroupTrees();
	
	public void restoreGroupTrees(String strGroupTrees);
	
	public void clearGroupsInCluster();
}