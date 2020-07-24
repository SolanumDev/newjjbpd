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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StandUser;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Hierophant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Magician;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.KakyoinSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;


public class Kakyoin extends StandUser {

	{
		spriteClass = KakyoinSprite.class;

		HP = HT = 100;
		defenseSkill = 2;

		state = WANDERING;


	}

	protected static final String STAND	        = "stand";
/*

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( STAND, stand );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		stand = (Hierophant)bundle.get(STAND);

	}
*/

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if(standIsActive()) {
			stand.HP = this.HP;
		}

	}

	@Override
	public void declareStand() {
		stand = new Hierophant(this);
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
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}

}
