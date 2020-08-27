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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.AlterableProjectile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class HorusSprite extends MobSprite {

	public HorusSprite() {
		super();
		
		texture( Assets.HORUS );
		
		TextureFilm frames = new TextureFilm( texture, 16, 24 );
		
		idle = new Animation( 4, true );
		idle.frames( frames, 0, 0, 0, 0, 0, 0, 1 );
		
		run = new Animation( 10, true );
		run.frames( frames, 0 );
		
		attack = new Animation( 15, false );
		attack.frames( frames, 0, 1, 2, 3, 4 );

		rushAttack = attack.clone();

		die = new Animation( 1, false );
		die.frames( frames, 0);
		
		play( idle );
	}

	@Override
	public void link( Char ch ) {
		super.link( ch );
		add( State.CHILLED );
	}

	@Override
	public void attack( int cell ) {
        ((MissileSprite)parent.recycle( MissileSprite.class )).
                reset( ch.pos, ((Mob) ch).enemy.pos, new AlterableProjectile(ItemSpriteSheet.ICE_MISSILE_LIGHT), new Callback() {
                    @Override
                    public void call() {
                        ch.onAttackComplete();
                    }
                } );
        play( rushAttack );
        turnTo( ch.pos , cell );
	}

	public void rushAttack(int cell)
	{
        ((MissileSprite)parent.recycle( MissileSprite.class )).
                reset( ch.pos, ((Mob) ch).enemy.pos, new AlterableProjectile(ItemSpriteSheet.ICE_MISSILE_LIGHT) , new Callback() {
                    @Override
                    public void call() {
                        ch.onAttackComplete();
                    }
                } );
        play( rushAttack );
        turnTo( ch.pos , cell );
	}

}
