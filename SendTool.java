package com.bufan.demo.service.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @date 2022/3/2 下午7:51
 **/
public class SendTool {

    public static final Charset CHARSET = Charset.forName("UTF8");

    public static void sendMsg(Channel ch, String msg) {
        ByteBuffer bf = CHARSET.encode(msg);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        Data d = new Data(TypeEnum.MSG, bfn);
        ch.writeAndFlush(d);
    }

    public static void sendCmd(Channel ch, String msg) {
        ByteBuffer bf = CHARSET.encode(msg);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        Data d = new Data(TypeEnum.CMD, bfn);
        ch.writeAndFlush(d);
    }

    public static String getMsg(Data data) {
        CharBuffer cb = CHARSET.decode(data.getData().nioBuffer());
        return cb.toString().trim();
    }

    public static void sendBegin(Channel ch, String msg) {
        ByteBuffer bf = CHARSET.encode(msg);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        Data d = new Data(TypeEnum.BEGIN, bfn);
        ch.writeAndFlush(d);
    }

    public static void sendData(Channel ch, ByteBuffer bf, int index) {
        Data data = new Data();
        data.setType(TypeEnum.DATA);
        ByteBuf bfn = Unpooled.copiedBuffer(bf);
        data.setData(bfn);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }

    public static void sendEnd(Channel ch, int index) {
        Data data = new Data(TypeEnum.END, Unpooled.EMPTY_BUFFER);
        data.setIndex(index);
        ch.writeAndFlush(data);
    }

    public static String size(long num) {
        long m = 1 << 20;
        if (num / m == 0) {
            return (num / 1024) + "KB";
        }
        return num / m + "MB";
    }
}
