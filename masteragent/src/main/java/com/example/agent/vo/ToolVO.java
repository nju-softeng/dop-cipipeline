package com.example.agent.vo;

import lombok.Data;

@Data
public class ToolVO {
    public String getToolurl() {
        return toolurl;
    }

    public void setToolurl(String toolurl) {
        this.toolurl = toolurl;
    }

    public String getToolname() {
        return toolname;
    }

    public void setToolname(String toolname) {
        this.toolname = toolname;
    }

    String toolurl;

    String toolname;

    public ToolVO(String toolurl, String toolname) {
        this.toolurl = toolurl;
        this.toolname = toolname;
    }
}
