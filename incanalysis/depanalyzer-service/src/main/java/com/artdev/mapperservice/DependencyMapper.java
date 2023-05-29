package com.artdev.mapperservice;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import po.Dependency;

public interface DependencyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Dependency record);

    Dependency selectByPrimaryKey(Integer id);

    Dependency selectByNameAndFileId(@Param("name") String name,@Param("fileId") Integer fileId);

    List<Dependency> selectAll();

    List<Dependency> selectByFileId(Integer fileId);

    int updateByPrimaryKey(Dependency record);
}