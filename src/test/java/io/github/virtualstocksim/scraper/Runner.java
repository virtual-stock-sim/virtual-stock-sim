package io.github.virtualstocksim.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
    I added this file because the test cases inherently take long due to delay (~10-15 seconds per stock)
    So we could have something for our demo if deemed necessary. Will remove after presentation
 **/
public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) throws IOException {

        Scraper example = new Scraper();
        logger.info(example.getDescriptionAndHistory("GOOGL",TimeInterval.ONEMONTH).toString());
        logger.info(example.getDescriptionAndHistory("BDX",TimeInterval.ONEMONTH).toString());
        logger.info(example.getDescriptionAndHistory("/GOOGL",TimeInterval.ONEMONTH).toString());       //error should be caught and printed in console (malformed ticker)
        logger.info(example.getDescriptionAndHistory("NONEXISTENT",TimeInterval.ONEMONTH).toString());  //redirect should be detected, stock does not exist on Yahoo Finance
    }
}
