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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.StarPlatinum;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;

public class TimeStop extends FlavourBuff {

	public static final float DURATION	= 10f;

	{
		type = buffType.SILENT;
	}

	public void processTime(float time, float timeToAct){
		float partialTime = 0;
		partialTime += time;
		//Dungeon.stand.
		while (partialTime >= 1f){
			partialTime --;
			timeToAct --;
		}

		if (timeToAct <= 0){
			detach();
		}

	}
	
	@Override
	public boolean attachTo( Char target ) {
		if (Dungeon.level != null) {
			for (Mob mobs : Dungeon.level.mobs.toArray(new Mob[0])) {
				if(mobs != target)
				{
					mobs.sprite.add(CharSprite.State.PARALYSED);
				}
			}
		}
		GameScene.freezeEmitters = true;
		return super.attachTo(target);
	}

	
	@Override
	public void detach() {
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			mob.sprite.remove(CharSprite.State.PARALYSED);
		}
		GameScene.freezeEmitters = false;
		super.detach();
			}

	@Override
	public int icon() {
		return BuffIndicator.PARALYSIS;
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.PARALYSED);
		else target.sprite.remove(CharSprite.State.PARALYSED);
	}

	@Override
	public String heroMessage() {
		return Messages.get(this, "heromsg");
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", dispTurns());
	}

	public static float duration( Char ch ) {
		return DURATION;
	}
}
