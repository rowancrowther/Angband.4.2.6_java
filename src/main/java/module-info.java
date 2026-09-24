module uk.co.jackoftradesltd {
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.antlr.antlr4.runtime;
    requires java.desktop;
    requires com.sun.jna.platform;
    requires com.sun.jna;
    requires java.sql;
    requires org.apache.logging.log4j.core;
    requires org.apache.commons.lang3;
    requires org.apache.commons.text;

    exports uk.co.jackoftradesltd;
    exports uk.co.jackoftradesltd.channel;
}