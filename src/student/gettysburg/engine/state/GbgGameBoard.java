package student.gettysburg.engine.state;

import static gettysburg.common.Direction.*;
import static student.gettysburg.engine.common.CoordinateImpl.makeCoordinate;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import gettysburg.common.*;
import student.gettysburg.engine.GettysburgFactory;
import student.gettysburg.engine.common.*;
import student.gettysburg.engine.utility.configure.UnitInitializer;

public class GbgGameBoard {
	private HashMap<GbgUnit, CoordinateImpl> board; 
	private HashMap<CoordinateImpl, ArrayList<GbgUnit>> positions;
	@SuppressWarnings("unchecked")
	private Set<CoordinateImpl>[] control = new Set[2];
	private ArrayList<GbgUnit> alreadyMoved;
	private ArrayList<GbgUnit> alreadyTurned;
	private ArrayList<GbgUnit> alreadyBattled;
	private Set<GbgUnit> attackersToResolve;
	private Set<GbgUnit> defendersToResolve;
	
	// Stores directions in order, for convenience
	private static final List<Direction> compass = Arrays.asList(
			NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST
	);

	// Initializes board
	public GbgGameBoard() {
		this.board = new HashMap<GbgUnit, CoordinateImpl>();
		this.positions = new HashMap<CoordinateImpl, ArrayList<GbgUnit>>();
		this.alreadyMoved = new ArrayList<GbgUnit>();
		this.alreadyTurned = new ArrayList<GbgUnit>();
		this.alreadyBattled = new ArrayList<GbgUnit>();
		this.control[ArmyID.UNION.ordinal()] = new HashSet<>();
		this.control[ArmyID.CONFEDERATE.ordinal()] = new HashSet<>();
	}
	
	// ======================================================================
	//				Functions dealing with units and their location
	// ======================================================================
	/**
	 * @return units at the specified position, or null if there are none
	 */
	public Collection<GbgUnit> getUnitsAt(Coordinate where) {
		ArrayList<GbgUnit> result = positions.get(where);
		if(result == null || result.isEmpty()) return null;
		else return new ArrayList<GbgUnit>(result);
	}
	
	/**
	 * @return the unit on the board with the leader and army specified, or null
	 */
	public GbgUnit getUnit(String leader, ArmyID army) {
		for(Entry<GbgUnit, CoordinateImpl> ent : board.entrySet()) {
			if(ent.getKey().getLeader().equals(leader) &&
			   ent.getKey().getArmy().equals(army))
				return ent.getKey();
		}
		return null;
	}
	
	/**
	 * @return the coordinate where the given unit is
	 */
	public Coordinate whereIsUnit(GbgUnit g) {
		return this.board.get(g);
	}
	
	/**
	 * @return all units currently on the board
	 */
	public Collection<GbgUnit> getAllUnits() {
		return this.board.keySet();
	}
	
	/**
	 * @return all units currently on the board from the specified army
	 */
	public Collection<GbgUnit> getFullArmy(ArmyID army) {
		return this.getAllUnits()
						.stream()
						.filter(u -> u.getArmy().equals(army))
						.collect(Collectors.toCollection(HashSet::new));
	}
	
	/**
	 * Summons all of the units that should appear during the current turn
	 * @param uis The BattleOrder to summon units from, usually either Union or Confederate.
	 * @param turn the turn number to summon units for
	 */
	public void summonReinforcements(List<UnitInitializer> uis, int turn) {
		Set<Coordinate> ok = new HashSet<Coordinate>();
		for(UnitInitializer ui : uis) {
			if(ui.turn > turn) break;
			else if(ui.turn == turn) {
				if(ok.contains(ui.where) || this.getUnitsAt(ui.where) == null) {
					ok.add(ui.where);
					placeUnit(ui.unit, ui.where);
				}
			}
		}
	}
	
	/**
	 * Places the unit at the specified location
	 * To be used internally, or by the test double
	 */
	public void placeUnit(GbgUnit u, Coordinate where) {
		CoordinateImpl newC = makeCoordinate(where);
		CoordinateImpl oldC = makeCoordinate(whereIsUnit(u));
		if(oldC != null) {
			this.positions.get(oldC).remove(u);
			this.control(u).remove(oldC);
			this.control(u).removeAll(this.getUnitControl(u));
		}
		
		this.board.put(u, newC);
		if(!this.positions.containsKey(newC)) {
			this.positions.put(newC, new ArrayList<GbgUnit>());
		}
		this.positions.get(newC).add(u);
		this.control(u).add(newC);
		this.control(u).addAll(this.getUnitControl(u));
	}
	
	/** 
	 * Removes a unit from the board entirely.
	 */
	public void removeUnit(GbgUnit g) {
		Coordinate d = whereIsUnit(g);
		this.control(g).remove(d);
		this.control(g).removeAll(this.getUnitControl(g));
		this.positions.get(d).remove(g);
		this.board.remove(g);
	}
	
	/**
	 * Same as place unit, except it includes marking the unit as already moved.
	 * To be used by actual implementations of moveUnit
	 */
	public void moveUnit(GbgUnit u, Coordinate where) {
		this.placeUnit(u, where);
		this.alreadyMoved.add(u);
	}
	
	/**
	 * Internal implementation of setUnitFacing
	 */
	public void turnUnit(GbgUnit u, Direction d) {
		this.control(u).removeAll(this.getUnitControl(u));
		u.setFacing(d);
		this.control(u).addAll(this.getUnitControl(u));
		this.alreadyTurned.add(u);
	}
	
