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


public class CreamSprite extends MobSprite {

	private Animation swallow;
	private Animation vanish;
	private Animation vanishMouth;
	private Animation reveal;
	private Animation runMouth;

	public CreamSprite() {
		super();
		
		texture( Assets.CREAM );

		TextureFilm film = new TextureFilm( texture, 13, 16 );
		
		idle = new Animation( 1, true );
		idle.frames( film, 0, 0, 1, 2);
		
		run = new Animation( 3, true );
		run.frames( film, 6, 7 );

		runMouth = new Animation( 3, true );
		runMouth.frames( film, 10, 11 );
		
		die = new Animation( 1, false );
		die.frames( film, 24 );
		
		attack = new Animation( 21, false );
		attack.frames( film, 2, 3, 4, 5, 4, 3, 2 );

		swallow = attack.clone();
		swallow.frames = attack.frames;

		vanish = new Animation(12, true);
		vanish.frames( film,  12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);

		vanishMouth = new Animation(12, true);
		vanishMouth.frames( film,    13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);

		reveal = new Animation(12, true);
		reveal.frames( film,  8, 9, 10);


		play(idle);
	}

	public void vanish( int from, int to, Callback callback ) {

		play(vanish);
		int distance = Dungeon.level.distance( from, to );
		Slide slide = new Slide( this, worldToCamera( to ), 0, distance * 0.1f);
		slide.listener = this;
		parent.add( slide );
		turnTo( from, to );
		//play(vanish);

		//Slide slide = new Slide( this, enemyPos ,0,0.5f);

	}

	public void vanishMouth( int from, int to, Callback callback ) {

		play(vanish);
		int distance = Dungeon.level.distance( from, to );
		Slide slide = new Slide( this, worldToCamera( to ), 0, distance * 0.1f);
		slide.listener = this;
		parent.add( slide );
		turnTo( from, to );
		//play(vanish);

		//Slide slide = new Slide( this, enemyPos ,0,0.5f);

	}

	@Override
	public void onComplete(Animation anim)
	{
		super.onComplete(anim);


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

}
