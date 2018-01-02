package org.textsim.util;

/**
 * Provide utilities for token processing.
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class TokenUtils {

    /**
     * Remove the special characters at the beginning and the end of the token.
     * 
     * @param  token  the token string to be processing.
     *
     * @return The cleaned token string. Return {@code null} if token has no English character.
     */
    public static String clean(String token) {

        int strInd = 0;
        int endInd = token.length() - 1;
        while (strInd <= endInd) {
            if (!Character.isLetter(token.charAt(strInd)) && !Character.isDigit(token.charAt(strInd))) {
                strInd++;
            } else if (!Character.isLetter(token.charAt(endInd)) && !Character.isDigit(token.charAt(endInd))) {
                endInd--;
            } else {
                break;
            }
        }
        return strInd > endInd ? null : token.substring(strInd, endInd + 1);
    }

}
