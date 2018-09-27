package student.gettysburg.engine.validation;

import gettysburg.common.Direction;
import gettysburg.common.GbgUnit;

@FunctionalInterface
public interface TurnValidator {
	/** Perform a validation check on the unit facing to change **/ 
    void validate(GbgUnit unit, Direction d);
}