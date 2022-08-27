package fr.redcraft.economy.helper;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Helper {

    public static boolean isInt(String str)
    {
        try {
            Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int getDistance(Player player,Player target){
        Location playerloc = player.getLocation();
        Location targetloc = target.getLocation();
        int distance = (int) playerloc.distance(targetloc);
        return distance;
    }

    public static int getPercent(int distance){
        int percent = 0;

        if (distance <100) {percent = 2;}
        else if(distance >100	  && distance <200) {percent = 10;}
        else if(distance >200	  && distance <300) {percent = 20;}
        else if(distance >300	  && distance <400) {percent = 30;}
        else if(distance >400	  && distance <500) {percent = 40;}
        else if(distance >500	  && distance <600) {percent = 50;}
        else if(distance >600	  && distance <700) {percent = 60;}
        else if(distance >700	  && distance <800) {percent = 70;}
        else if(distance >800 	  && distance <900) {percent = 80;}
        else if(distance >900 	  && distance <1000) {percent = 90;}
        else if(distance >1000    && distance <5000) {percent = 100;}
        else if(distance >5000    && distance <10000) {percent = 500;}
        else if(distance >10000   && distance <30000) {percent = 1000;}
        else if(distance >30000   && distance <70000) {percent = 2000;}
        else if(distance >70000   && distance <100000) {percent = 6000;}
        else if(distance >100000 ) {percent = 60000;}
        return percent;
    }
}

