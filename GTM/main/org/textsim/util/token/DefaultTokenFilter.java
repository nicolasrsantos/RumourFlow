package org.textsim.util.token;

public class DefaultTokenFilter
        extends TokenFilter {

    public DefaultTokenFilter() {
        super();
    }

    /*
     * @see org.textsim.util.token.TokenFilter#filterImpl(java.lang.String)
     */
    @Override
    protected String filterImpl(String token) {
        return token;
    }
}
