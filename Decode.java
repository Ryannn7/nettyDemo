package com.bufan.demo.service.netty;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;


public class Decode  extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        Data data = new Data();
        data.setType(TypeEnum.get(byteBuf.readShort()));
        data.setIndex(byteBuf.readInt());
        data.setLength(byteBuf.readInt());
        data.setData(byteBuf.readBytes(data.getLength()));
        list.add(data);
//        System.out.println("Decode :"+ JSON.toJSONString(byteBuf)+"| data "+JSON.toJSONString(data));
    }
}
