package com.example.demo.application.baseData.group.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.application.baseData.group.service.dao.dto.GroupTreeDto;

public interface GroupTreeDao extends JpaRepository<GroupTreeDto, String> {

}