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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Cream;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.VanillaIceSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Iced extends Mob {
	Stand stand;

//TODO: fix Ice crashing the game when being called back from bundle

	boolean inRange()
	{
		return Dungeon.level.distance(enemy.pos, pos) <= 4;
	}

	boolean superStomp = false;
	String SUPERSTOMP = "superStomp";


	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );
		bundle.put(SUPERSTOMP, superStomp);

	}

	@Override
	public void restoreFromBundle(Bundle bundle)
	{
		super.restoreFromBundle(bundle);
		bundle.getBoolean(SUPERSTOMP);

	}

	{
		spriteClass = VanillaIceSprite.class;

		HP = HT = 50;
		defenseSkill = 8;

		state = WANDERING;

		WANDERING = new Wandering();
		HUNTING = new Hunting();

		EXP = 225;
		maxLvl = 49;
	}


	public void abilityOne()
	{

	}

	public void abilityTwo()
	{}

	public void abilityThree()
	{
		stompToDeath();
	}

	public void stompToDeath()
	{
		superStomp = true;
	}


    public void recallStand()
	    {
		        stand.beckon(pos);
	    }

	public boolean checkRange()
	{
		return standIsActive() // &&
				//(Dungeon.level.distance(stand.enemy.pos, stand.pos) <= 4
						&& Dungeon.level.distance(pos, stand.pos) >4;
	}

	@Override
	public void notice() {

		super.notice();
		yell(Messages.get(this, "notice"));

		abilityThree();

	}


	@Override
	protected boolean doAttack( Char enemy ) {

		boolean visible = Dungeon.level.heroFOV[pos];

		if (visible) {

			if(superStomp) {
				((VanillaIceSprite) sprite).punchIntoStomp(enemy.pos);
			}
			else {
				sprite.attack(enemy.pos);
			}
		} else {
			attack( enemy );
		}
		spend( attackDelay() );

		return !visible;
	}

	protected Char chooseStandsEnemy(){
		Char enemy = super.chooseEnemy();

		//will never attack something far from the stand user
		if (enemy != null && standIsActive()){
			return stand.enemy;
		} else {
			return null;
		}
	}

	public boolean standIsActive()
	{
		return stand != null;
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if(standIsActive()) {
			stand.HP = this.HP;
		}

	}


	public void killStand() {

		yell("Come back to me, " + stand.name);

		stand.destroy();
		stand.sprite.die();
		stand = null;

	}

	public void summonStand() {
        {

            //for (Mob mob : Dungeon.level.mobs)
            for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
                if (mob instanceof Cream) {
                    stand = (Stand) mob;
                    stand.setStandUser(this);
                }
                else if(stand == null){
                    stand = new Cream();
                    yell(Messages.get(this, "summon"));
                    stand.setStandUser(this);

                    stand.standPosition(this);
                }
            }

        }
    }

	@Override
	public void die( Object cause ) {

		super.die( cause );
		yell(Messages.get(this, "killed"));
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.SND_DEATH );
		}

		if(stand == null)
		{
            //do nothing
		}
		else if(stand.isAlive())
		{
			stand.destroy();
			stand.sprite.killAndErase();
			//stand.die(cause);
		}

	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 8;
	}

	@Override
	protected float attackDelay(){
		if(superStomp) {
			superStomp = false;
			return 0.1f;
		}
		return 1;
	}
	
	@Override
	public int drRoll() {
		if(superStomp)
		{
			Random.NormalIntRange(0, enemy.HP/3);
		}
		return Random.NormalIntRange(0, 1);
	}

	private class Wandering extends Mob.Wandering {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Int( distance( enemy ) / 2 + enemy.stealth() ) == 0)) {

				enemySeen = true;

				notice();
				alerted = true;
				state = HUNTING;
				target = enemy.pos;



			} else {

				enemySeen = false;

				for(Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				{
					if(mob instanceof Cream) {
						killStand();
					}
				}


				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					spend( 1 / speed() );
					return moveSprite( oldPos, pos );
				} else {
					target = Dungeon.level.randomDestination();
					spend( TICK );
				}

			}
			return true;
		}

	}

	private class Hunting extends Mob.Hunting {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				if(standIsActive() && !inRange())
				{
					recallStand();
				}

				chooseStandsEnemy();
				if( standIsActive() && !checkRange()) {
					recallStand();
				}

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {

					if(!standIsActive() && inRange())
					{
						summonStand();
					}

					chooseStandsEnemy();
					if( standIsActive() && !checkRange()) {
                        recallStand();
                    }

					target = enemy.pos;
				} else if (enemy == null) {
					if(standIsActive()) {
						recallStand();
					}
					state = WANDERING;
					target = Dungeon.level.randomDestination();
					return true;
				}

				int oldPos = pos;
				if (target != -1 && getCloser( target )) {

					chooseStandsEnemy();
					if( standIsActive() && checkRange()) {
						//recallStand();
						killStand();
					}
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
