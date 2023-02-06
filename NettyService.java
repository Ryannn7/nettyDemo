package com.bufan.demo.service.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyService {

    private int port = 9091;
    private String ip = "127.0.0.1";
    private String receiverPath = "";
    private static NettyService nettyService = new NettyService();

    private ServerThread thread = new ServerThread();

    private NettyService() {
    }

    public static NettyService instance() {
        return nettyService;
    }

    public void start(int port, String receiverPath,String ip) {
        this.ip = ip;
        this.port = port;
        this.receiverPath = receiverPath;
        thread.start();
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup(1);
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .localAddress(ip,port)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new Decode());
                                ch.pipeline().addLast(new Encode());
                                ch.pipeline().addLast(new ServerHandler(receiverPath));
                            }
                        });
                ChannelFuture future = bootstrap.bind(ip,port).sync();
                if(future.isSuccess()){
                    System.out.println("Trans Server started,ip "+ip+" port: " + port);
                }

                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Trans Server shuting down");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }


}
