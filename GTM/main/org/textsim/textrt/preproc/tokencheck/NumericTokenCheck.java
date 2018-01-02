package org.textsim.textrt.preproc.tokencheck;

import java.util.regex.Pattern;

/**
 * Provide judgment rule for <i>numeric token</i> (which will be stored in pre-processed numeric word file
 * (<code>-num.ic</code>) for further processing).
 * 
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public class NumericTokenCheck
        implements TokenCheckable {

    private Pattern acceptPattern;

    /**
     * Initializes a newly created object with a compiled {@code Pattern}.
     */
    public NumericTokenCheck() {

        acceptPattern = Pattern.compile(".*[0-9].*");

    }

    /**
     * Checks whether a token is a <i>numeric token</i>.
     * 
     * @param token  the token string.
     *
     * @return {@code true} if the token string meets the requirement.
     */
    @Override
    public boolean matches(String token) {

        if (acceptPattern.matcher(token).matches())
            return true;
        else
            return false;
    }
}
