package uk.co.jackoftrades.middle.game.globals;

class World {
    private int level;
    private String levelName;

    private int up;
    private int down;

    public World(int level, String levelName, int up, int down) {
        this.level = level;
        this.levelName = levelName;
        this.up = up;
        this.down = down;
    }

    @Override
    public String toString() {
        return "World{" +
                "level=" + level +
                ", levelName='" + levelName + '\'' +
                ", up=" + up +
                ", down=" + down +
                '}';
    }
}