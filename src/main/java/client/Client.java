package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class Client {
    private String host;
    private int port;
    private static final String key = "123456789abcdefg";
    private static final String iv = "iviviviviviviviv";
    public static Set<String> serverFiles;
    private final EventLoopGroup workerGroup;
    private Channel channel;

    /**
     *
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.serverFiles = new HashSet<>();

        workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
//                            new DelimiterBasedFrameDecoder(1024 * 1024, Delimiters.lineDelimiter()),
                            new LengthFieldBasedFrameDecoder(1024 * 128, 0, 4, 0, 4),
                            new LengthFieldPrepender(4),
                            new StringDecoder(),
                            new StringEncoder(),
                            new ClientHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync(); // (5)
            channel = f.channel();
//            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String msg) {
        try {
            ChannelFuture channelFuture = channel.writeAndFlush(msg);
//            channelFuture.sync();
//            Thread.sleep(2000); // 等待response
            if(channelFuture != null)
                channelFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void upload(String path) {
        String flag = "@@@";
        String[] pp = path.split("/");
        String fileName = pp[pp.length - 1];
        try {
            String fileContent = Files.readString(Paths.get(path));
            String req = "upload" + flag + fileName + flag + Encryption.sha(fileContent) + flag +
                    Encryption.encrypt(fileContent, key, iv);
//            System.out.println(Encryption.encrypt(fileContent, key, iv));
            sendRequest(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void download(String fileName) {
        String flag = "@@@";
        String req = "download" + flag + fileName;
        try {
            sendRequest(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decrypt(String cipher) {
        return Encryption.decrypt(cipher, key, iv);
    }

    public void directory() {
        try {
            sendRequest("dir");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        String flag = "@@@";
        String path = "/Users/faded828x/Desktop/ciphertext/src/test/java/FileTest.java";
        String req1 = "upload" + flag + "FileTest.java" + flag + "checksum1" + flag + Files.readString(Paths.get(path));
        String req2 = "download" + flag + "FileTest.java";
        new Client("localhost", 8082).sendRequest(req2);
    }
}
