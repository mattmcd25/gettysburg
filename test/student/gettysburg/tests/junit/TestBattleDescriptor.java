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

package student.gettysburg.tests.junit;

import java.util.*;
import gettysburg.common.*;

/**
 * BattleDescriptor for testing
 * @version Oct 4, 2017
 */
class TestBattleDescriptor implements BattleDescriptor
{
	private final Collection<GbgUnit> attackers;
	private final Collection<GbgUnit> defenders;
	
	public TestBattleDescriptor()
	{
		attackers = new ArrayList<GbgUnit>();
		defenders = new ArrayList<GbgUnit>();
	}
	
	public void addAttacker(GbgUnit unit) 
	{
		attackers.add(unit);
	}
	
	public void addDefender(GbgUnit unit)
	{
		defenders.add(unit);
	}
	
	/*
	 * @see gettysburg.common.BattleDescriptor#getAttackers()
	 */
	@Override
	public Collection<GbgUnit> getAttackers()
	{
		return attackers;
	}

	/*
	 * @see gettysburg.common.BattleDescriptor#getDefenders()
	 */
	@Override
	public Collection<GbgUnit> getDefenders()
	{
		return defenders;
	}

}
