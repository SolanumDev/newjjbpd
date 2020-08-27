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

package com.shatteredpixel.shatteredpixeldungeon.levels;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MovieActor;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;

public abstract class Director extends MovieActor {

    public static int phase = 0;
    public static int counter = 0;
    public static final int END_OF_SCENE = 1000;

    public static boolean cutsceneActive = false;

    protected String DIRECTOR_CHAIR = "director chair";

    String COUNTER = "counter";
    String PHASE = "phase";
    String CUTSCENE = "cutsene active";



    protected int directorChair = 2;

        //TODO: what's the longest scene in JoJo that translates well into an interactive cutscene?

        {
            name = "Narrator";
            actPriority = HERO_PRIO + 1;
        }


        @Override
        public boolean act()
        {
            if(cutsceneActive)
            {
                Dungeon.hero.spend(1);
            }

            spend(TICK);
            return true;
        }

        public void focusCamera(Visual visual)
        {
            //GameScene.scenePause = true;
            Camera.main.target = visual;
        }

        public void focusCamera(Char actor)
        {
            Camera.main.target = actor.sprite;
        }

        public abstract void script();

        @Override
        public void damage( int dmg, Object src ) {
        //the director has god mode
        }

        @Override
        public void die( Object cause ) {
            //if he can somehow be damaged he certainly won't die
            HP = HT;
        }


    public void nextScene()
    {
        for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
            mob.sprite.idle();
            Dungeon.hero.sprite.idle();
        }



        nextPhase();
    }

    public void nextPhase()
    {
        phase++;
        counter = 0;
    }

    public int getCounter()
    {
        return counter;
    }

    public int getPhase()
    {
        return phase;
    }

    public void restartScript()
    {
        phase = 0;
        counter = 0;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DIRECTOR_CHAIR, directorChair);
        bundle.put(COUNTER, counter);
        bundle.put(PHASE, phase);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        directorChair = bundle.getInt(DIRECTOR_CHAIR);
        counter = bundle.getInt(COUNTER);
        phase = bundle.getInt(PHASE);

    }
}
