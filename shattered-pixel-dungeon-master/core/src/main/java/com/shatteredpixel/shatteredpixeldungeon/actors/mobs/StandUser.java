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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.DummyStand;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Hierophant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Magician;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class StandUser extends Mob {
	public Stand stand = null;

	protected int standLastPos = -1;
	protected boolean standIsActive = false;

	protected int currentRange = 8;

	protected static final String STAND_LAST_POS	        = "stand";

	{
		spriteClass = HumanSprite.class;

		state = WANDERING;

		WANDERING = new Wandering();
		HUNTING = new Hunting();

	}


	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		if(stand!= null)
		{
            standLastPos = stand.pos;
            bundle.put( STAND_LAST_POS, standLastPos );
        }
	}
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		standLastPos = bundle.getInt(STAND_LAST_POS);
	}


	protected void updateRange(int rangeToUpdate)
    {
        currentRange = rangeToUpdate;
    }


	public boolean inRange() {
		return Dungeon.level.distance(enemy.pos, pos) <= currentRange;
	}


    public boolean checkRange() {
        return standIsActive() // &&
                //(Dungeon.level.distance(stand.enemy.pos, stand.pos) <= 4
                && Dungeon.level.distance(pos, stand.pos) >= currentRange;
    }

	//light
	public void abilityOne() {
	}

	//medium
	public void abilityTwo() {
	}

	//heavy
	public void abilityThree() {
	}

	public void recallStand() {
		stand.beckon(pos);
	}

	@Override
	public void notice() {

		super.notice();
	}

	protected Char chooseStandsEnemy() {
		Char enemy = super.chooseEnemy();

		//will never attack something far from the stand user
		if (enemy != null && standIsActive()) {
			return stand.enemy = this.enemy;
		} else {
			return null;
		}
	}

	public boolean standIsActive() {
		return stand != null;
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if (standIsActive()) {
			stand.HP = this.HP;
		}

	}


	public void killStand() {

		stand.destroy();
		stand.sprite.die();
		stand = null;

	}

	public void summonStand() {

		if (standLastPos != -1)
		{
			declareStand();
			silentSummon(standLastPos);
		}

		else if(!standIsActive()) {

			declareStand();
			yell(stand.name + "!");
			updateRange(stand.range);
			stand.standPosition(this);

		}

		stand.setStandUser(this);
	}

	//Useful for forcefully summoning a stand outside of its normal range or allowing the AI
	//to create a pseudo-tandem setup
	public void silentSummon(int position)
	{
		stand.standPosition(position);
	}


	public void declareStand()
	{
		//pretend this is abstract
	}




	@Override
	public void die(Object cause) {

		super.die(cause);
		yell(Messages.get(this, "killed"));

        if(stand!= null && stand.isAlive())
        {
            stand.destroy();
            stand.sprite.killAndErase();

        }
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
					if(mob instanceof Magician) {
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
			//if (I've seen the enemy, and am not charmed by the enemy, and) I can attack the enemy...
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                //if my stand is active and the enemy is out of my stand's range...
				if(standIsActive() && !inRange())
				{
                    //recall my stand
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