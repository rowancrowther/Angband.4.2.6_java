package uk.co.jackoftrades.background.io.parsers;

/**
 * A stem class to handle the specification of a parser
 */
public class ParserSpec {
    private int type;
    private String name;

    /**
     * Getter for type
     * @return the type of this spec
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for type
     * @param type the type to set this spec to
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Getter for name
     * @return the name of this spec
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name the new name for this spec
     */
    public void setName(String name) {
        this.name = name;
    }
}