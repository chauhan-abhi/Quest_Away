package com.abhi.questaway.network;

import java.io.Serializable;

public class ResultModel implements Serializable {
    private String result;

    public ResultModel(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
