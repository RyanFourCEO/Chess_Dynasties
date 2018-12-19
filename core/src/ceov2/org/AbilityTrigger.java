package ceov2.org;

//the class that controls what has to happen for an ability to trigger
//this class will test to see if all the conditions are met for an ability
public class AbilityTrigger {
    //the index of the trigger
    int triggerIndex;
    //how many of a thing this ability needs to trigger
    //for example, if a piece needs to kill 3 pieces this value would be set to 3
    //if a piece needed to move four times this would be set to 4
    //more of these variables may be needed in the future
    int requiredNumber;


    public AbilityTrigger(int triggerIndex, int requiredNumber) {
        this.triggerIndex = triggerIndex;
        this.requiredNumber = requiredNumber;
    }
    //Unused Methods
    public AbilityTrigger(int triggerIndex) {
        this.triggerIndex = triggerIndex;
    }
}
