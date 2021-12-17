import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Gui extends JFrame {

  private final transient ImageGenerator ig;
  private FractalImage fractal;
  JFrame thisJframe = this;

  private class FractalImage extends JLabel implements MouseInputListener {

    transient BufferedImage img;
    boolean calculating;

    int anchorX = 0, anchorY = 0;
    int newX = 0, newY = 0;

    public FractalImage() {
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      addMouseListener(this);
      addMouseMotionListener(this);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      calculating = false;
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
      calculating = true;
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      ig.generateBuffer();
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      calculating = false;
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
      if (calculating) return;
      setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
      anchorX = e.getX();
      anchorY = e.getY();
      newX = e.getX();
      newY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (calculating) return;
      recalculateAfterShift();
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (calculating) return;
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

    JPanel buttonPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(boxLayout);

    // bouttons zoom
    JPanel zoomButtons = new JPanel();

    JFormattedTextField zoomLevel;
    zoomLevel = new JFormattedTextField(new DecimalFormat("#.##############"));
    zoomLevel.setValue(ig.getZoom());
    zoomLevel.setColumns(10);

    zoomLevel.addActionListener(
        action -> {
          double f = Double.parseDouble(zoomLevel.getText());
          ig.setZoom(f);
          fractal.recalculate();
        });

    JButton zoomMin = new JButton("-");
    zoomMin.addActionListener(
        action -> {
          double f = ig.getZoom() * 0.8;
          zoomLevel.setValue(f);
          ig.setZoom(f);
          fractal.recalculate();
        });

    JButton zoomPlus = new JButton("+");
    zoomPlus.addActionListener(
        action -> {
          double f = ig.getZoom() * 1.25;
          zoomLevel.setValue(f);
          ig.setZoom(f);
          fractal.recalculate();
        });

    zoomButtons.add(zoomLevel);
    zoomButtons.add(zoomPlus);
    zoomButtons.add(zoomMin);

    // bouton anti-aliasing
    String[] aaOptions = {"aucun", "x2", "x3", "x4", "x5", "x6", "x7", "x8"};
    JComboBox<String> antiAliBox = new JComboBox<>(aaOptions);

    int ind;
    if (!ig.getAntiAliasing()) ind = 0;
    else ind = ig.getAntiAliasingAmount() - 1;
    antiAliBox.setSelectedIndex(ind);

    antiAliBox.addActionListener(
        event -> {
          int i = antiAliBox.getSelectedIndex();
          if (i <= 0) ig.setAntiAliasing(false);
          else {
            ig.setAntiAliasing(true);
            ig.setAntiAliasingAmount(i + 1);
          }
          fractal.recalculate();
        });

    buttonPanel.add(new JLabel("zoom:"));
    buttonPanel.add(zoomButtons);
    buttonPanel.add(new JLabel("anti-crénelage:"));
    buttonPanel.add(antiAliBox);
    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    EventQueue.invokeLater(() -> setVisible(true));
  }
}
