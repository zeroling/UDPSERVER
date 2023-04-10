package com.example.udpserver.Netty;

import com.example.udpserver.server.RSACoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static com.example.udpserver.Netty.NettyUdp.channelFuture;
import static com.example.udpserver.UdpserverApplication.*;

public class UDPServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        ByteBuf byteBuf = datagramPacket.content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        /**
         * 私钥解密
         */
        byte[] get = RSACoder.decryptByPrivateKey(bytes,RSAprivatKEY);
        /**
         * userlist 存储 /ip//port//name,key
         */
        String inf = datagramPacket.sender().getAddress()+"//"+datagramPacket.sender().getPort()+"//"+new String(get).split("//")[0];
        String token = new String(get).split("//")[1];
        Set<String> KeySet = userlist.keySet();
        userlist.put(inf,token);
        usermap.put(inf,System.currentTimeMillis());
        for(String kg:KeySet)
        {
            if(Objects.equals(userlist.get(kg), token))
            {
                for(String sends:KeySet) {
                    if(Objects.equals(userlist.get(sends), token)) {
                        /**
                         * 私钥加密
                         */
                        byte[] send = RSACoder.encryptByPrivateKey(sends.getBytes(StandardCharsets.UTF_8), RSAprivatKEY);
                        /**
                         * userlist是用户ip,port,用户名,key
                         * usermap是用户名,上次上线时间.
                         */
                        ByteBuf byteBufs = Unpooled.copiedBuffer(send);
                        channelFuture.channel().writeAndFlush(new DatagramPacket(byteBufs, new InetSocketAddress(datagramPacket.sender().getAddress(), datagramPacket.sender().getPort())));
                    }
                }
                break;
            }
        }
        for(String kg:KeySet)
        {
            if(Objects.equals(userlist.get(kg), token))
            {
                byte[] send = RSACoder.encryptByPrivateKey(inf.getBytes(StandardCharsets.UTF_8), RSAprivatKEY);
                ByteBuf byteBufs = Unpooled.copiedBuffer(send);
                channelFuture.channel().writeAndFlush(new DatagramPacket(byteBufs, new InetSocketAddress(kg.split("//")[0].replace("/",""), Integer.parseInt(kg.split("//")[1]))));
            }
        }
    }
}
