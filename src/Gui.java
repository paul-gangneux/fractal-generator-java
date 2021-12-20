import java.awt.*;
import javax.swing.*;

public class Gui extends JFrame {

  // private final transient ImageGenerator ig;
  private FractalImage fractal;
  private ImageButtonPanel buttonPanel;

  public Gui(ImageGenerator ig) {

    // this.ig = ig;
    setFont(new Font("SansSerif", Font.PLAIN, 30));
    setMinimumSize(new Dimension(700, 600));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("fractals");
    setLayout(new FlowLayout());

    fractal = new FractalImage(ig, this);
    buttonPanel = new ImageButtonPanel(ig, fractal);
    fractal.setImageButtonPanel(buttonPanel);

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    pack();
    EventQueue.invokeLater(() -> setVisible(true));
  }
}
