package com.fly.live.service;

import com.fly.live.model.Live;

public interface IPlayService {
    public Live getLive(Integer id);
    boolean randomRtmp(Integer id);
    boolean updateLive(Live live);

}
