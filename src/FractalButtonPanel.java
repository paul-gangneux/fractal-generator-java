import java.awt.Color;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.border.Border;

public class FractalButtonPanel extends JPanel {

  private transient ImageGenerator ig;
  private FractalImage fractal;
  private transient TwoDoublesToInt function;

  private JComboBox<String> fractalOptions = new JComboBox<>(new String[] {"Julia", "Mandelbrot"});
  private JSpinner iterations = new JSpinner();

  private JLabel funcLabel = new JLabel("fonction:");
  private JTextField juliaFunc = new JTextField();
  private JLabel errorFormat = new JLabel("/!\\ erreur format");

  boolean allowSpinner = true;

  public FractalButtonPanel(
      ImageGenerator imageGenerator, FractalImage fractalImage, TwoDoublesToInt fun, Gui gui) {
    this.function = fun;
    this.ig = imageGenerator;
    this.fractal = fractalImage;

    BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    this.setLayout(boxLayout);

    ((JSpinner.DefaultEditor) (iterations.getEditor())).getTextField().setColumns(4);
    iterations.setValue(function.maxValue());

    fractalOptions.addActionListener(
        e -> {
          allowSpinner = false;
          fractalOptions.getUI().setPopupVisible(fractalOptions, false);
          String s = (String) fractalOptions.getSelectedItem();
          switch (s) {
            case "Julia":
              Function<Complex, Complex> f = Julia.parseFxFromString(juliaFunc.getText());
              if (f != null) {
                errorFormat.setVisible(false);
                function = Julia.JuliaFactory((Integer) iterations.getValue(), 0, 2, f);
              }
              else {
                errorFormat.setVisible(true);
                gui.pack();
                function = new Julia();
              }
              funcLabel.setVisible(true);
              juliaFunc.setVisible(true);
              juliaFunc.setEnabled(true);
              break;
            case "Mandelbrot":
              function = new Mandelbrot();
              funcLabel.setVisible(false);
              juliaFunc.setVisible(false);
              juliaFunc.setEnabled(false);
              break;
            default:
              break;
          }
          iterations.setValue(function.maxValue());
          ig.setFractalGenerationFunction(function);
          fractal.recalculate();
          allowSpinner = true;
        });

    iterations.addChangeListener(
        e -> {
          if (!allowSpinner) return;
          allowSpinner = false;
          function.setMax((Integer) iterations.getValue());
          iterations.setValue(function.maxValue());
          ig.setFractalGenerationFunction(function);
          fractal.recalculate();
          allowSpinner = true;
        });

    juliaFunc.addActionListener(
        e -> {
          Function<Complex, Complex> f = Julia.parseFxFromString(juliaFunc.getText());
          if (f != null) {
            errorFormat.setVisible(false);
            function = Julia.JuliaFactory((Integer) iterations.getValue(), 0, 2, f);
            ig.setFractalGenerationFunction(function);
            fractal.recalculate();
          }
          else errorFormat.setVisible(true);
          gui.pack();
        });

    errorFormat.setVisible(false);

    add(new JLabel("type de fractale:"));
    add(fractalOptions);
    add(new JLabel("nb d'itérations:"));
    add(iterations);
    add(funcLabel);
    add(juliaFunc);
    add(errorFormat);

    Border bo = BorderFactory.createLineBorder(new Color(0.4f, 0.4f, 0.4f));
    Border bo2 = BorderFactory.createTitledBorder(bo, "fractale");
    this.setBorder(bo2);
  }
}
