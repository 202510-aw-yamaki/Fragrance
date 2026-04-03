package com.fregrance.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.VisitType;

@Mapper
public interface VisitTypeMapper {

    VisitType findByCode(@Param("code") String code);
    List<VisitType> findAllActive();
    VisitType findById(@Param("id") Long id);
    VisitType findByName(@Param("name") String name);
    int insert(VisitType visitType);
    int logicalDelete(@Param("id") Long id);
}