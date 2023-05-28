package cn.com.devopsplus.dop.server.model.mapper;

import cn.com.devopsplus.dop.server.model.pojo.Model;
import cn.com.devopsplus.dop.server.model.pojo.TrainSetMode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
@Mapper
@Component
public interface modelMapper extends JpaRepository<Model,Long> {



}
