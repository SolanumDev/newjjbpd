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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.standAbilities;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class TimeAlteringLibrary{

	//The human limit to stop time is 5 seconds
	private int charge = 5;

	//When timestop is activated by a stand we want it to perform identical to how
	// the hourglass freezes time with some caveats to freeze all actors in place.
	// (This means blobs such as gases need to actually freeze and not harm actors, traps should activate after
	// time stop has ended etc)
	// Because mobs need to be able to come into the world of stopped time we must first test if they can behave in
	//

	public void updateCharge(int newCharge)
	{
		charge = newCharge;
	}


	public class timeStasisArtifact extends Buff {

		@Override
		public boolean attachTo(Char target) {

			if (super.attachTo(target)) {

				int usedCharge = Math.min(charge, 5);
				//buffs always act last, so the stasis buff should end a turn early.
				spend(usedCharge - 1);
				((Hero) target).spendAndNext(usedCharge);

				//shouldn't punish the player for going into stasis frequently
				Hunger hunger = target.buff(Hunger.class);
				if (hunger != null && !hunger.isStarving())
					hunger.satisfy(usedCharge);

				target.invisible++;

				Dungeon.observe();

				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}

		@Override
		public void detach() {
			if (target.invisible > 0)
				target.invisible --;
			super.detach();
			Dungeon.observe();
		}
	}

	public class timeFreezeArtifact extends Buff{

		float partialTime = 1f;

		ArrayList<Integer> presses = new ArrayList<Integer>();

		public void processTime(float time){
			partialTime += time;

			while (partialTime >= 1f){
				partialTime --;
				charge --;
			}

			if (charge < 0){
				charge = 0;
				detach();
			}

		}

		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}

		private void triggerPresses(){
			for (int cell : presses)
				Dungeon.level.press(cell, null, true);

			presses = new ArrayList<>();
		}

		@Override
		public boolean attachTo(Char target) {
			if (Dungeon.level != null)
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					mob.sprite.add(CharSprite.State.PARALYSED);
			GameScene.freezeEmitters = true;
			return super.attachTo(target);
		}

		@Override
		public void detach(){
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				mob.sprite.remove(CharSprite.State.PARALYSED);
			GameScene.freezeEmitters = false;

			super.detach();
			triggerPresses();
		}

		private static final String PRESSES = "presses";
		private static final String PARTIALTIME = "partialtime";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);

			int[] values = new int[presses.size()];
			for (int i = 0; i < values.length; i ++)
				values[i] = presses.get(i);
			bundle.put( PRESSES , values );

			bundle.put( PARTIALTIME , partialTime );
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);

			int[] values = bundle.getIntArray( PRESSES );
			for (int value : values)
				presses.add(value);

			partialTime = bundle.getFloat( PARTIALTIME );
		}
	}


	public class timeStasis extends Buff {

		@Override
		public boolean attachTo(Char target) {

			if (super.attachTo(target)) {

			    /*
				int usedCharge = Math.min(charge, 5);
				//buffs always act last, so the stasis buff should end a turn early.
				spend(usedCharge - 1);
				((Hero) target).spendAndNext(usedCharge);

				//shouldn't punish the player for going into stasis frequently
				Hunger hunger = target.buff(Hunger.class);
				if (hunger != null && !hunger.isStarving())
					hunger.satisfy(usedCharge);
                */


				target.invisible++;

				Dungeon.observe();

				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean act() {
			detach();
			return true;
		}

		@Override
		public void detach() {
			if (target.invisible > 0)
				target.invisible --;
			super.detach();
			//activeBuff = null;
			Dungeon.observe();
		}
	}

	static public class timeFreeze extends Buff{

		float partialTime = 1f;

		ArrayList<Integer> presses = new ArrayList<Integer>();

		public void freezeTime(float time){
			float seconds = time;

			while (seconds >= 1f) {
				seconds--;
			}

			if (seconds <= 0){
				seconds = 0;
				detach();
			}

		}

		public void setDelayedPress(int cell){
			if (!presses.contains(cell))
				presses.add(cell);
		}

		private void triggerPresses(){
			for (int cell : presses)
				Dungeon.level.press(cell, null, true);

			presses = new ArrayList<>();
		}

		@Override
		public boolean attachTo(Char target) {
			if (Dungeon.level != null)
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
					mob.sprite.add(CharSprite.State.PARALYSED);
			GameScene.freezeEmitters = true;
			return super.attachTo(target);
		}

		@Override
		public void detach(){
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				mob.sprite.remove(CharSprite.State.PARALYSED);
			GameScene.freezeEmitters = false;


			super.detach();

			triggerPresses();
		}

		private static final String PRESSES = "presses";
		private static final String PARTIALTIME = "partialtime";


	}

}
