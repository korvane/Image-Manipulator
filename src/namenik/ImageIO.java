/*
 * Course: CSC-1120 - 131
 * Lab 3 - Image Manipulator 3000!
 * Name: Korvan Nameni
 * Last Updated: 9/16/25
 */
package namenik;

import edu.msoe.cs1021.ImageUtil;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileWriter;
/**
 * image io
 */
public class ImageIO {
    private static final int RED_OFFSET = 16;
    private static final int GREEN_OFFSET = 8;
    private static final int ALPHA_OFFSET = 24;
    private static final int MASK = 0x000000FF;
    private static final double SCALAR = 255.0;


    private static Color intToColor(int color) {
        double red = ((color >> RED_OFFSET) & MASK)/SCALAR;
        double green = ((color >> GREEN_OFFSET) & MASK)/SCALAR;
        double blue = (color & MASK)/SCALAR;
        double alpha = ((color >> ALPHA_OFFSET) & MASK)/SCALAR;
        return new Color(red, green, blue, alpha);
    }

    private static int colorToInt(Color color) {
        int red = ((int)(color.getRed() * SCALAR)) & MASK;
        int green = ((int)(color.getGreen() * SCALAR)) & MASK;
        int blue = ((int)(color.getBlue() * SCALAR)) & MASK;
        int alpha = ((int)(color.getOpacity() * SCALAR)) & MASK;
        return (alpha << ALPHA_OFFSET) + (red << RED_OFFSET) + (green << GREEN_OFFSET) + blue;
    }







