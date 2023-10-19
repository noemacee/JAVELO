package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;


/**
 * Our final Application : JaVelo
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class JaVelo extends Application {

    /**
     * the main method starting the lauching our program
     * @param args the parameter given to launch to the program
     */

    public static void main(String[] args) { launch(args); }

    /**
     * The method "start" allowing the user to launch JaVelo
     */

    @Override
    public void start(Stage stage) throws IOException {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(Path.of("osm-cache"), "tile.openstreetmap.org");
        CostFunction costFunction = new CityBikeCF(graph);
        RouteComputer routeComputer = new RouteComputer(graph, costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                routeBean.elevationProfileProperty(),
                routeBean.highlightedPositionProperty());


        ErrorManager errorManager = new ErrorManager();

        AnnotatedMapManager mapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorManager::displayError);


        SplitPane splitPane = new SplitPane(mapManager.pane());
        splitPane.orientationProperty().set(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), true);


        Menu menu = new Menu("Fichier");
        MenuItem menuItem = new MenuItem("Exporter GPX");
        menu.getItems().add(menuItem);


        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGPX("javelo.gpx", routeBean.getRoute(), routeBean.getElevationProfile());
            } catch (UncheckedIOException ignored) {}
        });


        MenuBar menuBar = new MenuBar(menu);


        menuItem.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        (routeBean.getElevationProfile() == null),
                routeBean.elevationProfileProperty()));

        routeBean.elevationProfileProperty().addListener((p, oldValue, newValue) ->
        {
            if (oldValue == null && newValue != null) {

                splitPane.getItems().add(1, elevationProfileManager.pane());

            } else if (oldValue != null && newValue == null) {

                splitPane.getItems().remove(1);
            }
        });

        routeBean.highlightedPositionProperty().bind(Bindings
                .when(mapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                .then(mapManager.mousePositionOnRouteProperty())
                .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));


        StackPane stacked = new StackPane(splitPane, errorManager.pane());
        BorderPane borderPane = new BorderPane(stacked, menuBar, null, null, null);

        stage.setTitle("JaVelo");
        stage.setMinHeight(600);
        stage.setMinWidth(800);



        stage.setScene(new Scene(borderPane));
        stage.show();
    }
}
