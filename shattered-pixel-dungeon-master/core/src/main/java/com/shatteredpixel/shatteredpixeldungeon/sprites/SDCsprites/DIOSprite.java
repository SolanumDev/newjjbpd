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

package com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.watabou.noosa.TextureFilm;

public class DIOSprite extends HumanSprite {

	private Animation taunt;
	public DIOSprite() {
		super();

		texture( Assets.DIO );

		TextureFilm film = new TextureFilm( texture, 12, 15 );

		idle = new Animation( 1, true );
		idle.frames(film,  0, 0, 0, 1, 0, 0, 1, 1 );

		run = new Animation(20, true );
		run.frames(film, 2, 3, 4, 5, 6, 7 );

		die = new Animation(20, false );
		die.frames(film, 8, 9, 10, 11, 12, 11 );

		attack = new Animation( 15, false );
		attack.frames( film, 13, 14, 15, 0 );

		taunt = new Animation(10, false);
		taunt.frames( film, 0,0,29,29,30,30);

		play( idle );
	}

	public void taunt()
	{
		play(taunt);
	}

}
