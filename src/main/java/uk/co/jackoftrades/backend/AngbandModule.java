package uk.co.jackoftrades.backend;

public interface AngbandModule {
    String getName();

    void init();

    void cleanup();
}