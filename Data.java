package com.bufan.demo.service.netty;

import io.netty.buffer.ByteBuf;


public class Data {

    private TypeEnum type;

    private int index;

    private int length;

    private ByteBuf data;

    public Data() {
    }

    public Data(TypeEnum type, ByteBuf data) {
        this.type = type;
        this.data = data;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ByteBuf getData() {
        return data;
    }

    public void setData(ByteBuf data) {
        this.data = data;
    }
}
