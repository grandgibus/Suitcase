package cnrd;

import cnrd.logic.Couple;
import cnrd.logic.Step;
import cnrd.logic.Story;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.media.callback.seekable.RandomAccessFileMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StoryPlayer {

    private Story story;

    private MediaPlayer player;

    private List<Step> steps;
    private Step currentStep;
    private int currentStepIndex;


    private List<Couple> onLeds;
    private List<Couple> offLeds;

    private int stepCount;

    private boolean finished;

    public StoryPlayer(Story story, MediaPlayer player){
        this.story = story;
        this.steps = story.getSteps();
        this.stepCount = this.steps.size();
        this.player = player;
    }

    public void start(){

        if(!CNRD.IGNORE_VIDEOS){
            if(stepCount > 0){
                this.currentStep = steps.get(0);

                this.onLeds = currentStep.getOnLeds();
                this.offLeds = currentStep.getOffLeds();

                playStep(currentStep);
            }

        }else{
            this.finish();
        }

    }

    public void videoFinished(){
        executeLastEvents(this.offLeds, false);
        executeLastEvents(this.onLeds, true);
        startNextStep();
    }

    public double getVolume(){
        return this.currentStep.getVolume();
    }

    public void startNextStep(){

        long delay = currentStep != null ? this.currentStep.getDelayEnd() : 0;

        if(!isFinished() && this.currentStepIndex < stepCount - 1){

            this.currentStepIndex++;
            this.currentStep = steps.get(currentStepIndex);

            this.onLeds = currentStep.getOnLeds();
            this.offLeds = currentStep.getOffLeds();

            scheduleExecutorService(() -> playStep(currentStep), delay);

        }else{

            scheduleExecutorService(() -> finish(), delay);

        }

    }

    public boolean isFinished(){
        return finished;
    }

    public void updateAllEvents(){
        updateEvents(offLeds, false);
        updateEvents(onLeds, true);
    }

    public void updateEvents(List<Couple> events, boolean state){

        long time = player.getTime();

        List<Couple> toDelete = new ArrayList<>();

        for(Couple event : events){
            if(time > event.getTime()){
                InputOutputsControl.setLED(event.getId(), state);
                toDelete.add(event);
            }
        }

        events.removeAll(toDelete);
    }

    private void finish(){
        this.finished = true;
        InputOutputsControl.setAllOff();
    }

    private void playStep(Step step){

        if(player.isPlaying()){
            player.stop();
        }

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(
            () -> {
                player.playMedia(new RandomAccessFileMedia(new File(Data.getVideo(currentStep.getVideoName()))));
            }, 0, TimeUnit.MILLISECONDS
        );


    }

    private void executeLastEvents(List<Couple> events, boolean state){
        for(Couple e : events){
            InputOutputsControl.setLED(e.getId(), state);
        }
        events.clear();
    }

    private void scheduleExecutorService(Runnable runnable, long delay){
        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
        s.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

}
