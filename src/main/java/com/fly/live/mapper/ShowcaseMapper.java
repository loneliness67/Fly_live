package com.fly.live.mapper;

import com.fly.live.model.Showcase;

public interface ShowcaseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Showcase record);

    int insertSelective(Showcase record);

    Showcase selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Showcase record);

    int updateByPrimaryKey(Showcase record);
}