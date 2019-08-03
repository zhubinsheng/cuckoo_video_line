package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.GuildModel;

import java.util.List;

public class JsonGetGuildList extends JsonRequestBase{
    private List<GuildModel> list;

    public List<GuildModel> getList() {
        return list;
    }

    public void setList(List<GuildModel> list) {
        this.list = list;
    }
}
