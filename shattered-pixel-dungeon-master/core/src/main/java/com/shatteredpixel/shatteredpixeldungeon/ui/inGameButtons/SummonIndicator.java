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
/*
package com.shatteredpixel.shatteredpixeldungeon.ui.inGameButtons;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;

public class SummonIndicator extends Tag {
	
	public static final int COLOR	= 0xFF006E;

	private Image icon;
	private Stand stand;


	public SummonIndicator() {
		super( 0xFF006E );

		setSize( 24, 16 );

		if(Dungeon.hero.givenName() != joseph || Dungeon.hero.givenName() != jonathan) {
			visible = true;
		}
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		icon = SKULL
		add( icon );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = left() - 10;
		icon.y = y + (height - icon.height) / 3;

	}
	

	

	@Override
	protected void onClick() {
		if (mob instanceof summonStand) {
			mob.die(cause);
		} else {
			summonStand(Dungeon.hero, Dungeon.stand);
		}
	}

    @Override
    protected void summonStand( Mob host, Mob stand ) {
        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = host.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add( p );
            }
        }

        if (spawnPoints.size() > 0) {
			switch (this){

			}

            HeroClass.stand summonedStand = new HeroClass.stand();


            summonedStand.pos = Random.element( spawnPoints );

            GameScene.add( summonedStand );
            Actor.addDelayed( new Pushing( summonedStand, pos, summonedStand.pos ), -1 );
        }


    }

}

*/