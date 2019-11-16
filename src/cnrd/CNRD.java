package cnrd;

import cnrd.logic.Story;
import cnrd.logic.Story.StoryJson;
import cnrd.tlc5940.TLC5940Controller;
import com.google.gson.Gson;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CNRD {

    public static CNRD application;

    public static boolean RASPBERRYPI = false;
    public static boolean IGNORE_VIDEOS = false;

    public static boolean NO_WINDOWS_MODE = false;

    public static boolean VLC_DISCOVERED = false;
    public static int TICK_TIME = 40;

//    private static boolean videoPlaying = false;
//    private static boolean videoVisible = false;

    private static double volumeOffset = 0.0D;

    public static Gson GSON = new Gson();

    private JFrame window;
    private JPanel videoContainer;

    private CNRDPanel panel;

    private ImageIcon icon = new ImageIcon(Data.getImage("logo.png"));

    private EmbeddedMediaPlayerComponent mediaPlayer;
    private MediaPlayer player;

    private StoryPlayer currentStory;

    public static void main(String[] args) {
        RASPBERRYPI = !(System.getProperties().getProperty("os.name").toLowerCase().contains("win"));
        SwingUtilities.invokeLater(() -> application = new CNRD(args));
    }

    public CNRD(String[] args) {
        init();
        initWindow();
        run();
    }

    public static void changeVolume(boolean up){
        if(up && volumeOffset + 0.1D < 1.0D){
            volumeOffset += 0.1D;
        }else if(!up && volumeOffset - 0.1D > -1.0D){
            volumeOffset -= 0.1D;
        }
        CNRD.application.setVolume();
    }

    private void init() {

        if(RASPBERRYPI) {
            System.setProperty("libvlc", Data.PATH);
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), Data.PATH);
            VLC_DISCOVERED = true;
        }else {
            VLC_DISCOVERED = new NativeDiscovery().discover();
        }
    }

    private void run() {

        CNRD.debug("Debug mode: " + !CNRD.RASPBERRYPI);

        if (!VLC_DISCOVERED) {
            JLabel info = new JLabel();
            info.setText(Data.INFO_NO_VLC);
            info.setVerticalAlignment(JLabel.CENTER);
            info.setHorizontalAlignment(JLabel.CENTER);
            window.add(info);
        } else {

            mediaPlayer = new EmbeddedMediaPlayerComponent();
            player = mediaPlayer.getMediaPlayer();

            panel = new CNRDPanel();

            videoContainer = new JPanel();
            videoContainer.setLayout(new BorderLayout());
            videoContainer.add(mediaPlayer, BorderLayout.CENTER);
            videoContainer.setVisible(false);

            panel.setLayout(new BorderLayout());
            panel.add(videoContainer, BorderLayout.CENTER);

            if(!NO_WINDOWS_MODE) {
                window.add(panel);
                updateRatio();
            }

            InputOutputsControl.init();
            InputOutputsControl.IsKeyPressed.init();

            TLC5940Controller.init();


            player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

                @Override
                public void mediaPlayerReady(MediaPlayer mediaPlayer){
                    CNRD.debug("Playing video");
                    showVideo();
                    setVolume();
                }

                @Override
                public void finished(MediaPlayer mediaPlayer) {
//                    CNRD.debug(mediaPlayer.status().time() + " - " + mediaPlayer.status().length());
                    CNRD.debug("Stopping video");
//                    videoPlaying = false;
                    hideVideo();
                    mute();
                    if (currentStory != null) {
                        currentStory.videoFinished();
                    }
                }

            });

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(this::updateLoop, 0, TICK_TIME, TimeUnit.MILLISECONDS);

//            service.scheduleAtFixedRate(InputOutputsControl::test, 0, 400, TimeUnit.MILLISECONDS);
        }

    }

    private void setVolume(){
        this.mediaPlayer.getMediaPlayer().setVolume(this.currentStory != null ? (int)(Math.min(Math.max(this.currentStory.getVolume() + volumeOffset, 0.0D), 2.0D) * 100) : 0);
    }

    private void mute(){
        this.mediaPlayer.getMediaPlayer().setVolume(0);
    }

    private void playStory(Story story) {
        this.currentStory = new StoryPlayer(story, player);
        this.currentStory.start();
    }

    public static Story getStory(String src) {
        try {
            File storyFile = new File(Data.getStory(src));
            FileReader storyReader = new FileReader(storyFile);
            StoryJson storyJson = GSON.fromJson(storyReader, StoryJson.class);
            return storyJson.getStory();
        } catch (FileNotFoundException e) {
            debug("Story file " + src + " not found");
        }
        return null;
    }

    public void hideVideo() {
        videoContainer.setVisible(false);
//        videoVisible = false;
    }

    public void showVideo() {
        videoContainer.setVisible(true);
//        videoVisible = true;
    }

    public void updateLoop() {

        if (currentStory != null && !currentStory.isFinished()) {
            currentStory.updateAllEvents();
        }

        //When no story is played, if menu is faded after beeing pressed then start to play the story
        if(currentStory == null){
            if(!panel.hasEnded()){
                if(panel.choiceMenu.isMenuFaded()){
                    playStory(panel.choiceMenu.getChoice());
                }
            }
        }

        //If the current story just finished
        if(currentStory != null && currentStory.isFinished()){

            currentStory = null;

            if(!panel.hasEnded()){

                if(Data.REMOVE_CHOICE_AFTER_PLAYED){
                    panel.choiceMenu.removeCurrentChoice();
                }

                //If should switch to end menu
                if(!panel.choiceMenu.hasChoicesLeft()){
                    panel.endMenu();
                    panel.endMenu.fadeIn();
                }else{
                    panel.choiceMenu.fadeIn();
                }
            }
        }

        panel.repaint();
        InputOutputsControl.update();


    }

    public void buttonPressed(){

        if(!panel.hasEnded()){
            if(panel.choiceMenu.canRunStory()){
                panel.choiceMenu.fadeOut();
            }
        }

    }

    public void buttonTaped(){
        this.panel.buttonTapped();
    }

    private void initWindow() {

        if(!NO_WINDOWS_MODE) {

            window = new JFrame(Data.TITLE);
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            window.setMinimumSize(new Dimension(1600, 900));
            window.setIconImage(icon.getImage());
            window.setBackground(Color.WHITE);

            if(RASPBERRYPI) {

                window.setUndecorated(true);
                window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
                device.setFullScreenWindow(window);

                supressCursor();
            }

            window.setVisible(true);

        }
    }

    private void updateRatio() {
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension screenSize = window.getSize();

        double height = screenSize.getHeight();
        double width = screenSize.getWidth();

        String ratio = (int) width + ":" + (int) height;
        mediaPlayer.getMediaPlayer().setCropGeometry(ratio);
        debug("MediaPlayer cropped with aspect ratio: " + ratio);
    }

    public void supressCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        window.getContentPane().setCursor(blankCursor);
    }

    public static void debug(Object str) {
        System.out.println("[" + Data.PREFIX + "]: " + str);
    }
}
