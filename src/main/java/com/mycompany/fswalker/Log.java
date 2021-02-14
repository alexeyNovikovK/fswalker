/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fswalker;

import java.util.logging.Logger;
import java.util.logging.Level;
/**
 *
 * @author test
 */
public class Log {
    
    public static void setStopOnError(){stopOnError = true;}
    public static void resetStopOnError(){stopOnError = false;}
    public static void error(String format, Object... params){
        logger.log(Level.SEVERE, format, params);
        if (stopOnError)
            throw new RuntimeException("Fatal error. Stop.");
    }
    public static void warning(String format, Object... params){
        logger.log(Level.WARNING, format, params);
    }
    public static void info(String format, Object... params){
        logger.log(Level.INFO, format, params);
    }
    
    private static Logger logger = Logger.getGlobal();
    private static boolean stopOnError = false;
}
