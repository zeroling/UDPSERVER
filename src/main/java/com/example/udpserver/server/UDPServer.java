package com.example.udpserver.server;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.udpserver.Netty.NettyUdp.NettyUdprun;

public class UDPServer extends Thread{
	/**
	 * 问题是好像用户列表的更新不及时?,这样,直接把新用户加入之后直接调用dis就可以了
	 */
	ConcurrentHashMap<String,Long> usermap;
	ConcurrentHashMap<String,String> userlist;
	ConcurrentHashMap<String,byte[]> getbytes;

	public UDPServer(ConcurrentHashMap<String, Long> usermap, ConcurrentHashMap<String, String> userlist, ConcurrentHashMap<String, byte[]> getbytes) {
		this.usermap = usermap;
		this.userlist = userlist;
		this.getbytes = getbytes;
	}

	@Override
	public void run() {
		try {
			udpserve();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public void udpserve() throws IOException {
		online online = new online(userlist,usermap);
		online.poll();
		NettyUdprun();
	}

}




