package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.UserModel;

import java.util.ArrayList;

public class JsonDoRequestGetSearchList extends JsonRequestBase{
    private ArrayList<UserModel> list = new ArrayList<>();

    public ArrayList<UserModel> getList() {
        return list;
    }

    public void setList(ArrayList<UserModel> list) {
        this.list = list;
    }
}
