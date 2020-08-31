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

package com.shatteredpixel.shatteredpixeldungeon.levels.levels_SDC;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Kakyoin;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Director;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SDC_SchoolInfirmaryLevel extends MovieLevel {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

    private Director nurse;

	public int WIDTH = 14;
	public int HEIGHT = 14;

    private Kakyoin threat;

    //keep track of that need to be removed as the level is changed. We dump 'em back into the level at the end.
	private ArrayList<Item> storedItems = new ArrayList<>();
	
	@Override
	public String tilesTex() {
		return Assets.TILES_TOWN;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}

	private static final String STORED_ITEMS    = "storeditems";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put( STORED_ITEMS, storedItems);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);

		for (Bundlable item : bundle.getCollection(STORED_ITEMS)){
			storedItems.add( (Item)item );
		}
	}
	
	@Override
	protected boolean build() {
		
		setSize(WIDTH, HEIGHT);

		map = INFIRMARY.clone();

		buildFlagMaps();
		cleanWalls();

		CustomTiledVisual vis = new infirmaryVisual();
		vis.pos(0, 0);
		customTiles.add(vis);

		vis = new infirmaryVisualWalls();
		vis.pos(0, 0);
		customWalls.add(vis);

		addVisuals();


		entrance = WIDTH * 2 + 3;
		exit  = -1;

		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
	    nurse = new Director();
	    nurse.pos = 39;
	    mobs.add(nurse);

		threat = new Kakyoin();
		threat.pos = 165;
		threat.state = threat.LISTENING;
		mobs.add(threat);

	}
	
	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
		//TODO: put a medkit down here at pos 164
	}

	protected boolean engageKakyoin(int cell)
    {
         if(cell > 58 && cell < 67)
         {
             return true;
         }

     return false;
    }


	@Override
	public void press( int cell, Char ch ) {

		super.press(cell, ch);

		if (ch == Dungeon.hero){

            //TODO: when the player reaches the first set of curtains have Kakyoin run to pos 105
			/*
            if(engageKakyoin(cell))
            {
            	if(threat!= null)
				{
					threat.movieTarget = 105;
				}
            }*/
		}

	}

	@Override
	public int randomRespawnCell() {
		return 5+2*32 + PathFinder.NEIGHBOURS8[Random.Int(8)]; //random cell adjacent to the entrance.
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(PrisonLevel.class, "water_name");
			case Terrain.PEDESTAL:
				return "Bed";
			case Terrain.CURTAIN:
				return "Curtain";
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.PEDESTAL:
				return "This infirmary bed looks rather lackluster";
			case Terrain.CURTAIN:
				return "For some reason you can't even see silhouettes past these curtains";
			default:
				return super.tileDesc( tile );
		}
	}

    @Override
    public void script() {
	    if(getPhase() == 0)
        {
            if(threat != null && engageKakyoin(Dungeon.hero.pos) && (threat.HP == threat.HT) && threat.state == threat.LISTENING)
            {
                GameScene.show( new WndStory("Dear Jotaro Kujo, \n \nToday I will kill you with my stand! " +
                        "\n\n Sincerely, \nNoriaki Kakyoin"));

				threat.state = threat.HUNTING;
				threat.declareStand();
				threat.silentSummon(Dungeon.hero.pos + WIDTH * 2);

				threat.forceWarp(threat.stand.pos + WIDTH * 2);

                exit = -1;
                entrance = -1;
            }


            if(threat != null && (threat.HP <= threat.HT /2 ) )
            {
				entrance = WIDTH * 2 + 3;
				exit  = WIDTH + 3;

				map = BUSTED_WALL.clone();

				buildFlagMaps();
				cleanWalls();
				addVisuals();

				GameScene.resetMap();

				exit = 173;

				threat.sprite.die();
				threat.state = threat.LISTENING;
				threat.alignment = threat.alignment.ALLY;
				threat.stand.alignment = threat.alignment.ALLY;
				threat.killStand();
				threat.forceWarp(exit - WIDTH);
				focusCamera(threat);
				threat.exposite("Such a powerful stand...");

				threat.destroy();
				threat = null;
            }
        }

    }

    @Override
	public Group addVisuals() {
		super.addVisuals();
		PrisonLevel.addPrisonVisuals(this, visuals);
		return visuals;
	}

	private static final int W = Terrain.WALL;
    private static final int B = Terrain.BARRIER;
	private static final int P = Terrain.PEDESTAL;
	private static final int D = Terrain.DOOR;
	private static final int e = Terrain.EMPTY;
	private static final int V = Terrain.VOIDSPACE;//VOIDSPACE

    private static final int C = Terrain.CURTAIN;

    //Event Trigger
    private static final int T = Terrain.EMPTY;

	private static final int E = Terrain.ENTRANCE;

	//Unnecessary in this case the door behaves like the level exit
	//private static final int X = Terrain.EXIT;


	private static final int[] INFIRMARY =
			{
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,
					V, W, W, D, W, W, W, W, W, W, W, W, W, V,
					V, W, e, E, e, e, e, e, e, e, e, e, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, T, T, T, T, T, C, C, W, V,
					V, W, P, P, e, e, e, e, e, e, P, P, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, e, e, e, e, e, e, C, W, V,
					V, W, P, P, e, e, e, e, e, e, P, P, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, e, e, C, C, C, C, C, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, W, W, W, W, W, W, W, W, W, W, W, V,
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,

			};


	private static final int[] BUSTED_WALL =
			{
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,
					V, W, W, D, W, W, W, W, W, W, W, W, W, V,
					V, W, e, E, e, e, e, e, e, e, e, e, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, T, T, T, T, T, C, C, W, V,
					V, W, P, P, e, e, e, e, e, e, P, P, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, e, e, e, e, e, e, C, W, V,
					V, W, P, P, e, e, e, e, e, e, P, P, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, C, C, C, e, e, C, C, C, C, C, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, W, W, W, e, e, W, W, W, W, W, W, V,
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,

			};

	public static class infirmaryVisual extends CustomTiledVisual {

		private static short[] render = new short[]{

				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		};


		public infirmaryVisual(){
			super(Assets.SDC_SCHOOL_INFIRMARY);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 14;
			tileH = 14;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}

	public static class infirmaryVisualWalls extends CustomTiledVisual {
		private static short[] render = new short[]{

				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, //Beds are here
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, //and here
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1,
				1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,

		};

		public infirmaryVisualWalls() {
			super(Assets.SDC_SCHOOL_INFIRMARY);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 14;
			tileH = 14;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}
}
