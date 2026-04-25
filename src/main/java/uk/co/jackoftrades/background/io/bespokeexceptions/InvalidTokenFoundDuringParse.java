package uk.co.jackoftrades.background.io.bespokeexceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidTokenFoundDuringParse extends RuntimeException {
    private final Logger logger = LogManager.getLogger();

    public InvalidTokenFoundDuringParse(String message) {
        super(message);
        logger.error(message);
    }
}
