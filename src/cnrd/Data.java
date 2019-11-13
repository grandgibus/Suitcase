package cnrd;

public class Data {

    public static final String PATH = CNRD.RASPBERRYPI ? "/usr/lib/arm-linux-gnueabihf" : "C:\\Program Files\\VideoLAN\\VLC";
    public static final String RESOURCES = CNRD.RASPBERRYPI ? "/home/pi/Desktop/CNRDData/" : "E:\\CNRDData\\";

    public static final String PREFIX = "CNRD";
    public static final String TITLE = "Concours National de la Resistance et Déportation";
    public static final String INFO_NO_VLC = "VLC n'a pas été détecté, le programme ne peut pas se lancer";

    public static String getImage(String src){
        return getAssetsLink() + "img/" + src;
    }

    public static String getVideo(String src){
        return getAssetsLink() + "videos/" + src;
    }

    public static String getStep(String src) {
        return getAssetsLink() + "steps/" + src + ".json";
    }

    public static String getStory(String src){
        return getAssetsLink() + src;
    }

    public static String getAssetsLink(){
        return RESOURCES;
    }
}
