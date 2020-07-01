package org.codespeak.distribution.client.data;

/**
 * An enum listing dependency flags
 *
 * @author Vector
 */
public enum DependencyFlags {
    DISPLAY_ON_CLIENT(1),
    DISPLAY_ON_FRONTEND(2);
    
    private final long flagBit;
    
    private DependencyFlags(int flagBit) {
        this.flagBit = (long) Math.pow(2L, flagBit - 1);
    }
    
    /**
     * Gets this dependency flag
     * @return dependency flag
     */
    public long getFlagBit() {
        return flagBit;
    }

    /**
     * Checks if the specified flags contains this flag bit
     * @param flags flags to check
     * @return if the specified flags contains this flag bit
     */
    public boolean in(long flags) {
        return ((flags & this.flagBit) == this.flagBit);
    }
    
}
