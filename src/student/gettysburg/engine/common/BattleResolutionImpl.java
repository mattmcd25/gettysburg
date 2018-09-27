package student.gettysburg.engine.common;

import java.util.ArrayList;
import java.util.Collection;

import gettysburg.common.ArmyID;
import gettysburg.common.BattleResolution;
import gettysburg.common.BattleResult;
import gettysburg.common.GbgUnit;

public class BattleResolutionImpl implements BattleResolution {

	private BattleResult result;
	private Collection<GbgUnit> acU, acC, elU, elC;
	
	public BattleResolutionImpl(BattleResult result) {
		this.result = result;
		this.acU = new ArrayList<GbgUnit>();
		this.acC = new ArrayList<GbgUnit>();
		this.elU = new ArrayList<GbgUnit>();
		this.elC = new ArrayList<GbgUnit>();
	}
	
	/**
	 * Add surviving units after the battle. 
	 * Automatically separates union and confederate.
	 */
	public void addActive(Collection<GbgUnit> us) {
		for(GbgUnit u : us) {
			if(u.getArmy().equals(ArmyID.UNION)) this.acU.add(u);
			else this.acC.add(u);
		}
	}
	
	/**
	 * Add eliminated units after the battle.
	 * Automatically separates union and confederate.
	 */
	public void addEliminated(Collection<GbgUnit> us) {
		for(GbgUnit u : us) {
			if(u.getArmy().equals(ArmyID.UNION)) this.elU.add(u);
			else this.elC.add(u);
		}
	}
	
	/**
	 * Return surviving confederate units.
	 */
	@Override
	public Collection<GbgUnit> getActiveConfederateUnits() {
		return this.acC;
	}

	/**
	 * Return surviving union units.
	 */
	@Override
	public Collection<GbgUnit> getActiveUnionUnits() {
		return this.acU;
	}

	/**
	 * Return the result of the battle.
	 */
	@Override
	public BattleResult getBattleResult() {
		return this.result;
	}

	/**
	 * Return eliminated confederate units.
	 */
	@Override
	public Collection<GbgUnit> getEliminatedConfederateUnits() {
		return this.elC;
	}

	/**
	 * Return eliminated union units.
	 */
	@Override
	public Collection<GbgUnit> getEliminatedUnionUnits() {
		return this.elU;
	}

}
