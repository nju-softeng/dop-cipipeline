package cn.com.devopsplus.dop.server.testmanage.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellUtil {
    public static String[] runShell (String shellStr,String commandDir) {
        Process process = null;
        StringBuilder processOutput = new StringBuilder();
        StringBuilder processError = new StringBuilder();
        try {
            process=Runtime.getRuntime().exec(new String[]{"/bin/sh" , "-c",shellStr},null,new File(commandDir));
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            int exitValue = process.waitFor();
            System.out.println("[runShell] exitValue is :" + exitValue);
            if(exitValue!=0){
                String errorLine = "";
                while ((errorLine = errorInput.readLine()) != null) {
                    processError.append(errorLine+"\n");
                }
                input.close();
                return new String[]{String.valueOf(exitValue),processOutput.toString(),processError.toString()};
            }
            String line = "";
            while ((line = input.readLine()) != null) {
                processOutput.append(line+"\n");
            }
            input.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(processOutput.toString());
        return new String[]{"0",processOutput.toString()};
    }
}
