package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.GuildUserModel;

import java.util.List;

public class JsonGetGuildUserList extends JsonRequestBase{
    private List<GuildUserModel> list;

    public List<GuildUserModel> getList() {
        return list;
    }

    public void setList(List<GuildUserModel> list) {
        this.list = list;
    }
}
