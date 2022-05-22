import org.junit.Test;
import server.Server;

import java.sql.*;

/**
 * @author faded828x
 * @date 2022/5/22
 */
public class SQLTest {

    @Test
    public void sqlTest() {
//        System.out.println(dir());
//        System.out.println(upload("name2", "checksum2", "content2"));
        System.out.println(download("name1"));
    }

    private String dir() {
        Connection connection;
        String sql = "select file_name from file";
        StringBuilder response = new StringBuilder("dir");
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/faded828x/Desktop/ciphertext/src/main/java/server/serverFiles.db");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("file_name");
                response.append("@@@").append(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    private String upload(String name, String checksum, String content) {
        Connection connection;
        String response = "msg@@@";
        String sql = "insert into file ('file_name', 'file_checksum', 'file_content') " +
                "values ('" + name + "','" + checksum + "','" + content + "');";
        System.out.println(sql);
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/faded828x/Desktop/ciphertext/src/main/java/server/serverFiles.db");
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response + "File upload success!";
    }


    private String download(String name) {
        Connection connection;
        String response = "file@@@";
        String sql = "select file_content from file where file_name = '" + name + "';";
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/faded828x/Desktop/ciphertext/src/main/java/server/serverFiles.db");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                response += resultSet.getString("file_content");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

}
