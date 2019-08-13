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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DIO;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Iced;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.CreamSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

//TODO: fix Ice crashing the game when being called back from bundle
//also allow Cream to move after swallowing Iced

public class Cream extends Stand {

	boolean swallowed = false;
	boolean superVoid = false;
	boolean superSmash = false;
	private int maxCrit =  100;

	{
		spriteClass = CreamSprite.class;
		baseSpeed = (float) (1/speedB);

		HUNTING = new Hunting();
		WANDERING = new Wandering();

		defenseSkill = 10;
		state = HUNTING;

		//TODO: if this doesn't work check for Vanilla Ice
		//if in the world do nothing
		//else become hostile when not DIO
		if (Dungeon.hero.givenName() != "DIO" || Dungeon.hero.givenName() != "Shadow DIO" ){
			alignment = alignment.ENEMY;
		}


	}

	private final String SWALLOWED = "swallowed";
	private static final String ICED = "iced";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SWALLOWED, swallowed);
		bundle.put( ICED, standUser );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		swallowed = bundle.getBoolean(SWALLOWED);
	}

	public void restoreIced(Bundle bundle)
	{
		standUser = (Iced)bundle.get(ICED);
	}

	public void swallow(Char prey)
	{
		//JoJo Must Die only
		if(prey != standUser && Dungeon.level.adjacent(this.pos, prey.pos))
		{
			prey.HP -= maxCrit;
		}
		else if(prey == standUser && Dungeon.level.adjacent(this.pos, prey.pos))
		{
			sprite.attack(standUser.pos);

			Actor.remove(standUser);
			Dungeon.level.mobs.remove(standUser);
			//TODO: Give boss chars the health indicator
			//TargetHealthIndicator.instance.target(null);
			standUser.sprite.kill();

			swallowed = true;
		}

	}

/*
	public Cream(Char standMaster){
		this.standUser = standMaster;
		HP = standUser.HP;
		HT = standUser.HT;
		this.alignment = standUser.alignment;
	}
*/
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );
	}

	@Override
	public void notice()
	{
		sprite.showStatus(0x7E6790, "GRROSHOU!",this);
	}

	@Override
	protected Char chooseEnemy() {
		Char enemy = super.chooseEnemy();

		//will never attack something outside of the stand range
		if (enemy != null &&  Dungeon.level.distance(enemy.pos, standUser.pos) <= rangeE){
			return enemy;
		} else {
			return null;
		}
	}

	@Override
	public int attackSkill( Char target ) {
		return (int) (standUser.damageRoll()  * powerA);
	}

	@Override
	public int drRoll() {
		return (int) (standUser.drRoll() * powerA);
	}

	private class Wandering extends Mob.Wandering {

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			super.act(enemyInFOV, justAlerted);

			if(!swallowed)
			{
				//!RTD 1 in 10
				getCloser(standUser.pos);
				swallow(standUser);
			}

			return true;
		}
	}

	private class Hunting extends Mob.Hunting {

		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {

			if(!swallowed)
			{
				//!RTD 1 in 10
				/*!RTD 1 in 5
				getCloser(enemy.pos);
				swallow(enemy);
				 */

				getCloser(standUser.pos);
				swallow(standUser);
				return !doAttack(enemy);
			}

			return doAttack(enemy);
		}
	}

}
