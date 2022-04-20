package s04;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;


/**
 * @author wangzhaobin
 * @date 2022/4/21 上午12:12
 */
public class Client {

    private Channel channel = null;

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();
    }

    public void connect(){

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();

        // 不加sync写法
        try {

            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelInitializer())
                    .connect("localhost", 8888);//异步方法

            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        System.out.println("not connected");
                    }else{
                        System.out.println("connected!");
                        //判断连接成功，初始化 initialize the channel
                        channel = future.channel();
                    }
                }
            });


            f.sync();
            System.out.println("-------");
            f.channel().closeFuture().sync();
            System.out.println("已经退出");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }



    }

}


class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new ClientHandler());
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
            buf = (ByteBuf)msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            System.out.println(new String(bytes));

//            System.out.println("7"+buf);
//            System.out.println("9"+buf.refCnt());
        } finally {
            if(buf != null){
                ReferenceCountUtil.release(buf);
                //System.out.println(buf.refCnt());
            }
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //channel 第一次连上可用，写出一个字符串   direct Memory 直接内存 跳过了Java的垃圾回收机制
        ByteBuf buf = Unpooled.copiedBuffer("hello".getBytes());
        //写出去
        ctx.writeAndFlush(buf);
    }
}
