package View;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * When you press CTRL and scroll up and down with the mouse the function performs a zoom into the maze
 */
public class ZoomOnMaze implements IControlPlusScroll {

    double zoomConstant = 1;


    @Override
    public void Activate(ScrollEvent scrollEvent, MazeDisplayer mazeDisplayer) {


        if (scrollEvent.isControlDown()) {
            zoomConstant *= 1.5;
            if (scrollEvent.getDeltaY() > 0) {
                mazeDisplayer.setScaleX(zoomConstant);
                mazeDisplayer.setScaleY(zoomConstant);

            } else if (scrollEvent.getDeltaY() < 0) {
                zoomConstant *= 0.5;

                mazeDisplayer.setScaleX(zoomConstant);
                mazeDisplayer.setScaleY(zoomConstant);
            }
            if (mazeDisplayer.getScaleX() * zoomConstant < 0.9) {
                mazeDisplayer.setScaleX(1);
                mazeDisplayer.setScaleY(1);

            }

        }
    }
}


