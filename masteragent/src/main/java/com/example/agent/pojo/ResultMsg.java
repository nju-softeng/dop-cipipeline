package com.example.agent.pojo;

public class ResultMsg {
    boolean success;
    String msg;

    int MsgCode;


    public ResultMsg(String msg) {
        this.msg = msg;
    }
    public ResultMsg(int s){
        success=s==0?false:true;

    }
}
