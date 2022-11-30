import javax.swing.*;
import java.awt.*;

public class ClientInterface extends JPanel {
    String message;

    public void paintComponent(Graphics g){
        g.drawString(message, 290, 290);
    }

    public void setMessage(String mess){
        message= mess;
        repaint();
    }
}
