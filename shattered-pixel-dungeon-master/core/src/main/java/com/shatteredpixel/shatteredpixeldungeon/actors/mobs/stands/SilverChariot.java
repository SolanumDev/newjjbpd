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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Polnareff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.ChariotSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SilverChariot extends Mob {
	private Polnareff polnareff = new Polnareff();

	private static final String TXT_HERO_KILLED = "Skewered by _Silver Chariot_";
	{

		spriteClass = ChariotSprite.class;
		
		HP = 5;
		HT = 5;
		defenseSkill = (int)(polnareff.defenseSkill( enemy )*defB);

		
		EXP = 0;

		flying= true;

		state = WANDERING;

		properties.add(Property.STAND);


	}
	
	@Override
	public int damageRoll() {
		return (int)(polnareff.damageRoll()*powerA);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)(polnareff.attackSkill(target)*powerA);

	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 4);
	}



	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.SND_DEGRADE );
		}
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof Polnareff) {
				mob.die( cause );
			}
		}


	}

	public void standCrash(Object cause){
		if(HP <= HT * .3 )
		{
			Messages.get( polnareff, "stand_crash");
			super.die(cause);

		}
	}

}
