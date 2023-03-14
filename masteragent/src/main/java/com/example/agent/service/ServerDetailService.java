package com.example.agent.service;


import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Service
public class ServerDetailService {


    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    public static String getOS(){
        return osmxb.getName();
    }
    public static double getMemory(){

        double totalvirtualMemory = osmxb.getTotalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreeMemorySize();

        double value = freePhysicalMemorySize/totalvirtualMemory;
        int percentMemoryLoad = (int) ((1-value)*100);

        return percentMemoryLoad;
    }
    public static String getIP(){
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        String ip=ia.toString().split("/")[1];
        return ip;
    }

    public static String getCPU(){

        return osmxb.getArch();
    }

    public static String getLocalMac() {
        InetAddress ia= null;
        try {
            ia = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        //获取网卡，获取地址
        byte[] mac = new byte[0];
        try {
            mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("mac数组长度："+mac.length);
        StringBuffer sb = new StringBuffer("");
        for(int i=0; i<mac.length; i++) {
            if(i!=0) {
                sb.append("-");
            }
            //字节转换为整数
            int temp = mac[i]&0xff;
            String str = Integer.toHexString(temp);
            //System.out.println("每8位:"+str);
            if(str.length()==1) {
                sb.append("0"+str);
            }else {
                sb.append(str);
            }
        }

        String myMac=sb.toString().toUpperCase();
        //System.out.println("本机MAC地址:"+myMac);

        return myMac;

    }




}
