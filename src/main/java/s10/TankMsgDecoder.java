package s10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wangzhaobin
 * @date 2022/4/22 上午1:22
 */
public class TankMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 8){  //TCP 的拆包和粘包问题
            return;
        }
        //in.markReaderIndex();

        //保证了顺序
        int x = in.readInt();
        int y = in.readInt();
        out.add(new TankMsg(x,y));
    }
}
