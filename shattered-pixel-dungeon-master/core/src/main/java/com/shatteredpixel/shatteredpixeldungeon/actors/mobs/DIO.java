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
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.DIOSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.TheWorldSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;


public class DIO extends Mob {
	//TheWorld stand;
	public TheWorld stand = new TheWorld();

	//boolean standSummoned = Dungeon.level.mobs.clone() instanceof stand;
	{
		spriteClass = DIOSprite.class;

		HP = HT = 50;
		stand.HP = this.HP;
		stand.HT = this.HT;

		defenseSkill = 2;


		HUNTING = new Hunting();
		WANDERING = new Wandering();

		EXP = 100;
		maxLvl = 5;

		state = WANDERING;
	}


    private static final String STAND	        = "stand";


	@Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( STAND, stand );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);


        //in some states tengu won't be in the world, in others he will be.

        if (state == WANDERING || state == SLEEPING) {
            stand = (TheWorld)bundle.get(STAND);
        }
            else{
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob instanceof TheWorld) {
                        stand = (TheWorld) mob;

                        //standPosition();

                        break;
                    }
                }
            }

        }



	@Override
	public void notice() {

		super.notice();
		yell(Messages.get(this, "notice"));


		//if( Dungeon.level.distance(enemy.pos, pos) <= 4 && !standState());
		if(!standState()) {
			summonStand();
		}
	}

	public boolean standState()
	{
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

		return blob instanceof TheWorld;
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


            //stand.destroy();
            //stand.sprite.killAndErase();


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


				/*
				stand.destroy();
				stand.sprite.kill();*/
        } else if(!standState() && Dungeon.level.distance(enemy.pos, pos) <= 4) {
            yell("Gaze upon my stand which has the power to rule the world!");
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


                    if( Dungeon.level.distance(enemy.pos, pos) <= 4 && !standState())
                    {
                        summonStand();
                    }
                    else{yell("this doesn't work lol");}


                }

				else if (enemy == null) {
				    yell("Where did you run off to?");
					state = WANDERING;
					killStand();
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

	private class standWandering extends Mob.Wandering {

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

				int oldPos = pos;
				//always move towards the hero when wandering
				if (getCloser( target = Dungeon.hero.pos )) {
					//moves 2 tiles at a time when returning to the hero from a distance
					if (!Dungeon.level.adjacent(Dungeon.hero.pos, pos)){
						getCloser( target = Dungeon.hero.pos );
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


    public class standTheWorld extends TheWorld{

        {
            spriteClass = TheWorldSprite.class;

            HP = HT = 50;
            defenseSkill = 2;

            flying= true;

            maxLvl = 5;
            state = HUNTING;
        }

        public void standCrash(Object cause){
            super.die(cause);
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
}
