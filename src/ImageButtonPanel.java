import java.awt.Color;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.Border;

public class ImageButtonPanel extends JPanel {

  private transient ImageGenerator ig;
  private FractalImage fractal;
  // tous les boutons / champs / autre :

  // largeur et hauteur
  private JSpinner hspin = new JSpinner();
  private JSpinner wspin = new JSpinner();

  // zoom
  private JFormattedTextField zoomLevel = makeTextFieldForFloat(6);
  private JButton zoomMin = new JButton("-");
  private JButton zoomPlus = new JButton("+");

  // anti-aliasing
  private JComboBox<String> antiAliBox;
  // fonction d'affichage
  private JComboBox<String> drawOptionBox;

  // pas de discrétisation
  private JFormattedTextField step = makeTextFieldForFloat(8);

  // points de début et de fin du rectangle
  private JFormattedTextField point1r = makeTextFieldForFloat(8);
  private JFormattedTextField point1i = makeTextFieldForFloat(8);
  private JFormattedTextField point2r = makeTextFieldForFloat(8);
  private JFormattedTextField point2i = makeTextFieldForFloat(8);

  /*
  les spinners effectuent leurs actions même quand ils sont
  modifiés par le programme et non pas l'utilisateur,
  ce booléen permet de désactiver leurs actions pour
  éviter les comportements indésirables
  */
  private boolean spinnersAllowed = true;

  public ImageButtonPanel(ImageGenerator imageGenerator, FractalImage fractalImage) {
    this.ig = imageGenerator;
    this.fractal = fractalImage;

    BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    this.setLayout(boxLayout);

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
    zoomButtons.add(new JLabel("taux: "));
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

    updateValues();

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

    this.add(new JLabel("dimensions:"));
    this.add(sizeButtons);
    this.add(new JLabel("zoom:"));
    this.add(zoomButtons);
    this.add(new JLabel("anti-crénelage:"));
    this.add(antiAliBox);
    this.add(new JLabel("pas de disctétisation:"));
    this.add(step);
    this.add(new JLabel("coordonnées complexes:"));
    this.add(new JLabel("point 1:"));
    this.add(coordButtons1);
    this.add(new JLabel("point 2:"));
    this.add(coordButtons2);
    this.add(new JLabel("fonction d'affichage:"));
    this.add(drawOptionBox);

    Border bo = BorderFactory.createLineBorder(new Color(0.4f, 0.4f, 0.4f));
    Border bo2 = BorderFactory.createTitledBorder(bo, "image");
    this.setBorder(bo2);
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

  public void updateValues() {
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
