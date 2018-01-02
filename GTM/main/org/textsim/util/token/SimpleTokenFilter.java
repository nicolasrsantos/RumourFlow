package org.textsim.util.token;

public class SimpleTokenFilter
        extends TokenFilter {
    
    private boolean lower;

    public SimpleTokenFilter(boolean toLower) {
        super();
        this.lower = toLower;
    }

    /*
     * @see org.textsim.util.token.TokenFilter#filterImpl(java.lang.String)
     */
    @Override
    protected String filterImpl(String token) {
        String filtered = token;
        if (this.lower) {
            filtered.toLowerCase();
        }
        return null;
    }
}
