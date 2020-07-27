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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KenshiroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Kenshiro extends Mob {

	private boolean fisted = false;
	private String FISTED = "FISTED";

	{
	    name = "Kenshiro";
		spriteClass = KenshiroSprite.class;

		HP = HT = 55;
		defenseSkill = 8;

		EXP = 15;
		maxLvl = 10;
		state = WANDERING;
	}

	@Override
	public void storeInBundle( Bundle bundle ) {

		super.storeInBundle( bundle );
		bundle.put( FISTED, fisted);

	}

	@Override
	public void restoreFromBundle(Bundle bundle)
	{
		super.restoreFromBundle(bundle);
		bundle.getBoolean(FISTED);
	}

	public void crackedFist()
	{

		fisted = true;
		if(state != HUNTING)
		{
			state = HUNTING;
		}
	}

	@Override
	public void abilityOne() {

	}



	/*@Override
	protected float attackDelay(){
		if(fisted) {
			//fisted = false;
			return 0.02f;
		}
		return 1;
	}
*/
	@Override
	public int damageRoll() {
		if(fisted)
		{
		        int maxDam = enemy.HP/2;
		        int minDam = enemy.HP/10;

		        sprite.showStatus(255,"atatatata",this);
		        fisted = false;
				return Random.NormalIntRange(minDam, maxDam);
		}
		return Random.NormalIntRange( 1, 4 );
	}

	@Override
	public void notice()
	{
		yell("You are already dead");
		crackedFist();
	}
	
	@Override
	public int attackSkill( Char target ) {
		return 360;
	}

	@Override
    protected boolean doAttack( Char enemy ) {

        boolean visible = Dungeon.level.heroFOV[pos];

		if (visible) {

			if(fisted) {
				((KenshiroSprite) sprite).fistRush(enemy.pos);
				spend(attackDelay()* (float) 0.02);
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


    @Override
	public int drRoll() {
		return 0;
	}

	protected class Hunting extends Mob.Hunting {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

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
