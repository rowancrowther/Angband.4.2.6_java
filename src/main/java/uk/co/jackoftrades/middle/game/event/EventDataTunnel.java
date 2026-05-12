package uk.co.jackoftrades.middle.game.event;

public class EventDataTunnel implements GameEventData {
    private int nstep;      // The total number of tunnelling steps made
    private int npierce;    // The total number of wall piercings for rooms
    private int ndug;       // The number of times excavated (excluding wall piercings)

    private int dstart;     // The 'city block' distance between the starting point and the tunnel goal
    // i.e. ABS(start.x - end.x) + ABS(start.y - end.y)
    private int dend;       // The 'city block' distance between the final point in the tunnel and the goal
    //  dend value of 0 means the tunnel reached its goal

    private boolean early;  // Whether the tunnel was terminated by the random early termination criteria

    public EventDataTunnel(int nstep, int npierce, int ndug, int dstart, int dend, boolean early) {
        this.nstep = nstep;
        this.npierce = npierce;
        this.ndug = ndug;
        this.dstart = dstart;
        this.dend = dend;
        this.early = early;
    }

    public int getNstep() {
        return nstep;
    }

    public int getNpierce() {
        return npierce;
    }

    public int getNdug() {
        return ndug;
    }

    public int getDstart() {
        return dstart;
    }

    public int getDend() {
        return dend;
    }

    public boolean isEarly() {
        return early;
    }
}