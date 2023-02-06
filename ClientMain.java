package com.bufan.demo.service.netty;

/**
 *  启动入口
 * @date 2022/3/2 下午8:07
 **/
public class ClientMain {

    public static void main(String[] args) {
        String ip = "192.168.0.5";
        int port = 9098;
        //保存文件地址
        String savePath = "D:\\01_MyProject\\enc";

        //服务端启动
 //       NettyService.instance().start(port,receiverPath);
        //客户端启动
        NettyClient.instance().start(ip, port,savePath);
        NettyClient.instance().readCmd();
    }


}
