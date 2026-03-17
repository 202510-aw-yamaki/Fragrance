package com.fregrance.app.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fregrance.app.model.QuestionnaireResult;

@Mapper
public interface QuestionnaireResultMapper {

    int insert(QuestionnaireResult questionnaireResult);

    QuestionnaireResult findByResultCode(@Param("resultCode") String resultCode);
}