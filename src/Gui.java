import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class Gui extends JFrame {

  ImageButtonPanel imageButtonPanel;
  FractalButtonPanel fractalButtonPannel;

  JTextField nameField;
  JButton saveImageButton;
  JButton saveTextButton;

  JButton interruptButton;

  public Gui(ImageGenerator ig) {

    setFont(new Font("SansSerif", Font.PLAIN, 30));
    setMinimumSize(new Dimension(700, 600));
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setTitle("fractals");
    setLayout(new FlowLayout());

    FractalImage fractal = new FractalImage(ig, this);
    imageButtonPanel = new ImageButtonPanel(ig, fractal);
    fractalButtonPannel =
        new FractalButtonPanel(ig, fractal, ig.getFunction(), this);

    JPanel buttonPanel = new JPanel();
    BoxLayout b = new BoxLayout(buttonPanel, BoxLayout.Y_AXIS);
    buttonPanel.setLayout(b);

    // le panel pour enregistrer les images. comme il est simple je le fais ici :
    JPanel saveImagePanel = new JPanel();
    BoxLayout b2 = new BoxLayout(saveImagePanel, BoxLayout.Y_AXIS);
    saveImagePanel.setLayout(b2);
    Border bo = BorderFactory.createLineBorder(new Color(0.4f, 0.4f, 0.4f));
    Border bo2 = BorderFactory.createTitledBorder(bo, "sauvegarde");
    saveImagePanel.setBorder(bo2);

    nameField = new JTextField();
    nameField.setText("image");
    nameField.setColumns(8);

    saveImageButton = new JButton("sauvegarder image");

    saveImageButton.addActionListener(
        e -> {
          ig.createImage("images/" + nameField.getText());
        });

    saveTextButton = new JButton("créer fichier texte");

    saveTextButton.addActionListener(
        e -> {
          ig.createTextFile("images/" + nameField.getText());
        });

    interruptButton = new JButton("annuler");

    interruptButton.addActionListener(
        e -> {
          ig.interrupt();
        });

    interruptButton.setEnabled(false);

    saveImagePanel.add(new JLabel("nom:"));
    saveImagePanel.add(nameField);
    saveImagePanel.add(saveImageButton);
    saveImagePanel.add(saveTextButton);

    buttonPanel.add(imageButtonPanel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(fractalButtonPannel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(saveImagePanel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(interruptButton);

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    pack();
    EventQueue.invokeLater(() -> setVisible(true));
  }

  public void disableAll() {
    imageButtonPanel.disableAll();
    fractalButtonPannel.disableAll();
    nameField.setEnabled(false);
    saveImageButton.setEnabled(false);
    saveTextButton.setEnabled(false);
    interruptButton.setEnabled(true);
  }

  public void enableAll() {
    imageButtonPanel.enableAll();
    fractalButtonPannel.enableAll();
    nameField.setEnabled(true);
    saveImageButton.setEnabled(true);
    saveTextButton.setEnabled(true);
    interruptButton.setEnabled(false);
  }

  public void updateValues() {
    imageButtonPanel.updateValues();
  }
}
