/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeFreeze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStop;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Finger;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.StarPlatinumSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StarPlatinumTest extends Stand {

    boolean superPunch = false;
    boolean superFinger = false;
    boolean superStop = false;
    Ballistica finger;

    public float ACTIONS_IN_FROZEN_TIME = 5;
    {
        spriteClass = StarPlatinumSprite.class;

        power = powerA;
        speed = speedA;
        range = rangeC;
        def = defA;

        primaryColor = 0xEADD33;

        //HUNTING = new Hunting();
        //WANDERING = new Wandering();
    }


    public StarPlatinumTest(Char standMaster){
        //TODO: should a non-Jotaro character have star platinum
        //it will become a parasitic stand, calling parasitic();
        this.standUser = standMaster;
        this.alignment = standUser.alignment;
        HP = standUser.HP;
        HT = standUser.HT;
    }

    public StarPlatinumTest()
    {
    }


    @Override
    public void abilityOne()
    {
        starBreaker(worldCell);
    }

    //FIXME:
    //starbreaker's animations have been improved, however if star platinum doesn't connect its attack
    //it will temporarily have a range of 3 (from itself) and a 2x damage modifier, it can still only attack
    //if it's in the range of its own stand user but this is effectively still doubling its range

    //preferred solutions are reworking starbreaker to being a single punch with a penetrating projectile
    //instead of the current slide into a punch
    //alternatively we could try simply telling the game that once the motion is executed cancel the super
    //no idea how to do that though
    //if the ladder method is implemented one more fix needs to be put in place
    //TODO: starbreaker's slide/projectile should not exceed the stand's range

    public void starBreaker(Integer cell)
    {
        superPunch = true;

        Callback callback = new Callback() {
            @Override
            public void call() {
                //  Dungeon.observe();
                //  GameScene.updateFog();
            }
        };

        ((StarPlatinumSprite) sprite).punchStarBreaker(this.pos, cell, callback);
        attack(enemy);

    }

    @Override
    public void abilityTwo()
    {
        superFinger = true;
        if(state != HUNTING)
        {
            state = HUNTING;
        }

        starFinger(worldCell);
    }

    //FIXME:
    //this function is only boolean because I'm too lazy to not shamelessly steal 00-Evan's Guard code
    //it's also very convenient for exiting the function because of returns
    public boolean starFinger(Integer cell)
    {
        superFinger = true;

        Ballistica digits = new Ballistica(pos, cell, Ballistica.STOP_TERRAIN);


        ((StarPlatinumSprite) sprite).punchStarFinger();
        //FIXME: while this works fine, using the tiles and not the sprites for referencing looks a bit
        //jarring, it would be preferabble to have a way to go from the center of star platinum to the targeted cell

        sprite.parent.add(new Finger(pos, cell, new Callback() {
            @Override
            public void call() {

            }
        }));

        for (int pos : digits.subPath(1, digits.dist)) {

            //Attack everything caught in star finger's path
            Char ch = Actor.findChar(pos);
            if (ch == null) {
                continue;
            } else {
                attack(ch);
            }
        }


        if(Actor.findChar(cell) != null )
        {
            attack(Actor.findChar(cell));
        }

        superFinger = false;
        return true;
    }

    @Override
    public void abilityThree()
    {
        //timeStop();
        //tryToStopTime();
        standUser.sprite.showStatus(primaryColor, "Star Platinum:", standUser);
        standUser.sprite.showStatus(super.primaryColor, "The World!", standUser);
        doTimeStop();
    }

    protected boolean doTimeStop(){

        //if this evaluates true have the user's sprite say some phrase about activating time stop


        //check the users stamina
        //while your standUser has more than 20 points time will be frozen
        while(standUser.SP >= 20) {
            Dungeon.timeFreeze = true;
            GameScene.freezeEmitters = true;

            //apply the TimeFreeze debuff to anyone that isn't yourself or your standUser for 1 full turn
            for(Mob mobs : Dungeon.level.mobs.toArray(new Mob[0]))
            {
                if(mobs == this)
                {
                    continue;
                }
                Buff.prolong( mobs, TimeFreeze.class, 1 );
            }
            if(Dungeon.hero != standUser)
            {
                Buff.prolong( Dungeon.hero, TimeFreeze.class, 1 );
            }

            //decrement your stand users stamina by 20 points
            standUser.SP -= 20;

            if(standUser.SP < 20)
            {
                endTimeStop();
            }

            return true;
        }


        //make a call to endTimeStop()
        endTimeStop();

        //otherwise break and return false

        return false;
    }

    //this will forcefully end time stop so that the AI can save stamina or the hero can forcefully end it
    private boolean endTimeStop(){

        //if we ever make it here and (Dungeon.timeFreeze == true) we need to cycle through all the characters,
        if(Dungeon.timeFreeze == true)
        {

            //if we find any character that isn't frozen but isn't ourselves or our stand user
            //we will return true (letting time remain frozen but becoming frozen ourselves)
            for(Mob mobs : Dungeon.level.mobs.toArray(new Mob[0]))
            {
                if(!(mobs == this || mobs == standUser))
                {
                    //boolean notFrozen;

                    //for(Buff buffs: chars.buffs())
                    //{
                        if(!mobs.buffs().contains(TimeFreeze.class))
                        {
                            return true;
                        }

                    //}
                }



            }



            //if the cycle above executed without breaking we'll loop through again and unfreeze everyone
            for(Mob mobs : Dungeon.level.mobs.toArray(new Mob[0]))
            {
                Buff.detach( mobs, TimeFreeze.class);
            }

            Buff.detach( Dungeon.hero, TimeFreeze.class);

            //then set Dungeon.timeFreeze and Gamescene.freezeEmitters to false
            Dungeon.timeFreeze = false;
            GameScene.freezeEmitters = false;

            //have the user's sprite say some phrase about deactivating time stop
            standUser.sprite.showStatus(super.primaryColor, "Time has begun to move again", standUser);

            //beta testing only
            standUser.SP = standUser.ST;

        }



        return false;
    }



    @Override
    public void cancelAbility()
    {
        //cancelTimeStop();
        endTimeStop();
    }

    public void cancelTimeStop()
    {

        //TODO: find a way to prevent the hero from abusing time stop
        ACTIONS_IN_FROZEN_TIME = 5;

        if(standUser != Dungeon.hero) {
            actPriority = MOB_PRIO;
            standUser.actPriority = MOB_PRIO;

            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if(!(mob instanceof StarPlatinumHero))
                {
                    mob.sprite.remove(CharSprite.State.PARALYSED);
                    mob.remove(TimeFreeze.class);
                }

            }
            Dungeon.hero.remove(TimeFreeze.class);
            Dungeon.hero.sprite.remove(CharSprite.State.PARALYSED);
        }
        else
        {
            actPriority = MOB_PRIO;
            standUser.actPriority = HERO_PRIO;

            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if(!(mob instanceof StarPlatinumHero))
                {
                    mob.sprite.remove(CharSprite.State.PARALYSED);
                    mob.remove(TimeFreeze.class);

                }

            }
        }

        GameScene.freezeEmitters = false;
        superStop = false;
        Dungeon.observe();

    }


    public void timeStop()
    {
        if (Dungeon.level != null) {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                if(!(mob instanceof StarPlatinumHero))
                {
                    mob.sprite.add(CharSprite.State.PARALYSED);
                }

            }
        }
        GameScene.freezeEmitters = true;

    }

    public void tryToStopTime()
    {
        //when initiated by player should go into effect the following turn
        //TODO: prevent the AI from abusing time stop, give the player a chance to defend
        //on the preceding turn

        superStop = true;
/*
        if(standUser != null && standUser != Dungeon.hero)
        {
            this.standUser.actPriority = this.actPriority = TIME_STOP_PRIO;

        }
        else if(standUser == Dungeon.hero)
        {
            //the hero must act before their stand
            standUser.actPriority = TIME_STOP_PRIO;
            this.actPriority = TIME_STOP_PRIO -1;
        }
*/
        //cycle through the entire dungeon and freeze everyone besides yourself
        for (Mob mobs : Dungeon.level.mobs.toArray(new Mob[0])) {
            if(mobs != this)
            {
                Buff.prolong( mobs, TimeFreeze.class, ACTIONS_IN_FROZEN_TIME );
            }
        }

        //always make sure the stand user can move
        if(standUser != Dungeon.hero)
        {
            Buff.detach(standUser, TimeFreeze.class);

            Buff.prolong( Dungeon.hero, TimeFreeze.class, ACTIONS_IN_FROZEN_TIME );
        }
        GameScene.freezeEmitters = true;
    }


    @Override
    public boolean act()
    {
     //TODO: something needs to constantly call doTimeStop()
        if(GameScene.freezeEmitters) //and we aren't frozen
        {
            doTimeStop();
        }

        return super.act();
    }


    @Override
    public int damageRoll() {
        int punchDMG = 0;

        if(superPunch == true)
        {
            punchDMG += standUser.damageRoll() * 1.5;
            superPunch = false;
            return punchDMG;
        }

        if (superFinger == true)
        {
            //TODO: once the stamina system is in place
            //punchDMG += standUser.damageRoll() * (curStam/totStam  / 20);
            //standuser.curStam -= curStam - curStam;
            //superFinger = false;

            punchDMG += standUser.damageRoll() * 2f;
            superFinger = false;

            return punchDMG;
        }
        return (int) (standUser.damageRoll()  * powerA);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        if(superFinger == true && fieldOfView[enemy.pos]) {
            Ballistica aim = new Ballistica(pos, worldCell, Ballistica.STOP_TERRAIN);

            if (enemy.invisible == 0 && !isCharmedBy(enemy) && fieldOfView[enemy.pos] && aim.subPath(1, aim.dist).contains(enemy.pos)){
                finger = aim;
                worldCell = aim.collisionPos;
                return true;
            }
        }

      return super.canAttack(enemy);
    }

    protected boolean doAttack( Char enemy ) {

        boolean visible = Dungeon.level.heroFOV[pos];

        if(!superFinger) {
            if (visible) {
                sprite.attack(enemy.pos);
            } else {
                attack(enemy);
            }

            spend(attackDelay());
        }
        else
        {
            spend( attackDelay() );

            finger = new Ballistica(pos, worldCell, Ballistica.STOP_TERRAIN);
            if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[finger.collisionPos] ) {
                sprite.zap( finger.collisionPos );
                return false;
            }
        }
        return !visible;
    }

    @Override
    public void spend(float time)
    {
        TimeStop TheWorld = buff(TimeStop.class);
        if(TheWorld != null)
        {
            TheWorld.processTime(time, ACTIONS_IN_FROZEN_TIME);
        }
        else
        {
            super.spend(time);
        }
    }



    public void checkSuperStop()
    {
        if(superStop == true)
        {
            //let the two Chars move again
            actPriority--;
            standUser.actPriority--;
            ACTIONS_IN_FROZEN_TIME--;
            if(ACTIONS_IN_FROZEN_TIME <= 0)
            {
                cancelTimeStop();
            }

        }

    }


    /*
    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if(!standUser.isAlive())
            {
                destroy();
                sprite.die();
                return false;
            }

            checkSuperStop();

            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;

                int oldPos = pos;
                //always move towards the stand user when wandering
                if (getCloser( target = standUser.pos )) {
                    //moves 2 tiles at a time when returning to the stand user from a distance
                    if (!Dungeon.level.adjacent(standUser.pos, pos)){
                        getCloser( target = standUser.pos );
                    }
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    protected class Hunting extends Mob.Hunting {

        public static final String TAG	= "HUNTING";

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;
            if(!standUser.isAlive())
            {
                destroy();
                sprite.die();
                return false;
            }

            checkSuperStop();

            if(superFinger == true
                    && enemyInFOV
                    && Dungeon.level.distance( pos, enemy.pos ) <= 8)
            {
                return doAttack( enemy );
            }
            else if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                return doAttack( enemy );

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    state = WANDERING;
                    target = Dungeon.level.randomDestination();
                    return true;
                }

                int oldPos = pos;
                if (target != -1 && getCloser( target )) {

                    spend( 1 / speed() );
                    return moveSprite( oldPos,  pos );

                } else {
                    spend( TICK );
                    if (!enemyInFOV) {
                        sprite.showLost();
                        state = WANDERING;
                        target = Dungeon.level.randomDestination();
                    }
                    return true;
                }
            }
        }
    }
*/
}