    /**
     * write
     * @param path path
     * @param image image
     * @throws IOException ioexception
     * @throws IllegalArgumentException illeg
     */
    public static void write(Path path, Image image) throws IOException, IllegalArgumentException {
        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".bmsoe")) {
            writeBMSOE(path, image);
        } else if(fileName.endsWith(".msoe")) {
            writeMSOE(path, image);
        } else if(fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            ImageUtil.writeImage(path, image);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    /**
     * read
     * @param path path
     * @return image
     * @throws IOException ioexception
     * @throws IllegalArgumentException illegalargumentexception
     */
    public static Image read(Path path) throws IOException, IllegalArgumentException{
        String ext = path.getFileName().toString();
        if(!ext.contains(".")){
            throw new IllegalArgumentException("Missing file extension");
        }
        ext = ext.substring(ext.lastIndexOf("."));

        return switch (ext) {
            case ".png", ".jpg" -> ImageUtil.readImage(path);
            case ".msoe" -> readMSOE(path);
            case ".bmsoe" -> readBMSOE(path);
            default -> throw new IllegalArgumentException("invalid extension");
        };
    }

    /**
     * write msoe
     * @param path path
     * @param image image
     * @throws IOException ioexception
     * @throws IllegalArgumentException illegalargumentexception
     */
    public static void writeBMSOE(Path path, Image image)
            throws IOException, IllegalArgumentException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(path.toFile()))) {
            // Write header
            for (char c : "BMSOE".toCharArray()) {
                dos.writeChar(c);
            }

            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            dos.writeInt(width);
            dos.writeInt(height);

            PixelReader reader = image.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    dos.writeInt(colorToInt(reader.getColor(x, y)));
                }
            }
        }
    }

    /**
     * readbmsoe
     * @param path path
     * @return image
     * @throws IOException ioexception
     * @throws IllegalArgumentException illegal argument exception
     */
    public static Image readBMSOE(Path path) throws IOException, IllegalArgumentException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(path.toFile()))) {
            final int strlen = 10;
            byte[] headerBytes = new byte[strlen];
            dis.readFully(headerBytes);
            String he = new String(headerBytes, StandardCharsets.US_ASCII);
            StringBuilder header = new StringBuilder();
            for(int i = 0; i < he.length(); i++){
                if(i%2==1){
                    header.append(he.charAt(i));
                }
            }
            if (!header.toString().equals("BMSOE")) {

                throw new IllegalArgumentException("Invalid MSOE file header");
            }

            int width = dis.readInt();
            int height = dis.readInt();

            WritableImage image = new WritableImage(width, height);
            PixelWriter writer = image.getPixelWriter();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = dis.readInt();
                    writer.setColor(x, y, intToColor(argb));
                }
            }
            return image;
        }
    }

    /**
     * readmsoe
     * @param path path
     * @return image
     * @throws IOException ioexception
     * @throws IllegalArgumentException illegalargumentexception
     */
    public static Image readMSOE(Path path) throws IOException, IllegalArgumentException{
        final int sev = 7;
        final int nin = 9;
        try (Scanner scanner = new Scanner(path)) {
            if (!scanner.hasNextLine() || !scanner.nextLine().trim().equals("MSOE")) {
                throw new IllegalArgumentException("Invalid MSOE file header");
            }

            if (!scanner.hasNextInt()){
                throw new IllegalArgumentException("Missing width");
            }
            int width = scanner.nextInt();
            if (!scanner.hasNextInt()){
                throw new IllegalArgumentException("Missing height");
            }
            int height = scanner.nextInt();
            scanner.nextLine();

            WritableImage img = new WritableImage(width, height);
            PixelWriter pw = img.getPixelWriter();

            for (int y = 0; y < height; y++) {
                if (!scanner.hasNextLine()) {
                    throw new IllegalArgumentException("Missing pixels");
                }
                String line = scanner.nextLine().trim();
                String[] tokens = line.split("\\s+");
                if (tokens.length != width) {
                    throw new IllegalArgumentException("Invalid number of pixels in row " + y);
                }

                for (int x = 0; x < width; x++) {
                    String token = tokens[x];
                    if (!token.startsWith("#") || token.length() != sev && token.length() != nin) {
                        throw new IllegalArgumentException("Invalid color token: " + token);
                    }
                    Color color;
                    try {
                        final int sixtenn = 16;
                        final double ag = 255;
                        final int on = 1;
                        final int tre = 3;
                        final int fi = 5;
                        int r = Integer.parseInt(token.substring(on, tre), sixtenn);
                        int g = Integer.parseInt(token.substring(tre, fi), sixtenn);
                        int b = Integer.parseInt(token.substring(fi, sev), sixtenn);
                        double a = 1.0;
                        if (token.length() == nin) {

                            int alpha = Integer.parseInt(token.substring(sev, nin), sixtenn);
                            a = alpha / ag;
                        }
                        color = Color.rgb(r, g, b, a);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid color token: " + token);
                    }
                    pw.setColor(x, y, color);
                }
            }
            return img;
        }
    }

    /**
     * FAAA
     * @param path payth
     * @param image image
     * @throws IOException ioexpeouin
     * @throws IllegalArgumentException illegalargumentexception
     */
    public static void writeMSOE(Path path, Image image)
            throws IOException, IllegalArgumentException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("MSOE");
            writer.newLine();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            writer.write(width + " " + height);
            writer.newLine();

            PixelReader pr = image.getPixelReader();
            for (int y = 0; y < height; y++) {
                StringBuilder line = new StringBuilder();
                for (int x = 0; x < width; x++) {
                    Color c = pr.getColor(x, y);
                    final int ja = 255;
                    int r = (int) Math.round(c.getRed() * ja);
                    int g = (int) Math.round(c.getGreen() * ja);
                    int b = (int) Math.round(c.getBlue() * ja);
                    int a = (int) Math.round(c.getOpacity() * ja);
                    if (a < ja) {
                        line.append(String.format("#%02X%02X%02X%02X", r, g, b, a));
                    } else {
                        line.append(String.format("#%02X%02X%02X", r, g, b));
                    }
                    if (x < width - 1){
                        line.append(" ");
                    }
                }
                writer.write(line.toString());
                writer.newLine();
            }
        }
    }

}
