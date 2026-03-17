package com.fregrance.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.VisitType;

@Mapper
public interface VisitTypeMapper {

    VisitType findByCode(@Param("code") String code);
}
