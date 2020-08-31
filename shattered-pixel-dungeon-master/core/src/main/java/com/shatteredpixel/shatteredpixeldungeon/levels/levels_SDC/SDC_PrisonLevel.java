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

import android.os.Handler;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MovieActor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Avdol;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.levels_SDC.scripts.SDC_PrisonLevelDirector;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.CopSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.HolyKujoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.JosephSDCsprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SeniorSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.tweeners.CameraScrollTweener;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.Collection;

public class SDC_PrisonLevel extends MovieLevel {
	
	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	private static final int WIDTH = 23;
	private static final int HEIGHT = 23;

	private MovieActor cop1, cop2, mom, oldman;

	private Avdol mohammed;

	private SDC_PrisonLevelDirector mangaka;

    private enum State{
        DEFAULT,
        BF1,
        BF2,
		STORY,
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

	private static final String ACTORS = "actors";

	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( STATE, state);
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
		bundle.put( ACTORS, mobs);

		for(Mob mobs: Dungeon.level.mobs.toArray(new Mob[0]))
		{
			if(mobs instanceof MovieActor || mobs == mohammed)
			{
				mobs = null;
				Dungeon.level.mobs.remove(mobs);
			}
		}

		mohammed = null;

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
		directorChair = WIDTH * 20 - 12;

		setSize(WIDTH, HEIGHT);
		//state = State.DEFAULT;

		map = PrisonCell.clone();

		buildFlagMaps();
		cleanWalls();

		entrance =  3 * WIDTH - 9;
        exit = WIDTH * 10 - 2;

		return true;
	}

	@Override
	public Mob createMob() {
		return null;
	}
	
