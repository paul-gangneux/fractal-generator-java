import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

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
    FractalButtonPanel fractalButtonPannel =
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

    JTextField nameField = new JTextField();
    nameField.setText("image");
    nameField.setColumns(8);

    JButton saveImageButton = new JButton("sauvegarder image");

    saveImageButton.addActionListener(
        e -> {
          ig.createImage("images/" + nameField.getText());
        });

    JButton saveTextButton = new JButton("créer fichier texte");

    saveTextButton.addActionListener(
        e -> {
          ig.createTextFile("images/" + nameField.getText());
        });

    saveImagePanel.add(new JLabel("nom:"));
    saveImagePanel.add(nameField);
    saveImagePanel.add(saveImageButton);
    saveImagePanel.add(saveTextButton);

    buttonPanel.add(imageButtonPanel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(fractalButtonPannel);
    buttonPanel.add(new JLabel(" "));
    buttonPanel.add(saveImagePanel);

    getContentPane().add(buttonPanel);
    getContentPane().add(fractal);

    setLocationRelativeTo(null); // centre la fenêtre
    pack();
    EventQueue.invokeLater(() -> setVisible(true));
  }
}
