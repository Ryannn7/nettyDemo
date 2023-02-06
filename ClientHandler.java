package com.bufan.demo.service.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @date 2022/3/2 下午7:59
 **/
public class ClientHandler extends SimpleChannelInboundHandler<Data> {
    //文件保存地址
    private String savePath;

    private static Map<Integer, String> map = new HashMap();

    Receiver receiver;

    public ClientHandler(String savePath) {
        this.savePath = savePath;
    }

    public static String getName(int i) {
        return map.get(Integer.valueOf(i));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channelActive 连接成功");
        SendTool.sendCmd(ctx.channel(), "pwd");
        SendTool.sendCmd(ctx.channel(), "ls");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Data data) throws Exception {
        TypeEnum type = data.getType();
        if (type == TypeEnum.MSG) {
            String msg = SendTool.getMsg(data);
            if (msg.startsWith("ls ")) {
                praseLs(msg);
            } else if (msg.startsWith("msg ")) {
                System.out.println(msg.substring(4));
            } else {
                System.out.println(msg);
            }
        } else if (type == TypeEnum.DATA || type == TypeEnum.END) {
            receiver.receiver(data);
        } else if (type == TypeEnum.BEGIN) {
            receiver = new Receiver(data,savePath);
        } else {
            System.out.println(SendTool.getMsg(data));
        }
    }

    private void praseLs(String msg) {
        map.clear();
        String ss = msg.substring(3).trim();
        String[] paths = ss.split("\n");
        for (String p : paths) {
            p = p.trim();
            String[] dd = p.split("/:/");
            if (dd.length == 3) {
                System.out.println(dd[0] + " " + dd[1] + " " + dd[2]);
                map.put(Integer.valueOf(dd[0].trim()), dd[2].trim());
            }
        }
    }


}
