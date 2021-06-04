package View;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

public class ZoomOnMaze implements IControlPlusScroll {
    double zoomConstant=1;
    @Override
    public void Activate(ScrollEvent scrollEvent, MazeDisplayer mazeDisplayer) {



        if (scrollEvent.isControlDown()) {
            zoomConstant *= 1.5;
            if (scrollEvent.getDeltaY() > 0) {
//                m_zoom = 1.1*m_zoom;
                mazeDisplayer.setScaleX(zoomConstant);
                mazeDisplayer.setScaleY(zoomConstant);

            } else if (scrollEvent.getDeltaY() < 0) {
                zoomConstant *= 0.5;
                mazeDisplayer.setScaleX(zoomConstant);
                mazeDisplayer.setScaleY(zoomConstant);
            }
            if (mazeDisplayer.getScaleX() * zoomConstant <0.9)
            {
                mazeDisplayer.setScaleX(1);
                mazeDisplayer.setScaleY(1);

            }

//            mazeDisplayer.setTranslateY(25);



        }


    }
}

