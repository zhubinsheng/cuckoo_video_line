package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.UserRatioModel;

public class JsonGetUserRatio extends JsonRequestBase {

    private UserRatioModel data;

    public UserRatioModel getData() {
        return data;
    }

    public void setData(UserRatioModel data) {
        this.data = data;
    }
}
