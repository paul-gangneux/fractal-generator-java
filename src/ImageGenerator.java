import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;

public class ImageGenerator {

  private int width, height;
  private double zoom;
  private double shiftX, shiftY;
  private TwoDoublesToInt function;
  private BufferedImage image;
  private int antiAliasAmount; // taux d'anti-crénelage

  private double x1, y1;
  private double x2, y2;

  private double step;

  private int threshold; // pour le multithreading

  private boolean antiAliasing; // vrai si l'anti-crénelage est actif

  private HashMap<String, ThreeIntToInt> drawFunctionMap = new HashMap<>();
  private ThreeIntToInt currentDrawFunction;

  private class MainWork extends RecursiveAction {

    private int hmin;
    private int hmax;
    private double ox;
    private double oy;

    public MainWork(int hmin, int hmax, double ox, double oy) {
      this.hmin = hmin;
      this.hmax = hmax;
      this.ox = ox;
      this.oy = oy;
    }

    @Override
    protected void compute() {
      if (hmax - hmin > threshold) {
        invokeAll(
            new MainWork(hmin, (hmin + hmax) / 2, ox, oy),
            new MainWork((hmin + hmax) / 2, hmax, ox, oy));
      } else {
        int min = function.minValue();
        int max = function.maxValue();
        double x = ox;
        double yReset = oy + hmin * step * zoom;
        double y = yReset;

        for (int i = 0; i < width; i++) {
          for (int j = hmin; j < hmax; j++) {
            int val = function.doublesToInt(x, y);
            int col = currentDrawFunction.apply(val, min, max);
            image.setRGB(i, j, col);
            y += step * zoom;
          }
          x += step * zoom;
          y = yReset;
        }
      }
    }
  }

  private class AntiAliasingWork extends RecursiveAction {

    private int hmin, hmax;
    private transient BufferedImage buffer;

    public AntiAliasingWork(int hmin, int hmax, BufferedImage buffer) {
      this.hmin = hmin;
      this.hmax = hmax;
      this.buffer = buffer;
    }

    // ces fonction extraient les composantes R, G et B depuis la valeur d'un pixel

    private int extractRed(int color) {
      return (color & 0xFF0000) >> 16;
    }

    private int extractGreen(int color) {
      return (color & 0x00FF00) >> 8;
    }

    private int extractBlue(int color) {
      return (color & 0x0000FF);
    }

    @Override
    protected void compute() {
      if (hmax - hmin > threshold) {
        invokeAll(
            new AntiAliasingWork(hmin, (hmin + hmax) / 2, buffer),
            new AntiAliasingWork((hmin + hmax) / 2, hmax, buffer));
      } else {
        for (int i = 0; i < width; i++) {
          for (int j = hmin; j < hmax; j++) {

            int pR = 0;
            int pG = 0;
            int pB = 0;

            for (int a = 0; a < antiAliasAmount; a++) {
              for (int b = 0; b < antiAliasAmount; b++) {
                int rgb = image.getRGB(i * antiAliasAmount + a, j * antiAliasAmount + b);
                pR += extractRed(rgb);
                pG += extractGreen(rgb);
                pB += extractBlue(rgb);
              }
            }

            pR /= antiAliasAmount * antiAliasAmount;
            pG /= antiAliasAmount * antiAliasAmount;
            pB /= antiAliasAmount * antiAliasAmount;

            int pixel = rgbToInt(pR, pG, pB);
            buffer.setRGB(i, j, pixel);
          }
        }
      }
    }
  }

  public ImageGenerator() {
    // valeurs par défaut
    zoom = 1;
    shiftX = 0;
    shiftY = 0;
    antiAliasing = false;
    threshold = 64;
    antiAliasAmount = 2;

    x1 = -1;
    y1 = -1;
    x2 = 1;
    y2 = 1;

    setStep(0.005); // set height et width automatiquement
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // fonctions d'affichage par défaut :

    drawFunctionMap.put(
        "Teinte",
        (int val, int min, int max) -> {
          if (val < min || val > max) return 0x000000; // cas erreur
          if (val == max) return 0;
          return Color.HSBtoRGB((float) (val + min) / max, 0.8f, 0.7f);
        });

    drawFunctionMap.put(
        "Luminosité",
        (int val, int min, int max) -> {
          if (val > max || val < min) return 0xff0000; // cas erreur
          if (val == max) return 0xaaccff;
          val = 255 * (val + min) / max;
          return rgbToInt(val, val, val);
        });

    drawFunctionMap.put(
        null,
        (int a, int b, int c) -> {
          return 0;
        });

    setDrawFunction("Teinte");
  }

  // setters
  public void setWidth(int width) {
    if (width < 1) return;
    double d = ((width - this.width) * step) / 2;
    x1 -= d;
    x2 += d;
    this.width = width;
  }

  public void setHeight(int height) {
    if (height < 1) return;
    double d = ((height - this.height) * step) / 2;
    y1 -= d;
    y2 += d;
    this.height = height;
  }

  public void setZoom(double zoom) {
    if (zoom > 0) this.zoom = zoom;
  }

  public void setShiftX(double shiftX) {
    this.shiftX = shiftX;
  }

  public void setShiftY(double shiftY) {
    this.shiftY = shiftY;
  }

  public void setAntiAliasing(boolean antiAliasing) {
    this.antiAliasing = antiAliasing;
  }

