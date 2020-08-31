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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Kenshiro;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MovieActor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Avdol;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc.Kakyoin;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugStick;
import com.shatteredpixel.shatteredpixeldungeon.levels.Director;
import com.shatteredpixel.shatteredpixeldungeon.levels.MovieLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotHeartSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.HolyKujoSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.JosephSDCsprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTiledVisual;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SDC_KujoResidenceDepart extends SDC_KujoResidence {

	private Kakyoin deuteragonist;

	private MovieActor fleshBud, fly;
	private Kenshiro secretBoss;

	@Override
	protected boolean build() {
		super.build();

		fleshBud = new MovieActor();
		fleshBud.pos = directorChair;
		fleshBud.spriteClass = RotHeartSprite.class;
		mobs.add(fleshBud);

		fly = new MovieActor();
		fly.pos = directorChair;
		fly.spriteClass = SwarmSprite.class;
		mobs.add(fleshBud);

		entrance = 368;
		exit  = -1;

		deuteragonist = new Kakyoin();
		deuteragonist.pos = tearoom;
		deuteragonist.HP = deuteragonist.HT/2;
		deuteragonist.state = deuteragonist.LISTENING;
		deuteragonist.alignment = deuteragonist.alignment.ALLY;
		mobs.add(deuteragonist);

		secretBoss = new Kenshiro();
		secretBoss.pos = 253;
		secretBoss.state = secretBoss.LISTENING;
		mobs.add(secretBoss);



		return true;
	}

	@Override
	public void press(int cell, Char ch) {
		press( cell, ch, ch == Dungeon.hero);
	}

	@Override
	public void script() {

        if(phase == 0 )
        {

            if(counter == 0)
            {
				Dungeon.hero.HP = Dungeon.hero.HT;

                GameScene.show( new WndMessage("Implanted on this boy's forehead is a _flesh bud_ made of cells from DIO, " +
						"I'm afraid he doesn't have much time left before it destroys his brain \n \n -Mohammed Avdol"));
            }

            if(counter == 1)
			{
				Dungeon.hero.spend(1);
				focusCamera(Dungeon.hero);
				Dungeon.hero.turnAndExposite("My stand is accurate enough to catch bullets",deuteragonist.pos);
				Dungeon.hero.exposite("I'll pull this flesh bud out myself!");

				Dungeon.hero.sprite.parent.add(new Chains(Dungeon.hero.sprite.center(),
						deuteragonist.sprite.center(), Effects.Type.FINGER, new Callback() {
					public void call() {
					}
				}));
			}

			if(counter == 2)
			{
				focusCamera(deuteragonist);
				Banner stando = new Banner(fleshBud.sprite );
				stando.show( 0xFFFFFF, 0.3f, 5f );
				GameScene.showImage(stando, 0xFFFFFF, 0.3f, 1f  );
				fleshBud = null;
			}

			if(counter == 3)
			{
				deuteragonist.exposite("Why did you save me");
			}

			if(counter == 4)
			{
				Dungeon.hero.exposite("...");
			}

			if(counter == 5)
			{
				Dungeon.hero.exposite("I guess I don't really know");
				Dungeon.hero.spend(5);
			}

			if(counter == 10)
            {
                Sample.INSTANCE.play( Assets.SND_SHATTER );
                focusCamera(mom);
                ((HolyKujoSprite) mom.sprite).collapse();
                mom.description = "She's out cold";

                mohammed.forceMove(159);
                oldman.forceMove(237);
                Dungeon.hero.forceMove(238);
                deuteragonist.forceMove(239);

                deuteragonist.HP = deuteragonist.HT;

                mangaka.nextScene();
            }




        }

		if(phase == 1)
		{
			if(counter == 1)
			{
				((HolyKujoSprite) mom.sprite).collapse();

				 mohammed.turnAndExposite("Ms. Holly", mom.pos);
				 oldman.turnAndExposite("Holly!!!", mom.pos);
				 Dungeon.hero.turnAndExposite("Mom!", mom.pos);
				 deuteragonist.turnAndExposite("Holy-San!", mom.pos);

				 Dungeon.hero.spend(5);
			}

			if(counter == 2)
			{
				focusCamera(oldman);
				oldman.exposite("I feared as much");
			}

			if(counter == 3)
			{
				focusCamera(mohammed);
				mohammed.exposite("We'll have to defeat DIO to undo the curse");
			}

			if(counter == 5)
			{
				focusCamera(oldman);
				oldman.exposite("My stand's spirit photography shows me DIO");

			}

			if(counter == 6)
			{
				Banner stando = new Banner(BannerSprites.getCharacterBanner( BannerSprites.Character.DIO_SDC, 5 ) );
				stando.show( 0xFFFFFF, 0.3f, 5f );
				GameScene.showImage(stando, 0xFFFFFF, 0.3f, 1f  );

				deuteragonist.sprite.showAlert();
				deuteragonist.exposite("That fly next to him! I saw them back in Egypt!");
				deuteragonist.sprite.hideAlert();

			}

			if(counter == 7)
			{
				mohammed.exposite("Then it's settled");

				mohammed.movieTarget = 323;
				deuteragonist.movieTarget = 324;
				oldman.movieTarget = 326;

				mangaka.nextPhase();
			}

		}

		if(phase == 2)
		{
			if(Dungeon.hero.pos == 325)
			{
				GameScene.show( new WndMessage("Thank you for playing the JoJo Pocket Dungeon demo!" +
						"\n As you can see things aren't exactly the most polished, namely combat and story pacing. " +
						"This demo can be seen as a sort of proof of concept for myself. The most time consuming aspects are " +
						"attempting to abridge the story written by Araki into cutscenes for a turn based engine. I imagine a lot of the game " +
						"wouldn't be intuitive to many non-JoJo fans and many of the adaptational choices aren't partuclarly clear to others besides myself. " +
						"\n Nonetheless, I wanted to get something out as I've been working on this project on and off for quite some time. Again thank you so much" +
						"\n \n -Solanum \nP.S Have you checked Jotaro's room?"));
			}
		}


		if(secretBoss.state == secretBoss.LISTENING && (Dungeon.hero.pos == 257 ||  Dungeon.hero.pos == 356))
		{
			secretBoss.state = secretBoss.HUNTING;
		}

		if(!secretBoss.isAlive())
		{
			Dungeon.win(DebugStick.class);
            Dungeon.deleteGame( GamesInProgress.curSlot, true );
            Game.switchScene( RankingsScene.class );
		}

	}



}
