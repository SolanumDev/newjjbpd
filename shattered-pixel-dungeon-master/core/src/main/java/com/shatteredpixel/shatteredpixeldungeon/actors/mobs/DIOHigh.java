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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.TheWorld;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HighDIOSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DIOHigh extends Mob {
	TheWorld stand;
	{
		spriteClass = HighDIOSprite.class;

		HP = HT = 50;
		defenseSkill = 2;

		WANDERING = new Wandering();

		maxLvl = 5;
	}


	@Override
	public void notice() {

		super.notice();
		yell(Messages.get(this, "notice"));


		if( Dungeon.level.distance(enemy.pos, pos) <= 4 && !standState())
		{
			summonStand();
		}
		else{yell("this doesn't work lol");}


	}

	public boolean standState()
	{
		/*
		Mob blob = new TheWorld();

		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof TheWorld) {
				blob = mob;
				//return true;
			}
			else
			{
				blob = mob;
				//return false;
			}
		}
*/
		return stand != null;
	}

	//taking damage from the enemy
	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		stand.HP = this.HP;


	}


	public void killStand() {

		if (standState()) {

            	/*
            	Actor.remove(stand);
                stand.destroy();
                stand.sprite.killAndErase();
			*/

			yell("Come back to me, The World!");


			stand.destroy();
			stand.sprite.die();
/*

			Actor.remove(stand);
			Dungeon.level.mobs.remove(stand);
			TargetHealthIndicator.instance.target(null);
			stand.sprite.kill();
			//stand.destroy();



			yell("Nani");
			//stand.sprite.kill();
			yell("shimata");
			TargetHealthIndicator.instance.target(null);
			yell("shine kakyoin");
*/

				/*
				stand.destroy();
				stand.sprite.kill();*/
		} else if(!standState() && Dungeon.level.distance(enemy.pos, pos) <= 4) {
			yell("Gaze upon my stand which has the power to rule the world!");

		}else{
			yell("nice code retard");
		}
	}

	public void summonStand(){
		{
			killStand();

			stand = new TheWorld();

			yell("The World!");
			stand.HP = this.HP;
			stand.enemy = this.enemy;


			standPosition();

		}

	}

	public void standPosition()
	{


		ArrayList<Integer> spawnPoints = new ArrayList<>();

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add(p);
			}
		}

		if (spawnPoints.size() > 0) {

			stand.pos = Random.element(spawnPoints);

			GameScene.add(stand);
			Actor.addDelayed(new Pushing(stand, pos, stand.pos), -1);
		}

	}

	@Override
	public void die( Object cause ) {

		super.die( cause );
		yell(Messages.get(this, "killed"));
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.SND_DEATH );
		}

		if(stand.isAlive())
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
	public int drRoll() {
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

				//put it here
				for (Mob mob : Dungeon.level.mobs) {
					if (mob instanceof TheWorld) {
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
}
