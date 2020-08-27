package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.stands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.AlterableProjectile;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.standsprites.MagicianSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Magician extends Stand {


    boolean superCrossfire = false;
    boolean superBind = false;
    boolean superCFHS = false;

    //the actual affected cells
    private HashSet<Integer> affectedCells = null;
    //the cells to trace fire shots to, for visual effects.
    private HashSet<Integer> visualCells = null;
    private int direction = 0;

    {
        spriteClass = MagicianSprite.class;
        properties.add(Property.FIERY);

        power = powerB;
        speed = speedB;
        range = rangeC;
        def = defB;

        primaryColor = 0xFFA85A;

        state = WANDERING;

        HUNTING = new Hunting();
    }

    public Magician(Char standMaster){
        super();

        this.standUser = standMaster;
        this.alignment = standUser.alignment;
        HP = standUser.HP;
        HT = standUser.HT;
    }

    public Magician()
    {}


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

    private void crossFireHurricane()
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
        redBind();
    }

    private void redBind(){

        Ballistica bind = new Ballistica(pos, worldCell, Ballistica.MAGIC_BOLT);

        //if we find an enemy we'll want to damage and possibly move them
        if(Actor.findChar(worldCell) != null)
        {
            //if we find an enemy but they can't be moved simply damage them and apply the debuffs
            if (Actor.findChar(worldCell).properties().contains(Char.Property.IMMOVABLE)) {
                //TODO: cripple the enemy and apply suffocation debuffs
                enemy = Actor.findChar(worldCell);
                doAttack(enemy);
            }
            else
            {
                int bestPos = -1;
                for (int i : bind.subPath(1, bind.dist)){
                    //prefer to the earliest point on the path
                    if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
                        bestPos = i;
                        break;
                    }
                }

                sprite.showStatus(primaryColor, "scree!", this);
                doAttack(enemy);

                if(bestPos != -1)
                {
                    worldCell = bestPos;
                }




            }


        }

        //in any case we want to play the animation
        MagicMissile.boltFromChar( sprite.parent,
                MagicMissile.FIRE,
                this.sprite,
                worldCell,
                new Callback() {
                    @Override
                    public void call() {
                Actor.addDelayed(new Pushing(enemy, enemy.pos, worldCell, new Callback(){
                    public void call() {
                        enemy.pos = worldCell;
                        Dungeon.level.press(worldCell, enemy, true);
                        if (enemy == Dungeon.hero) {
                            Dungeon.hero.interrupt();
                            Dungeon.observe();
                            GameScene.updateFog();
                        }
                    }
                }), -1);
                superBind = false;
                onZapComplete();
            }
        });

/*

        Dungeon.hero.busy();
        standUser.spend(1);
        spend(1);
        sprite.parent.add(new Chains(sprite.center(), enemy.sprite.center(), new Callback() {
            public void call() {
                Actor.add(new Pushing(enemy, enemy.pos, worldCell, new Callback() {
                    public void call() {
                        Dungeon.level.press(worldCell, enemy, true);
                    }
                }));
                enemy.pos = worldCell;
                Dungeon.hero.interrupt();
                Dungeon.observe();
                GameScene.updateFog();
                next();
            }
        }));
*/
    }

    //TODO: if we find another stand that uses a cone blast consider making this a parent function
    //the code for abilityThree()/specialHurricane() is taken directly from Evan-00's WandofFireblast,
    //it is referenced in other cases where a class needs to fire some sort of cone shaped projectile
    @Override
    public void abilityThree() {
        worldCell = enemy.pos;
        superCFHS = true;
        specialHurricane();
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

    private void specialHurricane()
    {
        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        Ballistica blast = new Ballistica(pos, worldCell, Ballistica.MAGIC_BOLT);

        int maxDist = (int)(standUser.SP / 10);
        if(maxDist < 3)
        {
            maxDist = 3;
        }
        int dist = Math.min(blast.dist, maxDist);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
            if (blast.sourcePos+PathFinder.CIRCLE8[i] == blast.path.get(1)){
                direction = i;
                break;
            }
        }

        float strength = maxDist;
        for (int c : blast.subPath(1, dist)) {
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
        visualCells.remove(blast.path.get(dist));

        //shoots bolts toward cone's base
        for (int cell : visualCells){
            //this way we only get the cells at the tip, much better performance.
            ((MissileSprite)this.sprite.parent.recycle( MissileSprite.class ))
                    .reset( pos, cell, new AlterableProjectile(ItemSpriteSheet.MAGICIAN_ANKH),null);
        }

        //shoots bolt in cast direction
        MagicMissile.boltFromChar( sprite.parent,
                MagicMissile.FIRE_CONE,
                sprite,
                blast.path.get(dist/2),
                null );
        //I'll burn you to a crisp!!!!
        standUser.sprite.showStatus(primaryColor, "Cross Fire Hurricane", standUser);
        standUser.sprite.showStatus(primaryColor, "Special!!!", standUser);
        Sample.INSTANCE.play(Assets.SND_BURNING);


        //finally attack the cells
        for( int cell : affectedCells) {

            //ignore caster cell, magician's flames are fully controllable so Avdol is safe
            if (cell == pos || cell == standUser.pos) {
                continue;
            }

            Char ch = Actor.findChar(cell);
            if (ch != null) {
                ch.damage(damageRoll(), this);
                Buff.affect(ch, Burning.class).reignite(ch);
            }
        }

        superCFHS = false;

        affectedCells = null;
        visualCells = null;
    }

    public void onZapComplete() {
        next();
    }

    @Override
    public void notice()
    {
        superBind = true;
    }

    @Override
    public int damageRoll() {
        if(superCrossfire || superBind)
        {
            return (int) ((double) super.damageRoll() * 1.5);
        }

        else if(superCFHS)
        {
            //this is gonna hurt
            return super.damageRoll() * 2;
        }
        else
        {
         return super.damageRoll();
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

    private class Hunting extends Stand.Hunting{
        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {
            enemySeen = enemyInFOV;

            if(enemy != null)
            {
                updateCell(enemy.pos);
            }

            if (enemyInFOV
                    && !isCharmedBy( enemy )
                    && !Dungeon.level.adjacent( pos, enemy.pos ) && superBind)
            { redBind();
                return false;
            }

            else {
                return super.act( enemyInFOV, justAlerted );
            }

        }
    }

}
