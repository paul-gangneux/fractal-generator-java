import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageGenerator {

    private int width, height;
    private double zoom;
    private double shiftX, shiftY;

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

    private int rgbToInt(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    // changer cette fonction changera le rendu
    // peut être utile pour faire de jolis effets
    private int valueToColor(int v) {
        if (v==255) return 0;
        return rgbToInt(0, 255-v, v);
    }
    
    public void create(TwoDoublesToInt f, String pathname) {
        
        var img=new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int max = f.maxValue();
        int min = f.minValue();
    
        double sx = width*shiftX*2.0/zoom+width;
        double sy = height*shiftY*2.0/zoom+height;
        
        // le pas est fait tel que, si shiftX==0, shiftY==0 et zoom==1 i et j vont de -1 à -1
        for (int i=0; i<width; i++) {
            for (int j=0; j<height; j++) {
                double x = ((i*2-sx)*zoom)/(width);
                double y = ((j*2-sy)*zoom)/(height);
                int val = f.doublesToInt(x,y);
                val = ((val-min)*255)/(max-min);
                int col = valueToColor(val);
                img.setRGB(i,j,col);
            }
        }

        File file = new File(pathname); // todo: faire un chemin plus intelligent
        try {
            ImageIO.write(img, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
