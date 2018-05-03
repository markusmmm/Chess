package resources;

import javafx.geometry.Pos;
import main.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Animation {
    private ArrayList<BufferedImage> img = new ArrayList<>();
    private int timeBetweenFrames;
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

    public Animation(Tile[][] grid, String picturePath, int timeBetweenFrames) {
        this.grid = grid;
        this.timeBetweenFrames = timeBetweenFrames;
        loadPictures(picturePath);
    }
    public void play(Vector2 p) {
        for (BufferedImage img: img) {
            //grid[p.getY()][p.getX()].setFill();
        }
    }

    private void loadPictures(String picturePath) {
        File dir = new File("PATH_TO_YOUR_DIRECTORY");
        if(dir.isDirectory()) {
            for(File f: dir.listFiles(IMAGE_FILTER)) {
                try {
                    img.add(ImageIO.read(f));
                }
                catch (final IOException e) {
                    // handle errors here
                }
            }
        }

    }
}
