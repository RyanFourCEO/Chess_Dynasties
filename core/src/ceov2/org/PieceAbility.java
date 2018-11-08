package ceov2.org;

import java.util.ArrayList;

public class PieceAbility {
    //array of all triggers
    ArrayList<AbilityTrigger> allTriggers = new ArrayList<AbilityTrigger>();

    //the effect
    AbilityEffect effect;


    public PieceAbility(int numberOfTriggers, int[] triggers, int[] triggerRequirement, int numberOfEffectVariables, int effect, String[] effectVariables) {
        //all triggers are initialized
        for (int x = 0; x != numberOfTriggers; x++) {
            allTriggers.add(new AbilityTrigger(triggers[x], triggerRequirement[x]));
        }

        //effect initialized
        this.effect = new AbilityEffect(effect);
        this.effect.setEffectVariables(numberOfEffectVariables, effectVariables);


    }


}
