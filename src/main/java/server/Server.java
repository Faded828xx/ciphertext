package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class Server {

    static Set<String> checksums = new HashSet<>();
    static Connection connection;
    static Statement statement;

    public void run(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
//                                    new DelimiterBasedFrameDecoder(1024 * 1024, Delimiters.lineDelimiter()),
                                    new LengthFieldBasedFrameDecoder(1024 * 128, 0, 4, 0, 4),
                                    new LengthFieldPrepender(4),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new ServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            System.out.println("Server start at port : " + port);
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            statement.close();
//            connection.commit();
            connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8082;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:/Users/faded828x/Desktop/ciphertext/src/main/java/server/serverFiles.db");
        statement = connection.createStatement();
        String sql = "select file_checksum from file";
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()) {
            checksums.add(resultSet.getString("file_checksum"));
        }
        new Server().run(port);
    }
}
