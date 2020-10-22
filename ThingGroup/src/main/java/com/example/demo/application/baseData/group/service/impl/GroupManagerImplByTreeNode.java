package com.example.demo.application.baseData.group.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.application.baseData.group.model.Group;
import com.example.demo.application.baseData.group.service.GroupManager;
import com.example.demo.application.baseData.group.service.dao.GroupMapDao;
import com.example.demo.application.baseData.group.service.dao.GroupNativeQueryDao;
import com.example.demo.application.baseData.group.service.dao.GroupTreeDao;
import com.example.demo.application.baseData.group.service.dao.dto.GroupMapDto;
import com.example.demo.application.baseData.group.service.dao.dto.GroupTreeDto;
import com.example.demo.application.baseData.group.service.data.LayeredGroup;
import com.example.demo.application.baseData.group.service.data.LayeredGroups;
import com.example.demo.application.systemInterface.imdgInterface.model.exception.ExceptionHazelcastDistributedObjectNameConflict;
import com.example.demo.application.systemInterface.imdgInterface.model.exception.ExceptionHazelcastIMapNotFound;
import com.example.demo.application.systemInterface.imdgInterface.model.exception.ExceptionHazelcastServerAlreadyClosed;
import com.example.demo.application.systemInterface.imdgInterface.service.InMemoryServerManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

@Service("GroupManagerByTreeNode")
public class GroupManagerImplByTreeNode implements GroupManager {
	//
	@Autowired
	GroupMapDao groupMapDao;
	
	@Autowired
	GroupTreeDao groupTreeDao;
	
	@Autowired
	GroupNativeQueryDao groupNativeQueryDao;
	
	@Autowired
	InMemoryServerManager inMemoryServerManager;
	
	private ObjectMapper om = new ObjectMapper();
	
	@PostConstruct
	public void init() {
		//
		createTable();
		
		createMap();
		
		intialize();
	}
	
	private void createTable() {
		//
		groupNativeQueryDao.createGroupMapTable();
		groupNativeQueryDao.createGroupTreeTable();
	}
	
	private void createMap() {
		//
		try {
			inMemoryServerManager.createMap("groupMap");
			inMemoryServerManager.createMap("groupTree");
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastDistributedObjectNameConflict e) {
			e.printStackTrace();
		}
	}
	
