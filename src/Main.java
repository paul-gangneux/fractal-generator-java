public class Main {
  public static void main(String[] args) {
    if (args.length > 0) {
      new Cli().run(args);
    } else {
      ImageGenerator ig = new ImageGenerator();
      Julia juliaFun = new Julia();
      ig.setFractalGenerationFunction(juliaFun);
      ig.generateBuffer();
      new Gui(ig);
    }
  }
}
