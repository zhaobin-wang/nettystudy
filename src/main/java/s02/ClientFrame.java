package s02;

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

    public ClientFrame(){
        this.setSize(600,400);
        this.setLocation(100,20);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //把字符串发送到服务器，服务器收到之后转发到所有的客户端
                ta.setText(ta.getText() + tf.getText() + "\n");
                tf.setText("");
            }
        });
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
