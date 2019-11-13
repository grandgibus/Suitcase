package cnrd.logic;

public class Couple {

    private long time;
    private int id;

    public Couple(long time, int id){
        this.time = time;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public int getId() {
        //If it is the third left for example, return index 2
        return id - 1;
    }
}
