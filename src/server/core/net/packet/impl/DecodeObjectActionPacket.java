package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.minigame.castlewars.CastleWars;
import server.world.entity.player.minigame.castlewars.Team;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Location;
import server.world.map.Position;

/**
 * Sent when the player first/second/third clicks an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 132, 252, 70 })
public class DecodeObjectActionPacket extends PacketDecoder {

    /** The various packet opcodes. */
    private static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
            THIRD_CLICK = 70;

    @Override
    public void decode(final Player player, ReadBuffer in) {
        switch (player.getSession().getPacketOpcode()) {
            case FIRST_CLICK:
                final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final int objectId = in.readShort(false);
                final int objectY = in.readShort(false, ValueType.A);
                final int objSize;

                
                //defining the object size. For now we'll do it the hardcoded way
                //maybe add size definitions later.
                //4408 = Guthix portal (Castle wars)
                
                if(objectId == 4408 || objectId == 4406 || objectId == 4407) {
                    objSize = 2;
                } else if (objectId == 4418 || objectId == 4420) {
                     objSize = 3;
                }  else {
                    objSize = 1;
                }
                
                //System.out.println("First click: " + objectId);
                
                player.facePosition(new Position(objectX, objectY));
                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(objectX, objectY, player.getPosition().getZ()), objSize)) {
                            System.out.println("Object click: " + objectId);
                            
                            Position pos = player.getPosition().clone();
                            
                            switch (objectId) {

                                case 4408: //Guthix Portal
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).addPlayerToWaitingRoom(player);                                    
                                    break;
                                
                                case 4390: //Zamorak leave waiting room portal
                                case 4387: //Saradomin leave waiting room portal
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).removePlayerFromWaitingRoom(player);                                    
                                    break;
                                    
                                case 4903: //zammy base flagstand (available)
                                case 4378: //zammy base flagstand (gone)
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).flagAction(player, Team.ZAMORAK, Team.ZAMORAK.getFlagStand());
                                    break;
                                    
                                case 4902: //sara base flagstand (available)
                                case 4377: //sara base flagstand (gone)
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).flagAction(player, Team.SARADOMIN, Team.SARADOMIN.getFlagStand());
                                    break;
                                    
                                    
                                case 4900:// sara flag (field object) => created when dropped 
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).flagAction(player, Team.SARADOMIN, new Position(objectX, objectY));
                                    
                                    break;
                                case 4901:// zammy flag (field object)
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).flagAction(player, Team.ZAMORAK, new Position(objectX, objectY));
                                    break;
                                    
                                case 4406: //sara leave portal (change size!)
                                case 4407: //zammy leave portal
                                    
                                    break;
                                    
                                    
                                case 4458: //bandage table
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).takeBandage(player);
                                    break;
                                    
                                case 4463: //explosives table
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).takeExplosive(player);
                                    break;
                                    
                                case 4461: //barricades table
                                    ((CastleWars) MinigameFactory.getMinigames().get("Castle Wars")).takeBarricade(player);
                                    break;
                                    
                                case 4464: //pickaxe table
                                    
                                    break;
                                    
                                case 1747: //staircase up
                                case 6281:
                                    player.animation(new Animation(828));
                                    player.move(new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() + 1));
                                    
                                    break;
                                    
                                case 4911: //ladder down
                                case 4472: //this is actually a trapdoor that should be opened first but W/E
                                    player.animation(new Animation(828));
                                    player.move(new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ() - 1));
                                                                        
                                    break;
                                    
                                case 4912: //zammy go to dungeon
                                   if(player.getPosition().equals(new Position(2369, 3126, 0))) {
                                        pos = new Position(2374, 3130, 2);
                                   }
                                   
                                   player.move(pos);
                                    break;
                                    
                                case 4415: // zamy Staircase go down
                                    
                                    if(player.getPosition().equals(new Position(2373, 3133, 3))) {
                                        pos = new Position(2374, 3130, 2);
                                    } else if(player.getPosition().equals(new Position(2369, 3127, 2))) {
                                        pos = new Position(2372, 3126, 1);
                                    } else if(player.getPosition().equals(new Position(2379, 3127, 1))) {
                                        System.out.println("this");
                                        pos = new Position(2380, 3130, 0);
                                    } else if(player.getPosition().equals(new Position(2383, 3133, 0))) {
                                        pos = new Position(2382, 3133, 0);
                                    }
                                                                      
                                    player.move(pos);
                                    
                                    break;
                                case 4416:
                                    break;
                                case 4417:
                                    break;
                                    
                                
                                case 4418: // zamy Staircase go up
                                case 4420: //go to/from catapult
                                    if(player.getPosition().equals(new Position(2374, 3130, 2))) {
                                        pos = new Position(2373, 3133, 3);
                                    } else if(player.getPosition().equals(new Position(2372, 3126, 1))) {
                                        pos = new Position(2369, 3127, 2);
                                    } else if(player.getPosition().equals(new Position(2380, 3130, 0))) {
                                        pos = new Position(2379, 3127, 1);
                                    } else if(player.getPosition().equals(new Position(2382, 3130, 0))) {
                                        pos = new Position(2383, 3133, 0);
                                    } else if(player.getPosition().equals(new Position(2383, 3133, 0))) {
                                        pos = new Position(2382, 3130, 0);
                                    }
                                                                      
                                    player.move(pos);
                                    break;
                                    
                                case 3193:
                                case 2213:
                                    player.getBank().open();
                                    break;
                                case 409:
                                    if (player.getSkills()[Misc.PRAYER].getLevel() < player.getSkills()[Misc.PRAYER].getLevelForExperience()) {
                                        player.animation(new Animation(645));
                                        player.getSkills()[Misc.PRAYER].setLevel(player.getSkills()[Misc.PRAYER].getLevelForExperience());
                                        player.getPacketBuilder().sendMessage("You recharge your prayer points.");
                                        SkillManager.refresh(player, SkillConstant.PRAYER);
                                    } else {
                                        player.getPacketBuilder().sendMessage("You already have full prayer points.");
                                    }
                                    break;
                                case 6552:
                                    if (player.getSpellbook() == Spellbook.ANCIENT) {
                                        Spellbook.convert(player, Spellbook.NORMAL);
                                    } else if (player.getSpellbook() == Spellbook.NORMAL) {
                                        Spellbook.convert(player, Spellbook.ANCIENT);
                                    }
                                    break;
                                    
                                default:
                                    System.out.println("Unhandled first click: " + objectId);
                                    break;
                            }
                        } 
                    }
                });
                break;

            case SECOND_CLICK:
                final int objId = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final int objY = in.readShort(true, ByteOrder.LITTLE);
                final int objX = in.readShort(false, ValueType.A);
                final int size = 1;

                player.facePosition(new Position(objX, objY));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(objX, objY, player.getPosition().getZ()), size)) {
                            switch (objId) {

                            }
                        }
                    }
                });
                break;

            case THIRD_CLICK:
                final int x = in.readShort(true, ByteOrder.LITTLE);
                final int y = in.readShort(false);
                final int id = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final int objectSize = 1;

                player.facePosition(new Position(x, y));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(x, y, player.getPosition().getZ()), objectSize)) {
                            switch (id) {

                            }
                        }
                    }
                });
                break;
        }
    }
}
