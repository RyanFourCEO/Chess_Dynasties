package ceov2.org;

public class Animation { // used for animations

    boolean notOff; // used to determine if to play animations

    int speed; // explains itself

    Animation(){
        notOff = true;
        speed = 100;
    }

    Animation(int s){ // initialize with speed
        speed = s;
    }

    boolean toggleOff(){ // change if animations are off or not
        notOff = !notOff;
        return notOff;
    }

    void playAnimation(){

    }
}
