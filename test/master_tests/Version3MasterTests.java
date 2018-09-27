/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Copyright ©2016 Gary F. Pollice
 *******************************************************************************/

package master_tests;

import static student.gettysburg.engine.GettysburgFactory.*;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import gettysburg.common.*;
import gettysburg.common.exceptions.*;
import student.gettysburg.engine.common.GbgUnitImpl;
import static gettysburg.common.GbgGameStep.*;
import static gettysburg.common.UnitSize.*;
import static gettysburg.common.UnitType.*;
import static gettysburg.common.Direction.*;
import static gettysburg.common.ArmyID.*;
import static gettysburg.common.GbgGameStatus.*;
import static gettysburg.common.BattleResult.*;

/**
 * Description
 * @version Oct 8, 2017
 */
public class Version3MasterTests
{
	private GbgGame game;
	private TestGbgGame testGame;
	private GbgUnit gamble, rowley, schurz, devin, heth, pender, rodes, dance, hampton;

	@Before
	public void setup()
	{
		game = testGame = makeTestGame();
		gamble = game.getUnitsAt(makeCoordinate(11, 11)).iterator().next();
		devin = game.getUnitsAt(makeCoordinate(13, 9)).iterator().next();
		heth = game.getUnitsAt(makeCoordinate(8, 8)).iterator().next();
		// These work if the student kept that GbgUnitImpl.makeUnit method
		rowley = GbgUnitImpl.makeUnit(UNION, 3, NORTHEAST, "Rowley", 2, DIVISION, INFANTRY);
		schurz = GbgUnitImpl.makeUnit(UNION, 2, NORTH, "Schurz", 2, DIVISION, INFANTRY);
		pender = GbgUnitImpl.makeUnit(CONFEDERATE, 4, EAST, "Pender", 2, DIVISION, INFANTRY);
		rodes = GbgUnitImpl.makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2, DIVISION, INFANTRY);
		dance = GbgUnitImpl.makeUnit(CONFEDERATE, 2, EAST, "Dance", 4, BATTALION, ARTILLERY);
		hampton = GbgUnitImpl.makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4, BRIGADE, CAVALRY);
		// If the previous statements fail, comment them out and try these
