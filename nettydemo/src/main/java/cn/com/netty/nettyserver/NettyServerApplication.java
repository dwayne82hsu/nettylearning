package cn.com.netty.nettyserver;

import cn.com.netty.nettyserver.server.NettyServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class NettyServerApplication implements CommandLineRunner {

	@Value("${n.port}")
	private int port;

	@Value("${n.url}")
	private String url;

	@Autowired
	private NettyServer socketServer;

	public static void main(String[] args) {
		SpringApplication.run(NettyServerApplication.class, args);
	}

	@Override
	public void run(String... strings) {
		InetSocketAddress address = new InetSocketAddress(url, port);
		ChannelFuture future = socketServer.run(address);
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				socketServer.destroy();
			}
		});
		future.channel().closeFuture().syncUninterruptibly();
	}
}
