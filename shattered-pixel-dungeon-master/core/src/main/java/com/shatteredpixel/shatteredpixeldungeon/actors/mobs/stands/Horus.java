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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RushingFlurry;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StandUser;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.HorusSprite;
import com.watabou.utils.Random;

public class Horus extends Stand {

	private int attackRange = 6; //Horus can fire ice missiles from up to 6 tiles away

	{
		spriteClass = HorusSprite.class;

		HP = HT = 8;
		defenseSkill = 2;
		flying = false;

		maxLvl = 5;

		power = powerB;
		speed = speedB;
		range = rangeD; //how far Horus can be from Pet Shop
		def = defC;

		WANDERING = new Wandering();
		HUNTING = new Hunting();

		properties.add(Property.IMMOVABLE);
	}


	@Override
	public void move (int step)
	{
		//Horus is unable to move
	}

	public void pingStandUser()
	{
		((Mob)standUser).beckon(pos);
		if(Dungeon.level.adjacent(this.pos, standUser.pos))
		{
			((StandUser) standUser).killStand();
		}
		spend(1);

	}

	@Override
	protected boolean canAttack(Char enemy) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);

		return Dungeon.level.distance(this.pos, enemy.pos) <= attackRange && enemySeen;

	}

    @Override
    protected boolean doAttack(Char enemy) {

	    Buff.affect(this, RushingFlurry.class).extend(3f);

        return super.doAttack(enemy);
    }

	@Override
	public int attackSkill( Char target ) {
		return 8;
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}


	protected class Wandering extends Mob.Wandering {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || Random.Int( distance( enemy ) / 2 + enemy.stealth() ) == 0)) {

				enemySeen = true;

				notice();
				alerted = true;
				state = HUNTING;
				target = enemy.pos;

			} else {

				pingStandUser();

			}
			return true;
		}
	}

	protected class Hunting extends Mob.Hunting {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy ) ){// && Random.Int(3) == 0) {

				return doAttack( enemy );

			} else {


				pingStandUser();

			}

			return true;
		}
	}
}
