package com.eliaovideo.videoline.modle;

import com.eliaovideo.videoline.json.JsonRequestBase;

public class JsonDoGetVideoCallInfoModel extends JsonRequestBase{
    private String ext;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
