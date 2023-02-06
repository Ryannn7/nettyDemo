package com.bufan.demo.service.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

/**
 *
 * @date 2022/3/2 下午8:05
 **/
public class NettyClient {

    private static NettyClient client = new NettyClient();
    private String ip;
    private int port;
    private String savePath ;
    private Channel channel = null;
    private Thread t = new ClientThread();

    private NettyClient() {
    }

    public static NettyClient instance() {
        return client;
    }

    public void start(String ip, int port,String savePath) {
        if (t.isAlive()) {
            return;
        }
        this.savePath = savePath;
        this.ip = ip;
        this.port = port;
        t.start();
    }

    public void readCmd() {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String cmd = sc.nextLine().trim();
            if (cmd.equalsIgnoreCase("exit")) {
                channel.closeFuture();
                return;
            } else if (cmd.startsWith("get ")) {
                int i = Integer.valueOf(cmd.substring(4).trim());
                cmd = "get " + ClientHandler.getName(i);
            } else if (cmd.startsWith("cd ")) {
                String p = cmd;
                p = p.substring(3).trim();
                if (!p.equals("..")) {
                    try {
                        int i = Integer.valueOf(p);
                        cmd = "cd " + ClientHandler.getName(i);
                    } catch (Exception e) {
                    }
                }
            }
            SendTool.sendCmd(channel, cmd);
        }
    }

    private class ClientThread extends Thread {
        @Override
        public void run() {
            Bootstrap bootstrap = new Bootstrap();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                bootstrap.group(group).channel(NioSocketChannel.class);
                bootstrap.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        ch.pipeline().addLast(new Decode());
                        ch.pipeline().addLast(new Encode());
                        pipeline.addLast(new ClientHandler(savePath));
                    }
                });
                bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                bootstrap.option(ChannelOption.TCP_NODELAY,true);
                bootstrap.remoteAddress(ip,port);

                ChannelFuture  future = bootstrap.connect(ip, port).sync();
                if(future.isSuccess()){
                    System.out.println("Trans Client connect to " + ip + ":" + port);
                    channel = future.channel();
                }
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
                System.out.println("Trans Client stoped.");
            }
        }
    }
}
