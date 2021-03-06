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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Hierophant;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Magician;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.StarPlatinum;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands.StarPlatinumHero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.TheWorld;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.heroStands.StarPlatinumTest;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CharSelectPT3;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.stand;

public class SummonIndicator extends Tag {

	public static final int COLOR	= 0xFF006E;

	private Image icon;


	public SummonIndicator() {
		super( 0xFF4C4C );
		flip(true);
		setSize( 24, 16 );

    if(Dungeon.currentScene > Dungeon.CHARS_BT)
    {
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

        icon = Icons.get(Dungeon.hero.heroClass);
        add( icon );
	}

	@Override
	protected void layout() {
		super.layout();

		icon.x = x + (width - icon.width) / 2;
		icon.y = y + (height - icon.height) / 2;

	}

//FIXME: this function will handle the logic behind pairing a character to a stand
//hopefully it'll make the onClick() less confusing

	public Stand standChecker()
    {
     return Dungeon.stand;
    }


	@Override
	protected void onClick() {

        //Banner stando = new Banner(Dungeon.hero.sprite );
        //stando.show( 0xFFFFFF, 0.3f, 5f );
        //GameScene.showImage( stando, 0xFFFFFF, 0.3f, 5f  );

        //GameScene.show( new WndMessage( Messages.get(GameScene.class, "dont_know") ) ) ;
        if (stand == null) {
            switch (Dungeon.hero.heroClass)
            {
                //SDC
                case JOTARO:
                    stand = new StarPlatinumHero(Dungeon.hero);
                    Dungeon.hero.summonStand(stand);
                    GameScene.flash(0x7B588E);
                    break;
                case WARRIOR:
                case MAGE:
                case HUNTRESS:
                case ROGUE:
                    stand = new StarPlatinumTest();
                    stand.setStandUser(Dungeon.hero);

                    //Dungeon.hero.summonStand(stand);
                    //GameScene.flash(0x7B588E);

                    break;
                case KAKYOIN:
                    stand = new Hierophant();
                    stand.setStandUser(Dungeon.hero);
                    break;
            }

            Dungeon.hero.summonStand(stand);
            GameScene.flash(stand.primaryColor);
        }

        /*
        if (stand == null) {

            if(Dungeon.currentScene == Dungeon.CHARS_ORIG) {
                // for testing purposes the original heroes can summon stands too
                // In future releases they will be used only for testing
                // or completely removed

                //stand = new StarPlatinumTest(Dungeon.hero);
                stand = new StarPlatinumTest();
                stand.setStandUser(Dungeon.hero);

                Dungeon.hero.summonStand(stand);
                GameScene.flash(0x7B588E);
            }


            if(Dungeon.currentScene == Dungeon.CHARS_SDC) {
                if (CharSelectPT3.selectedClass == HeroClass.JOTARO) {
                    Dungeon.stand = new StarPlatinumHero(Dungeon.hero);
                    Dungeon.hero.summonStand(stand);
                    GameScene.flash(0x7B588E);
                }
            }
            else if (Dungeon.currentScene == Dungeon.CHARS_DIU)
            {

            }
            else if (Dungeon.currentScene == Dungeon.CHARS_GW)
            {

            }
            else if (Dungeon.currentScene == Dungeon.CHARS_SO)
            {

            }
            else if (Dungeon.currentScene == Dungeon.CHARS_SBR)
            {

            }
            else if (Dungeon.currentScene == Dungeon.CHARS_JJL)
            {

            }
            else if (Dungeon.currentScene == Dungeon.CHARS_SCRT)
            {

            }
        }*/

        else if(Dungeon.level.adjacent(stand.pos,Dungeon.hero.pos ) && stand != null){

            //prevents an "infinite" time stop (removing these would either crash the game
            // or cause frozen mob sprites to never
            // return to action, should the latter occur, a simple reset
            // of the level will return the mob sprites to normal)
            if(stand instanceof StarPlatinumHero || stand instanceof  StarPlatinumTest && GameScene.freezeEmitters == true)
            {
                Dungeon.hero.sprite.showStatus(0x7F006E, "Time has begun to move again", Dungeon.hero);
                stand.cancelAbility();
            }
            //TODO: make the world inherit stand logic
          /*  else if( Dungeon.stand instanceof TheWorld  && GameScene.freezeEmitters == true)
            {
                Dungeon.hero.sprite.showStatus(0xEADD33, "And so time moves once more", Dungeon.hero);
                Dungeon.stand.cancelAbility();
            }
*/

            stand.destroy();
            stand.sprite.die();
            stand = null;
        }

        else
        {
            GLog.w("Your stand must be next to you to recall it!");
        }
	}

/*
	//FIXME: preferably, the stand should only consider onClick or onLongClick independently
    @Override
    protected boolean onLongClick() {
        Dungeon.hero.sprite.showStatus(0xFF00DC, "I'll rush you down!");
        return false;
    }
*/

    @Override
    public void update() {
	    if(Dungeon.hero.isAlive() && Dungeon.currentScene > Dungeon.CHARS_BT)
        {
            visible = true;
        }
        else if(Dungeon.hero.isAlive() && Dungeon.currentScene == Dungeon.CHARS_ORIG) {
            visible = true;
        }
        else
        {
            visible = false;
        }
        super.update();
    }

}

