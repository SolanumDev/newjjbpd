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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.SilverChariot;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PolnareffSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.ChariotSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Polnareff extends Mob {

boolean canSummon = true;
	private static final String TXT_HERO_KILLED = "Fell by a wayward avenger";
	SilverChariot stand = new SilverChariot();

	{
		spriteClass = PolnareffSprite.class;
		
		HP = HT = 50;
		stand.HP = this.HP;
		stand.HT = this.HT;

		WANDERING = new Wandering();
		HUNTING = new Hunting();

		defenseSkill = 5;
		//stand.defenseSkill = (int)(defenseSkill( enemy )*defB);

		EXP = 100;
		maxLvl = 5;

		state = WANDERING;

	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 2, 10 );
	}



	@Override
	public void notice() {

		super.notice();
		checkSummon();
		yell(Messages.get(this, "notice"));


	}


		//	setStandsActive(1);

		//	changeSP(-30);

/*
	@Override
	protected Char chooseEnemy() {
		Char enemy = super.chooseEnemy();

		//will never attack something far from the user
		if (stand.enemy != null &&  Dungeon.level.distance(enemy.pos, this.pos) <= 4){
			return stand.enemy;
		} else {
			return null;
		}
	} */

	public void checkSummon()
	{
		boolean enemyInFOV = (enemy != null) && enemy.isAlive() && fieldOfView[enemy.pos] && (enemy.invisible <= 0);
		boolean standSummoned = Dungeon.level.mobs.clone() instanceof SilverChariot;

		if((enemyInFOV) && (Dungeon.level.distance(enemy.pos, this.pos) <= 4) && (!standSummoned) )
		{

		}
		else if(((!enemyInFOV) || (Dungeon.level.distance(enemy.pos, this.pos) > 4)) && (standSummoned)
				|| (Dungeon.level.distance(stand.pos, this.pos) > 4))
		{
            stand.destroy();
            ((ChariotSprite)sprite).vanishCompletely();
            //stand.sprite.play(vanish);

		}

	}


    public void summonStand(){


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




//TO DO: see if this function works properly in game
    /* public void desummon(Object cause) {
        if (state != HUNTING && for(Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone() mob instanceof  SilverChariot) {

            for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
                if (mob instanceof SilverChariot) {
                    mob.die(cause);
                }
            }
        }
    }
*/


        @Override
	public void die( Object cause ) {

		super.die( cause );
		yell(Messages.get(this, "killed"));
		if (Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play( Assets.SND_DEATH );
		}
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof SilverChariot) {
				mob.die( cause );
			}
		}

	}


	@Override
	public int attackSkill( Char target ) {
		return 12;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 5);
	}

    private class Hunting extends Mob.Hunting {

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

    private class Wandering extends Mob.Wandering {

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            if ( enemyInFOV ) {

                enemySeen = true;

                notice();
                alerted = true;
                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;

//                checkSummon();

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
