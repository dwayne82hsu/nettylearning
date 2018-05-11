package cn.com.netty.nettyclient.client;

import cn.com.netty.entity.Request;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class NettyClient {

	private static class SingletonHolder {
		private static final NettyClient instance = new NettyClient();
	}

	public static NettyClient getInstance() {
		return SingletonHolder.instance;
	}

	private EventLoopGroup group;
	private Bootstrap b;
	private ChannelFuture cf;

	private NettyClient() {
		group = new NioEventLoopGroup();
		b = new Bootstrap();
		b.group(group)
				.channel(NioSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new ObjectEncoder(),
								new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
								new ClientHandler());
					}
				});
	}

	public void connect() {
		try {
			this.cf = b.connect("127.0.0.1", 7000).sync();
			System.out.println("远程服务器已经连接, 可以进行数据交换..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ChannelFuture getChannelFuture() {

		if (this.cf == null) {
			this.connect();
		}
		if (!this.cf.channel().isActive()) {
			this.connect();
		}

		return this.cf;
	}

	public void send() throws Exception {
		final NettyClient c = NettyClient.getInstance();
		//c.connect();

		ChannelFuture cf = c.getChannelFuture();
		for (int i = 1; i <= 3; i++) {
			Request request = new Request();
			request.setId("" + i);
			request.setName("pro" + i);
			request.setRequestMessage("数据信息" + i);
			cf.channel().writeAndFlush(request);
			TimeUnit.SECONDS.sleep(3);
		}

		cf.channel().closeFuture().sync();

		System.out.println("断开连接..");
	}

}
