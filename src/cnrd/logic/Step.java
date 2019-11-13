package cnrd.logic;

import java.util.ArrayList;
import java.util.List;

public class Step {

    private String videoName;

    private List<Couple> onLeds;
    private List<Couple> offLeds;

    private long delayEnd;

    private double volume;

    private Step(String videoName, List<Couple> onLeds, List<Couple> offLeds, long delayEnd, double volume){
        this.videoName = videoName;
        this.delayEnd = delayEnd;
        this.volume = volume;
        this.onLeds = onLeds;
        this.offLeds = offLeds;
    }

    public List<Couple> getOnLeds() {
        return onLeds;
    }

    public List<Couple> getOffLeds() {
        return offLeds;
    }

    public String getVideoName(){
        return this.videoName;
    }

    public long getDelayEnd(){
        return delayEnd;
    }

    public double getVolume() {
        return volume;
    }

    public class StepJson {

        private String videoName = "no_video";
        private long delayEnd = 0L;
        private double volume = 0.5D;

        private List<Couple> onLeds = new ArrayList<>();
        private List<Couple> offLeds = new ArrayList<>();


        public Step getStep(){

            return new Step(videoName, onLeds, offLeds, this.delayEnd, volume);

        }

    }

}
