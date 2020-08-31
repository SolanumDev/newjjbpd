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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.watabou.utils.Bundle;

import org.json.JSONException;

public class MovieActor extends NPC {

    public String description = "All the World's a stage";

    private String DESCRIPTION = "desc";
    private String REAL_NAME = "actual name";
    private String COSTUME = "costume";

	{
		spriteClass = HumanSprite.class;
		name = "Actor";
		state = LISTENING;
	}

	public void initialize(String name, String desc, Class costume, int startpos)
    {
        this.name = name;
        this.description = desc;
        spriteClass = costume;
        pos = startpos;
    }

	@Override
	public boolean interact() {
		sprite.turnTo( pos, Dungeon.hero.pos );
		return true;
	}

    @Override
    public String description() {
        return description;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(REAL_NAME, name);
        bundle.put(DESCRIPTION, description);
        bundle.put(COSTUME, spriteClass);

    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        name = bundle.getString(REAL_NAME);
        description = bundle.getString(DESCRIPTION);
        spriteClass = bundle.getClass(COSTUME);
    }
}
