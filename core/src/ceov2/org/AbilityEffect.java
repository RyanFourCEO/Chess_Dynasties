package ceov2.org;

public class AbilityEffect {
    //the index of the effect
    int effectIndex;
    //how long the effect lasts for, i.e this would be 5 if a piece should be stunned for 5 turns
    //may need more than 1 variable in the future
    int effectLength;
    public AbilityEffect(int effectIndex){
        this.effectIndex=effectIndex;
    }

}
