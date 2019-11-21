package com.example.itime3.data;

import java.io.Serializable;

/**
 * Created by 谭小二 on 2019/11/20.
 */

public class Schedule implements Serializable {


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCoverResourceId() {
        return coverResourceId;
    }

    public void setCoverResourceId(int coverResourceId) {
        this.coverResourceId = coverResourceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    private String title;
    private int coverResourceId;
    private String remark;

    public Schedule(String title, int coverResourceId, String remark) {
        this.title = title;
        this.coverResourceId = coverResourceId;
        this.remark = remark;
    }
}