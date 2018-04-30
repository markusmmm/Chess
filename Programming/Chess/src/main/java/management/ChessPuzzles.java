package management;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Random;

public class ChessPuzzles {


    private final File[] files;
    private String path = "src/main/resources/chesspuzzles/";
    private final FileUtils fileUtils;
    private File file;
    private int sizeOfDirectory;


    public ChessPuzzles() {
        fileUtils = new FileUtils();
        file = new File(path);
        files = file.listFiles();
        sizeOfDirectory = getFile(path);


    }


    public String getFile(int i) {

        String fileString = files[i].toString();
        fileString = fileString.substring(0, fileString.lastIndexOf('.'));

        return fileString;
    }

    public void printFiles() {
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
    }

    private int getFile(String dirPath) {

        int count = -1;

        if (files != null)
            for (int i = 0; i < files.length; i++) {
                count++;
                File file = files[i];

                if (file.isDirectory()) {
                    getFile(file.getAbsolutePath());
                }
            }
        return count;
    }


    public int getSizeOfDirectory() {

        return sizeOfDirectory;
    }

    public String getRandomFile() {
        Random r = new Random();
        int i = r.nextInt(sizeOfDirectory + 1);

        String fileString = files[i].toString();
        fileString = fileString.substring(0, fileString.lastIndexOf('.'));

        return fileString;
    }


}
