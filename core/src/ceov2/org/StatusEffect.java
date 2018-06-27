package ceov2.org;

public class StatusEffect {
    //the index of the status
    int index;
//the length the status lasts, can be measured in turns, or in number of moves the piece makes or in the number of attacks the piece must take
    int statusEffectLength;

    //if the status is time based the length will be decreased at the end of each turn
    //some statuses decrease in other ways, i.e armoured is decreased whenever the piece is attacked by a melee ability
    boolean timeBased;
//when this is set to true, the status will be removed from the piece, and the status will no longer have any impact on the piece
    boolean setToBeRemoved =false;
//constructor
    public StatusEffect(int index, int statusEffectLength){
        this.index=index;
        this.statusEffectLength=statusEffectLength;

        //any of the following indices listed here have timebased marked as true, the index of armour is not included here as it is not timebased
   switch (index){
       case 0:
       case 1:
       case 2:
       case 3:
       case 4:
       case 5:
       case 6:
       case 7:
       case 10:
       case 13:
           timeBased=true;
           break;

   }
    }

    void setToBeRemoved(){
        setToBeRemoved=true;
    }

}
