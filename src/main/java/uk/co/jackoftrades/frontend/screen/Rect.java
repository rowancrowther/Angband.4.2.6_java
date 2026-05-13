package uk.co.jackoftrades.frontend.screen;

public class Rect {
    private long left;
    private long top;
    private long right;
    private long bottom;

    public Rect(long left, long top, long right, long bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public long getLeft() {
        return left;
    }

    public long getTop() {
        return top;
    }

    public long getRight() {
        return right;
    }

    public long getBottom() {
        return bottom;
    }
}