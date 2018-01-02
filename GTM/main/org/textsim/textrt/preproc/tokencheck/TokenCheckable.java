package org.textsim.textrt.preproc.tokencheck;

/**
 * Interface for token check modulars.
 * A judgment rule should be defined in the implementation classes.
 *
 * @author  <a href="mailto:mei.jie@hotmail.com">Jie Mei</a>
 */
public interface TokenCheckable {
 
    /**
     * Check whether a token can be accepted.
     * 
     * @param  token  the token string.
     *
     * @return {@code true} if the token string meets the requirement.
     */
    public boolean matches(String token);
    
}
