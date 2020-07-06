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
        //(sprite).zap(enemy.pos);

        sprite.showStatus(0xFFA85A, "SCREE!", this );
        //onZapComplete();

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

    @Override
    public int damageRoll() {
        return (int) (standUser.damageRoll()  * power);
    }

    @Override
    public int attackSkill( Char target ) {
        return (int) (standUser.damageRoll()  * power);
    }

    @Override
    public int drRoll() {
        return (int) (standUser.drRoll() * power);
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
        if((superCrossfire || superCFHS) && Dungeon.level.distance(this.pos, enemy.pos) <= 8) {
            superCFHS = false;
            superCrossfire = false;
            return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
        }
        else{
            return super.canAttack(enemy);
        }
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
