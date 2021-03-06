package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoison;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Location;

/**
 * A {@link Worker} implementation that deals a series of hits to an entity
 * after a delay.
 * 
 * @author lare96
 */
public class CombatHitTask extends Worker {

    /** The entity that will be dealing these hits. */
    private Entity attacker;

    /** The entity that will be dealt these hits. */
    private Entity target;

    /** The combat hit that will be used. */
    private CombatHitContainer combatHit;

    /** Determines if at least one hit was accurate. */
    private boolean oneHitAccurate;

    /**
     * Create a new {@link CombatHitTask}.
     * 
     * @param attacker
     *        the entity that will be dealing these hits.
     * @param target
     *        the entity that will be dealt these hits.
     * @param combatHit
     *        the combat hit that will be used.
     * @param oneHitAccurate
     *        if at least one hit was accurate.
     * @param delay
     *        the delay in ticks before the hit will be dealt.
     * @param initialRun
     *        if the task should be ran right away.
     */
    public CombatHitTask(Entity attacker, Entity target, CombatHitContainer combatHit, boolean oneHitAccurate, int delay, boolean initialRun) {
        super(delay, initialRun);
        this.attacker = attacker;
        this.target = target;
        this.combatHit = combatHit;
        this.oneHitAccurate = oneHitAccurate;
    }

