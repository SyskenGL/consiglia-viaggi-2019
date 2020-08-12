package it.isw.cvoffice.utils;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class SecurePasswordGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final static String SPECIAL_CHARACTERS = "!\"#$%&'()*+,-_/:;<=>?@[]^";
    private final static String NUMBERS = "0123456789";
    private final static String UPPERCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final static String LOWERCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
    public final static int MIN_PASSWORD_LENGTH = 10;
    public final static int MAX_PASSWORD_LENGTH = 256;


    public static @NotNull String generate(int length) {
        List<Character> charactersPool = new ArrayList<>();
        if(length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too short!");
        }
        if(length > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too long!");
        }
        fillCharactersPool(charactersPool, length);
        Collections.shuffle(charactersPool);
        return buildPassword(charactersPool, length);
    }

    private static void fillCharactersPool(@NotNull List<Character> charactersPool, int length) {
        while(true) {
            charactersPool.add(SPECIAL_CHARACTERS.charAt(SECURE_RANDOM.nextInt(SPECIAL_CHARACTERS.length())));
            if(charactersPool.size() == length) {
                break;
            }
            charactersPool.add(NUMBERS.charAt(SECURE_RANDOM.nextInt(NUMBERS.length())));
            if(charactersPool.size() == length) {
                break;
            }
            charactersPool.add(UPPERCASE_CHARACTERS.charAt(SECURE_RANDOM.nextInt(UPPERCASE_CHARACTERS.length())));
            if(charactersPool.size() == length) {
                break;
            }
            charactersPool.add(LOWERCASE_CHARACTERS.charAt(SECURE_RANDOM.nextInt(LOWERCASE_CHARACTERS.length())));
            if(charactersPool.size() == length) {
                break;
            }
        }
    }

    private static String buildPassword(List<Character> charactersPool, int length) {
        StringBuilder password = new StringBuilder(length);
        for(int passwordCharacter = 0; passwordCharacter < length; passwordCharacter++) {
            password.append(charactersPool.get(passwordCharacter));
        }
        return new String(password);
    }

}