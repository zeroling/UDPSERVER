package com.example.udpserver.Netty;

import com.example.udpserver.server.RSACoder;
import com.example.udpserver.server.SHA;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.example.udpserver.UdpserverApplication.RSAprivatKEY;
import static com.example.udpserver.server.mail.sendMail;
import static com.example.udpserver.server.mysql.SELECT;


/**
 * @author zwz
 */
public class ChatServer {
    public static void tcprun() {
        // NioEventLoopGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                List<Byte> BYTE = new ArrayList<>();
                ChannelPipeline chhanl=ch.pipeline();
                SSLEngine engine = SecureChatSslContextFactory.getServerContext("server.jks").createSSLEngine();
                engine.setUseClientMode(false);//设置为服务器模式
                chhanl.addLast("ssl", new SslHandler(engine));
                chhanl.addLast("read",new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                        byte[] temp = ByteBufUtil.getBytes((ByteBuf) msg);
                        for (byte b:temp)
                        {
                            BYTE.add(b);
                        }
                    }
                });
                chhanl.addLast("complete",new ChannelInboundHandlerAdapter(){
                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        Channel channel = ctx.channel();
                        if (BYTE.size()>=256)
                        {
                            byte[] Get = new byte[256];
                            for(int i=0;i<256;i++)
                            {
                                Get[i]=BYTE.get(i);
                            }
                            String head = new String(RSACoder.decryptByPrivateKey(Get,RSAprivatKEY));
                            if (head.contains("//")) {
                                int length = Integer.parseInt(head.split("//")[0]) + Integer.parseInt(head.split("//")[1]);
                                if (BYTE.size() == length + 256) {
                                    Handlebyte(channel, BYTE, Integer.parseInt(head.split("//")[0]), Integer.parseInt(head.split("//")[1]));
                                    ctx.close();
                                    ctx.disconnect();
                                    ch.close();
                                    ch.disconnect();
                                }
                            }else {
                                ByteBuf rs = Unpooled.buffer(2048);
                                rs.writeBytes(SELECT(SHA.getResult(head)).getBytes(StandardCharsets.UTF_8));
                                channel.writeAndFlush(rs);
                                ctx.close();
                                ctx.disconnect();
                                ch.close();
                                ch.disconnect();
                            }
                        }
                    }
                });
            }
        });
        // 服务端绑定27777的端口
        serverBootstrap.bind(27777);
    }

    public static void Handlebyte(Channel channel,List<Byte> BYTE,int ml,int il)
    {
        byte[] Get = new byte[BYTE.size()];
        for(int i=0;i<Get.length;i++)
        {
            Get[i]=BYTE.get(i);
        }
        if(il==0)
        {
            byte[] message = new byte[ml];
            System.arraycopy(Get,256,message,0,ml);
            sendMail(new String(message),null,null);
            ByteBuf rs = Unpooled.buffer(2048);
            rs.writeBytes("RECEIVE".getBytes(StandardCharsets.UTF_8));
            channel.writeAndFlush(rs);
        }
        else {
            byte[] message = new byte[ml];
            System.arraycopy(Get,256,message,0,ml);
            byte[] img = new byte[il];
            System.arraycopy(Get,256+ml,img,0,il);
            String filename = String.valueOf(System.currentTimeMillis());
            sendMail(new String(message), filename +".png",img);
            ByteBuf rs = Unpooled.buffer(2048);
            rs.writeBytes("RECEIVE".getBytes(StandardCharsets.UTF_8));
            channel.writeAndFlush(rs);
            System.gc();
        }
    }
}
