package cn.com.devopsplus.dop.server.model.config;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Getter
@Service
public class ModelConfig {

    private final String AlgorithmAddress="http://172.29.7.157:9001";

    //private final String dataPath="/tmp/";
    public final String dataPath="/LocalCodeBase";

    private final String pythonProjectPath="/root/code/src";

    private final String buildModelPythonPath=AlgorithmAddress+"/build_model";

    private final String trainDataPythonPath=AlgorithmAddress+"/get_data";

    private final String runModelPythonPath= AlgorithmAddress+"/run_model";

    private final String locationModelPythonPath= AlgorithmAddress+"/corpus";

    private final String prflPythonPath= AlgorithmAddress+"/locate";

    private final String prflFilePath= dataPath+"test.txt";
}
