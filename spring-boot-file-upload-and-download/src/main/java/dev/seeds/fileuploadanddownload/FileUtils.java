package dev.seeds.fileuploadanddownload;

public class FileUtils {
    public static String getFileSuffix(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
