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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Horus;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PetShopSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PolnareffSprite;
import com.watabou.utils.Random;

public class PetShop extends StandUser {

	{
		spriteClass = PetShopSprite.class;
		
		HP = HT = 80;
		defenseSkill = 5;
		baseSpeed = 2f;

		flying = true;
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 8 );
	}

	@Override
	public void declareStand() {
		stand = new Horus();
		stand.setStandUser(this);
	}

	@Override
	public void die(Object cause) {

		super.die(cause);

		if(stand!= null && stand.isAlive())
		{
			stand.destroy();
			stand.sprite.killAndErase();

		}
	}

	@Override
	public void summonStand() {

		if(!standIsActive()) {
			standsActive++;
			declareStand();
			stand.setAlignment(this.alignment);
			updateRange(stand.range);
			stand.standPosition(this);

		}

	}

	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}

}
