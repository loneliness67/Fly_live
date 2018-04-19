package com.fly.live.service.Impl;

import com.fly.live.mapper.LiveMapper;
import com.fly.live.model.Live;
import com.fly.live.model.User;
import com.fly.live.service.IPlayService;
import com.fly.live.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayServiceImpl implements IPlayService {

    @Autowired
    private LiveMapper liveMapper;

    public Live getLive(Integer id) {
        return liveMapper.selectByPrimaryKey(id);
    }

    public boolean randomRtmp(Integer id) {
        Live live = new Live();

        live.setId(id);
        live.setCode(UUID.randomUUID().toString().replace("-",""));

        liveMapper.updateByPrimaryKeySelective(live);

        return true;
    }

    public boolean updateLive(Live live) {
        int resultCount = 0;
        resultCount=liveMapper.updateByPrimaryKeySelective(live);
        if (resultCount > 0) {
            return true;
        }
        return false;
    }
}
