package edu.uob;

import java.util.ArrayList;

public class Parser {

    //TODO: CREATE TABLE marks (name, mark, pass);
    static void parse(ArrayList<String> commandArr) {
       isCommand(commandArr);
    }

    public static boolean isCommand(ArrayList<String> commandArr) {
        //check if there is a semicolon at the end
        //if true call isCommandType on
            //return value of isCommandType
        //else return false
    }

    public static boolean isCommandType(String command) {
        //switch case statement with these possible options
        //<Use> | <Create> | <Drop> | <Alter> | <Insert> | <Select> | <Update> | <Delete> | <Join>
        //if doesn't fit any of these then return back to isCommand as false
    }

    public static boolean () {

    }
}
