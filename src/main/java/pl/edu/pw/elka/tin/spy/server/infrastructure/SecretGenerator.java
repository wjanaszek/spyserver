package pl.edu.pw.elka.tin.spy.server.infrastructure;

import java.security.SecureRandom;

public class SecretGenerator {
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    public static String generate(int len){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append(characters.charAt(rnd.nextInt(characters.length())));
        return sb.toString();
    }
}
