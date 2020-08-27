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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.AlterableProjectile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.HierophantSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;


public class Hierophant extends Stand implements Callback {

    int TIME_TO_ZAP = 1;

    //the actual affected cells
    private HashSet<Integer> affectedCells = null;
    //the cells to trace fire shots to, for visual effects.
    private HashSet<Integer> visualCells = null;
    private int direction = 0;

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

    public Hierophant() {
    }

    public void notice() {
        worldCell = enemy.pos;
        abilityOne();
    }

    //the code for abilityOne()/emeraldSplash() is taken directly from Evan-00's WandofFireblast,
    //it is referenced in other cases where a class needs to fire some sort of cone shaped projectile
    @Override
    public void abilityOne() {
        emeraldSplash();
    }

    private void diffuse(int cell, float strength){
        if (strength >= 0 && (Dungeon.level.passable[cell] || Dungeon.level.flamable[cell])){
            affectedCells.add(cell);
            if (strength >= 1.5f) {
                visualCells.remove(cell);
                diffuse(cell + PathFinder.CIRCLE8[left(direction)], strength - 1.5f);
                diffuse(cell + PathFinder.CIRCLE8[direction], strength - 1.5f);
                diffuse(cell + PathFinder.CIRCLE8[right(direction)], strength - 1.5f);
            } else {
                visualCells.add(cell);
            }
        } else if (!Dungeon.level.passable[cell])
            visualCells.add(cell);
    }

    private int left(int direction){
        return direction == 0 ? 7 : direction-1;
    }

    private int right(int direction){
        return direction == 7 ? 0 : direction+1;
    }

    private void emeraldSplash()
    {
        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        Ballistica splash = new Ballistica(pos, worldCell, Ballistica.MAGIC_BOLT);

        int maxDist = (int)(standUser.SP / 10);
        if(maxDist < 3)
        {
            maxDist = 3;
        }
        int dist = Math.min(splash.dist, maxDist);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
            if (splash.sourcePos+PathFinder.CIRCLE8[i] == splash.path.get(1)){
                direction = i;
                break;
            }
        }

        float strength = maxDist;
        for (int c : splash.subPath(1, dist)) {
            strength--; //as we start at dist 1, not 0.
            affectedCells.add(c);
            if (strength > 1) {
                diffuse(c + PathFinder.CIRCLE8[left(direction)], strength - 1);
                diffuse(c + PathFinder.CIRCLE8[direction], strength - 1);
                diffuse(c + PathFinder.CIRCLE8[right(direction)], strength - 1);
            } else {
                visualCells.add(c);
            }
        }


        //going to call this one manually
        visualCells.remove(splash.path.get(dist));

        //shoots bolts toward cone's base
        for (int cell : visualCells){
            //this way we only get the cells at the tip, much better performance.

            //TODO DEMO: the emerald splash should *feel* like the emerald splash
            //replace the knife placeholder with the emeralds
            java.util.Random Randomizer = new java.util.Random();
            int roller = Randomizer.nextInt(3);
            switch(roller)
            {
                case 0:
                    ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class ))
                        .reset( pos, cell, new AlterableProjectile(ItemSpriteSheet.EMERALD),null);
                    break;
                case 1: ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class ))
                        .reset( pos, cell, new AlterableProjectile(ItemSpriteSheet.EMERALD_THREE),null);
                    break;
                case 2: ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class ))
                        .reset( pos, cell, new AlterableProjectile(ItemSpriteSheet.EMERALD_WAVE),null);
                    break;
            }


        }
/*
        //shoots bolt in cast direction
        MagicMissile.boltFromChar( sprite.parent,
                MagicMissile.FIRE_CONE,
                sprite,
                splash.path.get(dist/2),
                null );
        Sample.INSTANCE.play( Assets.SND_ZAP );
*/

        //finally attack the cells
        for( int cell : affectedCells) {

            //ignore caster cell (but not the stand user's cell!)
            if (cell == pos) {
                continue;
            }

            Char ch = Actor.findChar(cell);
            if (ch != null) {
                ch.damage(damageRoll(), this);
            }
        }
        affectedCells = null;
        visualCells = null;
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

            if (!enemy.isAlive() && (enemy == Dungeon.hero) ) {
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
