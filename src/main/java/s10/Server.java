package s10;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author wangzhaobin
 * @date 2022/4/11 下午11:05
 */
public class Server {
    //使用默认的线程处理这个通道组
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws Exception {


    }

    public void serverStart() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(2);
        try {
            ServerBootstrap b = new ServerBootstrap();
            ChannelFuture f = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println(Thread.currentThread().getId());
                            ChannelPipeline pl = ch.pipeline();//责任链
                            pl.addLast(new TankMsgDecoder())
                                    .addLast(new ServerChildHandler());

                            System.out.println(ch);
                        }
                    })
                    .bind(8888)
                    .sync();

            ServerFrame.INSTANCE.updateServerMsg("server start!");
            System.out.println("server started!!");

            f.channel().closeFuture().sync();

        } catch (Exception e){
            e.printStackTrace();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}

/**
 * ChannelInboundHandlerAdapter是一个骨架的实现   adapter
 */
class ServerChildHandler extends ChannelInboundHandlerAdapter{//SimpleChannelInboundHandler


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.clients.add(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf buf = null;

        try {
            TankMsg tm = (TankMsg) msg;
            System.out.println(tm);
        } finally {
            ReferenceCountUtil.release(msg);
        }

//        try {
//            buf = (ByteBuf)msg;
//            byte[] bytes = new byte[buf.readableBytes()];
//            buf.getBytes(buf.readerIndex(), bytes);
//
//            String s = new String(bytes);
//
//            ServerFrame.INSTANCE.updateClientMsg(s);
//
//            if(s.equals("_bye_")){
//                ServerFrame.INSTANCE.updateServerMsg("客户端请求退出");
//                Server.clients.remove(ctx.channel());
//                ctx.close();
//            }else{
//                Server.clients.writeAndFlush(msg);
//            }
//        } finally {
//
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //server端异常处理
        Server.clients.remove(ctx.channel());
        ctx.close();
    }
}