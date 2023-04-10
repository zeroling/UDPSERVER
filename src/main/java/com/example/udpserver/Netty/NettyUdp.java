package com.example.udpserver.Netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class NettyUdp {
    public static ChannelFuture channelFuture;
    public static void NettyUdprun()
    {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootStrap = new Bootstrap();
        bootStrap.group(group)
                .channel(NioDatagramChannel.class) // 指定传输数据包，可支持UDP
                .option(ChannelOption.SO_BROADCAST, true) // 广播模式
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) // 线程池复用缓冲区
                .handler(new UDPServerHandler());
        channelFuture = bootStrap.bind(41000);
    }
}
