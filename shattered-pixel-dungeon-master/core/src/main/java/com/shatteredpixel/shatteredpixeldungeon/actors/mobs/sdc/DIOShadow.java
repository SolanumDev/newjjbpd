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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.sdc;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StandUser;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.Stand;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands.TheWorldShadow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SDCsprites.DIOShadowSprite;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DIOShadow extends StandUser {

	{
		spriteClass = DIOShadowSprite.class;

		HP = HT = 100;
		SP = ST = 50;
		defenseSkill = 0;

		state = WANDERING;

	}

	@Override
	public int defenseProc( Char enemy, int damage ) {
/*
		//if we get hit by an enemy we'll warp away up to two tiles
		if(SP - 10 > 0)
		{
			ArrayList<Integer> warp = new ArrayList<>();
			ArrayList<Integer> trueWarp = new ArrayList<>();

			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
					warp.add( p );
				}
			}
		}*/

		enemy.sprite.showAlert();
		if(enemy instanceof Stand)
		{
			((Stand) enemy).standUser.sprite.showAlert();
		}

		return super.defenseProc(enemy, damage);
	}

	@Override
	protected boolean act() {
		//1/6 chance to heal 2 health per turn
		if(Random.NormalIntRange( 1, 6 ) == 5)
		{
			HP = Math.min( HT, HP+2 );
		}

		return super.act();
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);

		if(standIsActive()) {
			stand.HP = this.HP;
		}

	}

	@Override
	public void notice() {

		//what is it that you desire?
		if(enemy == Dungeon.hero) {
			Sample.INSTANCE.play(Assets.SND_DIO_DESIRE);
			yell(Messages.get(this, "notice", Dungeon.hero.givenName()));

			Camera.main.target = sprite;

			Music.INSTANCE.mute();
			Music.INSTANCE.play( Assets.THEME, true );
		}

	}

	@Override
	public void declareStand() {
		stand = new TheWorldShadow();
		stand.setStandUser(this);
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1, 4 );

	}

	@Override
    public void yellStand() {

    }

        @Override
	public int attackSkill( Char target ) {
		return 8;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 1);
	}

}
