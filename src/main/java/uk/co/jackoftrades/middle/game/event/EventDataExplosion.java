package uk.co.jackoftrades.middle.game.event;

import uk.co.jackoftrades.middle.cave.Loc;

import java.util.ArrayList;

public class EventDataExplosion implements GameEventData {
    private int projType;       // Probably going to be replaced by an enum
    private int numGrids;
    private ArrayList<Integer> distanceToGrid;
    private boolean drawing;
    private ArrayList<Boolean> playerSeesGrid;
    private ArrayList<Loc> blastGrid;
    private Loc centre;

    public EventDataExplosion(int projType, int numGrids, ArrayList<Integer> distanceToGrid, boolean drawing,
                              ArrayList<Boolean> playerSeesGrid, ArrayList<Loc> blastGrid, Loc centre) {
        this.projType = projType;
        this.numGrids = numGrids;
        this.distanceToGrid = distanceToGrid;
        this.drawing = drawing;
        this.playerSeesGrid = playerSeesGrid;
        this.blastGrid = blastGrid;
        this.centre = centre;
    }

    public int getProjType() {
        return projType;
    }

    public int getNumGrids() {
        return numGrids;
    }

    public ArrayList<Integer> getDistanceToGrid() {
        return distanceToGrid;
    }

    public boolean isDrawing() {
        return drawing;
    }

    public ArrayList<Boolean> getPlayerSeesGrid() {
        return playerSeesGrid;
    }

    public ArrayList<Loc> getBlastGrid() {
        return blastGrid;
    }

    public Loc getCentre() {
        return centre;
    }
}