import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Gui extends JFrame {

  private transient ImageGenerator ig;
  private JPanel buttonPanel = new JPanel();
  private FractalImage fractal;

  private class FractalImage extends JLabel implements MouseInputListener {

    transient BufferedImage img;

    int anchorX = 0, anchorY = 0;
    int newX = 0, newY = 0;

    public FractalImage() {
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      addMouseListener(this);
      addMouseMotionListener(this);
    }

    public void redraw() {
      img = ig.getImageWithShift(newX - anchorX, newY - anchorY);
      setIcon(new ImageIcon(img));
    }

    public void recalculateAfterShift() {
      ig.setShiftX(ig.getShiftX() - (newX - anchorX) * ig.getZoom() * ig.getStep());
      ig.setShiftY(ig.getShiftY() - (newY - anchorY) * ig.getZoom() * ig.getStep());
      recalculate();
      anchorX = 0;
      anchorY = 0;
      newX = 0;
      newY = 0;
    }

    public void recalculate() {
      ig.generateBuffer();
      img = ig.getImage();
      setIcon(new ImageIcon(img));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      // nothing
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      // nothing
    }

    @Override
    public void mouseExited(MouseEvent e) {
      // nothing
    }

    @Override
    public void mousePressed(MouseEvent e) {
      anchorX = e.getX();
      anchorY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      recalculateAfterShift();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      newX = e.getX();
      newY = e.getY();
      redraw();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      // nothing
    }
  }

  public Gui(ImageGenerator ig) {

    this.ig = ig;

    setFont(new Font("SansSerif", Font.PLAIN, 30));
    setMinimumSize(new Dimension(700, 600));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("fractals");
    setLayout(new FlowLayout());
    fractal = new FractalImage();

    BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(boxLayout);
    buttonPanel.add(new JButton("test1"));
    buttonPanel.add(new JButton("test2"));

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    EventQueue.invokeLater(() -> setVisible(true));
  }
}
