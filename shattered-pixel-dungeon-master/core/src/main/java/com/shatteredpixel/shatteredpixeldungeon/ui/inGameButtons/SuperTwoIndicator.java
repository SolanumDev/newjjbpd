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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands.StarPlatinumHero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands.StarPlatinumTest;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;

public class SuperTwoIndicator extends Tag {

	public static final int COLOR	= 0xFF006E;

	private Image icon;
	public Mob stand = null;


	public SuperTwoIndicator() {
		super( COLOR );
		flip(true);
		setSize( 24, 24 );

		if(Dungeon.stand != null){
            visible = true;
        }
        else
        {
            visible = false;
        }

	}

	@Override
	protected void createChildren() {
		super.createChildren();

        icon = Icons.MAGE.get();
        add( icon );
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		icon.x = x + (width - icon.width) / 2;
		icon.y = y + (height - icon.height) / 2;

	}

    public static CellSelector.Listener FINGER = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {

            if(cell != null) {
                Ballistica route = new Ballistica(Dungeon.stand.pos, cell, Ballistica.PROJECTILE);
                Char myEnemy;


                //if we find a character at the selected cell, engage that enemy

                 //if (Actor.findChar(cell) != null &&
                         if ( cell != Dungeon.stand.pos && Dungeon.level.distance(cell, Dungeon.stand.pos) <= 8) {
                    myEnemy = Actor.findChar(cell);
                    Dungeon.stand.engageEnemy(myEnemy);

                    Dungeon.stand.sprite.turnTo(Dungeon.stand.pos, cell);
                    Dungeon.stand.updateCell(cell);

                    Dungeon.hero.spendAndNext(1f);
                    Dungeon.stand.spend(1f);

                    Dungeon.stand.sprite.showStatus(0xB200FF, "ORA!", Dungeon.stand);
                    Dungeon.stand.abilityTwo();

                    //
                    route = null;
                 }

                else {
                    GLog.w("Select a valid target");
                }
            }
        }

        @Override
        public String prompt() {
            return "Select an enemy to finger!";
        }
    };

	@Override
	protected void onClick() {
        if(Dungeon.stand != null)
        {
            if(Dungeon.stand instanceof StarPlatinumHero || Dungeon.stand instanceof StarPlatinumTest) {
                Dungeon.hero.sprite.showStatus(0xB200FF,
                        "Star Finger", Dungeon.hero);

                for (Buff b : Dungeon.hero.buffs())
                {
                    if (b instanceof MindVision) {
                        b.detach();
                    }
                }
                GameScene.selectCell(FINGER);
                //Dungeon.stand.abilityTwo();

                //Dungeon.hero.spend(1);
                //Dungeon.stand.spend(1);
            }
        }
        else{
            GLog.w("Your stand must be active to use its power!");
        }
	    	}

    @Override
    public void update() {
        if(Dungeon.stand != null){
            visible = true;
        }
        else
        {
            visible = false;
        }
        super.update();
	}



}

