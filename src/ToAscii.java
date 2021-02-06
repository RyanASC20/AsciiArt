/**
 * Execution: java -cp bin ToAscii file w h
 * Dependencies: Picture.java
 * 
 * Converts file to ASCII. w and h are the widths and heights of subsections converted.
 * 
 */
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;

class ToAscii {

    public static int toGrayscale(int r, int g, int b) {
        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }


    public static void toGrayscale(Picture picture, int width, int height) {
        for (int col = 0; col < width; col++) {
            for(int row = 0; row < height; row++) {
                Color c = picture.get(col, row);
                int r = c.getRed();
                int b = c.getBlue();
                int g = c.getGreen();
                int gr = toGrayscale(r, g, b);
                Color gray = new Color(gr, gr, gr);
                picture.set(col, row, gray);
            }
        }
    }


    public static int[][] calculateAverages(Picture picture, int subBoxWidth, int subBoxHeight) {
        int width = picture.width();
        int height = picture.height();
        toGrayscale(picture, width, height);

        int averages[][] = new int[height / subBoxHeight][width / subBoxWidth];
        int currentPosX = 0;
        for (int col = 0; col < width; col++) {
            int currentPosY = 0;
            for (int row = 0; row < height - subBoxHeight; row++) {
                averages[currentPosY][currentPosX] +=  picture.get(col, row).getRed() / (subBoxWidth * subBoxHeight); 
                if (row != 0 && row % subBoxHeight == 0) currentPosY++;

            }
            if (col != 0 && col % subBoxWidth == 0) currentPosX++;
        }
        return averages;
    }


    public static int maxColor(int[][] averages) {
        int max = 0;
        for (int i = 0; i < averages.length; i++) {
            for (int j = 0; j < averages[i].length; j++) {
                max = Math.max(averages[i][j], max);
            }
        }
        return max;
    }


    public static int calculateCharacterInterval(int[][] averages, int numChars) {
        return maxColor(averages) / numChars + 1;
    }


    public static void draw(int[][] averages, int interval, char[] chars) {
        try {
            FileWriter output = new FileWriter("output.txt");
            for (int i = 0; i < averages.length; i++) {
                for (int j = 0; j < averages[i].length; j++) {
                    output.write(chars[averages[i][j] / interval]);
                }
                output.write("\n");
            }
            output.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        Picture picture = new Picture(args[0]);
        int subBoxWidth = Integer.parseInt(args[1]);
        int subBoxHeight = Integer.parseInt(args[2]);

        char[] chars = {' ', '.', ',', ':', ';', 'o', 'x', 'W', '#', '@'};
        char[] charsRev = new char[10];
        for (int i = 0; i < 10; i++) {
            charsRev[i] = chars[10 - i - 1];
        }


        int[][] averages = calculateAverages(picture, subBoxWidth, subBoxHeight);
        int interval = calculateCharacterInterval(averages, chars.length);

        draw(averages, interval, charsRev);

        // picture.show();
    }
}