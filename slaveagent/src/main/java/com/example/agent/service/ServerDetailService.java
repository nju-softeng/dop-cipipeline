package com.example.agent.service;


import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Enumeration;

@Service
public class ServerDetailService {


    private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


    public static String getOS(){
        return osmxb.getName();
    }
    public static int getMemory(){

        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();

        double value = freePhysicalMemorySize/totalvirtualMemory;
        int percentMemoryLoad = (int) ((1-value)*100);

        return percentMemoryLoad;
    }
    public String getIP(){
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            try {
                if (!ni.isLoopback() && ni.isUp()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    public static String getCPU(){

        return osmxb.getArch();
    }

    public  String getLocalMac()  {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            try {
                if (!ni.isLoopback() && ni.isUp()) {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < mac.length; i++) {
                            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                        }
                        return sb.toString();
                    }
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }




}
