package management;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class ChessPuzzles {


    private final String[] files = {"threeMoves.txt", "threeMoves1.txt", "threeMoves2.txt", "threeMoves3.txt", "threeMoves4.txt"};
    private int sizeOfDirectory;
    ArrayList<File> textFiles = new ArrayList<>();


    public ChessPuzzles() {


        sizeOfDirectory = files.length;


    }


    public String[] getAllFiles(){
        return files;
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




    public int getSizeOfDirectory() {

        return sizeOfDirectory;
    }

    public String getRandomFile() {
        Random r = new Random();
        int i = r.nextInt(sizeOfDirectory);

        String fileString = files[i].toString();
        fileString = fileString.substring(0, fileString.lastIndexOf('.'));

        return fileString;
    }


}
