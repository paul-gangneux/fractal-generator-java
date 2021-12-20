import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {

  public Gui(ImageGenerator ig) {

    setFont(new Font("SansSerif", Font.PLAIN, 30));
    setMinimumSize(new Dimension(700, 600));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("fractals");
    setLayout(new FlowLayout());

    FractalImage fractal = new FractalImage(ig, this);
    ImageButtonPanel imageButtonPanel = new ImageButtonPanel(ig, fractal);
    fractal.setImageButtonPanel(imageButtonPanel);
    FractalButtonPanel fractalButtonPannel = new FractalButtonPanel(ig, fractal, ig.getFunction());

    JPanel buttonPanel = new JPanel();
    BoxLayout b = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(b);

    buttonPanel.add(imageButtonPanel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(fractalButtonPannel);

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    pack();
    EventQueue.invokeLater(() -> setVisible(true));
  }
}
