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

  // tous les boutons / champs / autre :

  // largeur et hauteur
  JSpinner hspin = new JSpinner();
  JSpinner wspin = new JSpinner();

  // zoom
  JFormattedTextField zoomLevel = makeTextFieldForFloat(10);
  JButton zoomMin = new JButton("-");
  JButton zoomPlus = new JButton("+");

  // anti-aliasing
  JComboBox<String> antiAliBox;
  // fonction d'affichage
  JComboBox<String> drawOptionBox;

  // pas de discrétisation
  JFormattedTextField step = makeTextFieldForFloat(10);

  // points de début et de fin du rectangle
  JFormattedTextField point1r = makeTextFieldForFloat(8);
  JFormattedTextField point1i = makeTextFieldForFloat(8);
  JFormattedTextField point2r = makeTextFieldForFloat(8);
  JFormattedTextField point2i = makeTextFieldForFloat(8);

  /*
  les spinners effectuent leurs actions même quand ils sont
  modifiés par le programme et non pas l'utilisateur,
  ce booléen permet de désactiver leurs actions pour
  éviter les comportements indésirables
  */
  boolean spinnersAllowed = true;

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
      ig.applyShift();
      ig.applyZoom();
      updateAllValuesOnGUI();
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      ig.generateBuffer();
      thisJframe.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      img = ig.getImage();
      setIcon(new ImageIcon(img));
      calculating = false;
      thisJframe.pack();
      // thisJframe.setLocationRelativeTo(null);
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

    // panel ayant les options à gauche
    JPanel buttonPanel = new JPanel();
    BoxLayout boxLayout = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(boxLayout);

    // panel pour largeur et hauteurs
    JPanel sizeButtons = new JPanel();

    // change la taille par défaut de l'entrée texte
    ((JSpinner.DefaultEditor) (hspin.getEditor())).getTextField().setColumns(4);
    ((JSpinner.DefaultEditor) (wspin.getEditor())).getTextField().setColumns(4);

    sizeButtons.add(new JLabel("H"));
    sizeButtons.add(hspin);
    sizeButtons.add(new JLabel(" L"));
    sizeButtons.add(wspin);

    // bouttons zoom
    JPanel zoomButtons = new JPanel();
    zoomButtons.add(zoomLevel);
    zoomButtons.add(zoomPlus);
    zoomButtons.add(zoomMin);

    // bouton anti-aliasing
    String[] aaOptions = {"aucun", "x2", "x3", "x4", "x5", "x6", "x7", "x8"};
    antiAliBox = new JComboBox<>(aaOptions);

    int ind;
    if (!ig.getAntiAliasing()) ind = 0;
    else ind = ig.getAntiAliasingAmount() - 1;
    antiAliBox.setSelectedIndex(ind);

    drawOptionBox = new JComboBox<>(ig.getDrawFunctionStrings());
    drawOptionBox.setSelectedItem(ig.getCurrentDrawFunctionString());

    JPanel coordButtons1 = new JPanel();
    JPanel coordButtons2 = new JPanel();

    zoomLevel.setValue(2);

    updateAllValuesOnGUI();

    zoomLevel.addActionListener(
        event -> {
          double d = Double.parseDouble(zoomLevel.getText());
          if (d < 1) {
            d = 1;
            zoomLevel.setValue(1);
          }
        });

    zoomMin.addActionListener(
        event -> {
          double d = Double.parseDouble(zoomLevel.getText());
          if (d < 1) {
            d = 1;
            zoomLevel.setValue(1);
          }
          ig.setZoom(d);
          onAction();
        });

    zoomPlus.addActionListener(
        event -> {
          double d = Double.parseDouble(zoomLevel.getText());
          if (d < 1) {
            d = 1;
            zoomLevel.setValue(1);
          }
          ig.setZoom(1 / d);
          onAction();
        });

    addActionListenerToTextFields(zoomLevel, step, point1r, point2r, point1i, point2i);
    addActionListenerToSpinners(hspin, wspin);

    antiAliBox.addActionListener(
        event -> {
          // permet les inputs utilisateurs pendant le chargement
          antiAliBox.getUI().setPopupVisible(antiAliBox, false);
          onAction();
        });
    drawOptionBox.addActionListener(
        event -> {
          drawOptionBox.getUI().setPopupVisible(antiAliBox, false);
          onAction();
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

  private void setEnabledForAll(boolean bool) {
    hspin.setEnabled(bool);
    wspin.setEnabled(bool);
    zoomLevel.setEnabled(bool);
    step.setEnabled(bool);
    point1r.setEnabled(bool);
    point1i.setEnabled(bool);
    point2r.setEnabled(bool);
    point2i.setEnabled(bool);
  }

  private void updateAllValuesInIG() {
    ig.setHeight((Integer) hspin.getValue());
    ig.setWidth((Integer) wspin.getValue());
    ig.setPoint1(Double.parseDouble(point1r.getText()), Double.parseDouble(point1i.getText()));
    ig.setPoint2(Double.parseDouble(point2r.getText()), Double.parseDouble(point2i.getText()));
    ig.setStep(Double.parseDouble(step.getText()));

    int aai = antiAliBox.getSelectedIndex();
    if (aai <= 0) ig.setAntiAliasing(false);
    else {
      ig.setAntiAliasing(true);
      ig.setAntiAliasingAmount(aai + 1);
    }
    String s = (String) drawOptionBox.getSelectedItem();
    ig.setDrawFunction(s);
  }

  private void updateAllValuesOnGUI() {
    hspin.setValue(ig.getHeight());
    wspin.setValue(ig.getWidth());
    point1r.setValue(ig.getX1());
    point1i.setValue(ig.getY1());
    point2r.setValue(ig.getX2());
    point2i.setValue(ig.getY2());
    step.setValue(ig.getStep());
    antiAliBox.getUI().setPopupVisible(antiAliBox, false);
    drawOptionBox.getUI().setPopupVisible(antiAliBox, false);
  }

  private void onAction() {
    spinnersAllowed = false;
    setEnabledForAll(false);
    updateAllValuesInIG();
    fractal.recalculate();
    setEnabledForAll(true);
    spinnersAllowed = true;
  }

  // fait pour hspin et wspin. c'est bricolé
  private void addActionListenerToSpinners(JSpinner... components) {
    for (JSpinner c : components) {
      c.addChangeListener(
          event -> {
            if (!spinnersAllowed) return;
            setEnabledForAll(false);
            updateAllValuesInIG();
            ig.setHeight((Integer) hspin.getValue());
            ig.setWidth((Integer) wspin.getValue());
            fractal.recalculate();
            setEnabledForAll(true);
          });
    }
  }

  private void addActionListenerToTextFields(JFormattedTextField... components) {
    for (JFormattedTextField c : components) {
      c.addActionListener(event -> onAction());
    }
  }

  private JFormattedTextField makeTextFieldForFloat(int columns) {
    JFormattedTextField field =
    new JFormattedTextField(new DecimalFormat("#.##############################"));
    field.setColumns(columns);
    field.setValue(0.0);
    return field;
  }
}
