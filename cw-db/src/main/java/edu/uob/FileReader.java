package edu.uob;

import java.io.*;

public class FileReader
{
    public static void readFile(String fileName) {
        String lineOutput;
        File file = new File(fileName);
        try {
            java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while((lineOutput = bufferedReader.readLine()) != null) {

            }
            bufferedReader.close();
        }
        catch (FileNotFoundException noFile) {
            System.out.println(noFile.getMessage());
        }
        catch (IOException badIO) {
            System.out.println(badIO.getMessage());
        }
    }
}
