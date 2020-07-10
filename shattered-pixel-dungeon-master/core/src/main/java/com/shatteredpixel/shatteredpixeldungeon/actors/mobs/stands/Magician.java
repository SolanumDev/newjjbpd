package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.MagicianSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class Magician extends Stand {


    boolean superCrossfire = false;
    boolean superDetector = false;
    boolean superCFHS = false;

    {
        spriteClass = MagicianSprite.class;
        properties.add(Property.FIERY);

        power = powerB;
        speed = speedB;
        range = rangeC;
        def = defB;

        primaryColor = 0xFFA85A;

        state = WANDERING;
    }

    public Magician(Char standMaster){
        this.standUser = standMaster;
        this.alignment = standUser.alignment;
        HP = standUser.HP;
        HT = standUser.HT;
    }

    @Override
    public void abilityOne() {
        superCrossfire = true;
        crossFireHurricane();

        if (state != HUNTING)
        {
            state = HUNTING;
        }
        yell("SCREE!!!");
        act();
        superCrossfire = false;
    }

    public void crossFireHurricane()
    {
        spend( ZAP_TIME );
        (sprite).zap(enemy.pos);

        sprite.showStatus(primaryColor, "SCREE!", this );
        onZapComplete();

        if (hit( this, enemy, true )) {

          if (Random.Int( 5 ) == 0) {
              Buff.affect(enemy, Burning.class).reignite(enemy);
          }

           int dmg = (int) (damageRoll() * 1.2);
            enemy.damage( dmg, this );

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail( getClass() );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        }

    }

    @Override
    public void abilityTwo() {
        super.abilityTwo();
    }

    @Override
    public void abilityThree() {
        super.abilityOne();
    }

    public void onZapComplete() {
        next();
    }

    @Override
    public void notice()
    {
        abilityOne();
    }


    protected boolean doAttack( Char enemy ) {

        //TODO: do we go into the attack conditionals for supers?
        //yell("SCRAAA!");
        if((superCrossfire || superCFHS) && Dungeon.level.distance(this.pos, enemy.pos) <= 8)
        {
            boolean visible = fieldOfView[pos] || fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap( enemy.pos );
            }

            if(superCrossfire)
            {
                yell("ARRRRRRRRRRA");
                crossFireHurricane();
            }
            else if(superCFHS)
            {
                crossFireHurricane();
                //special
            }

            return !visible;
        }

        return super.doAttack(enemy);
    }

    @Override
    protected boolean canAttack( Char enemy ) {
        if((superCrossfire || superCFHS) && Dungeon.level.distance(this.pos, enemy.pos) <= 8) {
            superCFHS = false;
            superCrossfire = false;
            return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }
        else{
            return super.canAttack(enemy);
        }
    }

}
