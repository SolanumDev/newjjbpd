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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeFreeze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeStop;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Finger;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HumanSprite;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class AAGenericStand extends Stand {


	{
		spriteClass = HumanSprite.class;

		if(standUser != null){
            HP = standUser.HP;
            HT = standUser.HT;
        }
        else
        {
            HP = HT = 50;
        }
		defenseSkill = 10;

		state = WANDERING;

		EXP = 0;
		maxLvl = 5;
	}

	public AAGenericStand(Char standMaster){
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
        if (enemy != null &&  Dungeon.level.distance(enemy.pos, standUser.pos) <= rangeC){
            return enemy;
        } else {
            return null;
        }
    }

	@Override
	public int attackSkill( Char target ) {
		return standUser.attackSkill(target) * (int) powerA;
	}

	@Override
    protected boolean canAttack( Char enemy ) {

        return Dungeon.level.adjacent(pos, enemy.pos);
    }

	@Override
	public int drRoll() {
		return (int) (standUser.drRoll() * powerA);
	}

	@Override
    public void notice()
    {
        //TODO: find a way to hide the (!) icon
    }

    protected boolean doAttack( Char enemy ) {

        boolean visible = Dungeon.level.heroFOV[pos];

        return !visible;
    }

    @Override
    public void spend(float time)
    {
     super.spend(time);
    }


    @Override
    public void die( Object src ) {
        destroy();
        sprite.die();
        standUser.die(src);
    }

    @Override
    public void damage(int dmg, Object src)
    {
        super.damage(dmg, src);

        standUser.sprite.showStatus(CharSprite.WARNING,String.valueOf(dmg),this);
        standUser.HP = this.HP;
    }

    @Override
    public void standPosition(Char standUser)
    {


        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = standUser.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add(p);
            }
        }

        if (spawnPoints.size() > 0) {

            this.pos = Random.element(spawnPoints);

            GameScene.add(this);
            Actor.addDelayed(new Pushing(this, standUser.pos, this.pos), -1);
        }

    }

    public boolean isAlive() {
        return HP > 0 && standUser.isAlive();
    }





}
