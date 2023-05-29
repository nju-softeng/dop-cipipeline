package util;

import vo.ResultVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecHelper {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 4, 20, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());


    public static ResultVO execCommand(String command,String specialCharset){
        String sysOutInfo = null;
        String sysOutError = null;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String charset = isWindows?"GBK":"UTF-8";
        if(!specialCharset.equals("")){
            charset=specialCharset;
        }
        String[] cmdArray = {isWindows ? "cmd" : "/bin/sh", isWindows ? "/c" : "-c", command};
        try {
            Process process = Runtime.getRuntime().exec(cmdArray);

            String finalCharset = charset;
            FutureTask<String> sysOutTask = new FutureTask<>(() -> {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), finalCharset))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            });
            THREAD_POOL_EXECUTOR.execute(sysOutTask);


            FutureTask<String> sysErrorTask = new FutureTask<>(() -> {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), finalCharset))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            });
            THREAD_POOL_EXECUTOR.execute(sysErrorTask);

            sysOutInfo = sysOutTask.get();
            sysOutError = sysErrorTask.get();

            if (process.isAlive()) {
                process.destroy();
                System.out.println("线程已销毁");
            } else {
                System.out.println("线程已销毁");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResultVO(CONST.REQUEST_SUCCESS,sysOutError+sysOutInfo);
    }

    public static ResultVO execCommand(String command,String specialCharset,String append){
        String sysOutInfo = null;
        String sysOutError = null;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String charset = isWindows?"GBK":"UTF-8";
        if(!specialCharset.equals("")){
            charset=specialCharset;
        }
        String[] cmdArray = {isWindows ? "cmd" : "/bin/sh", isWindows ? "/c" : "-c", command};
        try {
            Process process = Runtime.getRuntime().exec(cmdArray);

            String finalCharset = charset;
            FutureTask<String> sysOutTask = new FutureTask<>(() -> {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), finalCharset))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line+append);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            });
            THREAD_POOL_EXECUTOR.execute(sysOutTask);


            FutureTask<String> sysErrorTask = new FutureTask<>(() -> {
                StringBuilder builder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), finalCharset))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line+append);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return builder.toString();
            });
            THREAD_POOL_EXECUTOR.execute(sysErrorTask);

            sysOutInfo = sysOutTask.get();
            sysOutError = sysErrorTask.get();

            if (process.isAlive()) {
                process.destroy();
                System.out.println("线程已销毁");
            } else {
                System.out.println("线程已销毁");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResultVO(CONST.REQUEST_SUCCESS,sysOutError+sysOutInfo);
    }
}
