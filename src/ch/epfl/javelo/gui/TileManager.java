package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Represents an OSM Tile manager
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class TileManager {

    /**
     * The maximum capacity of the LinkedHashMap
     */
    private static final int MAX_CAPACITY = 100;


    private final Path pathToDisk;
    private final String name;
    private final LinkedHashMap<TileId, Image> memory;


    /**
     * Gets the tiles from a tile server and stores them in a memory cache and in a disk cache
     *
     * @param pathToDisk the path to the directory containing the disk cache
     * @param name       the name of the tile server
     */

    public TileManager(Path pathToDisk, String name) {

        this.pathToDisk = pathToDisk;
        this.name = name;
        this.memory = new LinkedHashMap<>(MAX_CAPACITY, 0.75F, true);
    }

    /**
     * Method returning the image of the tile given its id
     *
     * @param tileId : The id of the wanted image tile
     * @return : the Image of the tile
     * @throws IOException if the image doesn't exist
     */

    public Image imageForTileAt(TileId tileId) throws IOException {

        Path pathToDirectory = pathToDisk
                .resolve(String.valueOf(tileId.zoomLevel))
                .resolve(String.valueOf(tileId.X));

        Path pathToY = pathToDirectory
                .resolve(tileId.Y + ".png");


        if (memory.containsKey(tileId)) {
            return memory.get(tileId);
        }


        Iterator<TileId> iterator = memory.keySet().iterator();

        if (memory.size() == MAX_CAPACITY) {
            memory.remove(iterator.next());
        }

        Image image;
        if (Files.exists(pathToY)) {
            try (FileInputStream s = new FileInputStream(pathToY.toFile())) {
                image = new Image(s);
            }

        } else {

            Files.createDirectories(pathToDirectory);

            URL u = new URL(
                    "https://"
                            + name
                            + '/'
                            + tileId.zoomLevel
                            + '/'
                            + tileId.X
                            + '/'
                            + tileId.Y
                            + ".png");
            URLConnection c = u.openConnection();

            c.setRequestProperty("User-Agent", "JaVelo");

            try (InputStream i = c.getInputStream(); OutputStream o = new FileOutputStream(pathToY.toFile())) {
                i.transferTo(o);
            }

            try (FileInputStream inputStream = new FileInputStream(pathToY.toFile())) {
                image = new Image(inputStream);
            }

        }

        memory.put(tileId, image);
        return image;

    }


    /**
     * Represents the id of the given (X,Y) tile at a certain zoomLevel
     */

    public record TileId(int zoomLevel, int X, int Y) {
        public static boolean isValid(int zoomLevel, int X, int Y) {

            int maximum = 1 << zoomLevel;

            return X >= 0
                    && X <= maximum
                    && Y >= 0
                    && Y <= maximum;
        }
    }
}
