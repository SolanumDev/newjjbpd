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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class HighDIOSprite extends HumanSprite {

	private Animation taunt;
	public HighDIOSprite() {
		super();

		texture( Assets.DIO );

		TextureFilm film = new TextureFilm( texture, 12, 15 );

		idle = new Animation( 1, true );
		idle.frames(film,  37, 37, 37, 38, 37, 37, 38, 38 );

		run = new Animation(20, true );
		run.frames(film, 39, 40, 41, 42, 43, 44 );

		die = new Animation(20, false );
		die.frames(film, 45, 46, 47, 48, 49, 49 );

		attack = new Animation( 15, false );
		attack.frames( film, 50, 51, 52, 37 );

		taunt = new Animation(10, false);
		taunt.frames( film, 37,37,29+37,66,67,67);

		play( idle );
	}

	public void taunt()
	{
		play(taunt);
	}

}

