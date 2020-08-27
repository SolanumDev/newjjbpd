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

package com.shatteredpixel.shatteredpixeldungeon.levels.levels_SDC.scripts;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MovieActor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Avdol;
import com.shatteredpixel.shatteredpixeldungeon.levels.Director;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;

public class SDC_PrisonLevelDirector extends Director {

    public boolean act() {

        //...and action!
        if(Dungeon.level instanceof MovieLevel)
        {
            script();
        }

        sprite.showStatus(0xC0C0C0, "Phase: " + phase + ", Count: " + counter);
        counter++;

        return super.act();
    }

    @Override
    public void script() {
        updateValues();
        ((MovieLevel)Dungeon.level).script();
    }

    public void updateValues()
    {
        ((MovieLevel)Dungeon.level).setPhase(phase);
        ((MovieLevel)Dungeon.level).setCounter(counter);
    }

}
