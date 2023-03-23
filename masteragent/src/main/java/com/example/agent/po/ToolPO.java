package com.example.agent.po;

import lombok.Data;

@Data
public class ToolPO {
    String toolurl;

    String toolname;

    String tooldir;

    String fixcmd;

    public ToolPO() {
    }

    public ToolPO(String toolurl, String toolname, String tooldir, String fixcmd) {
        this.toolurl = toolurl;
        this.toolname = toolname;
        this.tooldir = tooldir;
        this.fixcmd = fixcmd;
    }
}
