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

    public String getTooldir() {
        return tooldir;
    }

    public void setTooldir(String tooldir) {
        this.tooldir = tooldir;
    }

    public String getFixcmd() {
        return fixcmd;
    }

    public void setFixcmd(String fixcmd) {
        this.fixcmd = fixcmd;
    }

    String toolurl;

    String toolname;

    String tooldir;

    String fixcmd;


}
