package ch.epfl.javelo.gui;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * The class managing all the errors of the program
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class ErrorManager {

    private final VBox vBox = new VBox();
    private final SequentialTransition sequentialTransition;

    /**
     * The constructor managing the different possible errors
     */

    public ErrorManager() {

        vBox.getStylesheets().add("error.css");
        vBox.setMouseTransparent(true);

        FadeTransition fadeTransition1 = new FadeTransition(Duration.millis(200), vBox);
        fadeTransition1.setFromValue(0.0);
        fadeTransition1.setToValue(0.8);
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));

        FadeTransition fadeTransition2 = new FadeTransition(Duration.millis(500), vBox);
        fadeTransition2.setFromValue(0.8);
        fadeTransition2.setToValue(0.0);


        sequentialTransition = new SequentialTransition(fadeTransition1, pauseTransition, fadeTransition2);
    }

    /**
     * returns the pane
     *
     * @return the pane
     */
    public Pane pane() {
        return vBox;
    }

    /**
     * displays our error on the screen whenever one occurs
     *
     * @param error our error to display in String
     */
    public void displayError(String error) {

        java.awt.Toolkit.getDefaultToolkit().beep();

        Text text = new Text(error);
        vBox.getChildren().setAll(text);

        
        if (sequentialTransition.getStatus() == Animation.Status.RUNNING) {
            sequentialTransition.stop();
        }
        sequentialTransition.play();
    }
}
