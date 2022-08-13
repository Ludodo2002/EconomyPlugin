package fr.redcraft.economy.user;

import java.util.UUID;

public class User {

    public UUID uuid;
    public String name;
    public long firstconnexion;
    public long lastconnexion;
    public int coin;

    public User(UUID uuid,String name,long firstconnexion,long lastconnexion,int coin){
        this.uuid = uuid;
        this.name = name;
        this.firstconnexion = firstconnexion;
        this.lastconnexion = lastconnexion;
        this.coin = coin;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public long getFirstconnexion() {
        return firstconnexion;
    }

    public long getLastconnexion() {
        return lastconnexion;
    }

    public int getCoin() {
        return coin;
    }
}
