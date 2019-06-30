package ca.weizhi.fokotest;

public class Player {

    private int id;

    private String name;

    private String position;

    Player(int id,String name,String position){

        this.id=id;

        this.name=name;

        this.position=position;
    }

    public String getName() {

        return name;

    }

    public int getId() {

        return id;
    }

    public String getPosition() {

        return position;

    }
}
