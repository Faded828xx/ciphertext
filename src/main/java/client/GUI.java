package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * @author faded828x
 * @date 2022/5/16
 */
public class GUI extends JFrame {

    private int width = 400;
    private int height = 600;
    private JButton uploadButton;
    private JButton refreshButton;
    private JPanel staffPanel;
    private JScrollPane jScrollPane;
    private int Y;
    private Client client;

    public GUI() {

        initVariable();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width, height);
        this.setTitle("重复文件检测系统");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

    }

    private void initVariable() {
        this.client = new Client("localhost", 8082);
        client.directory();

        jScrollPane = new JScrollPane();
        this.getContentPane().add(jScrollPane);

        staffPanel = new JPanel();
        staffPanel.setLayout(null);
        staffPanel.setOpaque(false);
        staffPanel.setPreferredSize(new Dimension(width, height));

        jScrollPane.setViewportView(staffPanel);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.getViewport().setOpaque(false);
        jScrollPane.setOpaque(false);
        renderTop();

    }



    private void renderTop() {
        Y = 0;
        staffPanel.removeAll();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3, 3, 10));
        this.uploadButton = new JButton("upload");
        this.refreshButton = new JButton("refresh");
        panel.add(uploadButton);
        panel.add(refreshButton);
        panel.setBounds(2, 0, width, 30);
        this.staffPanel.add(panel);
        Y += 30;
        addEventListener();
    }

    private void addEventListener() {
        this.uploadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser f = new JFileChooser();
                f.showOpenDialog(null);
                File selectedFile = f.getSelectedFile();
                client.upload(selectedFile.getAbsolutePath());
                client.directory();
            }
        });
        this.refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                client.directory();
                renderTop();
//                System.out.println("add");
                for(String filename : Client.serverFiles) {
                    addToList(filename);
                }
            }
        });
    }

    public void addToList(String filename) {
        JLabel filenameLabel = new JLabel(filename);
        JButton downloadButton = new JButton("download");
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3, 0, 0));
        panel.add(filenameLabel);
        panel.add(downloadButton);

        panel.setBounds(2, Y, width, 30);
        this.staffPanel.add(panel);
        Y += 30;

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(Color.orange);
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.white);
            }
        });

        downloadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                client.download(filenameLabel.getText());
            }
        });

    }



    public static void main(String[] args) {
        new GUI();
    }

}
