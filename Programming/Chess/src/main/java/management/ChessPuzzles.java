package management;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ChessPuzzles {


    private final File[] files;
    private String path = "src/main/resources/chesspuzzles/";
    private final FileUtils fileUtils;
    private File file;
    private int sizeOfDirectory;
    ArrayList<File> textFiles = new ArrayList<>();


    public ChessPuzzles() {
        fileUtils = new FileUtils();
        file = new File(path);
        files = file.listFiles();

        removeDsStore();
        sizeOfDirectory = textFiles.size();


    }

    public void removeDsStore(){
        for (int i = 0; i < files.length; i++) {

            if (files[i].toString().equals("src/main/resources/chesspuzzles/.DS_Store")) {
                continue;
            } else {
                textFiles.add(files[i]);
            }
        }
    }


    public String getFile(int i) {

        String fileString = textFiles.get(i).toString();
        fileString = fileString.substring(0, fileString.lastIndexOf('.'));

        return fileString;
    }

    public void printFiles() {
        for (int i = 0; i < textFiles.size(); i++) {
            System.out.println(textFiles.get(i));
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
        int i = r.nextInt(sizeOfDirectory);

        String fileString = textFiles.get(i).toString();
        fileString = fileString.substring(0, fileString.lastIndexOf('.'));

        return fileString;
    }


}
