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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class DIOTestSprite extends MobSprite {

    private Tweener jumpTweener;
    private Callback jumpCallback;

	public DIOTestSprite() {
		super();
		
		texture( Assets.DIOTEST );
		
		TextureFilm frames = new TextureFilm( texture, 46, 41 );

		idle = new Animation( 1, true );
		idle.frames( frames, 0+19-19, 0+19-19, 0+19-19, 1+19-19, 0+19-19, 0+19-19, 1+19-19, 1+19-19);

		run = new Animation(20, true );
		run.frames(frames, 39-18-19, 40-18-19, 41-18-19, 42-18-19, 43-18-19, 44-18-19 );

		attack = new Animation( 15, false );
		attack.frames( frames, 50-18-19, 51-18-19, 52-18-19, 37-18-19 );

		zap = attack.clone();

		die = new Animation(20, false );
		die.frames(frames, 45-18-19, 46-18-19, 47-18-19, 48-18-19, 49-18-19, 49-18-19 );

		play( idle );
	}

	public void crush( int from, int to, Callback callback ){


		play( run );

	}

	public void crush(){


		play( run );

	}


	@Override
    public void jump( int from, int to, Callback callback )
    {
        jumpCallback = callback;

        int distance = Dungeon.level.distance( from, to );
        jumpTweener = new LeapTweener( this, worldToCamera( to ), 100, 7f );
        jumpTweener.listener = this;
        parent.add( jumpTweener );

        turnTo( from, to );

    }


	protected static class LeapTweener extends Tweener {

		public Visual visual;

		public PointF start;
		public PointF end;
		public PointF vertex;

		public float height;

		public LeapTweener( Visual visual, PointF pos, float height, float time ) {
			super( visual, time );

			this.visual = visual;
			start = visual.point();
			vertex.set((start.x + pos.x)/2,(start.y + pos.y)/2);
			end = vertex;
			this.height = height;
		}

		@Override
		protected void updateValues( float progress ) {
			visual.point( PointF.inter( start, end, progress ).offset( 0, -height * 4 * progress * (1 - progress) ) );
		}
	}

	protected static class LandTweener extends Tweener {

		public Visual visual;

		public PointF start;
		public PointF end;

		public float height;

		public LandTweener( Visual visual, PointF pos, float height, float time ) {
			super( visual, time );

			this.visual = visual;
			start = visual.point();
			end = pos;
			this.height = height;
		}

		@Override
		protected void updateValues( float progress ) {
			visual.point( PointF.inter( start, end, progress ).offset( 0, -height * 4 * progress * (1 - progress) ) );
		}
	}


}
