public class Main {

  public static void main(String[] args) {
    ImageGenerator ig = new ImageGenerator();
    Julia juliaFun = new Julia();
    ig.setFractalGenerationFunction(juliaFun);
    ig.generateBuffer();
    new Gui(ig);
  }
}
