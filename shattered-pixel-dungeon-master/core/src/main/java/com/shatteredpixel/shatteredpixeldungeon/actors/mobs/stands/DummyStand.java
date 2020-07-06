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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DummyStand extends Stand {

    public DummyStand(Char standMaster){
        this.standUser = standMaster;
        this.alignment = standUser.alignment;
        HP = standUser.HP;
        HT = standUser.HT;
    }

    @Override
    public void notice()
    {
        abilityOne();
    }

    @Override
    public int damageRoll() {
        return (int) (standUser.damageRoll()  * powerB);
    }

    @Override
    public int attackSkill( Char target ) {
        return (int) (standUser.damageRoll()  * powerB);
    }

    @Override
    public int drRoll() {
        return (int) (standUser.drRoll() * powerB);
    }

    @Override
    public void damage(int dmg, Object src)
    {
        super.damage(dmg, src);

        standUser.sprite.showStatus(CharSprite.WARNING,String.valueOf(dmg),this);
        standUser.HP = this.HP; }

    @Override
    public void die( Object src ) {
        destroy();
        sprite.die();
        standUser.die(src);
    }

    @Override
    protected boolean canAttack( Char enemy ) {

            return super.canAttack(enemy);
        }

    protected boolean doAttack( Char enemy ) {

        return super.doAttack(enemy);
    }


    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        //will never attack something outside of the stand range
        if (enemy != null &&  Dungeon.level.distance(enemy.pos, standUser.pos) <= rangeC){
            return enemy;
        } else {
            return null;
        }
    }

    protected class Hunting extends Mob.Hunting {

        public static final String TAG	= "HUNTING";

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

                return doAttack( enemy );

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    state = WANDERING;
                    target = Dungeon.level.randomDestination();
                    return true;
                }

                int oldPos = pos;
                if (target != -1 && getCloser( target )) {

                    spend( 1 / speed() );
                    return moveSprite( oldPos,  pos );

                } else {
                    spend( TICK );
                    if (!enemyInFOV) {
                        sprite.showLost();
                        state = WANDERING;
                        target = Dungeon.level.randomDestination();
                    }
                    return true;
                }
            }
        }
    }


}