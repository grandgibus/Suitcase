package cnrd.logic;

import cnrd.CNRD;
import cnrd.Data;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Story {

    private List<Step> steps;
    private Color color;

    public Story(List<Step> steps, Color color){
        this.steps = steps;
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public List<Step> getSteps(){
        return this.steps;
    }

    public static class StoryJson {

        private List<String> steps;
        private int color = 0xFFFFFF;

        public Story getStory(){

            List<Step> steps = new ArrayList<>();

            for(String string : this.steps){

                try{

                    FileReader reader = new FileReader(new File(Data.getStep(string)));
                    steps.add(CNRD.GSON.fromJson(reader, Step.StepJson.class).getStep());


                }catch (FileNotFoundException exception){
                    CNRD.debug("Could not find the step \"" + string + "\" file. Step ingored");
                }

            }

            return new Story(steps, new Color(color));

        }

    }
}
