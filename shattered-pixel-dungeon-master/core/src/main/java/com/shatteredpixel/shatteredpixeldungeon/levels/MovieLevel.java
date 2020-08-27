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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StandUser;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.CameraScrollTweener;
import com.watabou.utils.Bundle;

import java.util.Collection;

public abstract class MovieLevel extends Level{

    //protected Director director = null;
    public CameraScrollTweener koma = null;
    protected String DIRECTOR = "mangaka";
    protected String DIRECTOR_CHAIR = "director chair";
    String COUNTER = "counter";
    String PHASE = "phase";
    String CUTSCENE = "cutsene active";

    protected static int phase = 0;

    protected static int counter = 0;
    protected static boolean cutsceneActive = false;

    protected int directorChair = 2;

    public static abstract class Director extends TrainingDummy {

        //TODO: what's the longest scene in JoJo that translates well into an interactive cutscene?

        {
            //we technically don't act at all
            actPriority = BUFF_PRIO -1;

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

    }

    public boolean redirect()
    {
        GLog.w("Error: no script found");

        return true;
    }

    public void nextScene()
    {
        for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
            mob.sprite.idle();
            Dungeon.hero.sprite.idle();
        }



        nextPhase();
    }

    public int getCounter()
    {
        return counter;
    }

    public int getPhase()
    {
        return phase;
    }

    public static void setPhase(int phase) {
        MovieLevel.phase = phase;
    }

    public static void setCounter(int counter) {
        MovieLevel.counter = counter;
    }

    public void nextPhase()
    {
        phase++;
        counter = 0;
    }

    public abstract void script();

    public void blinder(Char toBlind)
    {
        Buff.prolong( toBlind, Blindness.class, 50 );
    }

    public void unblind(Char toBlind)
    {
        Buff.detach(toBlind, Blindness.class);
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

    public boolean nullifyCharacter(Char toNullify)
    {
        if(toNullify == Dungeon.hero)
        {
            return  false;
        }

        if(toNullify instanceof StandUser)
        {
            ((StandUser) toNullify).killStand();
        }

        toNullify = null;
        return  true;
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
