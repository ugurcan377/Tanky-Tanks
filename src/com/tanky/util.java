package com.tanky;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class util {
    private String line;

    public ArrayList<String> readFile() throws IOException
    {
        int count=0;
        ArrayList<String> list=new ArrayList<String>();
        File file = new File("highscores.txt");
        if(!file.exists()) file.createNewFile();
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(file));
            line= reader.readLine();
            while (line!=null)
            {
                list.add(count,line);
                count++;
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException ex)
        {}
        return list;
    }
    public void writeFile(String text) throws IOException {
        try {
            File file = new File("highscores.txt");
            if(!file.exists()) file.createNewFile();
            FileWriter writer=new FileWriter(file,true);
            writer.write("\n");
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
