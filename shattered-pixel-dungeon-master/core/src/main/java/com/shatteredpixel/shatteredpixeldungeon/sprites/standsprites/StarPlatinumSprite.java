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
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;


public class StarPlatinumSprite extends MobSprite {

private Animation chargePunch;
private Animation fingerShot;

	public StarPlatinumSprite() {
		super();
		
		texture( Assets.STAR_PLATIUNM );

		TextureFilm film = new TextureFilm( texture, 15, 15 );

		//TODO: give Star Platinum's hair some flare
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 0, 0);
		
		run = new Animation( 2, true );
		run.frames( film, 7, 8, 7, 8, 7, 8 );
		
		die = new Animation( 3, false );
		die.frames( film, 0 );
		
		attack = new Animation( 12, false );
		attack.frames( film, 1, 4, 1, 4, 1, 4 );

		rushAttack = new Animation(12, false);
		rushAttack.frames( film, 1, 4, 2, 5, 3, 6);

		chargePunch = new Animation( 4, false );
		chargePunch.frames( film, 0, 7, 4, 4, 7, 0);

        fingerShot = new Animation( 4, false );
        fingerShot.frames( film, 0, 7, 8, 4, 4, 4, 0);

        idle();
	}

	public void punchStarBreaker( int from, int to, Callback callback ) {

        int distance = Dungeon.level.distance( from, to );
        Slide slide = new Slide( this, worldToCamera( to ), 0, distance * 0.1f);
        slide.listener = this;
        parent.add( slide );
        turnTo( from, to );
		play(chargePunch);

		//Slide slide = new Slide( this, enemyPos ,0,0.5f);

	}

	public void punchStarFinger(){
        play(fingerShot);
        play(idle);
    }

    @Override
    public void rushAttack(int cell) {
        play(this.rushAttack);
        play(idle);
    }

    protected static class Slide extends Tweener {

        public Visual visual;

        public PointF start;
        public PointF end;
        //public PointF vertex;

        public float height;

        public Slide( Visual visual, PointF pos, float height, float time ) {
            super( visual, time );
            height = height*2;
            this.visual = visual;
            start = visual.point();
            end = pos;
            this.height = height;
        }

        @Override
        protected void updateValues( float progress ) {
            visual.point( PointF.inter( start, end, progress ).offset( 0,0));
        }
    }

    @Override
    public void onComplete (Animation anim)
    {
        super.onComplete(anim);

        if(anim == fingerShot || anim == chargePunch || anim == rushAttack) {
            ch.onAttackComplete();
            idle();
        }
    }


}
