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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Magician;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.DullKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class MagicianSprite extends MobSprite {

	public MagicianSprite() {
		super();
		
		texture( Assets.MAGICIAN );
		
		TextureFilm frames = new TextureFilm( texture, 13, 17 );
		
		idle = new Animation( 2, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 2, 1, 0 );
		
		run = new Animation( 10, true );
		run.frames( frames, 1, 2, 3, 2, 1 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 5, 6, 7, 0);

		die = new Animation( 10, false );
		die.frames( frames, 0,0,0,0,0,0,0,0);

		zap = attack.clone();
		
		play( idle );
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );
		add( State.BURNING );
	}

	@Override
	public void die() {
		super.die();
		remove( State.BURNING );
	}

	public void zap( int cell ) {

		turnTo( ch.pos , cell );
		play( zap );


		((MissileSprite)parent.recycle( MissileSprite.class ))
		.
				reset( ch.pos, cell, new ThrowingKnife(), new Callback() {
					@Override
					public void call() {
						ch.onAttackComplete();
					}
				} );


		MagicMissile.boltFromChar( parent,
				MagicMissile.FIRE,
				this,
				cell,
				new Callback() {
					@Override
					public void call() {
						((Magician)ch).onZapComplete();
					}
				} );
		Sample.INSTANCE.play( Assets.SND_BURNING );

	}



}
