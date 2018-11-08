package ceov2.org;

public class AbilityEffect {
    boolean inEffect = false;
    //the index of the effect
    int effectIndex;
    //these variables hold all the possible information about how a status should be executed
    //stores things like the index of a status that should be applied, the name of a piece that
    //should be summoned, the length a status should last for etc.
    String effectVar1 = "";
    String effectVar2 = "";
    String effectVar3 = "";

    //constructors for all possible amounts of extra variables.
    //some effects need no extra variables to describe them, some do
    public AbilityEffect(int effectIndex) {
        this.effectIndex = effectIndex;
    }

    public AbilityEffect(int effectIndex, String var1, String var2, String var3) {
        this.effectIndex = effectIndex;
        effectVar1 = var1;
        effectVar2 = var2;
        effectVar3 = var3;
    }

    public AbilityEffect(int effectIndex, String var1, String var2) {
        this.effectIndex = effectIndex;
        effectVar1 = var1;
        effectVar2 = var2;

    }

    public AbilityEffect(int effectIndex, String var1) {
        this.effectIndex = effectIndex;
        effectVar1 = var1;
    }

    void setEffectVariables(int numOfEffects, String[] effects) {
        if (numOfEffects == 1) {

            effectVar1 = effects[0];
        }
        if (numOfEffects == 2) {
            effectVar1 = effects[0];
            effectVar2 = effects[1];
        }

        if (numOfEffects == 3) {
            effectVar1 = effects[0];
            effectVar2 = effects[1];
            effectVar3 = effects[2];
        }

    }
}
