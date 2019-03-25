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
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.TheWorldSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AAGenericStandUser extends Mob {
	Mob stand;

	boolean inRange()
	{
		return Dungeon.level.distance(enemy.pos, pos) <= 4;
	}

	{
		spriteClass = HumanSprite.class;

		HP = HT = 50;
		defenseSkill = 2;

		state = WANDERING;

		WANDERING = new Wandering();
		HUNTING = new Hunting();


		maxLvl = 5;
	}

	//knives
	public void abilityOne()
	{}
	//timeStop
	public void abilityTwo()
	{}
	//roadRoller
	public void abilityThree()
	{}

    public void recallStand()
	    {
		        stand.beckon(pos);
	    }

	public boolean checkRange()
	{
		return standIsActive() // &&
				//(Dungeon.level.distance(stand.enemy.pos, stand.pos) <= 4
						&& Dungeon.level.distance(pos, stand.pos) >4;
	}

	@Override
	public void notice() {

		super.notice();
		yell(Messages.get(this, "notice"));

	}

	protected Char chooseStandsEnemy(){
		Char enemy = super.chooseEnemy();

		//will never attack something far from the stand user
		if (enemy != null && standIsActive()){
			return stand.enemy;
		} else {
			return null;
		}
	}

	public boolean standIsActive()
	{
		return stand != null;
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if(standIsActive()) {
			stand.HP = this.HP;
		}

	}



	public void killStand() {

		yell("Come back to me, The World!");

		stand.destroy();
		stand.sprite.die();
		stand = null;

	}

	public void summonStand(){
		{
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

		if(stand.isAlive() && stand!= null)
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

				for(Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				{
					if(mob instanceof TheWorld) {
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

	public void standDamage(int dmg, Object src)
	{
		HP = stand.HP;
	}

	private class Hunting extends Mob.Hunting {

		public static final String TAG	= "HUNTING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				if(standIsActive() && !inRange())
				{
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

	public class TheWorld extends com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.TheWorld {

		{
			spriteClass = TheWorldSprite.class;

			HP = HT = 50;
			defenseSkill = 2;

			flying = true;

			maxLvl = 5;
			state = HUNTING;
			EXP = 0;
		}

		public void standCrash(Object cause){
			super.die(cause);
		}

		@Override
		protected Char chooseEnemy() {
			return chooseStandsEnemy();
		}


		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);

			standDamage(dmg, src);

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

	protected class standWandering extends Mob.Wandering {

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

				enemySeen = false;

				int oldPos = pos;

				for(Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				{
					if(mob instanceof AAGenericStandUser) {

					}
				}

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
