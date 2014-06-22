package server.world.entity.player.minigame.castlewars;

import server.world.item.Item;
import server.world.map.Position;

public enum Team {

    SARADOMIN(new Item(4041), new Item(4037), new Position(2377, 9485), new Position(2426, 3076, 1), new Position(2429, 3074, 3), 4902),
    ZAMORAK(new Item(4042), new Item(4039), new Position(2421, 9524), new Position(2372, 3131, 1), new Position(2370, 3133, 3), 4903);
    
    private Item cape;
    private Item banner;
    private Position waitRoom;
    private Position gameRoom;
    private Position flagStand;
    private int bannerObj;
    

    
    
    private boolean flagAvailable;

    public void setFlagAvailable(boolean flagAvailable) {
        this.flagAvailable = flagAvailable;
    }

    public boolean isFlagAvailable() {
        return flagAvailable;
    }    
    public int getBannerObj() {
        return bannerObj;
    }    
    public Item getCape() {
        return cape;
    }

    public Item getBanner() {
        return banner;
    }

    public Position getWaitRoom() {
        return waitRoom;
    }

    public Position getGameRoom() {
        return gameRoom;
    }

    public Position getFlagStand() {
        return flagStand;
    }
    
    public Team opponent() {
        if(this.equals(SARADOMIN)) {
            return ZAMORAK;
        }
        return SARADOMIN;
    }
    

    private Team(Item capeId, Item bannerId, Position waitRoom, Position gameRoom, Position flagStand, int bannerObj) {
        this.cape = capeId;
        this.banner = bannerId;
        this.waitRoom = waitRoom;
        this.gameRoom = gameRoom;
        this.flagStand = flagStand;
        this.bannerObj = bannerObj;
        this.flagAvailable = true;

    }
}
