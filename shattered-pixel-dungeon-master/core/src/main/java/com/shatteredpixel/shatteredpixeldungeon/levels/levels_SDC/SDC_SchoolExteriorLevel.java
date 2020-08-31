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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.AlterableProjectile;
import com.shatteredpixel.shatteredpixeldungeon.levels.Director;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SDC_SchoolExteriorLevel extends MovieLevel {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}

	private int[] eventTrigger = {192, 228, 264,300 };

	public int WIDTH = 36;
	public int HEIGHT = 23;

	Director mangaka;

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
		directorChair = 92;

		setSize(WIDTH, HEIGHT);
		
		map = EXTERIOR.clone();

		buildFlagMaps();
		cleanWalls();

		CustomTiledVisual vis = new schoolVisual();
		vis.pos(0, 0);
		customTiles.add(vis);
		//((GameScene)ShatteredPixelDungeon.scene()).addCustomTile(vis);

		vis = new schoolVisualWalls();
		vis.pos(0, 0);
		customWalls.add(vis);
		//((GameScene)ShatteredPixelDungeon.scene()).addCustomWall(vis);

		//entrance = 5+2*32;
		entrance = 73;
		exit  = 676;// WIDTH * (HEIGHT-3) - 9;

		return true;
	}
	
	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
		mangaka = new Director();
		mangaka.pos = directorChair;
		mangaka.restartScript();
		mobs.add(mangaka);
	}

	@Override
	public void script() {
		if(phase == 0)
		{
			for(int i =0; i < eventTrigger.length; i++)
			{
				if(Dungeon.hero.pos == eventTrigger[i])
				{
					((MissileSprite)mangaka.sprite.parent.recycle( MissileSprite.class ))
							.reset( mangaka.pos, Dungeon.hero.pos, new AlterableProjectile(ItemSpriteSheet.EMERALD),null);

					Sample.INSTANCE.play( Assets.SND_ZAP );

					Cripple.prolong(Dungeon.hero, Cripple.class, 10f);
                    Buff.affect( Dungeon.hero, Bleeding.class).set( 3);

					mangaka.nextScene();
				}
			}
		}

		if(phase == 1)
		{
			//One of the girls will give you some medicine
		}

	}

	public Actor respawner() {
		return null;
	}

	@Override
	protected void createItems() {
	}


	@Override
	public void press( int cell, Char ch ) {

		super.press(cell, ch);

		if (ch == Dungeon.hero){
			//TODO: upon entering the doors, go to the infirmary
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
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(PrisonLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile );
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
		//PrisonLevel.addPrisonVisuals(this, visuals);
		return visuals;
	}

	private static final int W = Terrain.WALL;
    private static final int B = Terrain.BARRIER;
	private static final int D = Terrain.DOOR;
	private static final int e = Terrain.EMPTY;
	private static final int V = Terrain.VOIDSPACE;//VOIDSPACE

    //Event Trigger
    private static final int C = Terrain.EMPTY;

	private static final int E = Terrain.ENTRANCE;
	private static final int X = Terrain.EXIT;

	//TODO if I ever need to store more static maps I should externalize them instead of hard-coding
	//Especially as it means I won't be limited to legal identifiers

	//&& ((Room)new Room().set(2, 25, 8, 32)).inside(cellToPoint(cell))){

	private static final int[] EXTERIOR =
			{
					V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V,
					V, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, e, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, e, e, e, e, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, e, e, e, e, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, e, e, e, e, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, e, e, e, e, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, e, e, e, e, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, e, e, e, e, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, e, e, e, e, W, W, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, e, e, e, e, W, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, B, B, B, B, B, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, B, B, B, D, D, B, B, B, B, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, E, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, e, e, e, e, e, e, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, V,
					V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V,

			};


	public static class schoolVisual extends CustomTiledVisual {

		private static short[] render = new short[]{
				    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0,
				    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,

		};


		public schoolVisual(){
			super(Assets.SDC_SCHOOL);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 36;
			tileH = 23;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}

	public static class schoolVisualWalls extends CustomTiledVisual {
		private static short[] render = new short[]{
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
				    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,

		};

		public schoolVisualWalls() {
			super(Assets.SDC_SCHOOL);
		}

		@Override
		public CustomTiledVisual create() {
			tileW = 36;
			tileH = 23;
			mapSimpleImage(0, 0);
			return super.create();
		}

		@Override
		protected boolean needsRender(int pos) {
			return render[pos] != 0;
		}
	}
}
