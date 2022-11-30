# dop-cipipeline

本项目为Devops平台持续集成流水线管理子系统后端开发项目，使用java1.8和springboot框架开发，采用spring cloud实现微服务架构。   
开发过程中可参考dop-server项目（https://github.com/nju-softeng/dop-server.git）进行开发，具体的开发要点有：   
1）微服务名为XXXXX-server，包名统一为cn.com.devopsplus.dop.server.xxxxx   
2）数据对象分层，如po、bo、dto、vo   
3）Controller和Service层的接口添加必要日志输出，要求参考https://q5ci6smhhm.feishu.cn/docs/doccntrL4lFSExYvqV8uBlgmEwd?from=from_copylink    
4）类、方法添加必要功能说明注释   

代码统一在此仓库管理，其中master分支为保护分支，无法直接push代码，需使用自己的开发分支提交pr请求合并到master分支   
