import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Gui extends JFrame {

  private final transient ImageGenerator ig;
  private FractalImage fractal;
  private JFrame thisJframe = this;

  private class FractalImage extends JLabel implements MouseInputListener {

    private transient BufferedImage img;
    private boolean calculating;

    private int anchorX = 0, anchorY = 0;
    private int newX = 0, newY = 0;

    public FractalImage() {
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      addMouseListener(this);
      addMouseMotionListener(this);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      calculating = false;
    }

    private void redraw() {
      img = ig.getImageWithShift(newX - anchorX, newY - anchorY);
      setIcon(new ImageIcon(img));
    }

    private void recalculateAfterShift() {
      ig.setShiftX(ig.getShiftX() - (newX - anchorX) * ig.getZoom() * ig.getStep());
      ig.setShiftY(ig.getShiftY() - (newY - anchorY) * ig.getZoom() * ig.getStep());
      recalculate();
      anchorX = 0;
      anchorY = 0;
      newX = 0;
      newY = 0;
    }

    public void recalculate() {
      if (calculating) return;
      calculating = true;
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      ig.generateBuffer();
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      calculating = false;
      thisJframe.pack();
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

    // TODO : prendre en compte tous les changements avant de recalculer la fractale
    setFont(new Font("SansSerif", Font.PLAIN, 30));
    setMinimumSize(new Dimension(700, 600));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("fractals");
    setLayout(new FlowLayout());
    fractal = new FractalImage();

    // panel ayant les options à gauche
    JPanel buttonPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(boxLayout);

    // largeur et hauteurs
    JPanel sizeButtons = new JPanel();
    JSpinner hspin = new JSpinner();
    JSpinner wspin = new JSpinner();

    // change la taille par défaut de l'entrée texte
    ((JSpinner.DefaultEditor) (hspin.getEditor())).getTextField().setColumns(4);
    ((JSpinner.DefaultEditor) (wspin.getEditor())).getTextField().setColumns(4);

    hspin.setValue(ig.getHeight());
    wspin.setValue(ig.getWidth());

    hspin.addChangeListener(
        event -> {
          hspin.setEnabled(false);
          int i = (Integer) hspin.getValue();
          ig.setHeight(i);
          hspin.setValue(ig.getHeight()); // au cas où l'utilisateur entre une donnée non conforme
          fractal.recalculate();
          hspin.setEnabled(true);
        });

    wspin.addChangeListener(
        event -> {
          wspin.setEnabled(false);
          int i = (Integer) wspin.getValue();
          ig.setWidth(i);
          wspin.setValue(ig.getWidth()); // au cas où l'utilisateur entre une donnée non conforme
          fractal.recalculate();
          wspin.setEnabled(true);
        });

    sizeButtons.add(new JLabel("H"));
    sizeButtons.add(hspin);
    sizeButtons.add(new JLabel(" L"));
    sizeButtons.add(wspin);

    // bouttons zoom
    JPanel zoomButtons = new JPanel();
    JFormattedTextField zoomLevel = new JFormattedTextField(new DecimalFormat("#.##############"));
    JButton zoomMin = new JButton("-");
    JButton zoomPlus = new JButton("+");

    zoomLevel.setValue(ig.getZoom());
    zoomLevel.setColumns(10);

    zoomLevel.addActionListener(
        action -> {
          disableAll(zoomLevel, zoomMin, zoomPlus);
          double f = Double.parseDouble(zoomLevel.getText());
          ig.setZoom(f);
          zoomLevel.setValue(ig.getZoom());
          fractal.recalculate();
          enableAll(zoomLevel, zoomMin, zoomPlus);
        });

    zoomMin.addActionListener(
        action -> {
          disableAll(zoomLevel, zoomMin, zoomPlus);
          double f = ig.getZoom() * 0.8;
          ig.setZoom(f);
          zoomLevel.setValue(ig.getZoom());
          fractal.recalculate();
          enableAll(zoomLevel, zoomMin, zoomPlus);
        });

    zoomPlus.addActionListener(
        action -> {
          disableAll(zoomLevel, zoomMin, zoomPlus);
          double f = ig.getZoom() * 1.25;
          ig.setZoom(f);
          zoomLevel.setValue(ig.getZoom());
          fractal.recalculate();
          enableAll(zoomLevel, zoomMin, zoomPlus);
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
          // permet les inputs utilisateurs pendant le chargement
          antiAliBox.getUI().setPopupVisible(antiAliBox, false);
          fractal.recalculate();
        });

    // pas de disctétisation
    JFormattedTextField step = new JFormattedTextField(new DecimalFormat("#.##############"));
    step.setValue(ig.getStep());
    step.setColumns(10);

    step.addActionListener(
        action -> {
          step.setEnabled(false);
          double f = Double.parseDouble(step.getText());
          ig.setStep(f);
          step.setValue(ig.getStep());
          hspin.setValue(ig.getHeight());
          wspin.setValue(ig.getWidth());
          fractal.recalculate();
          step.setEnabled(true);
        });

    String[] drawOptions = {"Teinte", "Luminosité"};
    JComboBox<String> drawOptionBox = new JComboBox<>(drawOptions);

    drawOptionBox.addActionListener(
        event -> {
          int i = drawOptionBox.getSelectedIndex();
          if (i <= 0) ig.setValueToColorDefaultFunction();
          else ig.setValueToColorDefaultFunction2();
          // permet les inputs utilisateurs pendant le chargement
          drawOptionBox.getUI().setPopupVisible(drawOptionBox, false);
          fractal.recalculate();
        });

    // TODO : refactor
    JPanel coordButtons1 = new JPanel();
    JPanel coordButtons2 = new JPanel();
    JFormattedTextField point1r = new JFormattedTextField(new DecimalFormat("#.##############"));
    JFormattedTextField point1i = new JFormattedTextField(new DecimalFormat("#.##############"));
    JFormattedTextField point2r = new JFormattedTextField(new DecimalFormat("#.##############"));
    JFormattedTextField point2i = new JFormattedTextField(new DecimalFormat("#.##############"));
    point1r.setColumns(8);
    point1i.setColumns(8);
    point2r.setColumns(8);
    point2i.setColumns(8);

    point1r.setValue(ig.getX1());
    point1i.setValue(ig.getY1());
    point2r.setValue(ig.getX2());
    point2i.setValue(ig.getY2());

    point1r.addActionListener(
        action -> {
          double f = Double.parseDouble(point1r.getText());
          ig.setPoint1(f, ig.getY1());
          point1r.setValue(ig.getX1());
          fractal.recalculate();
        });

    point1i.addActionListener(
        action -> {
          double f = Double.parseDouble(point1i.getText());
          ig.setPoint1(ig.getX1(), f);
          point1i.setValue(ig.getY1());
          fractal.recalculate();
        });

    point2r.addActionListener(
        action -> {
          double f = Double.parseDouble(point2r.getText());
          ig.setPoint2(f, ig.getY2());
          point2r.setValue(ig.getX2());
          fractal.recalculate();
        });

    point2i.addActionListener(
        action -> {
          double f = Double.parseDouble(point2i.getText());
          ig.setPoint2(ig.getX2(), f);
          point2i.setValue(ig.getY2());
          fractal.recalculate();
        });

    coordButtons1.add(new JLabel("R"));
    coordButtons1.add(point1r);
    coordButtons1.add(new JLabel(" I"));
    coordButtons1.add(point1i);
    coordButtons2.add(new JLabel("R"));
    coordButtons2.add(point2r);
    coordButtons2.add(new JLabel(" I"));
    coordButtons2.add(point2i);

    buttonPanel.add(new JLabel("dimensions:"));
    buttonPanel.add(sizeButtons);
    buttonPanel.add(new JLabel("zoom:"));
    buttonPanel.add(zoomButtons);
    buttonPanel.add(new JLabel("anti-crénelage:"));
    buttonPanel.add(antiAliBox);
    buttonPanel.add(new JLabel("pas de disctétisation:"));
    buttonPanel.add(step);
    buttonPanel.add(new JLabel("coordonnées complexes:"));
    buttonPanel.add(new JLabel("point 1:"));
    buttonPanel.add(coordButtons1);
    buttonPanel.add(new JLabel("point 2:"));
    buttonPanel.add(coordButtons2);
    buttonPanel.add(new JLabel("fonction d'affichage:"));
    buttonPanel.add(drawOptionBox);

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    pack();
    EventQueue.invokeLater(() -> setVisible(true));
  }

  private void enableAll(JComponent... components) {
    for (JComponent c : components) {
      c.setEnabled(true);
    }
  }

  private void disableAll(JComponent... components) {
    for (JComponent c : components) {
      c.setEnabled(false);
    }
  }
}
