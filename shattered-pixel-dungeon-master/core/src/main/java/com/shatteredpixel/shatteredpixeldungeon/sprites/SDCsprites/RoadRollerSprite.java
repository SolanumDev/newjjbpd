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
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class RoadRollerSprite extends MobSprite {

    private Tweener jumpTweener;
    private Callback jumpCallback;
    public  static PointF arc;

	public RoadRollerSprite() {
		super();
		
		texture( Assets.ROAD_ROLLER );
		
		TextureFilm frames = new TextureFilm( texture, 46, 41 );

		idle = new Animation( 1, true );
		idle.frames( frames, 1, 1, 1, 1, 1);

		run = new Animation( 10, true );
		run.frames( frames, 0, 0 );

		attack = new Animation( 10, false );
		attack.frames( frames, 1, 1, 1, 2, 2 , 2, 3 , 4 , 5 , 3 , 4 , 5  , 3, 4 ,5);

		die = new Animation( 8, false );
		die.frames( frames, 8, 8, 8, 8, 8 );
		
		play( idle );
	}

	public void crush( int from, int to, Callback callback ){

		Callback crushCallback = callback;
		int distance = Dungeon.level.distance( from, to );

		play( run );
		LeapTweener leapTweener = new LeapTweener( this, worldToCamera( to ), -80, 1f);
		leapTweener.listener = this;
		parent.add( leapTweener );

		play( attack );
		LandTweener landTweener = new LandTweener( this, worldToCamera( to ), -80, 1f);
		landTweener.listener = this;
		parent.add( landTweener );


	}

	public void crush(){
		play( run );
	}


	@Override
    public void jump( int from, int to, Callback callback )
    {
        jumpCallback = callback;

        int distance = Dungeon.level.distance( from, to );
        jumpTweener = new LeapTweener( this, worldToCamera( to ), 100, 1f );
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

			vertex = pos;
			vertex = vertex.set((start.x ),(start.y - 100 ));
            arc = vertex;
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
			start = arc;//visual.point();
			end = pos;
			this.height = height;
		}

		@Override
		protected void updateValues( float progress ) {
			visual.point( PointF.inter( start, end, progress ).offset( 0, height * 4 * progress * (1 - progress) ) );
		}
	}


}
