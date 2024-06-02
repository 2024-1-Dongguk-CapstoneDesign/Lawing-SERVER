//package com.capstone.lawing.global;
//
//
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class SshTunnelManager implements DisposableBean {
//
//    @Value("${ssh.remote}")
//    private String dbEndPoint;
//    private Process sshProcess;
//
//    public SshTunnelManager() throws IOException {
//        startSshTunnel();
//    }
//
//    private void startSshTunnel() throws IOException {
//        sshProcess = new ProcessBuilder("ssh", "-i", "classpath:/key/LawingKey.pem", "-f", "-N", "-L", "3307:"+dbEndPoint+":3306", "ec2-user@15.164.148.174").start();
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        if (sshProcess != null) {
//            sshProcess.destroy();
//        }
//    }
//}
//