	private void intialize() {
		//
		System.out.println("start to intialize.");

		try {
			if (inMemoryServerManager.getMap("groupTree").isEmpty()) {
				//
				System.out.println("start to restore groupTree from DB to MEM.");
				
				List<GroupTreeDto> groupTreeDtos = getGroupTreeDtos();

				for (GroupTreeDto iter : groupTreeDtos) {
					//
					try {
						LayeredGroup layeredGroup = om.readValue(iter.getGroupTree(), LayeredGroup.class);
						restoreGroupToMemCluster(layeredGroup);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println("stop to restore groupTree from DB to MEM.");
			}
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
		
		try {
			if (inMemoryServerManager.getMap("groupMap").isEmpty()) {
				//
				System.out.println("start to restore groupMap from DB to MEM.");
				
				List<GroupMapDto> groupMapDtos = getGroupMapDtos();
			
				for (GroupMapDto iter : groupMapDtos) {
					//
					try {
						Group group = om.readValue(iter.getGroupInfo(), Group.class);
						addGroupMapToMemCluster(group);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println("stop to restore groupMap from DB to MEM.");
			}
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
		
		System.out.println("stop to intialize.");
	}
	
	@Override
	public Boolean addGroup(String groupName, String parentGroupName) {
		//
		return addGroupToClusters(groupName, parentGroupName, true);
	}
	
	private Boolean addGroupToClusters(String groupName, String parentGroupName, Boolean isSyncToDB) {
		//
		if (groupName == null || groupName.isEmpty())
			return false;
		
		if (parentGroupName == null || parentGroupName.isEmpty()) {
			return createRootGroupToClusters(groupName, isSyncToDB);
		}
		else {
			return addChildGroupToClusters(groupName, parentGroupName, isSyncToDB);
		}
	}
	
	private Boolean createRootGroupToClusters(String groupName, Boolean isSyncToDB) {
		//
		if (isExistGroupName(groupName)) {
			return false;
		}
		else {
			Group group = new Group(groupName, null, groupName);		// root and group is same.
			Map<String, TreeNode<String>> groupTree = createGroupTree(groupName);
			
			addGroupMapToClusters(group, isSyncToDB);
			addGroupTreeToClusters(groupName, groupTree, isSyncToDB);
			
			return true;
		}
	}
	
	private Map<String, TreeNode<String>> createGroupTree(String rootGroupName) {
		//
		Map<String, TreeNode<String>> groupTree = new HashMap<>();
		
		TreeNode<String> groupNode = new ArrayMultiTreeNode<String>(rootGroupName);
		groupTree.put(rootGroupName, groupNode);
		
		return groupTree;
	}
	
	// A group can have at most one direct parent.
	private Boolean addChildGroupToClusters(String groupName, String parentGroupName, Boolean isSyncToDB) {
		//
		if (!isExistGroupName(parentGroupName))
			return false;
		
		String rootGroupName = getRootGroupNameFromMemCluster(parentGroupName);
		
		try {
			lockGroupTree(rootGroupName);
			lockGroupMap(parentGroupName);
				
			Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(rootGroupName);
			if (groupTree.containsKey(groupName)) {
				//
				TreeNode<String> groupNode = groupTree.get(groupName);
				TreeNode<String> newParentGroup = groupTree.get(parentGroupName);
				TreeNode<String> prevParentGroupNode = groupNode.parent();
				
				if (prevParentGroupNode == null) {
					//
				}
				else {
					//
					prevParentGroupNode.remove(groupNode);
				}
				newParentGroup.add(groupNode);
			}
			else {
				//
				TreeNode<String> groupNode = new ArrayMultiTreeNode<>(groupName);
				TreeNode<String> newParentGroup = groupTree.get(parentGroupName);
				newParentGroup.add(groupNode);
				groupTree.put(groupName, groupNode);
				
				//System.out.println(groupTree.get(parentGroupName));
			}
			
			//------------------------------------------------
			Group group = new Group(groupName, parentGroupName, rootGroupName);
			
			addGroupMapToClusters(group, isSyncToDB);
			addGroupTreeToClusters(rootGroupName, groupTree, isSyncToDB);
			//------------------------------------------------
			
			unlockGroupMap(parentGroupName);
			unlockGroupTree(rootGroupName);
					
			return true;
		} catch (Exception e) {
			unlockGroupMap(parentGroupName);
			unlockGroupTree(rootGroupName);
					
			return false;
		}
	}
	
	
	
	@Override
	public Group describeGroup(String groupName) {
		//
		return getGroupFromMemCluster(groupName);
	}
	
	private Group getGroupFromMemCluster(String groupName) {
		//
		try {
			return (Group)inMemoryServerManager.getMap("groupMap").get(groupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Set<String> listAllGroupNames() {
		//
		return getGroupMapFromMemCluster().keySet();
	}
	
	public Set<Group> listAllGroups() {
		//
		return new HashSet<>(getGroupMapFromMemCluster().values());
	}
	
	private Map<String, Group> getGroupMapFromMemCluster() {
		//
		Map<String, Group> retMap = new HashMap<>();
		try {
			Map<Object, Object> resultMap = inMemoryServerManager.getMap("groupMap");
			for (Entry<Object, Object> iter : resultMap.entrySet()) {
				retMap.put((String)iter.getKey(), (Group)iter.getValue());
			}
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
		return retMap;
	}
		
	@Override
	public Set<String> listParentGroupNames(String groupName) {
		//
		return getChainedParentGroupNames(groupName);
	}
	
	@Override
	public Set<Group> listParentGroups(String groupName) {
		//
		return getChainedParentGroups(groupName);
	}
		
	private Set<String> getChainedParentGroupNames(String groupName) {
		//
		Set<String> parentGroupSet = new HashSet<>();
		
		if (groupName == null || groupName.isEmpty())
			return null;
		
		if (!isExistGroupName(groupName))
			return null;
		
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		TreeNode<String> group = groupTree.get(groupName);
		
		while(!group.isRoot()) {
			group = group.parent();
			parentGroupSet.add(group.data());
		}
		
		return parentGroupSet;
	}
	
	private Set<Group> getChainedParentGroups(String groupName) {
		//
		Set<Group> parentGroupSet = new HashSet<>();
		
		if (groupName == null || groupName.isEmpty())
			return null;
		
		if (!isExistGroupName(groupName))
			return null;
		
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		TreeNode<String> group = groupTree.get(groupName);
		
		while(!group.isRoot()) {
			group = group.parent();
			parentGroupSet.add(getGroupFromMemCluster(group.data()));
		}
		
		return parentGroupSet;
	}
		
	@Override
	public Set<String> listChildGroupNames(String groupName) {
		//
		return getChildGroupNames(groupName);
	}
	
	@Override
	public Set<Group> listChildGroups(String groupName) {
		//
		return getChildGroups(groupName);
	}
		
	private Set<String> getChildGroupNames(String groupName) {
		//
		Set<String> childGroups = new HashSet<>();
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		
		TreeNode<String> groupNode = groupTree.get(groupName);
		Collection<? extends TreeNode<String>> subtrees = groupNode.subtrees();
		
		for (TreeNode<String> childIter : subtrees) {
			childGroups.add(childIter.data());
		}
		
		return childGroups;
	}
		
	private Set<Group> getChildGroups(String groupName) {
		//
		Set<Group> childGroups = new HashSet<>();
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		
		TreeNode<String> groupNode = groupTree.get(groupName);
		Collection<? extends TreeNode<String>> subtrees = groupNode.subtrees();
		
		for (TreeNode<String> childIter : subtrees) {
			childGroups.add(getGroupFromMemCluster(childIter.data()));
		}
		
		return childGroups;
	}	
	
	@Override
	public Set<String> listChainedChildGroupNames(String groupName) {
		//
		return getChainedChildGroupNames(groupName);
	}
	
	@Override
	public Set<Group> listChainedChildGroups(String groupName) {
		//
		return getChainedChildGroups(groupName);
	}

	private Set<String> getChainedChildGroupNames(String groupName) {
		//
		Set<String> childGroups = new HashSet<>();
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		
		TreeNode<String> groupNode = groupTree.get(groupName);
		Collection<? extends TreeNode<String>> subtrees = groupNode.postOrdered();
		
		for (TreeNode<String> childIter : subtrees) {
			childGroups.add(childIter.data());
		}
		childGroups.remove(groupName);
		
		return childGroups;
	}
	
	private Set<Group> getChainedChildGroups(String groupName) {
		//
		Set<Group> childGroups = new HashSet<>();
		Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(getRootGroupNameFromMemCluster(groupName));
		
		TreeNode<String> groupNode = groupTree.get(groupName);
		Collection<? extends TreeNode<String>> subtrees = groupNode.postOrdered();
		
		for (TreeNode<String> childIter : subtrees) {
			String tmpGroupName = childIter.data();
			if (!tmpGroupName.equals(groupName)) {
				childGroups.add(getGroupFromMemCluster(tmpGroupName));
			}
		}
		
		return childGroups;
	}	
	
	private Boolean isExistGroupName(String groupName) {
		//
		try {
			return inMemoryServerManager.getMap("groupMap").containsKey(groupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void addGroupMapToClusters(Group group, Boolean isSyncToDB) {
		//
		addGroupMapToMemCluster(group);
		if (isSyncToDB) {
			addGroupMapToDBCluster(group);
		}
	}
	
	private void addGroupMapToMemCluster(Group group) {
		//
		try {
			inMemoryServerManager.getMap("groupMap").put(group.getGroupName(), group);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void addGroupMapToDBCluster(Group group) {
		//
		try {
			GroupMapDto groupMapDto = null;
			try {
				groupMapDto = new GroupMapDto(group.getGroupName(), om.writeValueAsString(group));
				saveGroupMapDtos(groupMapDto);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addGroupTreeToClusters(String rootGroupName, Map<String, TreeNode<String>> groupTree, Boolean isSyncToDB) {
		//
		addGroupTreeToMemCluster(rootGroupName, groupTree);
		if (isSyncToDB) {
			addGroupTreeToDBCluster(rootGroupName, groupTree);
		}
	}
	
	private void addGroupTreeToMemCluster(String rootGroupName, Map<String, TreeNode<String>> groupTree) {
		//
		try {
			inMemoryServerManager.getMap("groupTree").put(rootGroupName, groupTree);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void addGroupTreeToDBCluster(String rootGroupName, Map<String, TreeNode<String>> groupTree) {
		//
		try {
			TreeNode<String> rootNode = groupTree.get(rootGroupName);
			LayeredGroup layeredGroup = convertGroupTreeToLayeredGroup(rootNode);
			
			GroupTreeDto groupTreeDto;
			try {
				groupTreeDto = new GroupTreeDto(rootGroupName, om.writeValueAsString(layeredGroup));
				saveGroupTreeDtos(groupTreeDto);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private LayeredGroup convertGroupTreeToLayeredGroup(TreeNode<String> node) {
		//
		if (node.isLeaf()) {
			LayeredGroup ret = new LayeredGroup(node.data(), (node.parent() == null) ? null : node.parent().data(), node.root().data(), null);
			//System.out.println("leaf ret = " + ret.toString());
			return ret;
		}
		else {
			List<LayeredGroup> childGroups = new ArrayList<>();
			
			Collection<? extends TreeNode<String>> subtrees = node.subtrees();
			for (TreeNode<String> childIter : subtrees) {
				LayeredGroup childGroup = convertGroupTreeToLayeredGroup(childIter);
				childGroups.add(childGroup);
			}
			
			LayeredGroup ret = new LayeredGroup(node.data(), (node.isRoot()) ? null : node.parent().data(), node.root().data(), childGroups);
			//System.out.println("ret = " + ret.toString());
			return ret;
		}
	}
	
	@Override
	public Boolean deleteGroup(String groupName) {
		//
		if  (groupName == null || groupName.isEmpty()) {
			return false;
		}
		
		return deleteGroupFromClusters(groupName);
	}
	
	// can delete group which has no child.
	private Boolean deleteGroupFromClusters(String groupName) {
		//
		if (!isExistGroupName(groupName))
			return false;
		
		Boolean ret = false;
		
		String rootGroupName = getRootGroupNameFromMemCluster(groupName);
		
		if (groupName.equals(rootGroupName)) {
			//
			try {
				lockGroupTree(rootGroupName);
				lockGroupMap(rootGroupName);
				
				Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(rootGroupName);
				TreeNode<String> group = groupTree.get(groupName);
				
				if (group.isLeaf()) {
					ret = true;
				}
				else {
					ret =  false;
				}
				
				unlockGroupMap(rootGroupName);
				unlockGroupTree(rootGroupName);
				
				if (ret) {
					deleteGroupTreeFromClusters(rootGroupName, true);
					deleteGroupFromClusters(rootGroupName, true);
				}
				
			} catch (Exception e) {
				unlockGroupMap(rootGroupName);
				unlockGroupTree(rootGroupName);
				
				return false;
			}
		}
		else {
			//
			String parentGroupName = getParentGroupNameFromMemCluster(groupName);
			
			try {
				lockGroupTree(rootGroupName);
				lockGroupMap(parentGroupName);
				
				Map<String, TreeNode<String>> groupTree = getGroupTreeFromMemCluster(rootGroupName);
				TreeNode<String> group = groupTree.get(groupName);
				
				if (group.isLeaf()) {
					//
					TreeNode<String> parentGroup = group.parent();
					parentGroup.remove(group);
					
					groupTree.remove(groupName);
					
					addGroupTreeToClusters(rootGroupName, groupTree, true);
					deleteGroupFromClusters(groupName, true);
					
					ret = true;
				}
				else {
					ret =  false;
				}
				
				unlockGroupMap(parentGroupName);
				unlockGroupTree(rootGroupName);
			} catch (Exception e) {
				unlockGroupMap(parentGroupName);
				unlockGroupTree(rootGroupName);
				
				return false;
			}
		}
		
		return ret;
	}
	
	private void deleteGroupTreeFromClusters(String rootGroupName, Boolean isSyncToDB) {
		//
		deleteGroupTreeFromMemCluster(rootGroupName);
		if (isSyncToDB)
			deleteGroupTreeFromDBCluster(rootGroupName);
	}
	
	
	private void deleteGroupTreeFromMemCluster(String rootGroupName) {
		//
		try {
			inMemoryServerManager.getMap("groupTree").delete(rootGroupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void deleteGroupTreeFromDBCluster(String rootGroupName) {
		//
		try {
			deleteGroupTreeDto(rootGroupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void deleteGroupFromClusters(String groupName, Boolean isSyncToDB) {
		//
		deleteGroupFromMemCluster(groupName);
		if (isSyncToDB)
			deleteGroupFromDBCluster(groupName);
	}
	
	private void deleteGroupFromMemCluster(String groupName) {
		//
		try {
			inMemoryServerManager.getMap("groupMap").delete(groupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void deleteGroupFromDBCluster(String groupName) {
		//
		try {
			deleteGroupMapDto(groupName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Map<String, TreeNode<String>> getGroupTree(String rootGroupName) {
		//
		return getGroupTreeFromMemCluster(rootGroupName);
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, TreeNode<String>> getGroupTreeFromMemCluster(String rootGroupName) {
		//
		Map<String, TreeNode<String>> retGroupTree = new HashMap<>();
		try {
			retGroupTree = (Map<String, TreeNode<String>>)inMemoryServerManager.getMap("groupTree").get(rootGroupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
		return retGroupTree;
	}
	
	private String getRootGroupNameFromMemCluster(String groupName) {
		//
		try {
			return ((Group)inMemoryServerManager.getMap("groupMap").get(groupName)).getRootGroupName();
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getParentGroupNameFromMemCluster(String groupName) {
		//
		try {
			return ((Group)inMemoryServerManager.getMap("groupMap").get(groupName)).getParentGroupName();
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String backupGroupTrees() {
		//
		String strGroupTrees = null;
		Map<String, LayeredGroup> layeredGroupsMap = new HashMap<>();
		try {
			Map<Object, Object> groupTrees = inMemoryServerManager.getMap("groupTree");
			
			for (Entry<Object, Object> iter : groupTrees.entrySet()) {
				String rootGroupName = (String)iter.getKey();
				Map<String, TreeNode<String>> groupTree = (Map<String, TreeNode<String>>)iter.getValue();
				TreeNode<String> rootNode = groupTree.get(rootGroupName);
			
				LayeredGroup layeredGroup = convertGroupTreeToLayeredGroup(rootNode);
				layeredGroupsMap.put(rootGroupName, layeredGroup);
			}
			
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}

		try {
			LayeredGroups layeredGroups = new LayeredGroups(layeredGroupsMap);
			strGroupTrees = om.writeValueAsString(layeredGroups);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return strGroupTrees;
	}
	
	@Override
	public void restoreGroupTrees(String strGroupTrees) {
		//
		LayeredGroups layeredGroups = null;
		try {
			layeredGroups = om.readValue(strGroupTrees, LayeredGroups.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
		
		for (Entry<String, LayeredGroup> iter : layeredGroups.getLayeredGroups().entrySet()) {
			//
			restoreGroupToClusters(iter.getValue());
		}
	}
	
	private void restoreGroupToClusters(LayeredGroup layeredGroup) {
		//
		addGroupToClusters(layeredGroup.getGroupName(), layeredGroup.getParentGroupName(), true);
		
		if (layeredGroup.getChildGroups() != null) {
			for (LayeredGroup iter : layeredGroup.getChildGroups()) {
				restoreGroupToClusters(iter);
			}
		}
	}
	
	private void restoreGroupToMemCluster(LayeredGroup layeredGroup) {
		//
		addGroupToClusters(layeredGroup.getGroupName(), layeredGroup.getParentGroupName(), false);
		
		if (layeredGroup.getChildGroups() != null) {
			for (LayeredGroup iter : layeredGroup.getChildGroups()) {
				restoreGroupToMemCluster(iter);
			}
		}
	}
	
	private void lockGroupMap(String groupName) {
		//
		try {
			inMemoryServerManager.getMap("groupMap").lock(groupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void unlockGroupMap(String groupName) {
		//
		try {
			inMemoryServerManager.getMap("groupMap").unlock(groupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void lockGroupTree(String rootGroupName) {
		//
		try {
			inMemoryServerManager.getMap("groupTree").lock(rootGroupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void unlockGroupTree(String rootGroupName) {
		//
		try {
			inMemoryServerManager.getMap("groupTree").unlock(rootGroupName);
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void clearGroupsInCluster() {
		//
		clearGroupsInMemClusters();
		clearGroupsInDBClusters();
	}
	
	private void clearGroupsInMemClusters() {
		//
		clearGroupMapInMemClusters();
		clearGroupTreeInMemClusters();
	}
	
	private void clearGroupMapInMemClusters() {
		//
		try {
			inMemoryServerManager.getMap("groupMap").clear();
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void clearGroupTreeInMemClusters() {
		//
		try {
			inMemoryServerManager.getMap("groupTree").clear();
		} catch (ExceptionHazelcastServerAlreadyClosed | ExceptionHazelcastIMapNotFound e) {
			e.printStackTrace();
		}
	}
	
	private void clearGroupsInDBClusters() {
		//
		clearGroupMapInDBClusters();
		clearGroupTreeInDBClusters();
	}
	
	private void clearGroupMapInDBClusters() {
		//
		try {
			clearGroupMapDtos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearGroupTreeInDBClusters() {
		//
		try {
			clearGroupTreeDtos();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<GroupMapDto> getGroupMapDtos() {
		//
		return groupMapDao.findAll();
	}
	
	private List<GroupTreeDto> getGroupTreeDtos() {
		//
		return groupTreeDao.findAll();
	}
	
	private void saveGroupMapDtos(GroupMapDto groupMapDto) {
		//
		groupMapDao.save(groupMapDto);
	}
	
	private void saveGroupTreeDtos(GroupTreeDto groupTreeDto) {
		//
		groupTreeDao.save(groupTreeDto);
	}
	
	private void deleteGroupMapDto(String groupName) {
		//
		groupMapDao.deleteById(groupName);
	}
	
	private void deleteGroupTreeDto(String rootGroupName) {
		//
		groupTreeDao.deleteById(rootGroupName);
	}
	
	private void clearGroupMapDtos() {
		//
		groupMapDao.deleteAll();
	}
	
	private void clearGroupTreeDtos() {
		//
		groupTreeDao.deleteAll();
	}
}