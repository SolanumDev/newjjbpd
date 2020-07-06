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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Stand extends NPC {

	protected static final float ZAP_TIME	= 1f;
	protected static final float SUPER_ZAP_TIME	= 2f;

    protected final double powerA = 1.50f;
    protected final double powerB = 1.33f;
    protected final double powerC = 1f;
    protected final double powerD = 0.75f;
    protected final double powerE = 0.5f;

    protected final double speedA = 1.50f;
    protected final double speedB = 1.33f;
    protected final double speedC = 1f;
    protected final double speedD = 0.75f;
    protected final double speedE = 0.5f;

    protected final int rangeA = 15;
    protected final int rangeB = 8;
    protected final int rangeC = 4;
    protected final int rangeD = 3;
    protected final int rangeE = 2;

    protected final double defA = 1.50f;
    protected final double defB = 1.33f;
    protected final double defC = 1f;
    protected final double defD = 0.75f;
    protected final double defE = 0.5f;

    public double power = powerC;
    public double speed = speedC;
    public int range = rangeC;
    public double def = defC;

    public int primaryColor = 0xFF00DC;

	public Char standUser;
	{
		spriteClass = WraithSprite.class;

		HP = HT = 8;
		defenseSkill = 2;

		flying = true;
		EXP = 0;

		WANDERING = new Wandering();
		HUNTING = new Hunting();

        state = WANDERING;
		properties.add(Property.STAND);
	}

	public void parasitic()
	{
		if(standUser.alignment == Alignment.ALLY)
		{
			this.alignment = Alignment.ENEMY;
		}
		else if(standUser.alignment == Alignment.ENEMY)
		{
			this.alignment = Alignment.ALLY;
		}
		//TODO: create a stand-state that functions similar to
		//being hit by amok, attacking all entities
		//this.alignment = Alignmnet.BELLIGERENT
	}

	public void setStandUser(Char standMaster)
	{
		this.standUser = standMaster;
		HP = standUser.HP;
		HT = standUser.HT;
		this.alignment = standUser.alignment;
	}

	public void abilityOne()
    {}

    public void abilityTwo()
    {}

    public void abilityThree()
    {}

    @Override
    public void die( Object src ) {
        destroy();
        sprite.die();

        //ideally, there should be no instance where a stand would die but not the user
            //for canonical scenarios where this occurs (eg remote stands like Black Sabbath)
                //we can simply give the stand the corresponding property or override this function
        if(standUser != null) {
            standUser.die(src);
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

	public void standPosition(Char standUser)
	{


		ArrayList<Integer> spawnPoints = new ArrayList<>();

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = standUser.pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add(p);
			}
		}

		if (spawnPoints.size() > 0) {

			this.pos = Random.element(spawnPoints);

			GameScene.add(this);
			Actor.addDelayed(new Pushing(this, standUser.pos, this.pos), -1);
		}

	}

	public boolean interact()
	{
		if( (Dungeon.level.passable[pos] || Dungeon.hero.flying) && this.alignment == Alignment.ALLY) {
			int curPos = pos;

			moveSprite( pos, Dungeon.hero.pos );
			move( Dungeon.hero.pos );

			Dungeon.hero.sprite.move( Dungeon.hero.pos, curPos );
			Dungeon.hero.move( curPos );

			Dungeon.hero.spend( 1 / Dungeon.hero.speed() );
			Dungeon.hero.busy();


			return true;
		} else {
			//Dungeon.hero.sprite.play(attack(this.pos));
			this.sprite.showStatus(0xFF00DC,"MISS",this);
			return false;
		}
	}

	//using the stand user to check isAlive() is not only redundant, but also works against
        //certain users with unique stands (eg Vanilla Ice and Cream)
/*
    public boolean isAlive() {
        return HP > 0 && standUser.isAlive();
    }
*/

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if(!standUser.isAlive())
            {
                destroy();
                sprite.die();
                return false;
            }

            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;

                int oldPos = pos;
                //always move towards the stand user when wandering
                if (getCloser( target = standUser.pos )) {
                    //moves 2 tiles at a time when returning to the stand user from a distance
                    if (!Dungeon.level.adjacent(standUser.pos, pos)){
                        getCloser( target = standUser.pos );
                    }
                    spend( 1 / speed() );
                    return moveSprite( oldPos, pos );
                } else {
                    spend( TICK );
                }

            }
            return true;
        }

    }

    protected class Hunting extends Mob.Hunting {

        public static final String TAG	= "HUNTING";

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;
            if(!standUser.isAlive())
            {
                destroy();
                sprite.die();
                return false;
            }

            else if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                return doAttack( enemy );

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    state = WANDERING;
                    target = Dungeon.level.randomDestination();
                    return true;
                }

                int oldPos = pos;
                if (target != -1 && getCloser( target )) {

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

}
