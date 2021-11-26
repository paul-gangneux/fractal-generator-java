import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageGenerator {

    private int width, height;
    private double zoom;
    private double shiftX, shiftY;
    private TwoDoublesToInt function;
    private BufferedImage image;

    private class Work extends RecursiveAction {

        private static final int THRESHOLD = 8;
        private int hmin;
        private int hmax;
        private double sx; //shift x
        private double sy; //shift y
        
        private int rgbToInt(int r, int g, int b) {
            return (r << 16) | (g << 8) | b;
        }
    
        // changer cette fonction changera le rendu
        // peut être utile pour faire de jolis effets
        private int valueToColor(int v) {
            if (v>255 || v<0) return 0xff0000; //cas erreur
            if (v==255) return 0;
            return rgbToInt(0, 255-v, v);
        }

        public Work(int hmin, int hmax, double sx, double sy) {
            this.hmin = hmin;
            this.hmax = hmax;
            this.sx = sx;
            this.sy = sy;
        }

        @Override
        protected void compute() {
            if (hmax-hmin>THRESHOLD) {
                Work w1 = new Work(hmin, (hmin+hmax)/2, sx, sy);
                Work w2 = new Work((hmin+hmax)/2, hmax, sx, sy);
                w1.fork();
                w2.fork();
            } else {
                int min = function.minValue();
                int max = function.maxValue();
                for (int i=0; i<width; i++) {
                    for (int j=hmin; j<hmax; j++) {
                        double x = ((i*2-sx)*zoom)/(width);
                        double y = ((j*2-sy)*zoom)/(height);
                        int val = function.doublesToInt(x,y);
                        val = ((val-min)*255)/(max-min);
                        int col = valueToColor(val);
                        image.setRGB(i,j,col);
                    }
                }
            }
        }
    }

    public ImageGenerator() {
        // valeurs par défaut
        width = 500;
        height = 500;
        zoom = 1;
        shiftX = 0;
        shiftY = 0;
    }

    // setters
    public void setWidth(int width) {this.width = width;}
    public void setHeight(int height) {this.height = height;}
    public void setZoom(double zoom) {this.zoom = zoom;}
    public void setShiftX(double shiftX) {this.shiftX = shiftX;}
    public void setShiftY(double shiftY) {this.shiftY = shiftY;}

    // getters
    public int getWidth() {return width;}
    public int getHeight() {return height;}
    public double getZoom() {return zoom;}
    public double getShiftX() {return shiftX;}
    public double getShiftY() {return shiftY;}

    
    public void create(TwoDoublesToInt f, String pathname) {

        function=f;
        
        image=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //int max = f.maxValue();
        //int min = f.minValue();
    
        double sx = width*shiftX*2.0/zoom+width;
        double sy = height*shiftY*2.0/zoom+height;

        RecursiveAction work = new Work(0, height, sx, sy);
        ForkJoinPool pool = new ForkJoinPool();

        pool.invoke(work);

        // s'assure que tous les thread terminent
        pool.shutdown();
        try {
            pool.awaitTermination(100, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        
        
        File file = new File(pathname);
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        image=null;
    }
}
