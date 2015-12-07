package metropolia.fi.suondbubbles.bubble;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import metropolia.fi.suondbubbles.layouts.FixedLayout;

public class BubbleParentLayoutRandomizer {
    private ArrayList<FixedLayout> linesList;
    private int lines;
    private ArrayList<Integer> linesIntegersList;
    private final String DEBUG_TAG = "Randomizer";

    public BubbleParentLayoutRandomizer(ArrayList<FixedLayout> linesList){
        this.linesList = linesList;
        init();
    }

    private void init() {
        lines = linesList.size();
        linesIntegersList = new ArrayList<>();
        addIntegersToList();
    }

    private void addIntegersToList() {
        for (int i = 0; i < lines; i++){
            linesIntegersList.add(i);
        }
    }

    public int[] generateRandomIDs(int amount, int firstIndex){
        Collections.shuffle(linesIntegersList);
        Log.d(DEBUG_TAG, "shuffled indexes are: " + linesIntegersList.toString());

        int[] indexes = new int[amount];
        int matchIndex = -1;
        boolean matchFound = false;

        for(int i = 0; i < amount; i++){
            indexes[i] = linesIntegersList.get(i);
            if(linesIntegersList.get(i) == firstIndex){
                matchIndex = i;
                matchFound = true;
            }
        }

        if(firstIndex != -1) {
            if (matchFound)
                swap(indexes, matchIndex);
            else
                indexes[0] = firstIndex;
        }

        return indexes;
    }

    private void swap(int[] indexes, int matchIndex) {
        int temp = indexes[0];

        indexes[0] = indexes[matchIndex];
        indexes[matchIndex] = temp;
    }

}
