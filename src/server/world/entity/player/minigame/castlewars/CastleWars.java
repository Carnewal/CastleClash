package server.world.entity.player.minigame.castlewars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.UpdateFlags;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Position;
import server.world.object.WorldObject;

/**
 * A basic free-form {@link Minigame} that can provide implementation for a wide
 * variety of different types of minigames.
 * 
 * @author lare96
 */
public class CastleWars extends Minigame {
    
    private HashMap<Player, Team> playingPlayers;
    private HashMap<Player, Team> waitingPlayers;
    
    private Position lobby = new Position(2440,3088);
    
    private final int GAMETIME = 1200000; //in milis
    
    
    private int saraTotalPlayers, zamyTotalPlayers, saraWaitingPlayers, zamyWaitingPlayers, saraPlayingPlayers, zamyPlayingPlayers;
    
    
    
    public CastleWars() {
        playingPlayers = new HashMap<>();
        waitingPlayers = new HashMap<>();
    }
    
    public void refreshPlayerCounts() {
        this.saraTotalPlayers = 0;
        this.zamyTotalPlayers = 0;
        
        this.zamyWaitingPlayers = 0;
        this.saraWaitingPlayers = 0;
        
        this.saraPlayingPlayers = 0;
        this.zamyPlayingPlayers = 0;
        
        for(Team t : waitingPlayers.values()) {
            if(t.name().equals("SARADOMIN")) {
                saraTotalPlayers++;
                saraWaitingPlayers++;
            } else {
                zamyTotalPlayers++;
                zamyWaitingPlayers++;
            }
        }  
        
        for(Team t : playingPlayers.values()) {
            if(t.name().equals("SARADOMIN")) {
                saraTotalPlayers++;
                saraPlayingPlayers++;
            } else {
                zamyTotalPlayers++;
                zamyPlayingPlayers++;
            }
        }        
        
    }
    
    
    public void addPlayerToWaitingRoom(Player player) {       
        refreshPlayerCounts();
        
        
        player.getPacketBuilder().walkableInterface(11146);
        if(saraTotalPlayers >= zamyWaitingPlayers)
            addPlayerToWaitingRoom(player, Team.ZAMORAK);
         else 
            addPlayerToWaitingRoom(player, Team.SARADOMIN);
        
        
        
    }
    
    public void addPlayerToWaitingRoom(Player player, Team team) {
        waitingPlayers.put(player, team);
        player.move(team.getWaitRoom());
        
    }    
    
    public void removePlayerFromWaitingRoom(Player player) {
        waitingPlayers.remove(player);
        playingPlayers.remove(player); //just to make sure
        
        player.move(lobby);
        
    }    
    public void movePlayerToGame(Player player) {
        playingPlayers.put(player, waitingPlayers.get(player));
        waitingPlayers.remove(player);
    }
    
    public Worker gameWorker() {
        return new Worker(50, true) {
            Stopwatch sw = new Stopwatch();
            
            @Override
            public void fire() {
                                
                
                if(sw.elapsed() >= GAMETIME) {
                    this.cancel();
                    endGame();
                }
                    
                
                double timeLeft = (GAMETIME - sw.elapsed())/1000/60;
                System.out.println(timeLeft + " minutes left");
                
                    
                
                
                
            }
            
        };
    }
    
    
    public void startGame() {
        
        
        playingPlayers = (HashMap<Player, Team>) waitingPlayers.clone();
        waitingPlayers.clear();
        
        for(Player p : playingPlayers.keySet()) {
            p.move(playingPlayers.get(p).getGameRoom());
        }
        
        TaskFactory.getFactory().submit(gameWorker());
        
    }
    
    public void endGame() {
        
        
        
        for(Player player : playingPlayers.keySet()) {
            removeItems(player);
            player.move(lobby);
        }
        
        playingPlayers.clear();
        
    }
    
    
    public void takeSomething(Player player, Item item) {        
        if(player.getThievingTimer().elapsed() > 1500) {
            player.animation(new Animation(832));
            player.getInventory().addItem(item);
            player.getThievingTimer().reset();
        }        
    }
    
    public void takeBandage(Player player) {
        player.getPacketBuilder().sendMessage("You take a bandage."); 
        takeSomething(player, new Item(4049));               
    }
    public void takeExplosive(Player player) {
        player.getPacketBuilder().sendMessage("You take an explosive potion.");  
        takeSomething(player, new Item(4045));              
    }        
    public void takeBarricade(Player player) {
        player.getPacketBuilder().sendMessage("You take a barricade.");   
        takeSomething(player, new Item(4053));             
    }    
    
