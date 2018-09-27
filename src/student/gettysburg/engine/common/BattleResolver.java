package student.gettysburg.engine.common;

import static gettysburg.common.BattleResult.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import gettysburg.common.*;

public class BattleResolver {
	/** Recreation of the table from the game instructions **/
	//         1          2      3      4      5          6
	public static BattleResult[][] resultsTable = {
			{DELIM,	 	DBACK, DELIM, DELIM, DELIM, 	DELIM}, // >= 6.0
			{DELIM, 	DBACK, DELIM, DBACK, DELIM, 	DELIM}, // >= 5.0
			{DELIM,  EXCHANGE, DELIM, DBACK, DBACK, 	DELIM}, // >= 4.0
			{DELIM,  EXCHANGE, DBACK, DBACK, EXCHANGE,  DELIM}, // >= 3.0
			{DELIM,  EXCHANGE, DBACK, ABACK, EXCHANGE,  AELIM}, // >= 2.0
			{DELIM,  	ABACK, DBACK, ABACK, EXCHANGE,  AELIM}, // >= 1.0
			{DELIM,  EXCHANGE, DBACK, ABACK, AELIM,  	AELIM}, // >= 1/2
			{DBACK,  EXCHANGE, ABACK, ABACK, AELIM,  	AELIM}, // >= 1/3
			{ABACK,  	ABACK, ABACK, ABACK, AELIM,  	AELIM}, // >= 1/4
			{ABACK,  	AELIM, ABACK, ABACK, AELIM,  	AELIM}, // >= 1/5
			{AELIM,  	AELIM, ABACK, AELIM, AELIM,  	AELIM}, // >= 1/6
			{AELIM,  	AELIM, AELIM, AELIM, AELIM,  	AELIM}  //  < 1/6
	};	
	private static ArrayList<BattleResult> override;
	
	private BattleDescriptor bd;
	private BattleResult result;
	private BattleResolutionImpl resolution;
	private int totalAttack;
	private int totalDefense;
	private double battleRatio;
	
	public BattleResolver(BattleDescriptor bd) {
		this.bd = bd;
		this.totalAttack = bd.getAttackers().stream().map(GbgUnit::getCombatFactor).mapToInt(Integer::intValue).sum();
		this.totalDefense = bd.getDefenders().stream().map(GbgUnit::getCombatFactor).mapToInt(Integer::intValue).sum();
		this.battleRatio = totalDefense != 0 ? (double)totalAttack/totalDefense : 10.0;
	}
	
	/**
	 * @return Resolve the battle stored.
	 */
	public BattleResolution resolve() {
		if(this.resolution != null) return this.resolution;
		
		if(override != null && !override.isEmpty()) this.result = override.remove(0);
		else {
			int column = ThreadLocalRandom.current().nextInt(0, 6);
			int row = ratioToIndex(this.battleRatio);
			this.result = resultsTable[row][column];
		}
		
		resolution = new BattleResolutionImpl(this.result);
		switch (this.result) {
			case AELIM: this.resolveElim(bd.getDefenders(), bd.getAttackers()); break;
			case ABACK: this.resolveBack(bd.getDefenders(), bd.getAttackers()); break;
			case DBACK: this.resolveBack(bd.getAttackers(), bd.getDefenders()); break;
			case DELIM: this.resolveElim(bd.getAttackers(), bd.getDefenders());	break;
			case EXCHANGE: this.resolveExchange(); break;
			default: break;
		}
		
		return this.resolution;
	}
		
	/**
	 * @param winner The surviving side
	 * @param loser The eliminated side
	 * @return Resolve the battle for when one of the two sides is eliminated.
	 */
	private void resolveElim(Collection<GbgUnit> winner, Collection<GbgUnit> loser) {
		resolution.addActive(winner);
		resolution.addEliminated(loser);
	}
	
