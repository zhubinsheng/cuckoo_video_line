package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.DynamicCommonListModel;

import java.util.List;

public class JsonRequestDoGetDynamicCommonList extends JsonRequestBase {
    private List<DynamicCommonListModel> list;

    public List<DynamicCommonListModel> getList() {
        return list;
    }

    public void setList(List<DynamicCommonListModel> list) {
        this.list = list;
    }
}