	@Override
	protected void createMobs() {
		//Cop 1

		//Cop 2

		//Holly

		//Joseph

		//bring along our director for this test
		//redirect();
		mangaka = new SDC_PrisonLevelDirector();
		mangaka.pos = directorChair;
		mangaka.restartScript();
		mobs.add(mangaka);

        cop1 = new MovieActor();
		cop2 = new MovieActor();
		cop1.name = "Cop";
		cop2.name = "Cop";
		cop1.description = "One of this jail's many officers";
		cop2.description = "One of this jail's many officers";

		cop1.spriteClass = CopSprite.class;
		cop2.spriteClass = CopSprite.class;
		int inFrontOfCell = exit-10;
		cop1.pos = inFrontOfCell -1;
		cop2.pos = inFrontOfCell +1;

		mom = new MovieActor();
		mom.name = "Holly Kujo";
		mom.description = "This cheerful woman is your mother";
		mom.spriteClass = HolyKujoSprite.class;
		mom.pos = inFrontOfCell;

		mobs.add(cop1);
        mobs.add(cop2);
        mobs.add(mom);

		mohammed = new Avdol();
		mohammed.pos = inFrontOfCell + WIDTH * 2;
		mohammed.alignment = Char.Alignment.NEUTRAL;
		mobs.add( mohammed );

		oldman = new MovieActor();
		oldman.name = "Joseph Joestar";
		oldman.description = "This grizzled old man happens to be your grandfather";
		oldman.spriteClass = JosephSDCsprite.class;
		oldman.pos = inFrontOfCell - WIDTH;
		mobs.add(oldman);


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
	}

	
	private boolean insideCell( int cell ) {

		if(Dungeon.hero.pos == WIDTH * 6 + 11)
		{
			return false;
		}

		return cell < 7 * WIDTH + (width / 2 );
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
                exit = 0;
                break;
            case BF2:
			case STORY:
                changeMap(NeoCell);
				entrance = 16 + 2 * WIDTH;
				exit = WIDTH * 10 - 2;
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

        if(state != State.DEFAULT || state != null || state != State.STORY) {
            exit = entrance = 0;

        }
        else
        {
            entrance =  3 * WIDTH - 9;
            exit = WIDTH * 10 - 2;
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

	@Override
	public boolean redirect() {

		if(mohammed == null && phase == 0 || phase == 1)
		{
			for(Mob mobs: Dungeon.level.mobs.toArray(new Mob[0])) {
				if (mobs instanceof Avdol) {
					mohammed = (Avdol) mobs;
					return true;
				}

			}
		}
		else if(mohammed != null && phase == 0)
		{
			return true;
		}

		return  false;
	}

	@Override
	public void script() {

		if(mangaka.phase == 0)
		{
			if (redirect()) {
				mohammed.spend(1);
				mohammed.exposite("...");
			}

			switch (mangaka.counter) {
				case 0:
					break;
				case 3:
					focusCamera(oldman);
					oldman.forceMove(oldman.pos - WIDTH);

					oldman.sprite.idle();

					//TODO: face off with Jotaro and Joseph
					oldman.turnAndExposite("We've come for you, Jotaro", Dungeon.hero.pos);
					oldman.exposite("Step out of the cell");

					break;

				default:
					break;
			}

			if(mangaka.counter > 3 &&(Dungeon.hero.pos == oldman.pos - WIDTH*2) && mangaka.counter < mangaka.END_OF_SCENE - 1)
			{
				focusCamera(Dungeon.hero);
				Dungeon.hero.exposite("Get lost, old man");

				Dungeon.hero.sprite.parent.add(new Chains(Dungeon.hero.sprite.center(),
						oldman.sprite.center(), Effects.Type.FINGER, new Callback() {
					public void call() {
					}
				}));

				//TODO: use avatar banners to show the face off

				mangaka.counter = mangaka.END_OF_SCENE - 1;
			}


			if(mangaka.counter == mangaka.END_OF_SCENE)
			{
				focusCamera(oldman);
				oldman.exposite("Fine, you'll just have to learn by fire!");
				oldman.turnAndExposite("Avdol, you're up", mohammed.pos);
				oldman.forceMove(oldman.pos+1);
				mohammed.forceMove(mohammed.pos-1);
				mangaka.nextScene();
			}

		}

		if(mangaka.phase == 1)
		{
			redirect();

			if(mangaka.counter == 0 )
			{
				mohammed.forceMove(mohammed.pos - WIDTH);
				mohammed.state =  mohammed.LISTENING;


				oldman.movieTarget = WIDTH * 12 + 10;
			}

			if(mangaka.counter == 1)
			{
				mohammed.forceMove(mohammed.pos - WIDTH - 1);
				oldman.movieTarget = WIDTH * 12 + 10;
			}

			if(mangaka.counter == 2 || mangaka.counter == 3)
			{
				mohammed.forceMove(mohammed.pos - WIDTH + 1);
				oldman.movieTarget = WIDTH * 12 + 10;
			}

			if(mangaka.counter == 5);
			{

				if(mangaka.counter == 5)
				{
					focusCamera(mohammed);
					mohammed.turnAndExposite("The 'Evil Spirit' I control is called...", Dungeon.hero.pos);
					mangaka.nextScene();
					Dungeon.hero.spend(2);
				}


			}


		}

		if(mangaka.phase == 2)
		{
			mohammed.alignment = mohammed.alignment.ENEMY;
			mohammed.state = mohammed.LISTENING;

			if(mohammed.stand == null && mangaka.counter == 0)
			{

				mohammed.declareStand();
				mohammed.alignment = mohammed.alignment.ENEMY;
				mohammed.stand.setAlignment(mohammed.alignment);

				mohammed.yellStand();
				mohammed.updateRange(mohammed.stand.range);
				if(Actor.findChar(mohammed.pos - (WIDTH * 2) - 1) == null)
				{
					mohammed.silentSummon(mohammed.pos - (WIDTH * 2) - 1);
				}
				else
				{
					mohammed.silentSummon(mohammed.pos - (WIDTH * 2) + 1);
				}

			}

			mohammed.spend(1);

			//TODO: if the player can significantly damage Avdol, Joseph will intervene
			if(mohammed.HP <= (mohammed.HT /10))
			{
				oldman.turnAndExposite("That's enough, Jotaro!", Dungeon.hero.pos);
				mangaka.nextScene();
			}

			//now the fight has begun

			if(!insideCell(Dungeon.hero.pos) )
			{
				mangaka.nextScene();
			}

			if(Dungeon.stand != null)
			{
				state = State.STORY;
				//map[WIDTH * 6 + 11] = E;
				transition();
				GameScene.resetMap();
				//cleanWalls();
			}

		}

		if(mangaka.phase == 3)
		{
			if(mangaka.counter == 0)
			{
				mohammed.alignment = mohammed.alignment.NEUTRAL;
				mohammed.state = mohammed.LISTENING;

				mohammed.stand.forceWarp(mohammed.pos -1);
				mohammed.killStand();
				mohammed.stand = null;

				if(Dungeon.stand!= null)
                {
                    Dungeon.stand.enemy = null;
                }

				mohammed.spend(1);
				mohammed.turnAndExposite("You are now out of the cell", Dungeon.hero.pos);
				mohammed.movieTarget = exit;
			}



			if(mangaka.counter == 3)
			{
				mohammed.alignment = Char.Alignment.ALLY;
				mohammed.state = mohammed.LISTENING;

				focusCamera(oldman);
				oldman.turnAndExposite("Come, we'll explain at home", Dungeon.hero.pos);

				mohammed.movieTarget = exit;

			}

			if(mangaka.counter >=4)
			{

				for(Mob movieChars : mobs.toArray(new Mob[0]))
				{
					if((Actor.findChar(exit) instanceof MovieActor ||
							Actor.findChar(exit) == mohammed )&& Actor.findChar(exit) != null)
					{
						GLog.i(Actor.findChar(exit).name + " left the room");
						mobs.remove(Actor.findChar(exit));
						Actor.findChar(exit).sprite.fakeAttack();

						Actor.findChar(exit).sprite.move(exit, exit +1);
						Actor.remove(Actor.findChar(exit));
						nullifyCharacter(Actor.findChar(exit));
					}

					if(movieChars instanceof MovieActor)
					{
						movieChars.movieTarget = exit;
					}
				}
			}

		}
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
            W, I, I, I, I, W, W, W, I, I, I, L, I, I, I, W, W, W, I, I, I, I, W,
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
            W, W, W, W, W, W, W, W, W, W, W, E, W, W, W, W, W, W, W, W, W, W, W,
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
			W, E, E, E, E, W, W, W, B, B, E, E, E, P, P, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, E, E, E, E, W, W, W, E, E, E, E, E, E, E, W, W, W, E, E, E, E, W,
			W, I, I, I, I, W, W, W, I, I, I, E, I, I, I, W, W, W, I, I, I, I, W,
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
			W, W, W, W, W, W, W, W, W, W, W, E, W, W, W, W, W, W, W, W, W, W, W,
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