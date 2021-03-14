package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    public static Map<Integer, List<String>> knowledgeBase = new ConcurrentHashMap<Integer, List<String>>();
    public static int initialSize;
    public static boolean found = false;

    public static void main(String[] args) throws IOException {
	// write your code here

        readFile("task4.in");
        initialSize = knowledgeBase.size();
        negateClauses();
        knowledgeBase.forEach((c,p) -> System.out.println(c + "-" +p));
        resolution();
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");
        System.out.println("------------------------------------------");
        knowledgeBase.forEach((c,p) -> System.out.println(c + "-" +p));
    }


    public static Map<Integer, List<String>> readFile(String filePath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

        String line = bufferedReader.readLine();
        int numberOfClauses = 0;

        while (line != null) {
            String[] words = line.split("\\s+");
            List<String> clauses = new ArrayList<>();

            for (String word : words) {
                clauses.add(word);
            }
            clauses.add("{}");
            knowledgeBase.put(numberOfClauses, clauses);
            line = bufferedReader.readLine();
            numberOfClauses++;
        }

        bufferedReader.close();
        return knowledgeBase;
    }

    public static void negateClauses() {
        knowledgeBase.forEach((in, cl) -> cl.forEach(lit -> negateLiteral(lit)));

    }

    public static void negateLiteral(String lit) {
        int index = knowledgeBase.size() + 1;
        Character neg = '~';
        if (!lit.equalsIgnoreCase("{}")) {
            if (neg.compareTo(lit.charAt(0)) == 0 ) {
                List<String> clause = new ArrayList<>();
                clause.add(lit.substring(1));
                clause.add("{}");
                knowledgeBase.put(index, clause);
            } else {
                StringBuilder st = new StringBuilder();
                st.append("~");
                st.append(lit);
                List<String> clause = new ArrayList<>();
                clause.add(st.toString());
                clause.add("{}");
                knowledgeBase.put(index, clause);
            }
        }
    }

    public static void resolution() {
        outerloop:
        for (int inferredKey = initialSize + 1; inferredKey < knowledgeBase.size(); inferredKey++) {
            for (int key = 0; key < initialSize; key++){
                for (int index = 0; index < knowledgeBase.get(key).size(); index++) {
                    if (!knowledgeBase.get(key).get(index).equalsIgnoreCase("{}")
                            && !knowledgeBase.get(key).get(index).equalsIgnoreCase("false")) {
                        compare(key, inferredKey, index);
                        if (found) {
                            System.out.println("I WON BITCH");
                            break outerloop;
                        }
                    } else {
                        //do nothing
                    }
                }
            }

        }
    }

    public static void compare(int key, int inferredKey, int index) {
        boolean initialLitValue = knowledgeBase.get(key).get(index).substring(0,1).equalsIgnoreCase("~");
        boolean inferredLitValue = knowledgeBase.get(inferredKey).get(0).substring(0,1).equalsIgnoreCase("~");


        if(match(key, inferredKey, index) && ((initialLitValue && !inferredLitValue) || (!initialLitValue && inferredLitValue))) {
            System.out.println("-----------------------");
            System.out.println("WTF --->" + match(key,inferredKey, index));
            System.out.println("original :" + knowledgeBase.get(key).get(index) + " inferred :" + knowledgeBase.get(inferredKey).get(0));
            System.out.println("-----------------------");
            resolve(key,inferredKey, index);
        }
    }

    public static boolean match(int key, int inferredKey, int index) {
        String initial = normalize(knowledgeBase.get(key).get(index));
        String inferred = normalize(knowledgeBase.get(inferredKey).get(0));
        boolean value = false;
        if (initial.equalsIgnoreCase(inferred))
        {
            value = true;
        }
        return value;
    }

    public static String normalize(String str) {
        String temp = str.substring(0, 1);
        String normalized = str;
        if(temp.equalsIgnoreCase("~")) {
            normalized =str.replaceAll("~", "");
        }
        return normalized;
    }

    public static void resolve(int key, int inferredKey, int index) {
        List<String> clause = new ArrayList<>();
        if (knowledgeBase.get(key).size() == 2) {
            System.out.println("size is only 1");
            clause.add("false");
            clause.add("{"+ key + ","+ inferredKey +"}");
            System.out.println("clause is : " + clause);
            knowledgeBase.put(key + 1, clause);
            found = true;
        } else {
            System.out.println("size is not 1");
            clause = knowledgeBase.get(key);
            clause.remove(index);

            List<String> resolved = cleanUp(clause);
            resolved.add("{"+ key + ","+ inferredKey +"}");
            System.out.println("clause is : " + resolved);
            knowledgeBase.put(key + 1, resolved);
        }
        System.out.println("BIG ---> " + knowledgeBase.get(key + 1));
    }

    public static List<String> cleanUp(List<String> clause) {
        List<String> newClause = new ArrayList<>(clause);
        List<String> indexToBeRemoved = new ArrayList<>();

        for (int i = 0; i < newClause.size(); i++) {
            if (newClause.get(i).substring(0,1).equalsIgnoreCase("{")) {
                indexToBeRemoved.add(newClause.get(i));
            }
        }
        newClause.removeAll(indexToBeRemoved);
        return newClause;
    }

}
