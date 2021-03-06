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

package com.shatteredpixel.shatteredpixeldungeon.items;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Guard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Kenshiro;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TrainingDummy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Avdol;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.DIOShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Iced;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Kakyoin;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.PetShop;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.VanillaIceSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DebugStick extends Item {
	
	private static final String AC_Debug = "Debug";
	private static final String AC_Warp = "Warp";
	private static final String AC_Descend = "Fast Descend";

	private Mob toSpawn;
	
	{
		image = ItemSpriteSheet.STYLUS;
		
		stackable = true;

		bones = true;

		defaultAction = AC_Warp;
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_Debug );
		actions.add( AC_Warp);
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		switch(action)
		{
			case AC_Debug:
				toSpawn = new PetShop();

				ArrayList<Integer> spawnPoints = new ArrayList<>();

				for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
					int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
					if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
						spawnPoints.add(p);
					}
				}

				if (spawnPoints.size() > 0) {

					toSpawn.pos = Random.element(spawnPoints);

					GameScene.add(toSpawn);
					ScrollOfTeleportation.appear(toSpawn, toSpawn.pos);
				}
				else
				{
					//we're going to spawn the character on top of ourselves
					toSpawn.pos = Dungeon.hero.pos;

					GameScene.add(toSpawn);
					ScrollOfTeleportation.appear(toSpawn, toSpawn.pos);

					//Actor.addDelayed(new Pushing(toSpawn, Dungeon.hero.pos, toSpawn.pos), 0);
				}
                break;
			case AC_Warp:
				doWarp();
				break;

			default:
				toSpawn = new Spinner();
				toSpawn.pos = Dungeon.hero.pos;

				GameScene.add(toSpawn);
				Actor.addDelayed(new Pushing(toSpawn, Dungeon.hero.pos, toSpawn.pos), 0);
                break;

		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

	private void doWarp()
	{GameScene.selectCell(warper); }

    private static CellSelector.Listener warper = new CellSelector.Listener() {
        @Override
        public void onSelect( Integer target ) {
            if (target != null && Dungeon.level.passable[target]) {

            	//FIXME: the dev (and source code viewers) be able to bring their stand along

                Dungeon.hero.pos = target;
				Dungeon.observe();
				GameScene.updateFog();

				ScrollOfTeleportation.appear(curUser, target);
				Dungeon.level.press( target, curUser );
				curUser.spendAndNext(Actor.TICK);
            }
            else
			{
				GLog.w("You can't warp there!");
			}
        }
        @Override
        public String prompt() {
            //return Messages.get(this, "prompt");
			return "Select a tile to warp to";
        }
    };

	@Override
	public String desc() {
		return super.desc() + "\n \nCurrent Level: " + Dungeon.level.getClass().getSimpleName()
				+ "\nCurrent position: " + Dungeon.hero.pos;
	}

	@Override
	public int price() {
		return 30 * quantity;
	}
		}


