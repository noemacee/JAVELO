package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public final class ElevationProfileManager {
    private Pane fond;
    private DoubleProperty mousePositionOnProfileProperty;
    private ReadOnlyDoubleProperty position;
    private ReadOnlyObjectProperty<ElevationProfile> profile;
    private BorderPane borderFond;
    private Path path;
    private Group group;
    private Polygon polygon;
    private Line line;
    private VBox vbox;
    private Text text;
    private Insets insets = new Insets(10, 10, 20, 40);
    private ObjectProperty<Transform> screenToWorld;
    private ObjectProperty<Transform> worldToScreen;
    private ObjectProperty<Rectangle2D> rect;
    /**
     * Constante utilisée pour transformer des kilomètres en mètres
     */
    private final static float KM_IN_METERS = 1000;

    /**
     * Tableau contenant les espacements minimaux pour la position
     */
    private static final int[] POS_STEPS = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };

    /**
     * Tableau contenant les espacements minimaux pour l'élévation
     */
    private static final int[] ELE_STEPS = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    /**
     * Constructeur public
     * 
     * @param profile  profile de l'itinéraire
     * @param position position mise en évidence
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profile,
            ReadOnlyDoubleProperty position) {

        mousePositionOnProfileProperty = new SimpleDoubleProperty(Double.NaN);
        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());
        this.position = position;
        this.profile = profile;

        path = new Path();
        group = new Group();
        polygon = new Polygon();
        line = new Line();
        path.setId("grid");
        polygon.setId("profile");

        text = new Text();
        fond = new Pane(path, group, polygon, line);
        vbox = new VBox(text);
        vbox.setId("profile_data");
        borderFond = new BorderPane(fond, null, null, vbox, null);

        borderFond.setCenter(fond);
        borderFond.setBottom(vbox);

        borderFond.getStylesheets().add("elevation_profile.css");

        rect = new SimpleObjectProperty<>(Rectangle2D.EMPTY);
        bindRect();
        listeners();
    }

    /**
     * Auxilary methods that permit creation of necessary transformations
     * for the convertion in between the coordinates of the map and the coordinates
     * of the screen
     */
    private void transformation() {
        double minY = rect.get().getMinY();
        double minX = rect.get().getMinX();

        double rectHeight = rect.get().getHeight();
        double rectWidth = rect.get().getWidth();

        double profileLength = profile.get().length();
        double profileMaxElev = profile.get().maxElevation();
        double profileMinElev = profile.get().minElevation();

        Affine transform = new Affine();
        transform.prependTranslation(-minX, -minY);
        transform.prependScale(profileLength / rectWidth, -(profileMaxElev -
                profileMinElev) / rectHeight);
        transform.prependTranslation(0, profileMaxElev);
        screenToWorld.set(transform);

        try {
            worldToScreen.set(transform.createInverse());
        } catch (NonInvertibleTransformException e) {
            throw new Error(e);
        }
    }

    /**
     * Auxilary method that permit the binding of the rectangle to the pane
     */
    private void bindRect() {

        rect.bind(Bindings.createObjectBinding(() -> new Rectangle2D(
                insets.getLeft(),
                insets.getTop(),
                Math.max(0, (fond.getWidth() - (insets.getLeft() + insets.getRight()))),
                Math.max(0, (fond.getHeight() - (insets.getBottom() + insets.getTop())))), fond.widthProperty(),
                fond.heightProperty()));

    }

    /**
     * Auxilary method that creats the grid where the itinerary and its profile are
     * displayed
     */

    private void grille() {

        int nbOfVerticalLines = 0;
        int nbOfHorizontalLines = 0;
        int heightPerBlock = computePerBlock(ELE_STEPS, 25, "y");
        int lengthPerBlock = computePerBlock(POS_STEPS, 50, "x");
        int firstElevation = Math2.ceilDiv((int) Math.round(profile.get().minElevation()), heightPerBlock)
                * heightPerBlock;

        List<PathElement> liste = new ArrayList<>();
        List<Text> listeText = new ArrayList<>();

        while (nbOfHorizontalLines * heightPerBlock + firstElevation <= profile.get().maxElevation()) {
            Point2D start = worldToScreen.get().transform(0, nbOfHorizontalLines * heightPerBlock + firstElevation);
            Point2D end = worldToScreen.get().transform(Math.round(profile.get().length()),
                    nbOfHorizontalLines * heightPerBlock + firstElevation);
            liste.add(new MoveTo(start.getX(), start.getY()));
            liste.add(new LineTo(end.getX(), end.getY()));

            Text text = new Text();
            text.setFont(Font.font("Avenir", 10));
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("horizontal");
            text.textOriginProperty().setValue(VPos.CENTER);
            text.setText("" + (firstElevation + nbOfHorizontalLines * heightPerBlock));
            text.setLayoutX(rect.get().getMinX() - (text.prefWidth(0) + 2));
            text.setLayoutY(start.getY() - insets.getTop());
            listeText.add(text);
            group.getChildren().setAll(listeText);
            nbOfHorizontalLines++;

        }

        while (nbOfVerticalLines * lengthPerBlock <= profile.get().length()) {

            Point2D start = worldToScreen.get().transform(nbOfVerticalLines * lengthPerBlock,
                    profile.get().minElevation());
            Point2D end = worldToScreen.get().transform(nbOfVerticalLines * lengthPerBlock,
                    profile.get().maxElevation());
            liste.add(new MoveTo(start.getX(), start.getY()));
            liste.add(new LineTo(end.getX(), end.getY()));
            Text text = new Text();
            text.setFont(Font.font("Avenir", 10));
            text.getStyleClass().add("grid_label");
            text.getStyleClass().add("vertical");
            text.textOriginProperty().setValue(VPos.TOP);
            text.setText("" + "" + (int) Math.ceil((nbOfVerticalLines * lengthPerBlock) / KM_IN_METERS));
            text.setLayoutX(start.getX() - insets.getLeft() - (text.prefWidth(0)) / 2 + rect.get().getMinX());
            text.setLayoutY(rect.get().getMaxY());
            listeText.add(text);
            group.getChildren().setAll(listeText);
            nbOfVerticalLines++;

        }
        path.getElements().setAll(liste);

    }

    /**
     * Auxilary method that displays the statistics of the itinerary
     */
    private void statistics() {
        String stats = "";
        if (profile.get() != null) {
            stats = String.format("Longueur : %.1f km" +
                    "     Montée : %.0f m" +
                    "     Descente : %.0f m" +
                    "     Altitude : de %.0f m à %.0f m",
                    profile.get().length() / KM_IN_METERS,
                    profile.get().totalAscent(),
                    profile.get().totalDescent(),
                    profile.get().minElevation(),
                    profile.get().maxElevation());
            text.setId("elevation_profile.css");
            text.setId("profile_data");

            text.setText(stats);
        }

    }

    /**
     * Methods containing all the listeners that will allow us to manage all the
     * events
     */
    private void listeners() {

        if (profile.get() != null) {
            fond.heightProperty().addListener((a, b, c) -> {
                transformation();
                grille();
                polygonBuilder();
                statistics();
                createHighlightedPosition();
            });

            fond.widthProperty().addListener((a, b, c) -> {
                transformation();
                grille();
                polygonBuilder();
                statistics();
                createHighlightedPosition();

            });

            profile.addListener((a, b, c) -> {
                transformation();
                grille();
                polygonBuilder();
                statistics();
                createHighlightedPosition();

            });

            worldToScreen.addListener((a, b, c) -> {
                grille();
                createHighlightedPosition();
            });

            borderFond.setOnMouseMoved(e -> {
                if ((e.getX() <= rect.get().getMaxX() && e.getX() >= rect.get().getMinX() &&
                        e.getY() >= rect.get().getMinY() && e.getY() <= rect.get().getMaxY())) {
                    mousePositionOnProfileProperty
                            .set(screenToWorld.get().deltaTransform(e.getX() - insets.getLeft(), 0).getX());
                } else {
                    mousePositionOnProfileProperty.set(Double.NaN);
                }
            });

            fond.setOnMouseExited(e -> {
                mousePositionOnProfileProperty.set(Double.NaN);
            });

        }
    }

    /**
     * Auxilary method that calculates the correct spacing of each grid case,
     * depending on the itinerary and its elevation
     * 
     * @param distances table of reference spacings
     * @param minSpace  minimal space in pixels
     * @param s         auxiliary parameter allowing to differentiate the position
     *                  of the elevation
     * 
     * @return ideal spacing for each grid case to display
     */
    private int computePerBlock(int[] distances, int minSpace, String s) {
        double pix = 0;
        int value = 0;
        for (Integer i : distances) {
            if (s.equals("y")) {
                pix = worldToScreen.get().deltaTransform(0, -i).getY();
            } else {
                pix = worldToScreen.get().deltaTransform(i, 0).getX();
            }
            if (pix >= minSpace) {
                value = i;
                break;
            }

        }
        return value != 0 ? value : distances[distances.length - 1];
    }

    /**
     * Auxilary method that creates the polygon representing the elevation of the
     * itinerary
     */
    private void polygonBuilder() {
        polygon.getPoints().clear();
        Point2D pointBasGauche = new Point2D(rect.get().getMinX(), rect.get().getMaxY());
        Point2D pointBasDroite = new Point2D(rect.get().getMaxX(), rect.get().getMaxY());

        for (double i = rect.get().getMinX(); i < rect.get().getMaxX(); i++) {
            double newI = screenToWorld.get().transform(i, 0).getX();
            polygon.getPoints().add(i);
            polygon.getPoints().add(worldToScreen.get().transform(0, profile.get().elevationAt(newI)).getY());

        }
        addPoint(pointBasDroite);
        addPoint(pointBasGauche);

    }

    /**
     * Method allowing to access the pane
     * 
     * @return pane
     */
    public Pane pane() {
        return borderFond;
    }

    /**
     * Method allowing to access the property containing the position of the mouse
     * 
     * @return the property containing the position of the mouse
     * 
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }

    /**
     * Auxilary method that adds points during the construction of the polygon
     * 
     * @param point point to add
     */
    private void addPoint(Point2D point) {
        polygon.getPoints().add(point.getX());
        polygon.getPoints().add(point.getY());

    }

    /**
     * 
     * Auxilary method that creates the line representing the highlighted position
     */
    private void createHighlightedPosition() {
        line.startYProperty().bind(Bindings.select(rect, "minY"));
        line.endYProperty().bind(Bindings.select(rect, "maxY"));
        line.visibleProperty().bind(position.greaterThanOrEqualTo(0));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(
                () -> worldToScreen.get().transform(position.get(), 0).getX(), position, worldToScreen));

    }
}
