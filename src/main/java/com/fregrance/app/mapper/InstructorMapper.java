package com.fregrance.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.Instructor;

@Mapper
public interface InstructorMapper {
    List<Instructor> findAllActive();
    Instructor findById(@Param("id") Long id);
    Instructor findByName(@Param("name") String name);
    int insert(Instructor instructor);
    int logicalDelete(@Param("id") Long id);
}