import java.awt.Color;
import javax.swing.*;
import javax.swing.border.Border;

public class FractalButtonPanel extends JPanel {

  private transient ImageGenerator ig;
  private FractalImage fractal;
  private transient TwoDoublesToInt function;

  private JComboBox<String> fractalOptions = new JComboBox<>(new String[] {"Julia", "Mandelbrot"});
  private JSpinner iterations = new JSpinner();

  boolean allowSpinner = true;

  public FractalButtonPanel(
      ImageGenerator imageGenerator, FractalImage fractalImage, TwoDoublesToInt fun) {
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
              function = new Julia();
              break;
            case "Mandelbrot":
              function = new Mandelbrot();
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

    add(new JLabel("type de fractale:"));
    add(fractalOptions);
    add(new JLabel("nb d'itérations:"));
    add(iterations);

    Border bo = BorderFactory.createLineBorder(new Color(0.4f, 0.4f, 0.4f));
    Border bo2 = BorderFactory.createTitledBorder(bo, "fractale");
    this.setBorder(bo2);
  }
}
