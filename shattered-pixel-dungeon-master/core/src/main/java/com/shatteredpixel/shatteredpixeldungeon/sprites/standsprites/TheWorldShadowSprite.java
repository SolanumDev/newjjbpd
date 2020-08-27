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


public class TheWorldShadowSprite extends MobSprite {

	private Animation timestop;
	private Animation rushDown;
	private Animation vanish;

	public TheWorldShadowSprite() {
		super();
		
		texture( Assets.THE_WORLD );

		TextureFilm film = new TextureFilm( texture, 14, 15 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 84, 84, 84, 84);
		
		run = new Animation( 3, true );
		run.frames( film, 7+84, 7+84);
		
		die = new Animation( 10, false );
		die.frames( film, 0+84, 10+84, 10+84, 10+84, 10+84 );
		
		attack = new Animation( 12, false );
		attack.frames( film, 1+84,4+84,1+84,4+84);

		rushDown = new Animation(12,false);
		rushDown.frames(film, 1+84,4+84,2+84,5+84,3+84,6+84,1+84,4+84,2+84,5+84,3+84,
				6+84,1+84,4+84,2+84,5+84,3+84,6+84,1+84,4+84,2+84,5+84,3+84,6+84);

		timestop = new Animation( 12, false );
		timestop.frames( film, 0+84, 8+84, 9+84);

		vanish = new Animation(12, false);
		vanish.frames( film,  7+84);



		idle();
	}

	public void rushAttack()
	{
		play(rushDown);
	}
}
