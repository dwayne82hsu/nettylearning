package cn.com.netty.nettyserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class NettyServer {

	private static final Logger log = Logger.getLogger(NettyServer.class);

	private final EventLoopGroup bossGroup = new NioEventLoopGroup();
	private final EventLoopGroup workerGroup = new NioEventLoopGroup();

	private Channel channel;

	/**
	 * 启动服务
	 */
	public ChannelFuture run(InetSocketAddress address) {
		ChannelFuture cf = null;
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					//设置日志
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>() {
						protected void initChannel(SocketChannel sc) throws Exception {
							sc.pipeline().addLast(new ObjectEncoder(),
									new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
									new ReadTimeoutHandler(5), new ServerHandler());
						}
					});

			cf = b.bind(7000).syncUninterruptibly();
		} catch (Exception e) {
			log.error("Netty start error:", e);
		} finally {
			if (cf != null && cf.isSuccess()) {
				log.info("Netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
			} else {
				log.error("Netty server start up Error!");
			}
		}

		return cf;
	}

	public void destroy() {
		log.info("Shutdown Netty Server...");
		if (channel != null) {
			channel.close();
		}
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
		log.info("Shutdown Netty Server Success!");
	}

}
