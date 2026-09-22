module uk.co.jackoftradesltd {
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.antlr.antlr4.runtime;
    requires java.desktop;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires java.sql;
    requires org.apache.logging.log4j.core;

    exports uk.co.jackoftradesltd;
    exports uk.co.jackoftradesltd.channel;
}