	// ======================================================================
	//					Functions dealing with battles
	// ======================================================================	
	/**
	 * Resolve battle. Assumes it has already been validated
	 */
	public BattleResolution resolveBattle(BattleDescriptor bdx) {
		BattleDescriptorImpl bd = BattleDescriptorImpl.makeBattleDescriptorImpl(bdx);
		bd.getAttackers().forEach(b -> this.attackersToResolve.remove(b));
		bd.getDefenders().forEach(b -> this.defendersToResolve.remove(b));
		bd.getAllUnits().forEach(b -> this.alreadyBattled.add(b));
		
		BattleResolver br = new BattleResolver(bd);
		BattleResolution result = br.resolve();
		result.getEliminatedConfederateUnits().forEach(this::removeUnit);
		result.getEliminatedUnionUnits().forEach(this::removeUnit);
		
		return result;
	}
	
	/**
	 * @return the battles to resolve for the specified army
	 */
	public Collection<BattleDescriptor> getBattlesToResolve(ArmyID army) {
		// If the battles have already been gotten this turn, don't calculate again
		if(this.attackersToResolve == null && this.defendersToResolve == null) {
			Set<GbgUnit> attack = new HashSet<GbgUnit>();
			Set<GbgUnit> defend = new HashSet<GbgUnit>();
			for(GbgUnit g : GettysburgEngine.getTheGame().getGameBoard().getFullArmy(army)) {
				// Get the coordinate in front of this unit
				Set<CoordinateImpl> zoc = getUnitControl(g);
				for(CoordinateImpl c : zoc) {
					if(getUnitsAt(c) == null) continue; // if there's nobody there, continue
					else for(GbgUnit f : getUnitsAt(c)) { // else check them all
						if(f.getArmy() != army) { // if any of them is the enemy, save it bc this is a battle
							attack.add(g);
							defend.add(f);
						}
					}
				}
			}
			this.attackersToResolve = attack;
			this.defendersToResolve = defend;
		}
		
		if(this.attackersToResolve.isEmpty() && this.defendersToResolve.isEmpty()) return Arrays.asList();
		else return Arrays.asList(GettysburgFactory.makeBattleDescriptor(this.attackersToResolve, this.defendersToResolve));
	}
	
	// ======================================================================
	//					Administrative functions on the board
	// ======================================================================
	/**
	 * Clear the board and reset other variables.
	 */
	public void clearBoard() {
		this.board = new HashMap<GbgUnit, CoordinateImpl>();
		this.positions = new HashMap<CoordinateImpl, ArrayList<GbgUnit>>();
		this.alreadyMoved = new ArrayList<GbgUnit>();
		this.alreadyTurned = new ArrayList<GbgUnit>();
		this.alreadyBattled = new ArrayList<GbgUnit>();
		this.control[ArmyID.UNION.ordinal()] = new HashSet<>();
		this.control[ArmyID.CONFEDERATE.ordinal()] = new HashSet<>();
	}
	
	/**
	 * @return if a unit has already moved this turn
	 */
	public boolean unitAlreadyMoved(GbgUnit u) {
		return this.alreadyMoved.contains(u);
	}
	
	/**
	 * @return if a unit has already turned this turn
	 */
	public boolean unitAlreadyTurned(GbgUnit u) {
		return this.alreadyTurned.contains(u);
	}
	
	/**
	 * @return if a unit has already battled this turn
	 */
	public boolean unitAlreadyBattled(GbgUnit u) {
		return this.alreadyBattled.contains(u);
	}
	
	/**
	 * Reset movement and battle trackers at the end of the turn
	 */
	public void resetMovementTrackers() {
		this.alreadyMoved.clear();
		this.alreadyTurned.clear();
	}
	
	/**
	 * Reset battle trackers at the end of each player's battle phase
	 */
	public void resetBattleTrackers() {
		this.alreadyBattled.clear();
		this.attackersToResolve = this.defendersToResolve = null;
	}
	
	/**
	 * Remove any stacked units at the end of the movement phase
	 */
	public void removeStackedUnits() {
		for(ArrayList<GbgUnit> units : this.positions.values()) {
			if(units.size() > 1) units.clear();
		}
	}
	
	
	// ======================================================================
	// 				Functions dealing with a units Zone of Control
	// ======================================================================
	/**
	 * @return return the corresponding Set of controlled coordinates for the given unit's army
	 */
	private Set<CoordinateImpl> control(GbgUnit g) {
		return this.getArmyControl(g.getArmy());
	}
	
	/**
	 * @return return the corresponding Set of controlled coordinates for the given army
	 */
	public Set<CoordinateImpl> getArmyControl(ArmyID a) {
		return this.control[a.ordinal()];
	}
	
	/**
	 * @return the calculated Set of controlled coordinates corresponding to the given unit
	 */
	public Set<CoordinateImpl> getUnitControl(GbgUnit u) {
		if(u.getType().equals(UnitType.HQ)) return new HashSet<CoordinateImpl>();
		else return getZoneOfControl(whereIsUnit(u), u.getFacing());
	}
	
	public Set<CoordinateImpl> getZoneOfControl(Coordinate o, Direction d) {
		Set<CoordinateImpl> result = new HashSet<CoordinateImpl>();
		CoordinateImpl c = makeCoordinate(o);
		result.add(c.getNeighborInDirection(d));
		result.add(c.getNeighborInDirection(counterClockwise(d)));
		result.add(c.getNeighborInDirection(clockwise(d)));
		return result;
	}
	
	/**
	 * @return the Direction clockwise of the one provided
	 */
	private static Direction clockwise(Direction d) {
		int id = compass.indexOf(d);
		id++;
		if(id >= compass.size()) id = 0;
		return compass.get(id);
	}
	
	/**
	 * @return the Direction counterclockwise of the one provided
	 */
	private static Direction counterClockwise(Direction d) {
		int id = compass.indexOf(d);
		id--;
		if(id < 0) id = 7;
		return compass.get(id);
	}
}
