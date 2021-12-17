import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;

public class ImageGenerator {

  private int width, height;
  private double zoom;
  private double shiftX, shiftY;
  private TwoDoublesToInt function;
  private BufferedImage image;
  private int aa_amount; // taux d'anti-crénelage

  private double x1, y1;
  private double x2, y2;

  private double step;

  private int threshold; // pour le multithreading

  private boolean antiAliasing; // vrai si l'anti-crénelage est actif

  private ThreeIntToInt valueToColor;

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
            int col = valueToColor.apply(val, min, max);
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

            for (int a = 0; a < aa_amount; a++) {
              for (int b = 0; b < aa_amount; b++) {
                int rgb = image.getRGB(i * aa_amount + a, j * aa_amount + b);
                pR += extractRed(rgb);
                pG += extractGreen(rgb);
                pB += extractBlue(rgb);
              }
            }

            pR /= aa_amount * aa_amount;
            pG /= aa_amount * aa_amount;
            pB /= aa_amount * aa_amount;

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
    aa_amount = 2;

    x1 = -1;
    y1 = -1;
    x2 = 1;
    y2 = 1;

    setstep(0.005); // set height et width automatiquement
    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    setValueToColorDefaultFunction();
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

  public void setstep(double d) {
    if (d <= 0) return;
    step = d;
    width = (int) ((x2 - x1) / d);
    height = (int) ((y2 - y1) / d);
  }

  public void setAntiAliasingAmount(int amount) {
    if (amount < 2) return;
    this.aa_amount = amount;
  }

  public void setValueToColorFunction(ThreeIntToInt function) {
    valueToColor = function;
  }

  public void setValueToColorDefaultFunction() {
    valueToColor =
        (int val, int min, int max) -> {
          if (val < min || val > max) return 0x000000; // cas erreur
          if (val == max) return 0;
          return Color.HSBtoRGB((float) (val + min) / max, 0.8f, 0.7f);
        };
  }

  public void setValueToColorDefaultFunction2() {
    valueToColor =
        (int val, int min, int max) -> {
          if (val > max || val < min) return 0xff0000; // cas erreur
          if (val == max) return 0xaaccff;
          val = 255 * (val + min) / max;
          return rgbToInt(val, val, val);
        };
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
    return aa_amount;
  }

  public double getStep() {
    return step;
  }

  public BufferedImage getImage() {
    return image;
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

  // somme les valeurs R, G ou B de plusieurs couleurs, puis renvoie leur moyenne

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

  // calculate fractal and put it in buffer
  public void generateBuffer() {

    if (function == null) return;
    BufferedImage smol = null;

    int threadsNb = Runtime.getRuntime().availableProcessors();

    if (antiAliasing) {
      smol = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      height *= aa_amount;
      width *= aa_amount;
      step /= aa_amount;
    }

    image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    double ox = x1 + (1 - zoom) * (x2 - x1) / 2 + shiftX;
    double oy = y1 + (1 - zoom) * (y2 - y1) / 2 + shiftY;

    threshold = height / threadsNb;
    RecursiveAction work = new MainWork(0, height, ox, oy);
    ForkJoinPool pool = new ForkJoinPool();

    pool.invoke(work);

    if (antiAliasing) {
      height /= aa_amount;
      width /= aa_amount;
      step *= aa_amount;
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
    try {
      ImageIO.write(image, "PNG", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
