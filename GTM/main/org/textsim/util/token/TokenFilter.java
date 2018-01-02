package org.textsim.util.token;

/**
 * A {@code TokenFilter} pre-process the token for future use.
 * <p>
 * There are sub-interfaces:
 * <ul>
 * <li> {@link Stemmer}, a {@code TokenFilter} provides stemming process for token.
 * <li> {@link Lemmatizer}, a {@code TokenFilter} provides lemmatization process for token.
 * </ul>
 * This component provides extensive preprocess by piped architecture. The {@link #append(TokenFilter)} is called to
 * create pipe. A typical filtering pipe might look like:
 * <pre>
 *      TokenFilter1.append(TokenFilter2)
 *                  .append(TokenFilter3.append(TokenFilter3_1)
 *                                      .append(TokenFilter3_2))
 *                  .append(TokenFilter4)
 *                  ...
 * </pre>
 * 
 * @author Mei
 */
public abstract class TokenFilter {

    /**
     * The next {@code TokenFilter} in the filtering pipe.
     * This value should be assigned by {@link #append(TokenFilter)}.
     */
    protected TokenFilter next;

    protected TokenFilter() {
        this.next = null;
    }

    /**
     * Filter the given token.
     *
     * @param  token  The token to be filtered.
     *
     * @return A token pre-processed by all the applied {@TokenFilter}s.
     */
    public String filter(String token) {
        // Pass the copy of the original string, so that the modification in parameter will not affect the original object.
        String filtered = filterImpl(new String(token));
        return this.next == null ? filtered : this.next.filter(filtered);
    }

    /**
     * The underlying implementation of the filtering function.
     *
     * @param  token  A <i>deep copy</i> of the token to be filtered.
     *
     * @return A pre-processed token.
     */
    protected abstract String filterImpl(String token);
    
    /**
     * Append the given {@code TokenFilter} to the end of the filting pipe.
     *
     * @param  tf  A Token filter.
     *
     * @return Current object, which is a piped token filters with the given {@code TokenFilter} as the last step of the
     * filting pipe.
     */
    public TokenFilter append(TokenFilter tf) {
        TokenFilter last = this;
        while (last.next != null) {
            last = last.next;
        }
        last.next = tf;
        return this;
    }
}
