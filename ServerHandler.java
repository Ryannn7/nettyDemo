package com.bufan.demo.service.netty;

import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * @date 2022/3/2 下午7:54
 **/
public class ServerHandler extends SimpleChannelInboundHandler<Data> {

    //文件夹地址
    private String cpath = "";


    public ServerHandler(String receiverPath) {
        this.cpath = receiverPath;
    }

    /**
     * 客户端连接
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx){
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        System.out.println("有新的客户端连接 ："+ address.getAddress().getHostAddress());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data data) throws Exception {
        handle(ctx, data);
    }

    private void handle(ChannelHandlerContext ctx, Data data) throws Exception {
        System.out.println("ServerHandler data "+ JSON.toJSONString(data));
        if (data.getType() == TypeEnum.CMD) {
            String cmd = SendTool.getMsg(data);
            if (cmd.equalsIgnoreCase("ls")) {
                ls(ctx.channel());
            } else if (cmd.startsWith("cd ")) {
                cd(ctx.channel(), cmd);
            } else if (cmd.startsWith("get ")) {
                String name = cmd.substring(4);
                Sender sender = new Sender(cpath, name, ctx.channel());
                SenderThreadPool.exe(sender);
            } else if (cmd.equalsIgnoreCase("pwd")) {
                SendTool.sendMsg(ctx.channel(), "now at: " + cpath);
            } else {
                SendTool.sendMsg(ctx.channel(), "unknow command!");
            }
        }
    }

    private void ls(Channel channel) {
        int k = 0;
        StringBuilder sb = new StringBuilder();
        File file = new File(cpath);
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                sb.append(k);
                sb.append("/:/");
                sb.append("目录");
                sb.append("/:/");
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                sb.append(k);
                sb.append("/:/");
                sb.append(SendTool.size(f.length()));
                sb.append("/:/");
                sb.append(f.getName());
                sb.append("\n");
                k++;
            }
        }
        SendTool.sendMsg(channel, "ls " + sb.toString());
    }

    private void cd(Channel channel, String cmd) {
        String dir = cmd.substring(3).trim();
        if (dir.equals("..")) {
            File f = new File(cpath);
            f = f.getParentFile();
            cpath = f.getAbsolutePath();
            SendTool.sendMsg(channel, "new path " + cpath);
            ls(channel);
        } else {
            String path1 = cpath + File.separator + dir;
            File f1 = new File(path1);
            if (f1.exists()) {
                cpath = path1;
                SendTool.sendMsg(channel, "new path " + cpath);
                ls(channel);
            } else {
                SendTool.sendMsg(channel, "error, path not found");
            }
        }
    }

}
