package cn.com.netty.nettyclient;

import cn.com.netty.nettyclient.client.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class NettyClientApplication {

	@Autowired
	NettyClient nettyClient;

	public static void main(String[] args) {
		SpringApplication.run(NettyClientApplication.class, args);
	}

	@GetMapping("/send")
	public void sendMsg() throws Exception {

		nettyClient.send();

	}

}
