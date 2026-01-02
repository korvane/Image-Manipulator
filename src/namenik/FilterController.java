/*
 * Course: CSC-1120 - 131
 * Lab 3 - Image Manipulator 3000!
 * TestSuite
 * Name: Korvan Nameni
 * Last Updated: 9/16/25
 */
package namenik;

import edu.msoe.cs1021.ImageUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * filterController
 */
public class FilterController {
    @FXML private Stage filterStage;
    @FXML private TextField p0;
    @FXML private TextField p1;
    @FXML private TextField p2;
    @FXML private TextField p3;
    @FXML private TextField p4;
    @FXML private TextField p5;
    @FXML private TextField p6;
    @FXML private TextField p7;
    @FXML private TextField p8;
    @FXML private Button apply;
    private Controller controller;


    public void setStage(Stage stg){
        filterStage = stg;
    }
    public void setMainController(Controller c){
        controller = c;
    }
    public Stage getStage(){
        return filterStage;
    }
    @FXML
    private void blur(ActionEvent event) {
        final double n = 0;
        final double na = 1.0/5;
        final double naa = 5.0/5;
        setKernel(new double[] {
                n, na, n,
                na, naa, na,
                n, na, n});
    }

    @FXML
    private void apply(ActionEvent event) {
        double[] kernel = getKernel();
        if (kernel != null) {
            Image original = controller.getLastImageLoaded();
            Image newImage = ImageUtil.convolve(original, kernel);
            controller.setImage(newImage);
        }
    }


    @FXML
    private void sharpen(ActionEvent event) {
        final double n = 0;
        final double na = -1;
        final double naa = 5;
        setKernel(new double[] {
                n, na, n,
                na, naa, na,
                n, na, n});
    }
    @FXML
    private void validateKernel() {
        double[] kernel = getKernel();
        System.out.println(kernel==null);
        apply.setDisable(kernel == null);
    }

    private double[] getKernel() {
        try {
            double[] k = {
                    Double.parseDouble(p0.getText()),
                    Double.parseDouble(p1.getText()),
                    Double.parseDouble(p2.getText()),
                    Double.parseDouble(p3.getText()),
                    Double.parseDouble(p4.getText()),
                    Double.parseDouble(p5.getText()),
                    Double.parseDouble(p6.getText()),
                    Double.parseDouble(p7.getText()),
                    Double.parseDouble(p8.getText())
            };
            // Sum must be > 0
            double sum = 0;
            for (double v : k) {
                sum += v;
            }

            if (sum <= 0) {
                return null;
            }
            return k;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setKernel(double[] k) {
        final int ab = 0;
        final int ac = 1;
        final int ad = 2;
        final int ae = 3;
        final int af = 4;
        final int ag = 5;
        final int ah = 6;
        final int ai = 7;
        final int aj = 8;

        p0.setText(Double.toString(k[ab]));
        p1.setText(Double.toString(k[ac]));
        p2.setText(Double.toString(k[ad]));
        p3.setText(Double.toString(k[ae]));
        p4.setText(Double.toString(k[af]));
        p5.setText(Double.toString(k[ag]));
        p6.setText(Double.toString(k[ah]));
        p7.setText(Double.toString(k[ai]));
        p8.setText(Double.toString(k[aj]));
        validateKernel();
    }

}
