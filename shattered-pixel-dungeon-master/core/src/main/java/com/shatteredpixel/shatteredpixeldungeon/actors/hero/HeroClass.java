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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

	//import com.shatteredpixel.shatteredpixeldungeon.actors.stands;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.DebugStick;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DebugWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knuckles;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Boomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroClass {

	//original classes
	WARRIOR("warrior"),
	MAGE("mage"),
	ROGUE("rogue"),
	HUNTRESS("huntress"),

	//pt 1
	JONATHAN( "Jonathan"),

	//pt 2
	JOSEPH( "Joseph"),CAESAR("Caesar"),

	//pt 3
	JOTARO("Jotaro"),DIO("dio"),
	KAKYOIN( "kakyoin"),POLNAREFF("polnareff"),
	SHADOWDIO("Shadow DIO"),

	//pt 4
	JOSUKE( "Josuke"),KIRA("Kira"),
	OKUYASU( "Okuyasu"),KOICHI("Koichi"),
	ROHAN( "Rohan"),

	//pt 5
	GIORNO( "Giorno"),DIAVOLO("Diavolo"),
	BRUNO( "Bruno"),MISTA("Mista"),
	Narancia( "Narancia"),FUGO("Fugo"),

	//pt 6
	JOLYNE( "Jolyne"),PUCCI("Pucci"),
	ANASUI( "Anasui"),WEATHER("W. Report"),
	HERMES("Hermes"),

	//pt 7
	JOHNNY( "Johnny"),VALENTINE("Valentine"),
	GYRO( "Gyro"),DIEGO("Diego"),
	HOTPANTS( "Hot Pants"),

	//pt 8
	GAPPY( "Jousuke"),JOBIN("Jobin"),
	YASUHO( "Yasuho"),CUCKOLD("Joshu"),
	NORISUKE("Norisuke");


	private String title;

	HeroClass( String title ) {
		this.title = title;
	}

	public void initHero( Hero hero ) {

		hero.heroClass = this;

		initCommon( hero );

		switch (this) {
			case WARRIOR:
				initWarrior( hero );
				break;

			case MAGE:
				initMage( hero );
				break;

			case ROGUE:
				initRogue( hero );
				break;

			case HUNTRESS:
				initHuntress( hero );
				break;

			case JOTARO: case KAKYOIN: case POLNAREFF: case SHADOWDIO:
				initStandUser(hero);
				break;

			case DIO:
                initASSHAT(hero);
                break;
		}
		
	}

	private static void initCommon( Hero hero ) {
		if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
			(hero.belongings.armor = new ClothArmor()).identify();

		if (!Dungeon.isChallenged(Challenges.NO_FOOD))
			new Food().identify().collect();
	}

	public Badges.Badge masteryBadge() {
		switch (this) {
			case WARRIOR:
				return Badges.Badge.MASTERY_WARRIOR;
			case MAGE:
				return Badges.Badge.MASTERY_MAGE;
			case ROGUE:
				return Badges.Badge.MASTERY_ROGUE;
			case HUNTRESS:
				return Badges.Badge.MASTERY_HUNTRESS;
		}
		return null;
	}

	private static void initWarrior( Hero hero ) {
		(hero.belongings.weapon = new WornShortsword()).identify();
		//ThrowingStone stones = new ThrowingStone();
		PotionOfHealing hpots = new PotionOfHealing();
		PotionOfMindVision mpots = new PotionOfMindVision();
		PotionOfInvisibility ipots = new PotionOfInvisibility();

		WarriorArmor defense = new WarriorArmor();
		//defense.identify().collect();
		defense.level(100);
		defense.affixSeal(new BrokenSeal());
		hero.belongings.armor = defense;

		ScrollOfMagicMapping mscrolls = new ScrollOfMagicMapping();
		mscrolls.identify().quantity(30).collect();

		ipots.identify().quantity(20).collect();
		mpots.identify().quantity(20).collect();
		hpots.identify().quantity(20).collect();

		DebugStick stick = new DebugStick();
		stick.quantity(1).collect();

		//stones.identify().quantity(3).collect();

		TimekeepersHourglass hourglass = new TimekeepersHourglass();
		hourglass.level(10);
		(hero.belongings.misc1 = hourglass).identify();
		hero.belongings.misc1.activate( hero );

		WandOfBlastWave blaster = new WandOfBlastWave();
		blaster.identify().collect();
		blaster.level(100);

		DebugWeapon naginata = new DebugWeapon();
		naginata.identify();
		naginata.level(100);
		hero.belongings.weapon = naginata;


		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_WARRIOR) ){
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
				hero.belongings.armor.affixSeal(new BrokenSeal());
			//Dungeon.quickslot.setSlot(0, stones);
		} else {
			if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
				BrokenSeal seal = new BrokenSeal();
				seal.collect();
				Dungeon.quickslot.setSlot(0, seal);
			}

			Dungeon.quickslot.setSlot(0, hourglass);
			//Dungeon.quickslot.setSlot(1, stones);
		}


	}

	private static void initMage( Hero hero ) {
		MagesStaff staff;

		if ( Badges.isUnlocked(Badges.Badge.TUTORIAL_MAGE) ){
			staff = new MagesStaff(new WandOfMagicMissile());
		} else {
			staff = new MagesStaff();
			new WandOfMagicMissile().identify().collect();
		}

		(hero.belongings.weapon = staff).identify();
		hero.belongings.weapon.activate(hero);

		Dungeon.quickslot.setSlot(0, staff);

		new ScrollOfUpgrade().identify();
	}

	private static void initRogue( Hero hero ) {
		(hero.belongings.weapon = new Dagger()).identify();

		CloakOfShadows cloak = new CloakOfShadows();
		(hero.belongings.misc1 = cloak).identify();
		hero.belongings.misc1.activate( hero );

		ThrowingKnife knives = new ThrowingKnife();
		knives.quantity(3).collect();

		Dungeon.quickslot.setSlot(0, cloak);
		Dungeon.quickslot.setSlot(1, knives);

		new ScrollOfMagicMapping().identify();
	}

	private static void initHuntress( Hero hero ) {

		(hero.belongings.weapon = new Knuckles()).identify();
		Boomerang boomerang = new Boomerang();
		boomerang.identify().collect();

		Dungeon.quickslot.setSlot(0, boomerang);

		new PotionOfMindVision().identify();
	}

	private static void initStandUser( Hero hero)
	{
		(hero.belongings.weapon = new Knuckles()).identify();
		PotionOfHealing hpots = new PotionOfHealing();
		PotionOfMindVision mpots = new PotionOfMindVision();
		PotionOfInvisibility ipots = new PotionOfInvisibility();

		ipots.identify().quantity(20).collect();
		mpots.identify().quantity(20).collect();
		hpots.identify().quantity(20).collect();

		DebugStick stick = new DebugStick();
		stick.collect();

		/*
		TimekeepersHourglass hourglass = new TimekeepersHourglass();
		(hero.belongings.misc1 = hourglass).identify();
		hero.belongings.misc1.activate( hero );
		*/


			if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
				BrokenSeal seal = new BrokenSeal();
				seal.collect();
				Dungeon.quickslot.setSlot(0, seal);
			}

			//Dungeon.quickslot.setSlot(0, hourglass);

	}
    private static void initASSHAT( Hero hero)
    {
        (hero.belongings.weapon = new Knuckles()).identify();
        PotionOfHealing hpots = new PotionOfHealing();
        PotionOfMindVision mpots = new PotionOfMindVision();
        PotionOfInvisibility ipots = new PotionOfInvisibility();

        ipots.identify().quantity(20).collect();
        mpots.identify().quantity(20).collect();
        hpots.identify().quantity(20).collect();

        DebugStick stick = new DebugStick();
        stick.collect();

		/*
		TimekeepersHourglass hourglass = new TimekeepersHourglass();
		(hero.belongings.misc1 = hourglass).identify();
		hero.belongings.misc1.activate( hero );
		*/


        if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
            BrokenSeal seal = new BrokenSeal();
            seal.collect();
            Dungeon.quickslot.setSlot(0, seal);
        }

        //Dungeon.quickslot.setSlot(0, hourglass);

    }
	
	public String title() {
		return Messages.get(HeroClass.class, title);
	}
	
	public String spritesheet() {
		
		switch (this) {
		case WARRIOR:
			return Assets.WARRIOR;
		case MAGE:
			return Assets.MAGE;
		case ROGUE:
			return Assets.ROGUE;
		case HUNTRESS:
			return Assets.HUNTRESS;
		case JOTARO:
			return Assets.JOTARO;
		case DIO:
			return Assets.DIO;
		case KAKYOIN:
			return Assets.KAKYOIN;
		case POLNAREFF:
			return Assets.POLNAREFF;
		}

		
		return null;
	}
	
	public String[] perks() {
		
		switch (this) {
		case WARRIOR:
			return new String[]{
					Messages.get(HeroClass.class, "warrior_perk1"),
					Messages.get(HeroClass.class, "warrior_perk2"),
					Messages.get(HeroClass.class, "warrior_perk3"),
					Messages.get(HeroClass.class, "warrior_perk4"),
					Messages.get(HeroClass.class, "warrior_perk5"),
			};
		case MAGE:
			return new String[]{
					Messages.get(HeroClass.class, "mage_perk1"),
					Messages.get(HeroClass.class, "mage_perk2"),
					Messages.get(HeroClass.class, "mage_perk3"),
					Messages.get(HeroClass.class, "mage_perk4"),
					Messages.get(HeroClass.class, "mage_perk5"),
			};
		case ROGUE:
			return new String[]{
					Messages.get(HeroClass.class, "rogue_perk1"),
					Messages.get(HeroClass.class, "rogue_perk2"),
					Messages.get(HeroClass.class, "rogue_perk3"),
					Messages.get(HeroClass.class, "rogue_perk4"),
					Messages.get(HeroClass.class, "rogue_perk5"),
			};
		case HUNTRESS:
			return new String[]{
					Messages.get(HeroClass.class, "huntress_perk1"),
					Messages.get(HeroClass.class, "huntress_perk2"),
					Messages.get(HeroClass.class, "huntress_perk3"),
					Messages.get(HeroClass.class, "huntress_perk4"),
					Messages.get(HeroClass.class, "huntress_perk5"),
			};
		case JOTARO:
			return new String[]{
					Messages.get(HeroClass.class, "jotaro_perk1"),
					Messages.get(HeroClass.class, "jotaro_perk2"),
					Messages.get(HeroClass.class, "jotaro_perk3"),
					Messages.get(HeroClass.class, "jotaro_perk4"),
					Messages.get(HeroClass.class, "jotaro_perk5"),
			};
			case DIO:
				return new String[]{
						Messages.get(HeroClass.class, "dio_perk1"),
						Messages.get(HeroClass.class, "dio_perk2"),
						Messages.get(HeroClass.class, "dio_perk3"),
						Messages.get(HeroClass.class, "dio_perk4"),
						Messages.get(HeroClass.class, "dio_perk5"),
				};
			case KAKYOIN:
				return new String[]{
						Messages.get(HeroClass.class, "kakyoin_perk1"),
						Messages.get(HeroClass.class, "kakyoin_perk2"),
						Messages.get(HeroClass.class, "kakyoin_perk3"),
						Messages.get(HeroClass.class, "kakyoin_perk4"),
						Messages.get(HeroClass.class, "kakyoin_perk5"),
				};
			case POLNAREFF:
				return new String[]{
						Messages.get(HeroClass.class, "polnareff_perk1"),
						Messages.get(HeroClass.class, "polnareff_perk2"),
						Messages.get(HeroClass.class, "polnareff_perk3"),
						Messages.get(HeroClass.class, "polnareff_perk4"),
						Messages.get(HeroClass.class, "polnareff_perk5"),
				};
		}

		
		return null;
	}

	private static final String CLASS	= "class";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( CLASS, toString() );
	}
	
	public static HeroClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( CLASS );
		return value.length() > 0 ? valueOf( value ) : ROGUE;
	}
}
