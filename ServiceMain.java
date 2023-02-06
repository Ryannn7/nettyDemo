package com.bufan.demo.service.netty;

/**
 * @date 2022/3/2 下午8:43
 **/
public class ServiceMain {

    public static void main(String[] args) {
        String ip = "192.168.3.6";
        int port = 10198;
        //要暴露得文件夹地址
        String receiverPath = "D:\\01_MyProject\\enc";

        //服务端启动
        NettyService.instance().start(port,receiverPath,ip);


    }
}
