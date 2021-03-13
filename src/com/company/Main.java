package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        String s = readFile("/Users/jonathanfils-aime/Documents/codebase/CS-6301-Assignment-3/task6.in");
        System.out.println(s);
    }


    public static String readFile(String filePath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            break;
        }
        bufferedReader.close();
        return line;
    }
}
