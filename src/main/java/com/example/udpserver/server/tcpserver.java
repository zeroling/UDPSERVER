package com.example.udpserver.server;

import static com.example.udpserver.Netty.ChatServer.tcprun;

public class tcpserver extends Thread{
    @Override
    public void run() {
        tcprun();
    }
}
