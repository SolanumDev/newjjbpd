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
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.DIOHighSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.TheWorldSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;



public class DIOHigh extends Mob {
	Mob stand;
	Mob roadRoller;
	protected boolean throwing = false;
	boolean inRange()
	{
		return Dungeon.level.distance(enemy.pos, pos) <= 4;
	}
	boolean timeIsStopped = false;
	{
		spriteClass = DIOHighSprite.class;

		HP = HT = 50;
		defenseSkill = 2;

		state = WANDERING;

		WANDERING = new Wandering();
		HUNTING = new Hunting();


		maxLvl = 5;
	}

	//knives
	public void abilityOne()	{
		throwing = true;

		if (state != HUNTING)
		{
			state = HUNTING;
		}

		//checkmate da!
		yell("This is checkmate!");
		act();
		throwing = false;
	}

	//timeStop
	public void abilityTwo() {
		float timeToAct = 9;
		GameScene.flash(0xFFFFFF);
		Sample.INSTANCE.play(Assets.SND_TELEPORT);

		GameScene.freezeEmitters = true;

		if (state != HUNTING)
		{
			state = HUNTING;
		}

		//Toki wo tomare!
		yell("Time is stopped!");


		if (standIsActive())
		{
			stand.state = stand.HUNTING;
			stand.enemy = this.enemy;
		}

		int n = 0;

		while(n < timeToAct){

			if (Dungeon.level != null)
			{
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
				{
					if(!((mob instanceof TheWorld) && (mob instanceof DIOHigh))) {
						//mob.sprite.add(CharSprite.State.PARALYSED);
						mob.paralysed++;
					}
				}
				//Dungeon.hero.sprite.add(CharSprite.State.PARALYSED);
				Dungeon.hero.paralysed++;
			}
			/*
			spend(-1 );

			if(standIsActive()) {
				stand.spend(-1);
			}
			*/

			n++;
		}


		if(standIsActive())
		{
			removeStand();
		}

		yell("And so time moves once more...");

		for(int iterations = 0; iterations < timeToAct; iterations++) {
			if (Dungeon.level != null) {

				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (!((mob instanceof TheWorld) && (mob instanceof DIOHigh))) {
						//mob.sprite.remove(CharSprite.State.PARALYSED);
						mob.paralysed--;
					}
				}
				//Dungeon.hero.sprite.remove(CharSprite.State.PARALYSED);
				Dungeon.hero.paralysed--;
			}
		}
		GameScene.freezeEmitters = false;

	}

	//roadRoller
	public void abilityThree()
	{
		yell("IT'S A ROAD ROLLER!");
		roadRoller = new DIORoadRollerHeavy();
		//FIXME: find a way to hide DIO's sprite
		roadRoller.pos = this.pos;

		GameScene.add( roadRoller );
		roadRoller.enemy = this.enemy;

		//attempts to crush player to death
		roadRoller.notice();

		this.pos = roadRoller.pos;
		yell("I've done it, I finally won!");

		/*
		roadRoller.destroy();
		roadRoller.sprite.die();
		roadRoller = null;
		*/
	}

	//bloodySummoning
	public void abilityFour()
    {
        sprite.showStatus(0xFF0000, "WRRRRRRRRY",this);

    }


	public void recallStand()
	{
		stand.beckon(pos);
	}

	public boolean checkTime()
	{
		return timeIsStopped;
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

		//((HighDIOSprite) sprite).tauntEnemy(this.pos);

        abilityThree();
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
	public void spend( float time ) {
		super.spend(time);
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

		removeStand();
	}

	public void removeStand(){
		stand.destroy();
		stand.sprite.die();
		stand = null;

	}

	public void summonStand(){
		{
			stand = new TheWorld();

			yell("The World!");
			stand.HT = this.HT;
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

		if(stand!= null)
		{
			if(stand.isAlive() ) {
				stand.destroy();
				stand.sprite.killAndErase();
			}
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

	@Override
	protected boolean canAttack( Char enemy )
	{
		if(throwing == true)
		{
			return Dungeon.level.distance( this.pos , enemy.pos ) <= 8;
		}
			return super.canAttack( enemy );
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
						//abilityTwo();
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
		public void notice() {
			//do nothing
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
					if(mob instanceof DIOHigh) {

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
