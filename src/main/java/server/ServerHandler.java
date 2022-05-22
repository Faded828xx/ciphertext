package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    Set<String> checksums = Server.checksums;

    // client request
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String obj) {
        try {
            Channel channel = ctx.channel();

            String flag = "@@@";
            String[] req = obj.split(flag);
//            System.out.println(req[0]); //
//            System.out.println(req[1]); // filename
//            System.out.println(req[2]); // checksum
//            System.out.println(req[3]); // content
            if (req.length == 0) return;

            if (req[0].equals("upload")) {
//                String response = "msg" + flag;
//                if(checksums.contains(req[2])) {
//                    channel.writeAndFlush(response + "File already exits!");
//                    return ;
//                }
//                checksums.add(req[2]);
//                File file = new File("/Users/faded828x/Desktop/ciphertext/src/main/java/server/files/" + req[1]);
//                file.createNewFile();
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                fileOutputStream.write(req[3].getBytes());
//                response +=  + "File upload success!";
                String response = upload(req[1], req[2], req[3]);
                channel.writeAndFlush(response);
            } else if (req[0].equals("download")) {
//                String response = "file" + flag;
//                response += req[1] + flag;
//                String path = "/Users/faded828x/Desktop/ciphertext/src/main/java/server/files/" + req[1];
//                channel.writeAndFlush(response + Files.readString(Paths.get(path)));
                String response = download(req[1]);
                channel.writeAndFlush(response);
            } else if (req[0].equals("dir")) {
                String response = dir();
//                StringBuilder response = new StringBuilder("dir");
//                String path = "/Users/faded828x/Desktop/ciphertext/src/main/java/server/files/";
//                File dir = new File(path);
//                String[] list = dir.list();
//                for(String filename : list)
//                    response.append(flag).append(filename);

//                System.out.println(response);
                channel.writeAndFlush(response);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String dir() {
        String sql = "select file_name from file";
        StringBuilder response = new StringBuilder("dir");
        try {
            ResultSet resultSet = Server.statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("file_name");
                response.append("@@@").append(name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private String upload(String name, String checksum, String content) {
        String response = "msg@@@";
        if (Server.checksums.contains(checksum))
            return response + "File already exits!";
        Server.checksums.add(checksum);
        String sql = "insert into file ('file_name', 'file_checksum', 'file_content') " +
                "values ('" + name + "','" + checksum + "','" + content + "');";
        try {
            Server.statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response + "File upload success!";
    }


    private String download(String name) {
        String response = "file@@@";
        response += name + "@@@";
        String sql = "select file_content from file where file_name = '" + name + "';";
        try {
            ResultSet resultSet = Server.statement.executeQuery(sql);
            while (resultSet.next()) {
                response += resultSet.getString("file_content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


}
