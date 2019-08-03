package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.GuildInfoModel;

public class JsonGetGuildInfo extends JsonRequestBase {
    private GuildInfoModel data;

    public GuildInfoModel getData() {
        return data;
    }

    public void setData(GuildInfoModel data) {
        this.data = data;
    }
}
