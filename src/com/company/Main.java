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
    public static Map<Integer, List<String>> literalMap = new ConcurrentHashMap<Integer, List<String>>();
    public static int keyIndex;
    public static int initialSize;
    public static boolean found = false;

    public static void main(String[] args) throws IOException {
        readFile(args[0]);
        initialSize = knowledgeBase.size();
        negateClauses();
        resolution();
        knowledgeBase.forEach((c,p) -> {
                    String theString = p.toString();
                    theString = theString.replaceAll("\\[", "");
                    theString = theString.replaceAll("\\]", "");
                    System.out.println(c + ". " + theString);
                });
        System.out.println("Size of final clause set: " + (knowledgeBase.size() - 1));
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
        keyIndex = knowledgeBase.size();
        knowledgeBase.forEach((in, cl) -> cl.forEach(lit -> negateLiteral(lit)));
        knowledgeBase.putAll(literalMap);
    }

    public static void negateLiteral(String lit) {

        Character neg = '~';
        if (!lit.equalsIgnoreCase("{}")) {
            if (neg.compareTo(lit.charAt(0)) == 0 ) {
                List<String> clause = new ArrayList<>();
                clause.add(lit.substring(1));
                clause.add("{}");
                literalMap.put(keyIndex, clause);
                keyIndex++;
            } else {
                StringBuilder st = new StringBuilder();
                st.append("~");
                st.append(lit);
                List<String> clause = new ArrayList<>();
                clause.add(st.toString());
                clause.add("{}");
                literalMap.put(keyIndex, clause);
                keyIndex++;
            }
        }
    }

    public static void resolution() {
        outerloop:
        for (int inferredKey = initialSize; inferredKey < knowledgeBase.size(); inferredKey++) {
            for (int key = 0; key < initialSize; key++){
                for (int index = 0; index < knowledgeBase.get(key).size(); index++) {
                    if(found) {
                        break outerloop;
                    } else {
                        compare(key, inferredKey, index);
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
            System.out.println("original :" + knowledgeBase.get(key).get(index) + " inferred :" + knowledgeBase.get(inferredKey).get(0));
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
            System.out.println("clause is : " + knowledgeBase.get(key));
            clause.add("false");
            clause.add("{"+ key + ","+ inferredKey +"}");
            System.out.println("resolved clause is : " + clause);
            int size = knowledgeBase.size();
            knowledgeBase.put(size, clause);
            found = true;
        } else {
            System.out.println("clause is : " + knowledgeBase.get(key));
            clause = knowledgeBase.get(key);
            List<String> resolved = createNewLiteral(clause, key, inferredKey, index);
            System.out.println("resolved clause is : " + resolved);
            int size = knowledgeBase.size();
            knowledgeBase.put(size, resolved);
        }
    }

    public static List<String> createNewLiteral(List<String> clause, int key, int inferredKey, int index) {

        List<String> copyClause = new ArrayList<>(clause);
        List<String> indexToBeRemoved = new ArrayList<>();
        indexToBeRemoved.add(copyClause.get(index));
        for (int i = 0; i < clause.size(); i++) {
            if (clause.get(i).substring(0,1).equalsIgnoreCase("{")) {
                indexToBeRemoved.add(copyClause.get(i));
            }
        }
        copyClause.removeAll(indexToBeRemoved);
        List<String> brandNewClause = new ArrayList<>(copyClause);
        brandNewClause.add("{"+ key + ","+ inferredKey +"}");
        return brandNewClause;
    }
}