  public void setPoint1(double x, double y) {
    if (x > x2 - step) x1 = x2 - step;
    else x1 = x;
    if (y > y2 - step) y1 = y2 - step;
    else y1 = y;
    width = (int) ((x2 - x1) / step);
    height = (int) ((y2 - y1) / step);
  }

  public void setPoint2(double x, double y) {
    if (x < x1 + step) x2 = x1 + step;
    else x2 = x;
    if (y < y1 + step) y2 = y1 + step;
    else y2 = y;
    width = (int) ((x2 - x1) / step);
    height = (int) ((y2 - y1) / step);
  }

  public void setStep(double d) {
    if (d <= 0) return;
    step = d;
    width = (int) ((x2 - x1) / d);
    height = (int) ((y2 - y1) / d);
    if (width <= 0) width = 1;
    if (height <= 0) height = 1;
  }

  public void setAntiAliasingAmount(int amount) {
    if (amount < 2) return;
    this.antiAliasAmount = amount;
  }

  public void setDrawFunction(String functionDesc) {
    currentDrawFunction = drawFunctionMap.get(functionDesc);
  }

  public void setFractalGenerationFunction(TwoDoublesToInt f) {
    function = f;
  }

  // getters
  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public double getZoom() {
    return zoom;
  }

  public double getShiftX() {
    return shiftX;
  }

  public double getShiftY() {
    return shiftY;
  }

  public boolean getAntiAliasing() {
    return antiAliasing;
  }

  public int getAntiAliasingAmount() {
    return antiAliasAmount;
  }

  public double getStep() {
    return step;
  }

  public BufferedImage getImage() {
    return image;
  }

  public double getX1() {
    return x1;
  }

  public double getY1() {
    return y1;
  }

  public double getX2() {
    return x2;
  }

  public double getY2() {
    return y2;
  }

  public TwoDoublesToInt getFunction() {
    return function;
  }

  public String getCurrentDrawFunctionString() {
    for (Map.Entry<String, ThreeIntToInt> entry : drawFunctionMap.entrySet()) {
      if (currentDrawFunction == entry.getValue()) return entry.getKey();
    }
    return null;
  }

  public String[] getDrawFunctionStrings() {
    String[] a = new String[drawFunctionMap.size() - 1];
    int i = 0;
    for (String key : drawFunctionMap.keySet()) {
      if (key != null) {
        a[i++] = key;
      }
    }
    return a;
  }

  public BufferedImage getImageWithShift(int x, int y) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int xstart = (x > 0) ? x : 0;
    int ystart = (y > 0) ? y : 0;
    int xend = (width + x < width) ? width + x : width;
    int yend = (height + y < height) ? height + y : height;
    for (int i = xstart; i < xend; i++) {
      for (int j = ystart; j < yend; j++) {
        img.setRGB(i, j, image.getRGB(i - x, j - y));
      }
    }
    return img;
  }

  private int rgbToInt(int r, int g, int b) {
    return (r << 16) | (g << 8) | b;
  }

  // calculate fractal and put it in buffer
  public void generateBuffer() {

    if (function == null) return;
    BufferedImage smol = null;

    int threadsNb = Runtime.getRuntime().availableProcessors();

    if (antiAliasing) {
      smol = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      height *= antiAliasAmount;
      width *= antiAliasAmount;
      step /= antiAliasAmount;
    }

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    double ox = x1 + (1 - zoom) * (x2 - x1) / 2 + shiftX;
    double oy = y1 + (1 - zoom) * (y2 - y1) / 2 + shiftY;

    threshold = height / threadsNb;
    if (threshold < 10) threshold = 10;
    RecursiveAction work = new MainWork(0, height, ox, oy);
    ForkJoinPool pool = new ForkJoinPool();

    pool.invoke(work);

    if (antiAliasing) {
      height /= antiAliasAmount;
      width /= antiAliasAmount;
      step *= antiAliasAmount;
      threshold = height / threadsNb;
      RecursiveAction aawork = new AntiAliasingWork(0, height, smol);
      ForkJoinPool pool2 = new ForkJoinPool();
      pool2.invoke(aawork);
      image = smol;
    }
  }

  // creates image from buffer
  public void createImage(String pathname) {
    File file = new File(pathname);
    //TODO : s'assurer que le nom finisse en .png
    try {
      ImageIO.write(image, "PNG", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void applyZoom() {
    int h = getHeight();
    int w = getWidth();
    setStep(step * zoom);
    setHeight(h);
    setWidth(w);
    zoom = 1;
  }

  public void applyShift() {
    x1 += shiftX;
    x2 += shiftX;
    y1 += shiftY;
    y2 += shiftY;
    shiftX = 0;
    shiftY = 0;
  }

  // fonctions non utilisés

  // somme les valeurs R, G ou B de plusieurs couleurs, puis renvoie leur moyenne
  /*
  private int averageRed(int... colors) {
    int sum = 0;
    for (int c : colors) {
      sum += extractRed(c);
    }
    return sum / colors.length;
  }

  private int averageGreen(int... colors) {
    int sum = 0;
    for (int c : colors) {
      sum += extractGreen(c);
    }
    return sum / colors.length;
  }

  private int averageBlue(int... colors) {
    int sum = 0;
    for (int c : colors) {
      sum += extractBlue(c);
    }
    return sum / colors.length;
  }
  */

}
