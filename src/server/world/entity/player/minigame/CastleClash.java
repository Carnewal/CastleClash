package server.world.entity.player.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import server.core.worker.Worker;
import server.world.entity.Entity;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A basic free-form {@link Minigame} that can provide implementation for a wide
 * variety of different types of minigames.
 * 
 * @author lare96
 */
public class CastleClash extends Minigame {
    
    HashMap<Player, String> players;
    
    
    
    public CastleClash() {
        players = new HashMap<>();
    }
    
    public void addPlayer(Player player) {
        
        int zamy = 0; int sara = 0;
        
        //count amount of players on both teams
        for(String team : players.values()) {
            if(team.equals("saradomin")) {
                sara++;
            } else {
                zamy++;
            }
        }
        
        
        if(zamy >= sara) {
            players.put(player, "saradomin");
            
            
        } else {
            players.put(player, "zamorak");
        }      
        player.getPacketBuilder().sendMessage("You joined the " + getTeam(player) + " team.");
        player.move(getDeathPosition(player));
    }

    public String getTeam(Player player) {
        return players.get(player);
    }

    
    @Override
    public void fireOnLogin(Player player) {
        
    //shouldn't be used normally?
        
        System.out.println("Login fire for player: " + player.getUsername());
        
    }

    @Override
    public void fireOnForcedLogout(Player player) {
        players.remove(player);
        System.out.println("Removed player");
    
    }

    @Override
    public boolean inMinigame(Player player) {
        
        for(Player p : players.keySet()) {
            if(p.equals(player))
                return true;
        }
        return false; 
        
    }

    @Override
    public String name() {
        return "Castle Clash";
    }
    
    @Override
    public void fireOnEnter(Player player) {
        
        players.put(player, "saradomin");

    }

    @Override
    public void fireOnExit(Player player) {

        players.remove(player);
    }

    @Override
    public void fireOnDeath(Player player) {

    }

    @Override
    public void fireOnKill(Player player, Entity other) {
        System.out.println(player.getUsername() + " killed " + ((Player) other).getUsername());
    }
    
    @Override
    public boolean canEquip(Player player, Item item, int equipmentSlot) {
        return false;
    }
    
    @Override
    public boolean canUnequip(Player player, Item item, int equipmentSlot) {
        return false;
    }

    @Override
    public boolean canTrade(Player player, Player other) {
        return false;
    }

    /**
     * If a {@link Player} can fight while in this minigame.
     * 
     * @param player
     *        the player trying to attack.
     * @param other
     *        the entity being attacked.
     * @return false by default.
     */
    @Override
    public boolean canHit(Player player, Entity other) {
        
        String attackerTeam = (String) players.get(player);
        String defenderTeam = (String) players.get((Player) other);
        System.out.println(attackerTeam + ": attacks -> " + defenderTeam);
        
        return true;        
    }

    /**
     * If a {@link Player} can logout formally (using the logout button) while
     * in this minigame.
     * 
     * @param player
     *        the player trying to logout.
     * @return false by default.
     */
    @Override
    public boolean canFormalLogout(Player player) {
        return false;
    }

    /**
     * If a {@link Player} can teleport while in this minigame.
     * 
     * @param player
     *        the player trying to teleport.
     * @return false by default.
     */
    @Override
    public boolean canTeleport(Player player) {
        return false;
    }

    /**
     * If a {@link Player} can keep their items on death.
     * 
     * @param player
     *        the player trying to keep their items.
     * @return true by default.
     */
    @Override
    public boolean canKeepItems() {
        return true;
    }

    /**
     * The position that a {@link Player} will be returned to once dead.
     * 
     * @param player
     *        the player we are returning to the position.
     * @return the death position by default.
     */
    @Override
    public Position getDeathPosition(Player player) {
        
        if(players.get(player).equals("saradomin")) {
            return new Position(2403, 3100, 0);
        } else {
            return new Position(2396, 3107, 0);
        }
        
        
    }

  
    
}
