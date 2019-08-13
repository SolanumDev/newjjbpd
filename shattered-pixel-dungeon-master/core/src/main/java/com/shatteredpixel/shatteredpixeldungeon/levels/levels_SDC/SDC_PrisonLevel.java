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
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SDC_PrisonLevel extends Level {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}


	private static final int WIDTH = 23;
	private static final int HEIGHT = 23;



    private enum State{
        DEFAULT,
        BF1,
        BF2,
        SECBOSS
    }

    private State state;

	private int arenaDoor;
	private boolean enteredArena = false;
	private boolean keyDropped = false;

	@Override
	public String tilesTex() {
		return Assets.TILES_JAIL;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_CITY;
	}

    private static final String STATE	= "state";
	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STATE, state);
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
        state = bundle.getEnum( STATE, State.class );
	}
	
	@Override
	protected boolean build() {
		setSize(WIDTH, HEIGHT);
		//state = State.DEFAULT;

		map = PrisonCell.clone();

		buildFlagMaps();
		cleanWalls();

		entrance =  3 * WIDTH - 9;
        exit = WIDTH * 10 - 2;
        //map[Terrain.TRAP] = 2 + WIDTH * 10;
		//map[exit] = WIDTH * 10 - 1;

		//= WIDTH * 11 - 1;
		return true;
	}

	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		Item item = Bones.get();
		if (item != null) {
			drop( item, Dungeon.hero.pos ).type = Heap.Type.REMAINS;
		}
	}

	@Override
	public int randomRespawnCell() {
		int cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		while (!passable[cell]){
			cell = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)];
		}
		return cell;
	}

	@Override
	public void press( int cell, Char hero ) {
		super.press( cell, hero );

		if(state == null){
			state = State.DEFAULT;
		}

		switch(state)
		{
			case DEFAULT:
				if(Dungeon.hero.pos == 1 + WIDTH * 9)
				{
					state = State.BF1;
					//TODO: workout the logic for transitioning between prison floors
                    Dungeon.hero.pos =  WIDTH * 9 + 2;
					transition();
				}
				break;
			case BF1:
				if(Dungeon.hero.pos == 1 + WIDTH * 9)
				{
					state = State.DEFAULT;
					//TODO: workout the logic for transitioning between prison floors
					transition();
				}
				break;

			default:
				break;
		}
		if(state == State.DEFAULT || state == null )
        {

            if(hero == Dungeon.hero && Dungeon.hero.pos == 1 + WIDTH * 9)
            {
                state = State.BF1;
                //TODO: workout the logic for transitioning between prison floors
                transition();
            }

        }

        if(state == State.BF1)
		{
			if(hero == Dungeon.hero && Dungeon.hero.pos == 1 + WIDTH * 9)
			{
				state = State.DEFAULT;
				transition();
			}

		}
/*
        if(state == State.BF1)
        {
			if(hero == Dungeon.hero && Dungeon.hero.pos == 3 + WIDTH * 9)
            {
                state = State.DEFAULT;
                transition();
            }
            if(hero == Dungeon.hero && Dungeon.hero.pos == WIDTH * 10 - 2)
            {
                state = State.BF2;
                transition();
            }

        }

        if(state == State.BF2)
        {
			if(hero == Dungeon.hero && Dungeon.hero.pos == 3 + WIDTH * 9)
			{
				state = State.SECBOSS;
				transition();
			}
			if(hero == Dungeon.hero && Dungeon.hero.pos == WIDTH * 10 - 2)
            {
                state = State.BF1;
                transition();
            }


        }

        if(state == State.SECBOSS)
        {
			if(hero == Dungeon.hero && Dungeon.hero.pos == 3 + WIDTH * 9)
            {
                state = State.BF2;
                transition();
            }
        }

*/
	}

	
	private boolean insideCell( int cell ) {
		return cell / width() < arenaDoor / width();
	}

	public void transition()
    {
		Dungeon.hero.interrupt();
        switch(state)
        {

            case DEFAULT:
                changeMap(PrisonCell);
                entrance = 16 + 2 * WIDTH;
                exit = WIDTH * 10 - 2;
                break;
            case BF1:
                changeMap(StandardCell);
                break;
            case BF2:
                changeMap(NeoCell);
                break;
            case SECBOSS:
                changeMap(SecretBossCell);
             default:
                break;
        }

    }


	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				//return Messages.get(CityLevel.class, "water_name");
				return  "It's a bloody stream";
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
            case Terrain.IRON_BARS:
                return "Iron Bars";
            case Terrain.IRON_BARS_LOCKED:
                return "Locked Iron Bars";


		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.ENTRANCE:
				//return Messages.get(CityLevel.class, "entrance_desc");
                if(state == State.DEFAULT || state == null)
                {
                    return "Stairs leading to the police station";
                }
                else
                {
                    return "Stairs leading to the higher levels of the prison";
                }
            case Terrain.IRON_BARS:
            case Terrain.IRON_BARS_LOCKED:
                return "Imposing steel bars block your path";
			case Terrain.EXIT:
				//return Messages.get(CityLevel.class, "exit_desc");
                return "Stairs leading to the lower levels of the prison";
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				//return Messages.get(CityLevel.class, "deco_desc");
				return "Yare Yare Daze";
			case Terrain.EMPTY_SP:
				//return Messages.get(CityLevel.class, "sp_desc");
				return "The flooring is awfully regal for a prison";
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				//return Messages.get(CityLevel.class, "statue_desc");
				return "Your 'evil spirit' must have brought this";
			case Terrain.PEDESTAL:
				return "This is a placeholder for the prison cots";
			case Terrain.BOOKSHELF:
				//return Messages.get(CityLevel.class, "bookshelf_desc");
				return "It's full of quality manga and light novels, like Sword Art Online";
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public Group addVisuals( ) {
		super.addVisuals();
		CityLevel.addCityVisuals(this, visuals);
		return visuals;
	}

    private void changeMap(int[] map){
        this.map = map.clone();
        buildFlagMaps();
        cleanWalls();

        if(state != State.DEFAULT || state != null) {
            exit = entrance = 0;
            /*
            for (int i = 0; i < length(); i++)
                if (map[i] == Terrain.ENTRANCE)
                    entrance = i;
                else if (map[i] == Terrain.EXIT)
                    exit = i;
                    */
        }
        BArray.setFalse(visited);
        BArray.setFalse(mapped);

        for (Blob blob: blobs.values()){
            blob.fullyClear();
        }
        addVisuals(); //this also resets existing visuals

        GameScene.resetMap();
        Dungeon.observe();
    }

	private static final int W = Terrain.WALL;
	private static final int D = Terrain.DOOR;
	private static final int T = Terrain.INACTIVE_TRAP;
	private static final int N = Terrain.ENTRANCE;
	private static final int E = Terrain.EMPTY;
	private static final int M = Terrain.EMPTY_SP;
	private static final int B = Terrain.BOOKSHELF;
	private static final int X = Terrain.EXIT;
    private static final int P = Terrain.PEDESTAL;
    private static final int I = Terrain.IRON_BARS;
    private static final int L = Terrain.IRON_BARS_LOCKED;

    private static final int[] PrisonCell = {
    		W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, B, B, E, E, E, P, P, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, X, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, N, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
	};

    private static final int[] StandardCell = {
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, M, E, E, E, E, E, E, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, M, E, E, E, E, E, E, E, E, E, E, E, E, M, M, M, M, M, M, M, M, W,
			W, N, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, M, X, W,
			W, M, E, E, E, E, E, E, E, E, E, E, E, E, M, M, M, M, M, M, M, M, W,
			W, M, E, E, E, E, E, E, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
    };

	private static final int[] NeoCell = {
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, E, I, I, I, W, W, W, I, I, I, I, W,
			W, M, M, M, M, W, W, W, M, M, M, M, M, M, M, W, W, W, M, M, M, M, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, N, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, X, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
	};

    private static final int[] SecretBossCell = {
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, E, E, E, E, W, W, W, B, B, B, B, B, B, B, W, W, W, E, E, E, E, W,
            W, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, W,
            W, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, I, I, I, I, W, W, W, I, I, I, L, I, I, I, W, W, W, I, I, I, I, W,
            W, M, M, M, M, W, W, W, M, M, M, M, M, M, M, W, W, W, M, M, M, M, W,
            W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, N, W,
            W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
    };

}
/*
Jotaro
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, B, B, E, E, E, P, P, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, M, M, M, M, W, W, W, M, M, M, M, M, M, M, W, W, W, M, M, M, M, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, N, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, X, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
Others
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, B, B, E, E, E, P, P, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, M, M, M, M, W, W, W, M, M, M, M, M, M, M, W, W, W, M, M, M, M, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, N, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, X, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,

			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
            W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,
			W, E, E, E, E, W, W, W, B, B, E, E, E, P, P, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
			W, N, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, X, W,
			W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, M, W,
            W, I, I, I, I, W, W, W, I, I, I, D, I, I, I, W, W, W, I, I, I, I, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
            W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W, W,




 */