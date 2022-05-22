package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author faded828x
 * @date 2022/5/15
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

//    private Client client = new Client("localhost", 8082);

    // server response
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String obj)
            throws Exception {
//        Channel incoming = ctx.channel();
        String flag = "@@@";
        String[] resp = obj.split(flag);
        if(resp[0].equals("msg")) {
            System.out.println(resp[1]);
            JOptionPane.showMessageDialog(null, resp[1]);
        } else if(resp[0].equals("file")) {
            File file = new File("/Users/faded828x/Desktop/ciphertext/src/main/java/client/files/" + resp[1]);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            System.out.println(resp[2]);
            fileOutputStream.write(Client.decrypt(resp[2]).getBytes());
            JOptionPane.showMessageDialog(null, "Download success");
        } else if(resp[0].equals("dir")) {
            // resp[1] ~ resp[n-1] is filename
            if(resp.length > 1)
                for(int i = 1; i < resp.length; i++)
                    Client.serverFiles.add(resp[i]);

//            System.out.println(Client.serverFiles);

        }

    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.flush();
    }

}
