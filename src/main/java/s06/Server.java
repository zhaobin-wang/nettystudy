package s06;


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
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author wangzhaobin
 * @date 2022/4/11 下午11:05
 */
public class Server {
    //使用默认的线程处理这个通道组
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void main(String[] args) throws Exception {
//        ServerSocket ss = new ServerSocket();
//        ss.bind(new InetSocketAddress(8888));
//
//        Socket s = ss.accept();
//        System.out.println("a client connect!!");

//只负责链接
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
                            pl.addLast(new ServerChildHandler());

                            System.out.println(ch);
                        }
                    })
                    .bind(8888)
                    .sync();


            System.out.println("server started!!");

            f.channel().closeFuture().sync();

        } finally {
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
        ByteBuf buf = null;

        try {
            buf = (ByteBuf)msg;



            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(new String(bytes));



            Server.clients.writeAndFlush(msg);

//            System.out.println("7"+buf);
//            System.out.println("9"+buf.refCnt());
        } finally {
//            if(buf != null){
//                ReferenceCountUtil.release(buf);
//                //System.out.println(buf.refCnt());
//            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}