package com.example.udpserver;

import com.example.udpserver.server.UDPServer;
import com.example.udpserver.server.tcpserver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class UdpserverApplication {
    public static ConcurrentHashMap<String,String> userlist= new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String,Long> usermap = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String,byte[]> getbytes= new ConcurrentHashMap<>();
	public static final String RSAprivatKEY = "RSA私钥"
public static void main(String[] args) {
        SpringApplication.run(UdpserverApplication.class, args);
		Thread udpserver = new UDPServer(usermap, userlist,getbytes);
		udpserver.start();
		Thread tcpserver = new tcpserver();
		tcpserver.start();
    }

}
