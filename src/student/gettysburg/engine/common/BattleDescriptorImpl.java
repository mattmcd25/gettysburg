/*******************************************************************************
 * This files was developed for CS4233: Object-Oriented Analysis & Design.
 * The course was taken at Worcester Polytechnic Institute.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Copyright ©2016-2017 Gary F. Pollice
 *******************************************************************************/
package student.gettysburg.engine.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import gettysburg.common.BattleDescriptor;
import gettysburg.common.GbgUnit;

/**
 * Implementation of the BattleDescriptor interface. There is als
 * a factory method that creates a battle unit. The constructor and
 * is up to the implementor and additional methods may be added as
 * necessary for the student's design needs.
 * 
 * @version Jul 27, 2017
 */
public class BattleDescriptorImpl implements BattleDescriptor
{
	// Save the attackers and defenders as sets, so there are no duplicates
	private Set<GbgUnit> attackers;
	private Set<GbgUnit> defenders;
	
	private BattleDescriptorImpl(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
		this.attackers = new HashSet<GbgUnit>(attackers != null ? attackers : Arrays.asList());
		this.defenders = new HashSet<GbgUnit>(defenders != null ? defenders : Arrays.asList());
	}
	
	public static BattleDescriptorImpl makeBattleDescriptorImpl(Collection<GbgUnit> attackers, Collection<GbgUnit> defenders) {
		return new BattleDescriptorImpl(attackers, defenders);
	}
	
	public static BattleDescriptorImpl makeBattleDescriptorImpl(BattleDescriptor bd) {
		return makeBattleDescriptorImpl(bd.getAttackers(), bd.getDefenders());
	}
	
	/*
	 * @see gettysburg.common.BattleDescriptor#getAttackers()
	 */
	@Override
	public Collection<GbgUnit> getAttackers()
	{
		return this.attackers;
	}

	/*
	 * @see gettysburg.common.BattleDescriptor#getDefenders()
	 */
	@Override
	public Collection<GbgUnit> getDefenders()
	{
		return this.defenders;
	}
	
	/**
	 * @return all Units involved in this Battle
	 */
	public Collection<GbgUnit> getAllUnits()
	{
		Collection<GbgUnit> result = new HashSet<GbgUnit>(this.attackers);
		result.addAll(this.defenders);
		return result;
	}
	
	/**
	 * Convert to string for testing.
	 */
	@Override
	public String toString() {
		return "Atk: " + this.attackers + " Def: " + this.defenders + "";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BattleDescriptorImpl other = (BattleDescriptorImpl) obj;
		if (attackers == null) {
			if (other.attackers != null)
				return false;
		} else if (!attackers.equals(other.attackers))
			return false;
		if (defenders == null) {
			if (other.defenders != null)
				return false;
		} else if (!defenders.equals(other.defenders))
			return false;
		return true;
	}
}
