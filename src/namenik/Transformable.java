/*
 * Course: CSC-1120 - 131
 * Lab 3 - Image Manipulator 3000!
 * TestSuite
 * Name: Korvan Nameni
 * Last Updated: 9/16/25
 */
package namenik;

import javafx.scene.paint.Color;

/**
 * transformable
 */
public interface Transformable {
    /**
     * apply
     * @param pixY pixel y
     * @param col column
     * @return return
     */
    Color apply(int pixY, Color col);
}
