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
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;

public class JosephSDCsprite extends MobSprite {

	public JosephSDCsprite() {
		super();

		//37, 74, 111, 148,
		//185 is reserved for Badass Joseph

		int altSkin = 0;

		altSkin = 37 * (int) ((Math.random() * (5)) + 0);

		texture( Assets.OLD_JOSEPH );

		TextureFilm film = new TextureFilm( texture, 12, 15 );

		idle = new Animation( 1, true );
		idle.frames(film,  altSkin + 0, altSkin + 0, altSkin + 0, altSkin + 1, altSkin + 0, altSkin + 0, altSkin + 1, altSkin + 1 );

		run = new Animation(20, true );
		run.frames(film, altSkin + 2, altSkin + 3, altSkin + 4, altSkin + 5, altSkin + 6, altSkin + 7 );

		die = new Animation(20, false );
		die.frames(film, altSkin + 8, altSkin + 9, altSkin + 10, altSkin + 11, altSkin + 12, altSkin + 11 );

		attack = new Animation( 15, false );
		attack.frames( film, altSkin + 13, altSkin + 14, altSkin + 15, altSkin + 0 );

		play( idle );
	}
}
