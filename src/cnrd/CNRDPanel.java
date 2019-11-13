package cnrd;

import cnrd.logic.Story;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CNRDPanel extends JPanel {

    public static final int FADED_COMPLETE =  1 * 1000 / CNRD.TICK_TIME;

    private boolean hasEnded = false;

    public EndMenu endMenu;
    public ChoiceMenu choiceMenu;

    public CNRDPanel(){
        this.choiceMenu = new ChoiceMenu(this);
        this.endMenu = new EndMenu(this);

        this.setBackground(Color.WHITE);
    }

    public boolean hasEnded(){
        return hasEnded;
    }

    public void endMenu(){
        this.hasEnded = true;
    }

    public void buttonTapped(){
        if(!hasEnded() && this.choiceMenu.isAvailable()){
            this.choiceMenu.switchChoice();
        }
    }


    public Image getImage(String src){
        return Toolkit.getDefaultToolkit().getImage(Data.getImage(src));
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        Menu menuToDraw;

        if(!hasEnded()){
            menuToDraw = choiceMenu;
        }else{
            menuToDraw = endMenu;
        }

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        menuToDraw.draw(g2d);

    }

//    public void update(){
//        this.endMenu.update();
//        this.choiceMenu.update();
//    }

    public void drawImageCenter(int x, int y, float size, Graphics2D g, Image image){

        int imgWidth = (int)(size * this.getSize().getWidth());
        int imgHeight = (int)((float)image.getHeight(null) / image.getWidth(null) * imgWidth);

        g.drawImage(image, x - imgWidth / 2, y - imgHeight / 2, imgWidth, imgHeight, null);

    }

    public static class ChoiceMenu extends Menu{

        private Image mainImage;

        private List<StoryChoice> choices;
        private StoryChoice currentChoice;

        public ChoiceMenu(CNRDPanel panel) {
            super(panel);
            choices = new ArrayList<>();

            choices.add(new StoryChoice(CNRD.getStory("deporteebretonne.json"), panel.getImage("deporteebretonne.png")));
            choices.add(new StoryChoice(CNRD.getStory("communisme.json"), panel.getImage("communisme.png")));
            choices.add(new StoryChoice(CNRD.getStory("deportetsigane.json"), panel.getImage("deportetsigane.png")));
            choices.add(new StoryChoice(CNRD.getStory("temoignage.json"), panel.getImage("temoignage.png")));

            this.mainImage = panel.getImage("banniere.png");

            currentChoice = choices.get(0);
        }

        public boolean hasChoicesLeft(){
            return choices.size() > 0;
        }

        public boolean canRunStory(){
            return this.isAvailable() && this.hasChoicesLeft();
        }

        public Story getChoice(){
            return currentChoice.getStory();
        }

        public void switchChoice(){

            if(choices.size() > 0){
                int i = choices.indexOf(currentChoice);
                i++;
                if(i < choices.size()){
                    currentChoice = choices.get(i);
                }else{
                    currentChoice = choices.get(0);
                }
            }
        }

        public void removeCurrentChoice(){
            this.choices.remove(currentChoice);
            this.currentChoice = choices.size() > 0 ? choices.get(0) : null;
        }

        @Override
        public void draw(Graphics2D g) {

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F - (float)faded / FADED_COMPLETE));

            panel.drawImageCenter((int)(panel.getSize().getWidth() * 0.35), (int)(panel.getSize().getHeight() * 0.3D), 0.5F, g, mainImage);

//            int spacing = (int)(0.2F * panel.getSize().getWidth());
//
//            int nbOfChoices = choices.size();
            int i = 0;

            int baseX = (int)(panel.getSize().getWidth() * 0.25);
            int baseY = (int)(0.6D * panel.getSize().getHeight());

            for(StoryChoice choice : choices){

                int x = baseX;
                int y = baseY;
//

                if (i % 2 != 00) {
                    x += (0.33 * panel.getSize().getWidth());
                }
                if(i >= 2){
                    y += (0.15 * panel.getSize().getHeight());
                }

                float nameSize = 0.3F;

                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F - (float)faded / FADED_COMPLETE));
                panel.drawImageCenter(x, y, nameSize, g, choice.getImage());

                if(choice == currentChoice){
                    int size = 20;

                    float o = (float)((Math.sin((double)System.currentTimeMillis() / 300) + 1) / 2) * 0.7F + 0.3F - (float)faded / FADED_COMPLETE;
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(o, 0)));

                    g.setColor(choice.getStory().getColor());
                    g.fillOval(x - 60 - (int)(nameSize * panel.getSize().getWidth()) / 2, y - size / 2 + 10, size, size);

                }

                i++;
            }

        }
    }

    public static class StoryChoice {

        private Story story;
        private Image image;

        public StoryChoice(Story story, Image image) {
            this.story = story;
            this.image = image;
        }

        public Story getStory(){
            return story;
        }

        public Image getImage(){
            return image;
        }
    }

    public static class EndMenu extends Menu{

        private Image endImage;

        public EndMenu(CNRDPanel panel){
            super(panel);
            this.endImage = panel.getImage("end.png");

            this.faded = FADED_COMPLETE;
            this.isMenuFaded = true;
        }

        @Override
        public void draw(Graphics2D g) {

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1F - (float)faded / FADED_COMPLETE));
            panel.drawImageCenter((int)(panel.getSize().getWidth() / 2), (int)(panel.getSize().getHeight() / 2), 0.1F, g, endImage);

        }
    }

    public static abstract class Menu {

        protected CNRDPanel panel;

        public int faded = 0;
        private boolean isAvailable = true;

        protected boolean isMenuFaded = false;

        public Menu(CNRDPanel panel){
            this.panel = panel;
        }

        public abstract void draw(Graphics2D g);

//        private boolean shouldFadeOut = false;
//        private boolean shouldFadeIn = false;

        public void fadeOut(){

            isAvailable = false;
//            shouldFadeOut = true;

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> {
                faded++;
                if(faded >= FADED_COMPLETE){
                    isMenuFaded = true;
                    service.shutdown();

                }
            }, 0, CNRD.TICK_TIME, TimeUnit.MILLISECONDS);
        }

        public void fadeIn(){

            isMenuFaded = false;
//            shouldFadeIn = true;
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(() -> {
                faded--;

                if(faded <= 0){

                    faded = 0;
                    isAvailable = true;

                    service.shutdown();
                }
            }, 0, CNRD.TICK_TIME, TimeUnit.MILLISECONDS);
        }

//        public void update(){
//
//            if(shouldFadeOut) {
//                faded++;
//                if (faded >= FADED_COMPLETE) {
//                    isMenuFaded = true;
//                    shouldFadeOut = false;
//                    CNRD.debug("Stop fade out");
//                }
//            }
//
//
//            if(shouldFadeIn) {
//                faded--;
//
//                if (faded <= 0) {
//                    faded = 0;
//                    isAvailable = true;
//                    shouldFadeIn = false;
//                }
//            }
//
//        }

        public boolean isMenuFaded(){
            return isMenuFaded;
        }

        public boolean isAvailable(){
            return isAvailable;
        }
    }

}
