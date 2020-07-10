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

package com.shatteredpixel.shatteredpixeldungeon.ui.inGameButtons;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands.StarPlatinumHero;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class SuperThreeIndicator extends Tag {

	public static final int COLOR	= 0xFF006E;

	private Image icon;
	public Mob stand = null;


	public SuperThreeIndicator() {
		super( COLOR );
		flip(true);
		setSize( 24, 24 );
	}

	@Override
	protected void createChildren() {
		super.createChildren();

        icon = Icons.BUSY.get();
        add( icon );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = x + (width - icon.width) / 2;
		icon.y = y + (height - icon.height) / 2;

	}



	@Override
	protected void onClick() {
        if(Dungeon.stand != null)
        {

        	//TODO: switch cases
            if(Dungeon.stand instanceof StarPlatinumHero) {
            	if(GameScene.freezeEmitters == false)
				{
					Dungeon.hero.sprite.showStatus(0x6E266E, "Star Platinum:", Dungeon.hero);
					Dungeon.hero.sprite.showStatus(0xEADD33, "'The World'", Dungeon.hero);
					Dungeon.stand.abilityThree();
				}
				else {
					Dungeon.hero.sprite.showStatus(0xEADD33, "Time has begun to move again", Dungeon.hero);
					Dungeon.stand.cancelAbility();
				}

                }

         }

         else{
            GLog.w("Your stand must be active to use its power!");
         }
	 }

    @Override
    public void update() {
	    super.update();
        if(Dungeon.stand != null){
            visible = true;
        }
        else
        {
            visible = false;
        }
    }



}

