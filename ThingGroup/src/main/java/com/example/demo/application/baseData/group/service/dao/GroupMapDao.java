package com.example.demo.application.baseData.group.service.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.application.baseData.group.service.dao.dto.GroupMapDto;

public interface GroupMapDao extends JpaRepository<GroupMapDto, String> {

}