package s05;



import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 多人聊天
 *
 * @author wangzhaobin
 * @date 2022/4/19 上午2:42
 */
public class ClientFrame extends Frame {

    TextArea ta = new TextArea();
    TextField tf = new TextField();

    Client c = null;

    public ClientFrame(){
        this.setSize(600,400);
        this.setLocation(100,20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //把字符串发送到服务器
                c.send(tf.getText());
                ta.setText(ta.getText() + tf.getText() + "\n");
                tf.setText("");
            }
        });
        this.setVisible(true);
        connectToServer();

    }

    private void connectToServer() {
        //把Client初始化，然后调用connect 连接到服务器
        c = new Client();
        c.connect();
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
