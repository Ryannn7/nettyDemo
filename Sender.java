package com.bufan.demo.service.netty;

import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * @date 2022/3/2 下午7:49
 **/
public class Sender implements Runnable {
    String path;
    String name;
    File f;
    FileInputStream in;
    FileChannel ch;
    ByteBuffer bf;
    int index = 0;
    Channel channel;
    private long t0;

    public Sender(String path, String name, Channel c) {
        this.path = path;
        this.name = name;
        this.channel = c;
    }

    @Override
    public void run() {
        begin(path);
        send();
    }

    public void begin(String path) {
        f = new File(path + File.separator + name);
        if (!f.exists() || !f.isFile() || !f.canRead()) {
            SendTool.sendMsg(channel, "file can not read.");
        }

        try {
            in = new FileInputStream(f);
            ch = in.getChannel();
            bf = ByteBuffer.allocate(20480);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        t0 = System.currentTimeMillis();
        System.out.println("send begin: " + name + "  " + SendTool.size(f.length()));
        SendTool.sendBegin(channel, name + "/:/" + f.length());
    }

    public void send() {
        if (in == null) {
            return;
        }
        try {
            while (ch.read(bf) != -1) {
                while (!channel.isWritable()) {
                    TimeUnit.MILLISECONDS.sleep(5);
                }
                bf.flip();
                SendTool.sendData(channel, bf, index);
                index++;
                bf.clear();
            }
            SendTool.sendEnd(channel, index);

            long cost = Math.round((System.currentTimeMillis() - t0) / 1000f);
            System.out.println("send over: " + f.getName() + "   Cost Time: " + cost + "s");
            clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clear() throws IOException {
        in.close();
        bf.clear();
        f = null;
        in = null;
        index = 0;
    }
}
