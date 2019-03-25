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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.StarPlatinum;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SuperOneIndicator extends Tag {

	public static final int COLOR	= 0xFF006E;

	private Image icon;
	public Mob stand = null;


	public SuperOneIndicator() {
		super( COLOR );
		flip(true);
		setSize( 24, 24 );


	}

	@Override
	protected void createChildren() {
		super.createChildren();

        icon = Icons.TARGET.get();
        add( icon );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = x + (width - icon.width) / 2;
		icon.y = y + (height - icon.height) / 2;

	}


       public static CellSelector.Listener ORA = new CellSelector.Listener(){

           @Override
           public void onSelect(Integer cell) {


                    Ballistica route = new Ballistica(Dungeon.stand.pos, cell, Ballistica.PROJECTILE);
                    cell = route.collisionPos;
                    Char myEnemy;

                    //can't occupy the same cell as another char, so move back one.
                    if (Actor.findChar(cell) != null && cell != Dungeon.stand.pos) {
                        myEnemy = Actor.findChar(cell);
                        Dungeon.stand.engageEnemy(myEnemy);
                        cell = route.path.get(route.dist - 1);
                    }

                    Dungeon.hero.spendAndNext(1);
                    Dungeon.stand.spend(1);

                    Dungeon.stand.sprite.turnTo(Dungeon.stand.pos, cell);
                    Dungeon.stand.updateCell(cell);

                    Dungeon.stand.sprite.showStatus( 0xB200FF,"ORA!",Dungeon.stand);
                    Dungeon.stand.abilityOne();

                    Dungeon.hero.spendAndNext(1);
                    Dungeon.stand.spend(1);
                    Dungeon.stand.next();

           }

           @Override
           public String prompt() {
               return "Select an enemy to smash!";
           }
       };
	@Override
	protected void onClick() {
        if(Dungeon.stand != null)
        {
            if(Dungeon.stand instanceof StarPlatinum) {
                if (Dungeon.level.adjacent(Dungeon.stand.pos, Dungeon.hero.pos)) {
                    Dungeon.hero.sprite.showStatus(0xB200FF, "Star Breaker", Dungeon.hero);
                    GameScene.selectCell(ORA);
                }
                if (!Dungeon.level.adjacent(Dungeon.stand.pos, Dungeon.hero.pos)) {
                    GLog.w("Your stand must be next to you to use that!");
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

