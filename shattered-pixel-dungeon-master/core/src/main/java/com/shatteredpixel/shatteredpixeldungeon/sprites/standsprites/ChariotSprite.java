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

package com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;



public class ChariotSprite extends MobSprite {

	private Animation raydarts;
	private Animation vanish;

	public ChariotSprite() {
		super();
		
		texture( Assets.CHARIOT );

		TextureFilm film = new TextureFilm( texture, 13, 14 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 1);
		
		run = new Animation( 3, true );
		run.frames( film, 1, 2, 1, 2, 1, 2 );
		
		die = new Animation( 10, false );
		die.frames( film, 1, 1, 3, 3, 3 );
		
		attack = new Animation( 12, false );
		attack.frames( film, 4, 5, 6 );

		raydarts = new Animation( 12, false );
		raydarts.frames( film, 4, 5, 6, 7 );

		vanish = new Animation(12, true);
		vanish.frames( film,  7);



		idle();
	}

	public void vanishCompletely()
	{
		play(vanish);
	}
}
