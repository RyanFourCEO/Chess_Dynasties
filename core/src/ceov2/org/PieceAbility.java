package ceov2.org;

import java.util.ArrayList;

public class PieceAbility {
ArrayList<AbilityTrigger> allTriggers=new ArrayList<AbilityTrigger>();
AbilityEffect effect;
    public PieceAbility(int numberOfTriggers,int[] triggers,int effect){
		for(int x=0;x!=numberOfTriggers;x++){
		    allTriggers.add(new AbilityTrigger(triggers[x]));
        }
        this.effect=new AbilityEffect(effect);

    }



}
