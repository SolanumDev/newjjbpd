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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class DIOHighSprite extends HumanSprite {

	public Animation taunt;
    public Animation checkmate;
    private int cellToAttack;

	public DIOHighSprite() {
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

		zap = attack.clone();

		taunt = new Animation(10, true);
		taunt.frames( film, 37,37,29+37,66,67,67);

		play( idle );
	}

	public void tauntEnemy(int curPos)
	{

			place(curPos);
			play(taunt);
            showStatus(255, "WRRRRY",this);
	}

    @Override
    public void attack( int cell ) {
        if (!Dungeon.level.adjacent( cell, ch.pos )) {

            cellToAttack = cell;
            turnTo( ch.pos , cell );
            play( zap );

        } else {

            super.attack( cell );

        }
    }

	@Override
	public void onComplete( Animation anim )
    {
        if (anim == zap) {
            idle();

            ((MissileSprite)parent.recycle( MissileSprite.class )).
                    reset( ch.pos, cellToAttack, new ThrowingKnife(), new Callback() {
                        @Override
                        public void call() {
                            ch.onAttackComplete();
                        }
                    } );
        } else {
            super.onComplete( anim );
        }
    }
}

