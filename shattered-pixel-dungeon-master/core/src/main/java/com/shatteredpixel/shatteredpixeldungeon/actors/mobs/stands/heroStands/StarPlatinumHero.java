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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeFreeze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStop;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.effects.Finger;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.StarPlatinumSprite;
import com.watabou.utils.Callback;

public class StarPlatinumHero extends Stand {
	Char standUser;
	boolean superPunch = false;
    boolean superFinger = false;
    boolean superStop = false;
    public float ACTIONS_IN_FROZEN_TIME = 5;
	{
		spriteClass = StarPlatinumSprite.class;

		if(standUser != null){
            HP = standUser.HP;
            HT = standUser.HT;
        }

		defenseSkill = 10;

		HUNTING = new Hunting();
        WANDERING = new Wandering();
		state = WANDERING;

	}


	public StarPlatinumHero(Char standMaster){
		standUser = standMaster;

		//TODO: should a non-Jotaro character have star platinum
        //it will become a parasitic stand, calling parasitic();

		alignment = standUser.alignment;
		HP = standUser.HP;
		HT = standUser.HT;


	}

    public void updateCell( Integer cell)
    {
        worldCell = cell;
    }

	@Override
    public void abilityOne()
    {
        starBreaker(worldCell);
    }

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
        starFinger();
    }

    //FIXME:
    //this function is only boolean because I'm too lazy to not shamelessly steal 00-Evan's Guard code
    //it's also very convenient for exiting the function because of returns
    public boolean starFinger()
    {
        //superFinger = true;

        Ballistica finger = new Ballistica(pos, worldCell, Ballistica.PROJECTILE);

        {
            int newPos = -1;
            for (int i : finger.subPath(1, finger.dist)){
                if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                    newPos = i;
                    break;
                }
            }

            if (newPos == -1){
                return false;
            } else {
                final int newPosFinal = newPos;
                //this.target = newPos;
                ((StarPlatinumSprite) sprite).punchStarFinger();

                sprite.parent.add(new Finger(sprite.center(), enemy.sprite.center(), new Callback() {
                    public void call() {
                        Actor.addDelayed(new Pushing(enemy, enemy.pos, newPosFinal, new Callback(){
                            public void call() {
                                enemy.pos = newPosFinal;
                                Dungeon.level.press(newPosFinal, enemy, true);
                                attack(enemy);
                                if (enemy == Dungeon.hero) {
                                    Dungeon.hero.interrupt();
                                    Dungeon.observe();
                                    GameScene.updateFog();
                                }
                            }
                        }), -1);


                        superFinger = false;
                    }
                }));
            }
        }


        return true;
    }

    @Override
    public void abilityThree()
    {
        //timeStop();
        tryToStopTime();
    }

    @Override
    public void cancelAbility()
    {
        cancelTimeStop();
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

        for (Mob mobs : Dungeon.level.mobs.toArray(new Mob[0])) {
            if(mobs != this)
            {
                mobs.sprite.add(CharSprite.State.PARALYSED);
                Buff.prolong( mobs, TimeFreeze.class, ACTIONS_IN_FROZEN_TIME );

            }
        }
        if(standUser != Dungeon.hero)
        {
            standUser.sprite.remove(CharSprite.State.PARALYSED);
            Dungeon.hero.sprite.add(CharSprite.State.PARALYSED);
            Buff.prolong( Dungeon.hero, TimeFreeze.class, ACTIONS_IN_FROZEN_TIME );
        }
        GameScene.freezeEmitters = true;
    }

	@Override
    public int defenseProc( Char enemy, int damage ) {

	    if(enemy == standUser)
        {
            interact();
            return 0;
        }

	    return super.defenseProc(enemy, damage);
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

            if(enemy == Dungeon.hero)
            {
                punchDMG = 0;
            }

            return punchDMG;
        }
		return (int) (standUser.damageRoll()  * powerA);
	}

    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        //will never attack something outside of the stand range
        if (enemy != null &&  Dungeon.level.distance(enemy.pos, standUser.pos) <= rangeC){
            return enemy;
        } else {
            return null;
        }
    }

	@Override
	public int attackSkill( Char target ) {
		return standUser.attackSkill(target) * (int) powerA;
	}

	@Override
    protected boolean canAttack( Char enemy ) {
	    if(superFinger == true) {
            return Dungeon.level.distance( pos, enemy.pos) <= 8;
        }
        if(superPunch == true)
        {
            return Dungeon.level.distance(pos, enemy.pos) <= rangeC;
        }
        return Dungeon.level.adjacent(pos, enemy.pos);
    }

	@Override
	public int drRoll() {
		return (int) (standUser.drRoll() * powerA);
	}

	@Override
    public void notice()
    {
        //TODO: find a way to hide the (!) icon
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
            starFinger();
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


    @Override
    public void die( Object src ) {
        destroy();
        sprite.die();
        standUser.die(src);
    }

    @Override
    public void damage(int dmg, Object src)
    {
        super.damage(dmg, src);

        standUser.sprite.showStatus(CharSprite.WARNING,String.valueOf(dmg),this);
        standUser.HP = this.HP;
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

}
