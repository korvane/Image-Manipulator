/*
 * Course: CSC-1120 - 131
 * Lab 3 - Image Manipulator 3000!
 * TestSuite
 * Name: Korvan Nameni
 * Last Updated: 9/16/25
 */
package namenik;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


/**
 * controller class for imagemanipulator3000.fxml
 */
public class Controller {
    @FXML
    private ImageView imageView;
    @FXML
    private Button filter;
    @FXML
    private Stage filterStage;

    private Image lastImageLoaded;
    private File selectedFile;
    private FilterController filterController;



    private final Transformable grayscale = (_, colo) -> {
        double avg = (colo.getBlue() + colo.getGreen() + colo.getRed()) / 3;
        return new Color(avg, avg, avg, 1.0);
    };
    private final Transformable negative = (_, c) -> {
        final double fi = 1;
        double r = fi - c.getRed();
        double g = fi - c.getGreen();
        double b = fi - c.getBlue();
        return new Color(r, g, b, 1);
    };
    private final Transformable red = (_, c) -> new Color(c.getRed(), 0, 0, 1);
    private final Transformable redGray = (pixY, c) -> {
        if(pixY%2==0){
            return new Color(c.getRed(), 0, 0, 1);
        } else {
            double avg = (c.getBlue() + c.getGreen() + c.getRed()) / 3;
            return new Color(avg, avg, avg, 1);
        }
    };

    private Image transformImage(Image image, Transformable transform) {
        int height = (int) image.getHeight();
        int width = (int) image.getWidth();
        WritableImage wi = new WritableImage(width, height);
        PixelReader pr = image.getPixelReader();
        PixelWriter pw = wi.getPixelWriter();
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color original = pr.getColor(x, y);
                Color neu = transform.apply(y, original);
                pw.setColor(x, y, neu);
            }
        }

        return wi;
    }

    public Image getLastImageLoaded() {
        return lastImageLoaded;
    }

    /**
     * setImage
     * @param image image
     */
    public void setImage(Image image){
        lastImageLoaded = image;
        imageView.setImage(image);
    }

    /**
     * handleOpen
     */
    @FXML
    public void open() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.msoe"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("MSOE", "*.msoe"),
                new FileChooser.ExtensionFilter("BMSOE", "*.bmsoe")
        );

        selectedFile = chooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                lastImageLoaded = ImageIO.read(selectedFile.toPath());
                imageView.setImage(lastImageLoaded);
            } catch (IllegalArgumentException | IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not read the file.");
                alert.setContentText("The file is invalid or corrupt.");
                alert.showAndWait();
            }
        }
    }

    /**
     * Utility to show an ERROR Alert.
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setFilterStage(Stage stage){
        filterStage = stage;
    }

    /**
     *reload
     */
    @FXML
    public void reload(){
        try {
            if (lastImageLoaded != null) {

                Image i = ImageIO.read(selectedFile.toPath());
                lastImageLoaded = i;
                imageView.setImage(i);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error reloading image: no image has been loaded yet.");
                alert.showAndWait();
            }
        } catch(IllegalArgumentException | IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error reloading image: no image has been loaded yet.");
            alert.showAndWait();
        }
    }
    /**
     * handle save
     * @throws IOException IOException
     */
    @FXML
    public void save() throws IOException{
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Resource File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.msoe"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("MSOE", "*.msoe")
        );
        File selectedFile = chooser.showSaveDialog(imageView.getScene().getWindow());
        if (selectedFile == null || imageView.getImage() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error saving image: no file or no image selected.");
            alert.showAndWait();
        } else {
            ImageIO.write(selectedFile.toPath(), imageView.getImage());
        }
    }


    /**
     * handle grayscale
     */
    @FXML
    public void grayscale(){
        Image image = imageView.getImage();
        lastImageLoaded = toGrayscale(image);
        imageView.setImage(lastImageLoaded);
    }

    /**
     * negative handler
     */
    @FXML
    public void negative(){
        Image image = imageView.getImage();
        lastImageLoaded = toNegative(image);
        imageView.setImage(lastImageLoaded);
    }

    /**
     * red
     */
    @FXML
    public void red(){
        Image image = imageView.getImage();
        lastImageLoaded = toRed(image);
        imageView.setImage(lastImageLoaded);
    }

    /**
     * redgray
     */
    @FXML
    public void redGray(){
        Image image = imageView.getImage();
        lastImageLoaded = toRedGray(image);
        imageView.setImage(lastImageLoaded);
    }




    /**
     * filter
     */
    @FXML
    public void filter() {
        final int fo = 400;
        try {
            if (filterController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("filter.fxml"));
                Parent root = loader.load();

                filterStage = new Stage();
                filterStage.setScene(new Scene(root, fo, fo));

                filterController = loader.getController();
                filterController.setStage(filterStage);
                filterController.setMainController(this);
            }
            showFilter();
        } catch (IOException e) {
            showError("IO Error", e.getMessage());
        } catch(IllegalArgumentException e){
            showError("Illegal argument", e.getMessage());
        }
    }

    /**
     * showFilter
     */
    @FXML
    public void showFilter() {

        if (filterStage != null) {

            if (filterStage.isShowing()) {
                filter.setText("Show Filter");
                filterStage.hide();
            } else {
                // Show the filter stage
                filter.setText("Hide Filter");
                filterStage.show();
            }
        }
    }


    /**
     * grayscale
     * @param image Image
     * @return image
     */
    public Image toGrayscale(Image image){
        return transformImage(image, grayscale);
    }

    /**
     * toNegative
     * @param image Image
     * @return image
     */
    public Image toNegative(Image image){
        return transformImage(image, negative);
    }

    /**
     * tored
     * @param image image
     * @return image
     */
    public Image toRed(Image image){
        return transformImage(image, red);
    }

    /**
     * redgray
     * @param image image
     * @return return
     */
    public Image toRedGray(Image image){
        return transformImage(image, redGray);
    }

}
