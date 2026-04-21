package uk.co.jackoftrades.background;

public interface AngbandModule {
    String getName();

    void init();

    void cleanup();
}