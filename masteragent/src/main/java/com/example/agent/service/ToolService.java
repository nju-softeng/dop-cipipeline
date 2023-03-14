package com.example.agent.service;


import com.example.agent.pojo.ResultMsg;
import com.example.agent.vo.ToolVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class ToolService {
    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public ResultMsg saveToolConfig(ToolVO toolVO){
        String sql="insert into tools(toolurl,toolname,tooldir,fixcmd) values(?,?,?,?)";
        int res=jdbcTemplate.update(sql,toolVO.getToolurl(),toolVO.getToolname(),toolVO.getTooldir(),toolVO.getFixcmd());
        return new ResultMsg(res);
    }
}
