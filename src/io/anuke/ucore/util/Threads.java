package io.anuke.ucore.util;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;

/**Class for thread-specific utilities. Assumes the application has two threads: logic and graphics.
 * In a single-threaded environment, the one and only thread is both logic and graphics.*/
public class Threads{
    private static Method sleepMethod, notifyMethod;
    private static ThreadInfoProvider info = new ThreadInfoProvider(){
        @Override
        public boolean isOnLogicThread(){
            return true;
        }

        @Override
        public boolean isOnGraphicsThread(){
            return true;
        }
    };

    public static void wait(Object object){
        if(Gdx.app.getType() == ApplicationType.WebGL) return;

        try{
            if(sleepMethod == null){
                sleepMethod = ClassReflection.getMethod(Object.class, "wait");
            }
            sleepMethod.invoke(object);
        }catch(Throwable r){
            r.printStackTrace();
        }
    }

    public static void notify(Object object){
        if(Gdx.app.getType() == ApplicationType.WebGL) return;

        try{
            if(notifyMethod == null){
                notifyMethod = ClassReflection.getMethod(Object.class, "notify");
            }
            notifyMethod.invoke(object);
        }catch(Throwable r){
            r.printStackTrace();
        }
    }

    public static void setThreadInfoProvider(ThreadInfoProvider prov){
        info = prov;
    }

    /**Returns whether the logic thread is currently active.
     * In a single-threaded environment, this should always return true.*/
    public static boolean isLogic(){
        return info.isOnLogicThread();
    }

    /**Asserts that a method is being called on the logic thread.*/
    public static void assertLogic(){
        if(!info.isOnLogicThread()) throw new UnsupportedOperationException("This method can only be called on the logic thread.");
    }

    /**Asserts that a method is being called on the graphics thread.*/
    public static void assertGraphics(){
        if(!info.isOnGraphicsThread()) throw new UnsupportedOperationException("This method can only be called on the graphics thread.");
    }

    /**Provides information about the currently running thread.
     * The base implementation always returns true.*/
    public interface ThreadInfoProvider{
        boolean isOnLogicThread();
        boolean isOnGraphicsThread();
    }
}