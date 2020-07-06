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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.HierophantSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Hierophant extends Stand {

    int TIME_TO_ZAP = 1;

    {
		spriteClass = HierophantSprite.class;

        power = powerC;
        speed = speedB;
        range = rangeA;
        def = defB;

		state = WANDERING;

	}


	public Hierophant(Char standMaster){
        this.standUser = standMaster;
		this.alignment = standUser.alignment;
		HP = standUser.HP;
		HT = standUser.HT;
	}

    public void updateCell( Integer cell)
    {
        worldCell = cell;
    }

	@Override
    public void abilityOne()
    {

    }

    @Override
    public void abilityTwo()
    {

    }


    @Override
    public void abilityThree()
    {
    }

    @Override
    public void cancelAbility()
    {

    }

    public void onAttackComplete()
    {
        next();
    }

	@Override
    public int defenseProc( Char enemy, int damage ) {

	    if(enemy == standUser)
        {
            interact();
            return 0;
        }

	    return super.defenseProc(enemy, damage);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 1, 4 );
    }

    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        //will never attack something outside of the stand range
        if (enemy != null &&  Dungeon.level.distance(enemy.pos, standUser.pos) <= rangeA){
            return enemy;
        } else {
            return null;
        }
    }

	@Override
	public int attackSkill( Char target ) {
		return standUser.attackSkill(target) * (int) power;
	}

	@Override
    protected boolean canAttack( Char enemy ) {

        return Dungeon.level.adjacent(pos, enemy.pos);
    }

	@Override
	public int drRoll() {
		return (int) (standUser.drRoll() * power);
	}

	@Override
    public void notice()
    {
        //TODO: find a way to hide the (!) icon
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

        boolean visible = Dungeon.level.heroFOV[pos];

        if(visible)
        {
            sprite.zap( enemy.pos );
        }
        else
        {
            zap();
        }

        return !visible;
    }
/*
    @Override
    public void spend(float time)
    {
     super.spend(time);
    }
*/

    @Override
    public void damage(int dmg, Object src)
    {
        super.damage(dmg, src);

        standUser.sprite.showStatus(CharSprite.WARNING,String.valueOf(dmg),this);
        standUser.HP = this.HP;
    }


}
