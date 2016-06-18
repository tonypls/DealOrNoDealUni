package newdeal;

import java.util.HashMap;

/**
 * Player class holds a players name. 
 * 
 * Created by 
 * @author Andre Cowie 14862344 on 5/04/2016.
 * @author Tony van Swet 0829113
 */
public class Player {
    private String name;
    
    //Again we believe this class to be rather self-explainatory.

    public Player(String contestant_name){
        setName(contestant_name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this.name.equals(((Player)o).name)){
            return true;
        }
        return false;
    }
    
}
