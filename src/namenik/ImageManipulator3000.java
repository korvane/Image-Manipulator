/*
 * Course: CSC-1120 - 131
 * Lab 3 - Image Manipulator 3000!
 * TestSuite
 * Name: Korvan Nameni
 * Last Updated: 9/16/25
 */
package namenik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.util.Objects;

/**
 * ImageManipulator3000
 */
public class ImageManipulator3000 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        try {

            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource("imagemanipulator3000.fxml")));
            final int sze = 400;
            stage.setScene(new Scene(root, sze, sze));
            stage.setTitle("Image Manipulator 3000");
            stage.show();




        } catch (NullPointerException | IIOException e) {
            System.out.println("error :" + e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load UI");
            alert.setContentText("Details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
