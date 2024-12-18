package ucore.input;

import com.badlogic.gdx.controllers.PovDirection;
import ucore.util.OS;

public class XboxBinds{
    // Buttons
    public static final int A;
    public static final int B;
    public static final int X;
    public static final int Y;
    public static final int GUIDE;
    public static final int L_BUMPER;
    public static final int R_BUMPER;
    public static final int BACK;
    public static final int START;
    public static final int DPAD_UP;
    public static final int DPAD_DOWN;
    public static final int DPAD_LEFT;
    public static final int DPAD_RIGHT;
    public static final int L_STICK;
    public static final int R_STICK;

    // Axes
    /** left trigger, -1 if not pressed, 1 if pressed **/
    public static final int L_TRIGGER;
    /** right trigger, -1 if not pressed, 1 if pressed **/
    public static final int R_TRIGGER;
    /** left stick vertical axis, -1 if up, 1 if down **/
    public static final int L_STICK_VERTICAL_AXIS;
    /** left stick horizontal axis, -1 if left, 1 if right **/
    public static final int L_STICK_HORIZONTAL_AXIS;
    /** right stick vertical axis, -1 if up, 1 if down **/
    public static final int R_STICK_VERTICAL_AXIS;
    /** right stick horizontal axis, -1 if left, 1 if right **/
    public static final int R_STICK_HORIZONTAL_AXIS;

    static{
        if(OS.isWindows){
            A = 0;
            B = 1;
            X = 2;
            Y = 3;
            GUIDE = 8;
            L_BUMPER = 4;
            R_BUMPER = 5;
            BACK = 6;
            START = 7;
            DPAD_UP = PovDirection.north.ordinal();
            DPAD_DOWN = PovDirection.south.ordinal();
            DPAD_LEFT = PovDirection.west.ordinal();
            DPAD_RIGHT = PovDirection.east.ordinal();
            L_TRIGGER = 4;
            R_TRIGGER = 5;
            L_STICK_VERTICAL_AXIS = 1;
            L_STICK_HORIZONTAL_AXIS = 0;
            L_STICK = 9;
            R_STICK_VERTICAL_AXIS = 3;
            R_STICK_HORIZONTAL_AXIS = 2;
            R_STICK = 10;
        }else if(OS.isLinux || OS.isMac){
            A = 0;
            B = 1;
            X = 2;
            Y = 3;
            GUIDE = 8;
            L_BUMPER = 4;
            R_BUMPER = 5;
            BACK = 6;
            START = 7;
            DPAD_UP = PovDirection.north.ordinal();
            DPAD_DOWN = PovDirection.south.ordinal();
            DPAD_LEFT = PovDirection.west.ordinal();
            DPAD_RIGHT = PovDirection.east.ordinal();
            L_TRIGGER = 2;
            R_TRIGGER = 5;
            L_STICK_VERTICAL_AXIS = 1;
            L_STICK_HORIZONTAL_AXIS = 0;
            L_STICK = 9;
            R_STICK_VERTICAL_AXIS = 4;
            R_STICK_HORIZONTAL_AXIS = 3;
            R_STICK = 10;
        }else if(OS.isAndroid){
            A = 96;
            B = 97;
            X = 99;
            Y = 100;
            GUIDE = 110;
            L_BUMPER = 102;
            R_BUMPER = 103;
            BACK = 109;
            START = 108;
            DPAD_UP = 19;
            DPAD_DOWN = 20;
            DPAD_LEFT = 21;
            DPAD_RIGHT = 22;
            L_TRIGGER = 17;
            R_TRIGGER = 18;
            L_STICK_VERTICAL_AXIS = 1;
            L_STICK_HORIZONTAL_AXIS = 0;
            L_STICK = 106;
            R_STICK_VERTICAL_AXIS = 14;
            R_STICK_HORIZONTAL_AXIS = 11;
            R_STICK = 107;
        }else{ //fallback controls.
            A = -1;
            B = -1;
            X = -1;
            Y = -1;
            GUIDE = -1;
            L_BUMPER = -1;
            R_BUMPER = -1;
            L_TRIGGER = -1;
            R_TRIGGER = -1;
            BACK = -1;
            START = -1;
            DPAD_UP = -1;
            DPAD_DOWN = -1;
            DPAD_LEFT = -1;
            DPAD_RIGHT = -1;
            L_STICK_VERTICAL_AXIS = -1;
            L_STICK_HORIZONTAL_AXIS = -1;
            L_STICK = -1;
            R_STICK_VERTICAL_AXIS = -1;
            R_STICK_HORIZONTAL_AXIS = -1;
            R_STICK = -1;
            /*A = Xbox.A;
            B = Xbox.B;
            X = Xbox.X;
            Y = Xbox.Y;
            GUIDE = Xbox.GUIDE;
            L_BUMPER = Xbox.L_BUMPER;
            R_BUMPER = Xbox.R_BUMPER;
            L_TRIGGER = Xbox.L_TRIGGER;
            R_TRIGGER = Xbox.R_TRIGGER;
            BACK = Xbox.BACK;
            START = Xbox.START;
            DPAD_UP = Xbox.DPAD_UP;
            DPAD_DOWN = Xbox.DPAD_DOWN;
            DPAD_LEFT = Xbox.DPAD_LEFT;
            DPAD_RIGHT = Xbox.DPAD_RIGHT;
            L_STICK_VERTICAL_AXIS = Xbox.L_STICK_VERTICAL_AXIS;
            L_STICK_HORIZONTAL_AXIS = Xbox.L_STICK_HORIZONTAL_AXIS;
            R_STICK_VERTICAL_AXIS = Xbox.R_STICK_VERTICAL_AXIS;
            R_STICK_HORIZONTAL_AXIS = Xbox.R_STICK_HORIZONTAL_AXIS;
            L_STICK = -1;
            R_STICK = -1;*/
        }
    }
}