    public void dropBanner(Player player) {
        
        if(player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).equals(Team.SARADOMIN.getBanner())) {
            player.getEquipment().getContainer().remove(Team.SARADOMIN.getBanner(), Misc.EQUIPMENT_SLOT_WEAPON);            
                       
        } else if(player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).equals(Team.ZAMORAK.getBanner())) {
            player.getEquipment().getContainer().remove(Team.ZAMORAK.getBanner(), Misc.EQUIPMENT_SLOT_WEAPON);            
            World.getGroundItems().register(new GroundItem(Team.ZAMORAK.getBanner(), player.getPosition().clone()));
        }
    }

    public String getTeam(Player player) {
        return playingPlayers.get(player).name();
    }
    
    @Override
    public void fireOnLogin(Player player) {
        
    //shouldn't be used normally?
        
        System.out.println("Login fire for player: " + player.getUsername());
        
    }

    @Override
    public void fireOnForcedLogout(Player player) {
        waitingPlayers.remove(player);
        playingPlayers.remove(player);
        System.out.println("Removed player " + player.getUsername() + " (forced logout)");
    
    }

    @Override
    public boolean inMinigame(Player player) {
        
        for(Player p : waitingPlayers.keySet()) {
            if(p.equals(player))
                return true;
        }
        for(Player p : playingPlayers.keySet()) {
            if(p.equals(player))
                return true;
        }
        return false; 
        
    }

    //This is the name that gets used in the Factory as the String key!    
    @Override
    public String name() {
        return "Castle Wars";
    }
    
    @Override
    public void fireOnEnter(Player player) {
        
        //players.put(player, "saradomin");

    }

    @Override
    public void fireOnExit(Player player) {
        
    }

    @Override
    public void fireOnDeath(Player player) {      

    }

    @Override
    public void fireOnKill(Player player, Entity other) {
        System.out.println(player.getUsername() + " killed " + ((Player) other).getUsername());
        
        dropBanner(player);
        
        for(Player p : playingPlayers.keySet()) {
            p.getPacketBuilder().sendPositionHintArrow(player.getPosition(), 5);
        }
        
        
        
    }
    
    @Override
    public boolean canEquip(Player player, Item item, int equipmentSlot) {
        
        return true;
    }
    
    @Override
    public boolean canUnequip(Player player, Item item, int equipmentSlot) {
        
        if(item.equals(Team.SARADOMIN.getBanner()) || item.equals(Team.ZAMORAK.getBanner()) || item.equals(playingPlayers.get(player).getCape())) {
            player.getPacketBuilder().sendMessage("You can't unequip that item.");
            return false;
        }
        return true;
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
        
       // String attackerTeam = (String) players.get(player);
        //String defenderTeam = (String) players.get((Player) other);
        //System.out.println(attackerTeam + ": attacks -> " + defenderTeam);
        return playingPlayers.get(player).equals(playingPlayers.get((Player) other));
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
        return playingPlayers.get(player).getGameRoom();       
    }




    public void flagAction(Player player, Team team, Position position) {
        
        if(position.equals(team.getFlagStand())) { // action on a flag stand by player in (team)'s base
            
            if(team.isFlagAvailable()) {
                
                if(playingPlayers.get(player).equals(team)) {
                    if(player.getEquipment().getContainer().getIdBySlot(Misc.EQUIPMENT_SLOT_WEAPON) == playingPlayers.get(player).opponent().getBanner().getId()) {
                        player.getPacketBuilder().sendMessage("You scored a point for the team. Good job!");
                        player.getEquipment().getContainer().remove(playingPlayers.get(player).opponent().getBanner());                        
                        World.getObjects().register(new WorldObject(team.opponent().getBannerObj(), team.opponent().getFlagStand(), WorldObject.Rotation.SOUTH, 10));  
                        team.opponent().setFlagAvailable(true);
                        player.getEquipment().refresh();
                        player.getFlags().flag(UpdateFlags.Flag.APPEARANCE);
                        
                    } else if (player.getEquipment().getContainer().getIdBySlot(Misc.EQUIPMENT_SLOT_WEAPON) == playingPlayers.get(player).getBanner().getId()) {
                        player.getPacketBuilder().sendMessage("You returned your own flag. Fairplay!");
                        player.getEquipment().getContainer().remove(playingPlayers.get(player).getBanner());                        
                        World.getObjects().register(new WorldObject(team.getBannerObj(), team.getFlagStand(), WorldObject.Rotation.SOUTH, 10));  
                        team.setFlagAvailable(true);
                        player.getEquipment().refresh();
                        player.getFlags().flag(UpdateFlags.Flag.APPEARANCE);
                        
                    } else {
                        player.getPacketBuilder().sendMessage("You cannot take your own flag.");
                    }
                            
                            
                    
                } else {
                    
                    if(player.getInventory().getContainer().freeSlots() >= 2) {
                        player.getEquipment().removeItem(Misc.EQUIPMENT_SLOT_WEAPON);
                        player.getEquipment().removeItem(Misc.EQUIPMENT_SLOT_SHIELD);
                        player.getEquipment().getContainer().add(team.getBanner(), Misc.EQUIPMENT_SLOT_WEAPON);
                        World.getObjects().register(new WorldObject(team.getBannerObj() - 525, team.getFlagStand(), WorldObject.Rotation.SOUTH, 10));  
                        team.setFlagAvailable(false);
                        player.getPacketBuilder().sendMessage("You take the enemy flag.");
                        player.getEquipment().refresh();
                        player.getFlags().flag(UpdateFlags.Flag.APPEARANCE);
                    } else {
                       player.getPacketBuilder().sendMessage("You don't have enough free slots to do that!");
                    }
                    
                }
                
                
            } else {
                player.getPacketBuilder().sendMessage("The flag seems to be gone.");
            }
            
            
            
        } else { //action on a flag in the open field
            if(player.getInventory().getContainer().freeSlots() >= 2) {
                player.getEquipment().removeItem(Misc.EQUIPMENT_SLOT_WEAPON);
                player.getEquipment().removeItem(Misc.EQUIPMENT_SLOT_SHIELD);
                player.getEquipment().getContainer().add(team.getBanner(), Misc.EQUIPMENT_SLOT_WEAPON);
                World.getObjects().unregister(World.getObjects().getObjectOnPosition(position));  
                team.setFlagAvailable(false);
                player.getPacketBuilder().sendMessage("The flag is yours now!");
                player.getEquipment().refresh();
                player.getFlags().flag(UpdateFlags.Flag.APPEARANCE);
            } else {
                player.getPacketBuilder().sendMessage("You don't have enough free slots to do that!");
            }                     
        }
        
        
        
    }

    private void removeItems(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

 

 



  
    
}
