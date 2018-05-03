package resources;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import main.Tile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class Animation {
    private ArrayList<ImagePattern> img = new ArrayList<>();
    private long MillSecFrame;
    private Tile[][] grid;
    private static final String[] SUPPORTED_EXTENSIONS = {"png"};

    // filter to identify images based on their extensions
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

    /**
     *
     * @param grid Tile object where animation take plase
     * @param picturePath folder with images
     * @param timeBetweenFrames time between each frame
     */
    public Animation(Tile[][] grid, String picturePath, int timeBetweenFrames) {
        this.grid = grid;
        this.MillSecFrame = timeBetweenFrames;
        loadPictures(picturePath);
    }
    public void play(Vector2 p) {
        for (ImagePattern img: img) {
            grid[p.getY()][p.getX()].setFill(img);

            try {
                wait(MillSecFrame);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

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
}
