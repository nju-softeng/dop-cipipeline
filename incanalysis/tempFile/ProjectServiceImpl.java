package com.seiii.backend_511.service.serviceimpl.projectImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seiii.backend_511.mapperservice.ProjectMapper;
import com.seiii.backend_511.mapperservice.RecommendStrategyMapper;
import com.seiii.backend_511.mapperservice.TypeMapper;
import com.seiii.backend_511.mapperservice.UserProjectMapper;
import com.seiii.backend_511.po.project.Project;
import com.seiii.backend_511.po.project.UserProject;
import com.seiii.backend_511.po.task.Task;
import com.seiii.backend_511.service.device.DeviceService;
import com.seiii.backend_511.service.file.FileService;
import com.seiii.backend_511.service.project.ProjectService;
import com.seiii.backend_511.service.recommend.RecommendStrategyFactory;
import com.seiii.backend_511.service.report.ReportService;
import com.seiii.backend_511.service.task.TaskService;
import com.seiii.backend_511.service.user.UserService;
import com.seiii.backend_511.util.CONST;
import com.seiii.backend_511.util.PageInfoUtil;
import com.seiii.backend_511.vo.ResultVO;
import com.seiii.backend_511.vo.project.ProjectVO;
import com.seiii.backend_511.vo.project.UserProjectVO;
import com.seiii.backend_511.vo.task.TaskVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Resource
    private TaskService taskService;
    @Resource
    private UserService userService;
    @Resource
    private ProjectMapper projectMapper;
    @Resource
    private UserProjectMapper userProjectMapper;
    @Resource
    private DeviceService deviceService;
    @Resource
    private TypeMapper typeMapper;
    @Resource
    private RecommendStrategyFactory recommendStrategyFactory;
    @Resource
    private RecommendStrategyMapper recommendStrategyMapper;

    public List<ProjectVO> setMemberNum(List<Project> projectList){
        List<ProjectVO> ans = new ArrayList<>();
        for(Project project:projectList){
            ans.add(setMemberNum(project));
        }
        return ans;
    }
    private ProjectVO setMemberNum(Project po){
        if(getProjectNumbers(po.getId()).getCode().equals(CONST.REQUEST_FAIL)){
            return toProjectVO(po);
        }
        ProjectVO vo = toProjectVO(po);
        vo.setMemberNum(getProjectNumbers(po.getId()).getData());
        return vo;
    }


    @Override
    public ResultVO<ProjectVO> getProjectByIdWithUid(Integer projectId, Integer uid) {
        ProjectVO project = getProjectById(projectId);
        onClick(projectId);
        if(project==null){
            return new ResultVO<>(CONST.REQUEST_FAIL,"查询失败");
        }
        for(UserProject userProject:userProjectMapper.selectByUser(uid)){
            if(userProject.getProjectId().equals(projectId)){
                project.setJoined(true);
                break;
            }
        }
        return new ResultVO<>(CONST.REQUEST_SUCCESS,"成功",project);
    }

    @Override
    public PageInfo<ProjectVO> getAllProjects(Integer currPage) {
        if(currPage==null || currPage<1) currPage=1;
        List<ProjectVO> projectVO = new ArrayList<>();
        for(Project project:projectMapper.selectAll()){
            ProjectVO p = toProjectVO(project);
            p.setMemberNum(userProjectMapper.selectByProjects(p.getId()).size());
            projectVO.add(p);
        }
        return PageInfoUtil.ListToPageInfo(projectVO,currPage);
    }

    @Override
    public ProjectVO getProjectById(Integer projectId) {
        if(projectMapper.selectByPrimaryKey(projectId)==null){
            return null;
        }
        return setMemberNum(projectMapper.selectByPrimaryKey(projectId));
    }
    private ProjectVO toProjectVO(Project project){
        ProjectVO projectVO = new ProjectVO(project);
        if(deviceService.getDeviceById(projectVO.getDeviceId())!=null)
            projectVO.setDeviceInfo(deviceService.getDeviceById(projectVO.getDeviceId()).getDeviceInfo());
        if(typeMapper.selectByPrimaryKey(projectVO.getType())!=null)
            projectVO.setTypeInfo(typeMapper.selectByPrimaryKey(projectVO.getType()).getTypeInfo());
        return projectVO;
    }
    private List<ProjectVO> toProjectVO(List<Project> projectList){
        List<ProjectVO> projectVOList = new ArrayList<>();
        for(Project project:projectList){
            projectVOList.add(toProjectVO(project));
        }
        return projectVOList;
    }
    public List<Project> selectAllByClickOrder(int nums,Integer uid){
        return projectMapper.selectAllByClickOrder(nums,uid);
    }
    @Override
    public ResultVO<ProjectVO> onClick(Integer pid) {
        Project project = projectMapper.selectByPrimaryKey(pid);
        if(project==null){
            return new ResultVO<>(CONST.REQUEST_FAIL,"失败");
        }
        project.setClickTimes(project.getClickTimes()+1);
        updateProject(new ProjectVO(project));
        return new ResultVO<>(CONST.REQUEST_SUCCESS,"成功");
    }

    @Override
    public ResultVO<List<ProjectVO>> getRecommendation(Integer uid) {
        return new ResultVO<>(CONST.REQUEST_SUCCESS,"成功",setMemberNum(recommendStrategyFactory.getRecommendStrategy(uid).getRecommend(uid,recommendStrategyMapper.selectOnUse())));
    }

    @Override
    public boolean isActive(Project p) {
        a=1;
        return userProjectMapper.selectByProjects(p.getId()).size()<p.getWorkerAmount()&&p.getState().equals(CONST.STATE_OPEN);
    }

}