    @Override
    public void fire() {

        /** Stop the task if the target isn't registered or has died. */
        if (target.isHasDied() || target.isUnregistered()) {
            this.cancel();
            return;
        }

        /** A complete miss! None of the hits were accurate. */
        if (!oneHitAccurate) {
            if (combatHit.getHitType() == CombatType.MAGIC) {
                target.gfx(new Gfx(85));
                attacker.getCurrentlyCasting().endCast(attacker, target, false);

                if (attacker.isPlayer()) {
                    Player player = (Player) attacker;
                    SkillManager.addExperience(player, attacker.getCurrentlyCasting().baseExperience(), SkillConstant.MAGIC);

                    if (!player.isAutocast()) {
                        player.setCastSpell(null);
                    }
                }
                attacker.setCurrentlyCasting(null);
            }
        }

        /** Send the hitsplats if needed. */
        if (combatHit.getHits() != null) {
            int totalDamage = 0;

            if (combatHit.getHitType() != CombatType.MAGIC || oneHitAccurate) {
                if (combatHit.getHits().length == 1) {
                    target.dealDamage(combatHit.getHits()[0].getHit());
                    totalDamage += combatHit.getHits()[0].getHit().getDamage();
                } else if (combatHit.getHits().length == 2) {
                    target.dealDoubleDamage(combatHit.getHits()[0].getHit(), combatHit.getHits()[1].getHit());
                    totalDamage += combatHit.getHits()[0].getHit().getDamage();
                    totalDamage += combatHit.getHits()[1].getHit().getDamage();
                } else if (combatHit.getHits().length == 3) {
                    target.dealTripleDamage(combatHit.getHits()[0].getHit(), combatHit.getHits()[1].getHit(), combatHit.getHits()[2].getHit());
                    totalDamage += combatHit.getHits()[0].getHit().getDamage();
                    totalDamage += combatHit.getHits()[1].getHit().getDamage();
                    totalDamage += combatHit.getHits()[2].getHit().getDamage();
                } else if (combatHit.getHits().length == 4) {
                    target.dealQuadrupleDamage(combatHit.getHits()[0].getHit(), combatHit.getHits()[1].getHit(), combatHit.getHits()[2].getHit(), combatHit.getHits()[3].getHit());
                    totalDamage += combatHit.getHits()[0].getHit().getDamage();
                    totalDamage += combatHit.getHits()[1].getHit().getDamage();
                    totalDamage += combatHit.getHits()[2].getHit().getDamage();
                    totalDamage += combatHit.getHits()[3].getHit().getDamage();
                }

                /** Give range/melee/magic exp. */
                if (attacker.isPlayer()) {
                    // XXX: These exp rates could use some work.

                    Player player = (Player) attacker;
                    int defaultExp = ((totalDamage * 10) / 3);

                    switch (player.getFightType().getTrainType()) {
                        case ATTACK:
                            SkillManager.addExperience(player, defaultExp, SkillConstant.ATTACK);
                            break;
                        case STRENGTH:
                            SkillManager.addExperience(player, defaultExp, SkillConstant.STRENGTH);
                            break;
                        case DEFENCE:
                            SkillManager.addExperience(player, defaultExp, SkillConstant.DEFENCE);
                            break;
                        case RANGED:
                            SkillManager.addExperience(player, defaultExp, SkillConstant.RANGED);
                            break;
                        case MAGIC:
                            SkillManager.addExperience(player, defaultExp, SkillConstant.MAGIC);
                            break;
                        case ATTACK_STRENGTH_DEFENCE:
                            SkillManager.addExperience(player, ((totalDamage * 10) / 5), SkillConstant.ATTACK);
                            SkillManager.addExperience(player, ((totalDamage * 10) / 5), SkillConstant.STRENGTH);
                            SkillManager.addExperience(player, ((totalDamage * 10) / 5), SkillConstant.DEFENCE);
                            break;
                        case RANGED_DEFENCE:
                            SkillManager.addExperience(player, ((totalDamage * 10) / 4), SkillConstant.RANGED);
                            SkillManager.addExperience(player, ((totalDamage * 10) / 5), SkillConstant.DEFENCE);
                            break;
                    }
                }
            }

            /** Add the total damage to the target's damage map. */
            target.getCombatBuilder().addDamage(attacker, totalDamage);

            if (oneHitAccurate) {

                /** Various armor and weapon effects. */
                if (Misc.getRandom().nextInt(4) == 0) {
                    if (combatHit.getHitType() == CombatType.MELEE) {
                        if (attacker.isPlayer() && target.isPlayer()) {
                            Player player = (Player) attacker;
                            Player victim = (Player) target;

                            if (CombatFactory.isWearingFullTorags(player)) {
                                victim.decrementRunEnergy(Misc.getRandom().nextInt(19) + 1);
                                victim.gfx(new Gfx(399));
                            } else if (CombatFactory.isWearingFullAhrims(player)) {
                                victim.getSkills()[Misc.STRENGTH].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                                SkillManager.refresh(victim, SkillConstant.STRENGTH);
                                victim.gfx(new Gfx(400));
                            } else if (CombatFactory.isWearingFullGuthans(player)) {
                                target.gfx(new Gfx(398));
                                player.getSkills()[Misc.HITPOINTS].increaseLevel(totalDamage, 99);
                                SkillManager.refresh(player, SkillConstant.HITPOINTS);
                            }
                        } else if (attacker.isPlayer()) {
                            Player player = (Player) attacker;

                            if (CombatFactory.isWearingFullGuthans(player)) {
                                target.gfx(new Gfx(398));
                                player.getSkills()[Misc.HITPOINTS].increaseLevel(totalDamage, 99);
                                SkillManager.refresh(player, SkillConstant.HITPOINTS);
                            }
                        }
                    } else if (combatHit.getHitType() == CombatType.RANGE) {
                        if (attacker.isPlayer() && target.isPlayer()) {
                            Player player = (Player) attacker;
                            Player victim = (Player) target;

                            if (CombatFactory.isWearingFullKarils(player)) {
                                victim.gfx(new Gfx(401));
                                victim.getSkills()[Misc.AGILITY].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                                SkillManager.refresh(victim, SkillConstant.AGILITY);
                            }
                        }
                    } else if (combatHit.getHitType() == CombatType.MAGIC) {
                        if (attacker.isPlayer() && target.isPlayer()) {
                            Player player = (Player) attacker;
                            Player victim = (Player) target;

                            if (CombatFactory.isWearingFullAhrims(player)) {
                                victim.getSkills()[Misc.STRENGTH].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                                SkillManager.refresh(victim, SkillConstant.STRENGTH);
                                victim.gfx(new Gfx(400));
                            }
                        }
                    }
                }

                /** Various entity effects take place here. */
                if (attacker.isNpc()) {
                    Npc npc = (Npc) attacker;

                    if (npc.getDefinition().isPoisonous()) {
                        CombatFactory.poisonEntity(target, CombatPoison.STRONG);
                    }
                } else if (attacker.isPlayer()) {
                    Player player = (Player) attacker;

                    if (combatHit.getHitType() == CombatType.MELEE || combatHit.getHitType() == CombatType.RANGE) {
                        Item weapon = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON);

                        if (weapon != null) {
                            if (weapon.getDefinition().getItemName().endsWith("(p)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.MILD);
                            } else if (weapon.getDefinition().getItemName().endsWith("(p+)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.STRONG);
                            } else if (weapon.getDefinition().getItemName().endsWith("(p++)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.SEVERE);
                            }
                        }
                    }

                    if (combatHit.getHitType() == CombatType.RANGE) {
                        Item weapon = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_ARROWS);

                        if (weapon != null) {
                            if (weapon.getDefinition().getItemName().endsWith("(p)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.MILD);
                            } else if (weapon.getDefinition().getItemName().endsWith("(p+)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.STRONG);
                            } else if (weapon.getDefinition().getItemName().endsWith("(p++)")) {
                                CombatFactory.poisonEntity(target, CombatPoison.SEVERE);
                            }
                        }
                    }
                }

                /**
                 * If both the attacker and target are players then check for
                 * retribution and do smite prayer effects.
                 */
                if (attacker.isPlayer() && target.isPlayer()) {
                    Player player = (Player) attacker;
                    Player victim = (Player) target;

                    /** Retribution prayer check and function here. */
                    if (CombatPrayer.isPrayerActivated(victim, CombatPrayer.RETRIBUTION)) {
                        if (victim.getSkills()[Misc.HITPOINTS].getLevel() < 1) {
                            if (Location.inWilderness(player) || MinigameFactory.inMinigame(player)) {

                                victim.gfx(new Gfx(437));

                                if (Location.inMultiCombat(target)) {
                                    for (Player plr : World.getPlayers()) {
                                        if (plr == null) {
                                            continue;
                                        }

                                        if (!plr.getUsername().equals(victim.getUsername()) && plr.getPosition().withinDistance(target.getPosition().clone(), 5)) {
                                            plr.dealDamage(new Hit(Misc.getRandom().nextInt(15)));
                                        }
                                    }
                                } else {
                                    player.dealDamage(new Hit(Misc.getRandom().nextInt(9)));
                                }
                            }
                        }
                    }

                    /** Smite prayer check and function here. */
                    if (CombatPrayer.isPrayerActivated(player, CombatPrayer.SMITE)) {
                        victim.getSkills()[Misc.PRAYER].decreaseLevel(totalDamage / 4);
                        SkillManager.refresh(victim, SkillConstant.PRAYER);
                    }
                }
            }
        }

        /**
         * If the target is a player then check for the redemption prayer
         * effect.
         */
        if (target.isPlayer() && combatHit.getHits() != null) {
            Player player = (Player) target;

            /** Redemption prayer check here. */
            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.REDEMPTION)) {
                if (player.getSkills()[Misc.HITPOINTS].getLevel() <= (player.getSkills()[Misc.HITPOINTS].getLevelForExperience() / 10)) {
                    player.getSkills()[Misc.HITPOINTS].increaseLevel(Misc.getRandom().nextInt((player.getSkills()[Misc.HITPOINTS].getLevelForExperience() - player.getSkills()[Misc.HITPOINTS].getLevel())));
                    player.gfx(new Gfx(436));
                    player.getSkills()[Misc.PRAYER].setLevel(0);
                    player.getPacketBuilder().sendMessage("You've run out of prayer points!");
                    CombatPrayer.deactivateAllPrayer(player);
                    SkillManager.refresh(player, SkillConstant.PRAYER);
                    SkillManager.refresh(player, SkillConstant.HITPOINTS);
                }
            }
        }

        /** Various checks for different combat types. */
        if (combatHit.getHitType() == CombatType.MAGIC) {
            if (oneHitAccurate) {
                target.gfx(attacker.getCurrentlyCasting().endGfx());
                attacker.getCurrentlyCasting().endCast(attacker, target, true);

                if (attacker.isPlayer()) {
                    Player player = (Player) attacker;

                    if (combatHit.getHits() == null) {
                        SkillManager.addExperience(player, attacker.getCurrentlyCasting().baseExperience(), SkillConstant.MAGIC);
                    }
                    if (!player.isAutocast()) {
                        player.setCastSpell(null);
                    }
                }

                attacker.setCurrentlyCasting(null);
            }
        } else if (combatHit.getHitType() == CombatType.MELEE) {
            if (target.isPlayer()) {
                Player player = (Player) target;
                player.animation(new Animation(404));
            }
        } else if (combatHit.getHitType() == CombatType.RANGE) {
            if (target.isPlayer()) {
                Player player = (Player) target;
                player.animation(new Animation(404));
            }

            if (attacker.isPlayer()) {
                if (Misc.getRandom().nextInt(3) != 0) {

                    Player player = (Player) attacker;

                    if (player.getFireAmmo() != 0) {
                        World.getGroundItems().registerAndStack(new GroundItem(new Item(player.getFireAmmo(), 1), target.getPosition(), player));
                        player.setFireAmmo(0);
                    }
                }
            }
        }

        /** Auto-retaliate the attacker if needed. */
        if (target.isAutoRetaliate() && !target.getCombatBuilder().isAttacking()) {
            target.getCombatBuilder().attack(attacker);
        }

        /** And last but not least cancel the task. */
        this.cancel();
    }
}
