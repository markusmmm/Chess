package resources;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import main.Tile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * play images from a folder as an animation:
 * The animation takes place on a Tile-Object in the array grid,
 * assigned by cartesian coordinates
 * 
 * @author: Jan Olav Berg
 */
public class Animation {
    private ArrayList<ImagePattern> img = new ArrayList<>();
    private long millisecondsBetweenFrames;
    private Tile[][] grid;
    private static final String[] SUPPORTED_EXTENSIONS = {"png"};

    /**
     *
     * @param grid Tile-object where the animation take plase
     * @param picturePath folder with images
     * @param millisecondsBetweenFrames time between each frame
     */
    public Animation(Tile[][] grid, String picturePath, int millisecondsBetweenFrames) {
        this.grid = grid;
        this.millisecondsBetweenFrames = millisecondsBetweenFrames;
        loadPictures(picturePath);
    }

    /**
     *
     * @param p position in grid where animation takes place
     */
    public void play(Vector2 p) {
        for (ImagePattern img: img) {
            grid[p.getY()][p.getX()].setFill(img);

            try {
                wait(millisecondsBetweenFrames);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * opens picturePath and stores the images as immagepatterns in the ArrayList img
     * @param picturePath folder wich contains pictures for animation
     */
    private void loadPictures(String picturePath) {
        File dir = new File(picturePath);
        ImagePattern img;
        if(dir.isDirectory()) {
            for(File f: dir.listFiles(IMAGE_FILTER)) {
                img = new ImagePattern(new Image(f.getPath(), 64, 64, true, true));
                this.img.add(img);
            }
        }

    }

    /**
     * filter to identify images based on their extensions
     */
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : SUPPORTED_EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
}
