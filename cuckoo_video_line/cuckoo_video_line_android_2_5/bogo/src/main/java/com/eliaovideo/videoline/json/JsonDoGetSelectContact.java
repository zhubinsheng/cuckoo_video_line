package com.eliaovideo.videoline.json;

import com.eliaovideo.videoline.modle.ConfigModel;

public class JsonDoGetSelectContact extends JsonRequestBase{
    private String number;
    private String price;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPrice() {
        return price;
    }
    public String getPriceFormat() {
        return price + ConfigModel.getInitData().getCurrency_name();
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
