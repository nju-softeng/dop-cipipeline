package cn.com.devopsplus.dop.server.cipipeline.config;

import cn.com.devopsplus.dop.server.cipipeline.mq.MessageSender;
import cn.com.devopsplus.dop.server.cipipeline.mq.RocketMQMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import cn.com.devopsplus.dop.server.cipipeline.util.JenkinsFileUtil;

/**
 * JenkinsFile生成工具配置类
 *
 * @author yangyuyan
 * @since 2023-01-18
 */
@SpringBootConfiguration
public class JenkinsFileUtilConfig {
    private static final Logger logger = LoggerFactory.getLogger(JenkinsFileUtilConfig.class);

    @Bean(name = "JenkinsFileUnit")
    public JenkinsFileUtil createJenkinsFileUtil() {
        logger.info("begin init JenkinsFileUnit config");
        return new JenkinsFileUtil();
    }
}