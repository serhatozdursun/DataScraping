package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class WriteToTxt {

    public PrintWriter createWriter(String pathAndFileName) {

        int index = pathAndFileName.lastIndexOf("/");
        String dirPath = pathAndFileName.substring(0,index);

        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        try {
            deleteFileIfExitst(pathAndFileName);
            return new PrintWriter(pathAndFileName, "UTF-8");
        } catch (FileNotFoundException e) {
            new Throwable("txt file con'not created");
            return null;
        } catch (UnsupportedEncodingException e) {
            new Throwable("txt file con'not created");
            return null;
        }
    }

    public void writeToTxt(PrintWriter printWriter, String lineData) {
        printWriter.println(lineData);
    }

    public void closeWriter(PrintWriter printWriter) {
        printWriter.close();
    }


    public void deleteFileIfExitst(String pathAndFileName) {

        File file = new File(pathAndFileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
