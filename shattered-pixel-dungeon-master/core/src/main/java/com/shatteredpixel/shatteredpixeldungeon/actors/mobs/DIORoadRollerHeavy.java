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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.RoadRollerSprite;
import com.watabou.noosa.Camera;
import com.watabou.utils.Random;
import com.watabou.utils.Callback;

public class DIORoadRollerHeavy extends Mob {

	{
		spriteClass = RoadRollerSprite.class;

		HP = HT = 1;
		defenseSkill = 1;
		//act on the seventh second of stopped time
		actPriority = MOB_PRIO-8;

		EXP = 0;
	}
	
	@Override
	public int damageRoll() {
	    int lowerLim;
	    int upperLim;
	    if(enemy.HP == 1){
        lowerLim = 0;
        upperLim = 0;
        }

        else
        {
	        lowerLim = enemy.HP / 3;
	        upperLim = enemy.HP-2;
        }

		return Random.NormalIntRange( lowerLim, upperLim );
	}

	protected void crushToDeath( Char defender )
	{
	    if(state != HUNTING)
        {
            state = HUNTING;
        }

		int startingPos = this.pos;

        Ballistica route = new Ballistica(this.pos, defender.pos, Ballistica.PROJECTILE);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar( cell ) != null && cell != this.pos)
            {cell = route.path.get(route.dist-1);}

		final int dest = cell;
		((RoadRollerSprite) sprite).crush( startingPos, dest, new Callback(){
            @Override
            public void call() {

                CellEmitter.center(dest).burst(Speck.factory(Speck.DUST), 10);
                CellEmitter.center(dest).burst(Speck.factory(Speck.ROCK), 10);
                Camera.main.shake(7, 1f);
                //act();

            }

        }

        );
        attack(defender);
	}

	@Override
    public void notice() {
        crushToDeath(enemy);
    }
	
	@Override
	public int attackSkill( Char target ) {
		int flatten = enemy.HP;
	    return flatten;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}


    protected class Hunting extends Mob.Hunting {

        public static final String TAG	= "HUNTING";

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

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
