package com.example.demo.application.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.application.baseData.group.service.GroupManager;

@Service("GroupTest")
public class GroupTest {
	//
	@Autowired
	@Qualifier("GroupManagerByTreeNode")
	GroupManager groupManager;
	
	private Map<Integer, List<String>> layeredGroupMap = new ConcurrentHashMap<>();
	
	public void doInitTest() {
		//
		System.out.println("// Test 0 ----------------------------------------------------------------------------------------------------");
		System.out.println(groupManager.listAllGroups());
		System.out.println(groupManager.backupGroupTrees());
	}
	
	public void doTest() {
		//
		System.out.println("// Test 0 ----------------------------------------------------------------------------------------------------");
		System.out.println(groupManager.listAllGroups());
		
		Integer depth = 4;
		Integer width = 3;
		
		List<String> groupNames;
		
		System.out.println("create groups");

		System.out.println("lv0 created");
		
		String rootGroupName = "root";
		groupNames = new ArrayList<>();
		groupNames.add(rootGroupName);
		groupManager.addGroup(rootGroupName, null);
		
		layeredGroupMap.put(0, groupNames);
		
		for (int d = 1 ; d < depth ; d++) {
			//
			List<String> parentGroupNames = layeredGroupMap.get(d-1);
			groupNames = new ArrayList<>();
			
			int count = 0;
			for (String iter : parentGroupNames) {
				//
				for (int w = 0 ; w < width ; w++) {
					//String groupName = String.valueOf(d) + "_" + UUID.randomUUID().toString().substring(0, 5);
					String groupName = String.valueOf(d) + "_name_" + String.valueOf(count);
					Boolean ret = groupManager.addGroup(groupName, iter);
					if (ret.equals(false)) {
						break;
					}
					else {
						groupNames.add(groupName);
					}
					count = count + 1;
				}
			}
			
			if (!groupNames.isEmpty()) {
				layeredGroupMap.put(d, groupNames);
			}
			
			System.out.println("lv" + d+ " created");
		}
		
		System.out.println("display groups");
		
		for (Entry<Integer, List<String>> entry : layeredGroupMap.entrySet()) {
			System.out.println("layer = " + entry.getKey() + " / size = " + entry.getValue().size());
		}
		
		String searchName;
		
		System.out.println("// Test 1 ----------------------------------------------------------------------------------------------------");
		searchName = "1_name_1";
		System.out.println(groupManager.describeGroup(searchName));
		
		System.out.println("// Test 2 ----------------------------------------------------------------------------------------------------");
		System.out.println(groupManager.listAllGroups());
		
		System.out.println("// Test 3 ----------------------------------------------------------------------------------------------------");
		searchName = "1_name_2";
		System.out.println(groupManager.listChildGroups(searchName));
		
		System.out.println("// Test 4 ----------------------------------------------------------------------------------------------------");
		searchName = "1_name_2";
		System.out.println(groupManager.listChainedChildGroups(searchName));
		
		System.out.println("// Test 5 ----------------------------------------------------------------------------------------------------");
		searchName = "1_name_2";
		System.out.println(groupManager.deleteGroup(searchName));
		
		System.out.println("// Test 6 ----------------------------------------------------------------------------------------------------");
		searchName = "3_name_7";
		System.out.println(groupManager.describeGroup(searchName));
		System.out.println(groupManager.deleteGroup(searchName));
		System.out.println(groupManager.describeGroup(searchName));
		
		System.out.println("// Test 7 ----------------------------------------------------------------------------------------------------");
		searchName = "3_name_6";
		System.out.println(groupManager.listParentGroups(searchName));
		
		System.out.println("// Test 8 ----------------------------------------------------------------------------------------------------");		
		String backupGroupTrees = groupManager.backupGroupTrees();
		System.out.println("backupGroupTrees = " + backupGroupTrees);
		
		groupManager.clearGroupsInCluster();
		
		groupManager.restoreGroupTrees(backupGroupTrees);
		
		System.out.println("listAllGroups = " + groupManager.listAllGroups());
	}
}