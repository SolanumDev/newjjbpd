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
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DIO;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.DIOShadow;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.PetShop;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Director;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.WelcomeScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SDC_MansionCourtyard extends MovieLevel {


	public int WIDTH = 14;
	public int HEIGHT = 18;

	private DIOShadow shadowDIO;
	private PetShop bird;

	private Director telence;

	private int currentTime = -2;

	private ArrayList<Item> storedItems = new ArrayList<>();
	
	@Override
	public String tilesTex() {
		return Assets.TILES_TOWN;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_BLOOD;
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

		map = COURTYARD.clone();

		buildFlagMaps();
		cleanWalls();

		CustomTiledVisual vis = new courtyardVisual();
		vis.pos(0, 0);
		customTiles.add(vis);

		vis = new courtyardVisualWalls();
		vis.pos(0, 0);
		customWalls.add(vis);

		addVisuals();


		entrance = WIDTH * HEIGHT - (WIDTH) - 7;
		exit  = -1;

		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
		telence = new Director();
		telence.pos = WIDTH * 5 + (WIDTH/2);
		mobs.add(telence);

		shadowDIO = new DIOShadow();
		shadowDIO.pos = WIDTH * 7 + (WIDTH/2);
		mobs.add(shadowDIO);

		bird = new PetShop();
		bird.pos = shadowDIO.pos -1;
		bird.alignment = bird.alignment.NEUTRAL;
		bird.state = bird.LISTENING;
		mobs.add(bird);

	}
	
	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}

	@Override
	public void script() {

		if(telence.phase == 0)
		{
			if(shadowDIO.stand != null)
			{
				Banner stando = new Banner(BannerSprites.getCharacterBanner( BannerSprites.Character.DIO_SDC, 0 ) );
				stando.show( 0xFFFFFF, 0.3f, 5f );
				GameScene.showImage(stando, 0xFFFFFF, 0.3f, 1f  );
			}

			telence.nextScene();
		}

		if(shadowDIO.HP <= shadowDIO.HT * 0.8 )
		{
			if(Actor.findChar(WIDTH * 7 + (WIDTH/2)) == null)
			{
			    if(shadowDIO.stand!= null)
                {
                    shadowDIO.killStand();
                }

				ScrollOfTeleportation.appear(shadowDIO,WIDTH * 7 + (WIDTH/2));
			}
			else
			{
				ScrollOfTeleportation.appear(shadowDIO, entrance);
			}

			focusCamera(shadowDIO);
			//shadowDIO.turnAndExposite(Messages.get(DIOShadow.class, "enough", null), Dungeon.hero.pos);
			shadowDIO.yell("Enough! I've seen enough!");

			Dungeon.hero.spend(3);
			ArrayList<Integer> spawnPoints = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					spawnPoints.add(p);
				}
			}

			if (spawnPoints.size() > 0) {

				shadowDIO.pos = Random.element(spawnPoints);

				ScrollOfTeleportation.appear(shadowDIO, shadowDIO.pos);

			}
			//shadowDIO.turnAndExposite(Messages.get(DIOShadow.class, "charisma", null), Dungeon.hero.pos);
			shadowDIO.yell("You'll make an excellent addition " + Dungeon.hero.givenName() + "!");

			Banner stando = new Banner(BannerSprites.getCharacterBanner( BannerSprites.Character.DIO_SDC, 4 ) );
			stando.show( 0xFFFFFF, 0.3f, 10f );
			GameScene.showImage(stando, 0xFFFFFF, 0.3f, 10f  );

			currentTime = telence.counter;
		}

		if(telence.counter == currentTime+1)
		{
			Dungeon.deleteGame( GamesInProgress.curSlot, true );
			Game.switchScene( TitleScene.class );
		}
	}

	@Override
	public int randomRespawnCell() {
		return 5+2*32 + PathFinder.NEIGHBOURS8[Random.Int(8)]; //random cell adjacent to the entrance.
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.BARRIER:
				return "Water Fountain";
			default:
				return "Manor Courtyard";
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.BARRIER:
				return "Suspiciously colored water flows from this fountain";
			default:
				return "Imposing doors block the way";
		}
	}


	private void changeMap(int[] map){
		this.map = map.clone();
		buildFlagMaps();
		cleanWalls();

		exit = entrance = 0;
		for (int i = 0; i < length(); i ++)
			if (map[i] == Terrain.ENTRANCE)
				entrance = i;
			else if (map[i] == Terrain.EXIT)
				exit = i;

		BArray.setFalse(visited);
		BArray.setFalse(mapped);
		
		for (Blob blob: blobs.values()){
			blob.fullyClear();
		}
		addVisuals(); //this also resets existing visuals

		GameScene.resetMap();
		Dungeon.observe();
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


	private static final int[] COURTYARD =
			{
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,
					V, W, W, W, W, e, e, e, e, W, W, W, W, V,
					V, W, e, e, W, e, e, e, e, W, e, e, W, V,
					V, W, e, e, e, e, e, e, e, e, e, e, W, V,
					V, W, e, e, W, T, T, T, T, W, e, e, W, V,
					V, W, e, e, W, e, e, e, e, W, e, e, W, V,
					V, V, W, W, W, W, W, W, W, W, W, W, V, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, B, B, e, e, e, W, e, V,
					V, e, W, e, e, e, B, B, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, e, e, e, e, e, e, e, e, W, e, V,
					V, e, W, W, W, W, W, e, W, W, W, W, e, V,
					V, V, V, V, V, V, V, V, V, V, V, V, V, V,
			};


	public static class courtyardVisual extends CustomTiledVisual {

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
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		};


		public courtyardVisual(){
			super(Assets.SDC_COURTYARD);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 14;
			tileH = 18;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}

	public static class courtyardVisualWalls extends CustomTiledVisual {
		private static short[] render = new short[]{

				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1,
				1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1,
				1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1,

		};

		public courtyardVisualWalls() {
			super(Assets.SDC_COURTYARD);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 14;
			tileH = 18;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}
}