	/**
	 * @param winner The winning side
	 * @param loser The retreating side
	 * @return Resolve the battle for one one of the two sides retreats
	 */
	private void resolveBack(Collection<GbgUnit> winner, Collection<GbgUnit> loser) {
		resolution.addActive(winner);
		for(GbgUnit lose : loser) {
			ArmyID enemy = ArmyID.values()[1-lose.getArmy().ordinal()];
			CoordinateImpl c = CoordinateImpl.makeCoordinate(GettysburgEngine.getTheGame().getGameBoard().whereIsUnit(lose));
			ArrayList<CoordinateImpl> allowedNeighbors = 
					c.getNeighbors().stream()
									.filter(n -> !GettysburgEngine.getTheGame().getGameBoard().getArmyControl(enemy).contains(n))
									.filter(n -> GettysburgEngine.getTheGame().getGameBoard().getUnitsAt(n) == null)
									.collect(Collectors.toCollection(ArrayList::new));
			if(allowedNeighbors.isEmpty()) resolution.addEliminated(Arrays.asList(lose));
			else {
				Coordinate en = GettysburgEngine.getTheGame().getGameBoard().whereIsUnit(winner.iterator().next());
				allowedNeighbors.sort((n1, n2) -> n2.distanceTo(en) - n1.distanceTo(en));
				GettysburgEngine.getTheGame().getGameBoard().moveUnit(lose, allowedNeighbors.get(0));
				resolution.addActive(Arrays.asList(lose));
			}
		}
	}
	
	/**
	 * @return Resolve the battle for when there is an exchange.
	 */
	private void resolveExchange() {
		if(totalDefense == totalAttack) {
			resolution.addEliminated(bd.getAttackers());
			resolution.addEliminated(bd.getDefenders());
		}
		else {
			Collection<GbgUnit> winner;
			Collection<GbgUnit> loser;
			int loserStat;	
			
			if(totalDefense > totalAttack) {
				winner = bd.getDefenders();
				loser = bd.getAttackers();
				loserStat = totalAttack;
			}
			else {
				winner = bd.getAttackers();
				loser = bd.getDefenders();
				loserStat = totalDefense;
			}
			
			resolution.addEliminated(loser);
			Set<GbgUnit> elim = getMinimumRemove(winner, loserStat);
			Set<GbgUnit> survive = new HashSet<GbgUnit>(winner);
			survive.removeAll(elim);
			resolution.addEliminated(elim);
			resolution.addActive(survive);
		}
	}
	
	/**
	 * A helper function that takes a battle ratio and decides which index to use,
	 * based on the table in the instruction manual.
	 * @return the corresponding table index based on the battle ratio
	 */
	public static int ratioToIndex(double battleRatio) {
		if(battleRatio >= 1) return 6-((int)Math.floor(Math.min(battleRatio,6.0)));
		else {
			double inter = (1.0/Math.max(battleRatio,0.166));
			double floored = (Math.floor(inter)==inter) ? Math.floor(inter)-1 : Math.floor(inter);
			return (int)floored+5;
		}
	}
	
	/**
	 * @return the weakest combination of units to remove that satisfies the combat factor
	 * Not perfect, but this problem is NP complete and implementing a perfect solution
	 * is definitely outside the scope of this course...
	 */
	private static Set<GbgUnit> getMinimumRemove(Collection<GbgUnit> units, int cf) {
		int sofar = 0;
		Set<GbgUnit> removing = new HashSet<GbgUnit>();
		ArrayList<GbgUnit> sorted = new ArrayList<GbgUnit>(units);
		sorted.sort((GbgUnit a1, GbgUnit a2) -> a1.getCombatFactor() - a2.getCombatFactor());
		for(GbgUnit u : sorted) {
			sofar += u.getCombatFactor();
			removing.add(u);
			if(sofar >= cf) break;
		}
		return removing;
	}
	
	/**
	 * Add a manual override of BattleResults. For use with the test double.
	 */
	public static void setOverride(List<BattleResult> results) {
		override = new ArrayList<BattleResult>(results);
	}
}
