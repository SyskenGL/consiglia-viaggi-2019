package it.isw.cvoffice.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public abstract class Examiner {

    private static final String BLACK_LIST = "utils/black_list.txt";
    public static final List<String> BLACK_LISTED_WORDS;


    static {
        BLACK_LISTED_WORDS = new ArrayList<>();
        String blackListedWord = null;
        InputStream inputStream = Examiner.class.getClassLoader().getResourceAsStream(BLACK_LIST);
        if(inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                while((blackListedWord = bufferedReader.readLine()) != null) {
                    BLACK_LISTED_WORDS.add(blackListedWord);
                }
                bufferedReader.close();
            } catch (IOException e) {
                throw new RuntimeException(BLACK_LIST + " loading failed");
            }
        } else {
            throw new RuntimeException(BLACK_LIST + " file not found");
        }
    }


    public static boolean examine(String buffer) {
        buffer = buffer.toLowerCase();
        for(String blackListedWord: BLACK_LISTED_WORDS) {
            if(buffer.contains(blackListedWord.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

}