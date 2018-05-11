package cn.com.netty.nettyserver.server;

import cn.com.netty.entity.Request;
import cn.com.netty.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		Request request = (Request)msg;
		System.out.println("Server : " + request.getId() + ", " + request.getName() + ", " + request.getRequestMessage());
		Response response = new Response();
		response.setId(request.getId());
		response.setName("response" + request.getId());
		response.setResponseMessage("响应内容" + request.getId());
		ctx.writeAndFlush(response);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("channelActive>>>>>>>>");
	}

	private static final Logger log = Logger.getLogger(ServerHandler.class);

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		log.info("channelReadComplete");
	}

}