//		rowley = TestUnit.makeUnit(UNION, 3, NORTHEAST, "Rowley", 2);
//		schurz = TestUnit.makeUnit(UNION,  2,  NORTH, "Shurz", 2);
//		pender = TestUnit.makeUnit(CONFEDERATE, 4, EAST, "Pender", 2);
//		rodes = TestUnit.makeUnit(CONFEDERATE, 4, SOUTH, "Rodes", 2);
//		dance = TestUnit.makeUnit(CONFEDERATE, 2, EAST, "Dance", 4);
//		hampton = TestUnit.makeUnit(CONFEDERATE, 1, SOUTH, "Hampton", 4);
//		devin.setFacing(SOUTH);
//		gamble.setFacing(WEST);
//		heth.setFacing(EAST);
	}

	@Test
	public void endStep() {
		game.endStep();
		assertEquals(UBATTLE, game.getCurrentStep());
	}

	@Test
	public void checkTurn1() {
		game.endStep();
		assertEquals(1, game.getTurnNumber());
	}

	@Test
	public void checkFacing() {
		GbgUnit g = findUnit("Gamble", makeCoordinate(11, 11));
		assertEquals(WEST, game.getUnitFacing(g));
		assertNotNull(findUnit("Gamble", makeCoordinate(11, 11)));
	}

	@Test
	public void checkAttacker() {
		GbgUnit g = findUnit("Gamble", makeCoordinate(11, 11));
		GbgUnit h = findUnit("Heth", makeCoordinate(8, 8));
		testGame.clearBoard();
		testGame.putUnitAt(h, 10, 10, EAST);
		testGame.putUnitAt(g, 11, 11, NORTHWEST);
		game.endStep();
		isAttacker(g);
	}

	@Test
	public void gameStatusIsOneOnInitializedGame() {
		assertEquals(IN_PROGRESS, game.getGameStatus());
	}

	@Test
	public void checkGambleIsAtCorrectSquare() {
		assertEquals(makeCoordinate(11, 11), game.whereIsUnit("Gamble", UNION));
	}

	@Test
	public void unionBattleFollowsUnionMove() {
		game.endStep();
		assertEquals(UBATTLE, game.getCurrentStep());
	}

	@Test
	public void confederateMoveFollowsUnionBattle() {
		game.endStep();
		game.endStep();
		assertEquals(CMOVE, game.getCurrentStep());
	}

	@Test
	public void confederateBattleFollowsConfederateMove() {
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(CBATTLE, game.getCurrentStep());
	}

	@Test
	public void turnChangesAfterConfederateBattle() {
		game.endStep();
		game.endStep();
		game.endStep();
		game.endStep();
		assertEquals(2, game.getTurnNumber());
		assertEquals(UMOVE, game.getCurrentStep());
	}

	@Test
	public void gambleMovesEast() {
		GbgUnit g = findUnit("Gamble", makeCoordinate(11, 11));
		game.moveUnit(g, makeCoordinate(11, 11), makeCoordinate(13, 11));
		assertEquals(makeCoordinate(13, 11), game.whereIsUnit("Gamble", UNION));
	}

	@Test
	public void hethMovesNorthWest() {
		game.endStep();
		game.endStep();
		GbgUnit h = findUnit("Heth", makeCoordinate(8, 8));
		game.moveUnit(h, makeCoordinate(8, 8), makeCoordinate(6, 6));
		assertEquals(makeCoordinate(6, 6), game.whereIsUnit("Heth", CONFEDERATE));
	}

	@Test
	public void stackedEntryUnitIsAsCorrectLocation() {
		testGame.setGameTurn(8);
		testGame.setGameStep(CBATTLE);
		game.endStep();
		assertEquals(makeCoordinate(22, 22), game.whereIsUnit("Geary", UNION));
	}

	@Test
	public void devinMovesNonStraight() {
		GbgUnit d = findUnit("Devin", makeCoordinate(13, 9));
		game.moveUnit(d, makeCoordinate(13, 9), makeCoordinate(14, 7));
		assertEquals(makeCoordinate(14, 7), game.whereIsUnit("Devin", UNION));
	}

	@Test
	public void stackedEntryUnitsNotMovedAreEliminated() {
		testGame.setGameTurn(8);
		testGame.setGameStep(CBATTLE);
		game.endStep();
		game.endStep();
		assertNull(game.getUnitsAt(makeCoordinate(22, 22)));
	}

	@Test
	public void allStackedUnitsAtStartOfGameMove()
	{
		Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(8, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(9, 28));
		Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertTrue(remaining == null || remaining.isEmpty());
	}

	@Test
	public void someEntryUnitsRemainAndAreRemoved()
	{
		Iterator<GbgUnit> units = game.getUnitsAt(makeCoordinate(7, 28)).iterator();
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(5, 28));
		game.moveUnit(units.next(), makeCoordinate(7, 28), makeCoordinate(6, 28));
		Collection<GbgUnit> remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertEquals(2, remaining.size());
		game.endStep();
		remaining = game.getUnitsAt(makeCoordinate(7, 28));
		assertTrue(remaining == null || remaining.isEmpty());
	}

	@Test(expected=GbgInvalidMoveException.class)
	public void tryToMoveThroughEnemyZOC() {
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 10, 10, SOUTH);
		testGame.putUnitAt(hampton, 13, 10, SOUTH);
		testGame.putUnitAt(devin, 11, 12, SOUTH);
		testGame.setGameStep(UMOVE);
		game.moveUnit(devin, makeCoordinate(11, 12), makeCoordinate(11, 9));
	}

	@Test
	public void hethDefeatsDevin()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.endStep();		// CBATTLE
		setBattleResults(DELIM);
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		assertEquals(BattleResult.DELIM, game.resolveBattle(battle).getBattleResult());
	}

	@Test
	public void hethDefeatsDevinAndGamble()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.putUnitAt(gamble, 6, 7, SOUTH);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.endStep();		// CBATTLE
		setBattleResults(DELIM);
		BattleDescriptor battle = game.getBattlesToResolve().iterator().next();	// Only 1
		assertEquals(BattleResult.DELIM, game.resolveBattle(battle).getBattleResult());
	}

	@Test
	public void battleWithoutAskingForBattles()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.putUnitAt(gamble, 6, 7, SOUTH);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.endStep();		// CBATTLE
		setBattleResults(DELIM);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(heth), collectUnits(gamble, devin));
		assertEquals(BattleResult.DELIM, testGame.resolveBattle(battle).getBattleResult());
	}

	@Test
	public void twoBattles()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(devin, 5, 7, SOUTH);
		testGame.putUnitAt(rodes, 20, 20, NORTH);
		testGame.putUnitAt(schurz, 20, 18, SOUTHWEST);
		testGame.setGameStep(CMOVE);
		game.moveUnit(heth, makeCoordinate(5,5), makeCoordinate(5, 6));
		game.moveUnit(rodes, makeCoordinate(20, 20), makeCoordinate(20, 19));
		game.endStep();		// CBATTLE
		Collection<BattleDescriptor> battles = game.getBattlesToResolve();
		BattleDescriptor battle = battles.iterator().next();
		if (battles.size() == 1) {
			assertTrue(battle.getAttackers().contains(heth));
		} else {
			assertTrue(battle.getAttackers().contains(heth) || battle.getAttackers().contains(rodes));
		}
	}

	@Test
	public void fightTwoBattles()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(rowley, 5, 7, NORTH);
		testGame.putUnitAt(hampton, 18, 18, EAST);
		testGame.putUnitAt(gamble, 20, 18, WEST);
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(rowley);
		bd.addDefender(heth);
		TestBattleDescriptor bd1 = new TestBattleDescriptor();
		bd1.addAttacker(gamble);
		bd1.addDefender(hampton);
		game.moveUnit(rowley, makeCoordinate(5, 7), makeCoordinate(5, 6));
		game.moveUnit(gamble, makeCoordinate(20, 18), makeCoordinate(19, 18));
		game.endStep();	// CBATTLE
		setBattleResults(EXCHANGE, EXCHANGE);
		assertEquals(BattleResult.EXCHANGE, testGame.resolveBattle(bd).getBattleResult());
		assertEquals(BattleResult.EXCHANGE, testGame.resolveBattle(bd1).getBattleResult());
	}

	@Test(expected=GbgInvalidActionException.class)
	public void unitTriesToFightTwoBattesInSameTurn()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(hampton, 7, 5, SOUTH);
		testGame.putUnitAt(schurz, 6, 7, NORTH);
		game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
		game.endStep();	// UBATTLE
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(schurz);
		bd.addDefender(heth);
		TestBattleDescriptor bd1 = new TestBattleDescriptor();
		bd1.addAttacker(schurz);
		bd1.addDefender(hampton);
		game.resolveBattle(bd);
		game.resolveBattle(bd1);
	}

	@Test(expected=Exception.class)
	public void notAllUnitsHaveFoughtAtEndOfBattleStep()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 5, 5, SOUTH);
		testGame.putUnitAt(hampton, 7, 5, SOUTH);
		testGame.putUnitAt(schurz, 6, 7, NORTH);
		game.moveUnit(schurz, makeCoordinate(6, 7), makeCoordinate(6, 6));
		game.endStep();	// UBATTLE
		TestBattleDescriptor bd = new TestBattleDescriptor();
		bd.addAttacker(schurz);
		bd.addDefender(heth);
		game.resolveBattle(bd);
		game.endStep(); 	// CMOVE: hampton did not engage
	}

	// Version 3 specific tests
	@Test(expected=GbgInvalidMoveException.class)
	public void unitDoesNotStopWhenEnemyIsInItsZOC()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(gamble,  10, 10, EAST);
		testGame.putUnitAt(heth, 12, 9, NORTH);
		testGame.putUnitAt(pender, 12, 11, SOUTH);
		testGame.putUnitAt(rodes, 12, 8, NORTH);
		testGame.putUnitAt(hampton, 12, 12, SOUTH);
		game.moveUnit(gamble, makeCoordinate(10, 10), makeCoordinate(13, 10));
	}

	@Test
	public void battleWhenPuttingEnemyInMoverZOC()
	{	
		game.setUnitFacing(gamble, WEST);
		game.moveUnit(gamble, makeCoordinate(11, 11), makeCoordinate(9, 9));
		game.endStep();
		Collection<BattleDescriptor> battles = game.getBattlesToResolve();
		assertEquals(1, battles.size());
		BattleDescriptor battle = battles.iterator().next();
		BattleResolution resolution = game.resolveBattle(battle);
		BattleResult result = resolution.getBattleResult();
		assertTrue(result == AELIM || result == ABACK);
	}

	@Test
	public void exchangeExactNumber()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.setGameStep(UMOVE);
		testGame.putUnitAt(heth, 8, 8, EAST);
		testGame.putUnitAt(dance, 8, 7, EAST);
		testGame.putUnitAt(hampton, 8, 9, EAST);
		testGame.putUnitAt(rowley, 10, 8, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(10, 8), makeCoordinate(9, 8));
		game.endStep();	// UBATTLE
		setBattleResults(EXCHANGE);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(heth, dance, hampton));
		BattleResolution resolution = testGame.resolveBattle(battle);
		assertTrue(resolution.getActiveConfederateUnits().contains(heth));
		assertEquals(1, resolution.getActiveConfederateUnits().size());
		assertEquals(2, resolution.getEliminatedConfederateUnits().size());
		assertEquals(1, resolution.getEliminatedUnionUnits().size());
		assertNull(game.getUnitsAt(makeCoordinate(10, 8)));
		assertNull(game.getUnitsAt(makeCoordinate(8, 9)));
	}

	@Test
	public void exactlyOneBattle()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 8, 8, EAST);
		testGame.putUnitAt(dance, 8, 7, EAST);
		testGame.putUnitAt(hampton, 8, 9, EAST);
		testGame.putUnitAt(rowley, 10, 8, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(10, 8), makeCoordinate(9, 8));
		game.endStep();	// UBATTLE
		assertEquals(1, game.getBattlesToResolve().size());
	}

	@Test
	public void exchangeUnequalNumbers()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 8, 8, EAST);
		testGame.putUnitAt(hampton, 8, 9, EAST);
		testGame.putUnitAt(rowley, 10, 8, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(10, 8), makeCoordinate(9, 8));
		game.endStep();	// UBATTLE
		setBattleResults(EXCHANGE);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(heth, hampton));
		BattleResolution resolution = testGame.resolveBattle(battle);
		assertEquals("Hampton", resolution.getActiveConfederateUnits().iterator().next().getLeader());
	}

	@Test
	public void exchangeAllUnits()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(hampton, 8, 9, EAST);
		testGame.putUnitAt(rowley, 10, 8, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(10, 8), makeCoordinate(9, 8));
		game.endStep();	// UBATTLE
		setBattleResults(EXCHANGE);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(hampton));
		BattleResolution resolution = testGame.resolveBattle(battle);
		assertNull(game.getUnitsAt(makeCoordinate(8,9)));
		assertNull(game.getUnitsAt(makeCoordinate(9, 8)));
	}

	@Test(expected=GbgInvalidActionException.class)
	public void attemptToBattleWithNonFightingUnit()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 20, 8, EAST);
		testGame.putUnitAt(hampton, 8, 9, EAST);
		testGame.putUnitAt(rowley, 10, 8, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(10, 8), makeCoordinate(9, 8));
		game.endStep();	// UBATTLE
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(heth, hampton));
		game.resolveBattle(battle);
	}

	@Test
	public void attackerBack()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 3, 4, SOUTH);
		testGame.putUnitAt(dance, 3, 6, NORTH);
		testGame.putUnitAt(rodes, 6, 3, SOUTH);
		testGame.putUnitAt(hampton, 6, 7, NORTH);
		testGame.putUnitAt(rowley, 5, 5, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(5, 5), makeCoordinate(4, 5));
		game.endStep();
		setBattleResults(ABACK);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(heth, dance));
		BattleResolution resolution = testGame.resolveBattle(battle);
		assertEquals(ABACK, resolution.getBattleResult());
		assertEquals(rowley, game.getUnitsAt(makeCoordinate(5, 5)).iterator().next());
	}

	@Test
	public void attackerEliminatedCannotRetreat()
	{
		testGame.clearBoard();
		testGame.setGameTurn(2);
		testGame.putUnitAt(heth, 3, 4, SOUTH);
		testGame.putUnitAt(dance, 3, 6, NORTH);
		testGame.putUnitAt(rodes, 6, 4, SOUTH);
		testGame.putUnitAt(hampton, 6, 6, NORTH);
		testGame.putUnitAt(rowley, 5, 5, WEST);
		testGame.setGameStep(UMOVE);
		game.moveUnit(rowley, makeCoordinate(5, 5), makeCoordinate(4, 5));
		game.endStep();
		setBattleResults(ABACK);
		BattleDescriptor battle = makeBattleDescriptor(collectUnits(rowley), collectUnits(heth, dance));
		BattleResolution resolution = testGame.resolveBattle(battle);
		assertTrue(resolution.getEliminatedUnionUnits().contains(rowley));
	}

	// Helper methods
	private GbgUnit findUnit(String leader, Coordinate c)
	{
		for (GbgUnit unit : game.getUnitsAt(c)) {
			if (unit.getLeader().equals(leader)) return unit;
		}
		return null;
	}

	private void isAttacker(GbgUnit unit)
	{
		Iterator<BattleDescriptor> battles = game.getBattlesToResolve().iterator();
		while (battles.hasNext()) {
			BattleDescriptor bd = battles.next();
			if (bd.getAttackers().contains(unit)) {
				assertTrue(true);
				return;
			}
		}
		fail();
	}

	private void isDefender(GbgUnit unit)
	{
		Iterator<BattleDescriptor> battles = game.getBattlesToResolve().iterator();
		while (battles.hasNext()) {
			BattleDescriptor bd = battles.next();
			if (bd.getDefenders().contains(unit)) {
				assertTrue(true);
				return;
			}
		}
		fail();
	}

	private void setBattleResults(BattleResult... results)
	{
		List<BattleResult> theList = new ArrayList<BattleResult>();
		for (BattleResult r : results) theList.add(r);
		testGame.setBattleResults(theList);
	}

	private Collection<GbgUnit> collectUnits(GbgUnit... units)
	{
		Collection<GbgUnit> results = new ArrayList<GbgUnit>();
		for (GbgUnit u : units) results.add(u);
		return results;
	}
}
