package com.fly.live.mapper;

import com.fly.live.model.Live;

public interface LiveMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Live record);

    int insertSelective(Live record);

    Live selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Live record);

    int updateByPrimaryKey(Live record);

    int checkById(int id);
}