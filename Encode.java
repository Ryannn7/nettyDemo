package com.bufan.demo.service.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class Encode extends MessageToByteEncoder<Data> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Data data, ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(data.getType().value())
                .writeInt(data.getIndex())
                .writeInt(data.getData().readableBytes())
                .writeBytes(data.getData());
//        System.out.println("Encode :"+ JSON.toJSONString(byteBuf)+"| data "+JSON.toJSONString(data));
    }
}
