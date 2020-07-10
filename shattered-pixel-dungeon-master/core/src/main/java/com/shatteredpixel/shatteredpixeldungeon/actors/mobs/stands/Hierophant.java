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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.HierophantSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;


public class Hierophant extends Stand implements Callback {

    int TIME_TO_ZAP = 1;

    {
        spriteClass = HierophantSprite.class;

        power = powerC;
        speed = speedB;
        range = rangeA;
        def = defB;

        primaryColor = 0x5C8844;

    }


    public Hierophant(Char standMaster) {
        this.standUser = standMaster;
        this.alignment = standUser.alignment;
        HP = standUser.HP;
        HT = standUser.HT;
    }

	@Override
    protected boolean canAttack( Char enemy ) {

        return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        //return Dungeon.level.adjacent(pos, enemy.pos);
    }

    public void onZapComplete()
    {
        zap();
        next();
    }

    private void zap() {
        spend( TIME_TO_ZAP );

        if (hit( this, enemy, true )) {

            int dmg = Random.Int( standUser.damageRoll() );
            enemy.damage( dmg, this );

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "splash") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    protected boolean doAttack( Char enemy ) {

        if (Dungeon.level.adjacent(pos, enemy.pos)) {

            return super.doAttack(enemy);

        } else {

            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            } else {
                zap();
            }

            return !visible;
        }
    }

    @Override
    public void call() {
        next();
    }

/*
    @Override
    public void spend(float time)
    {
     super.spend(time);
    }
*/

}
