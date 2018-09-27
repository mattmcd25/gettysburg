package student.gettysburg.engine.validation;

import gettysburg.common.GbgUnit;
import student.gettysburg.engine.common.CoordinateImpl;

@FunctionalInterface
public interface MoveValidator {
	/** Perform a validation check on the unit movement request **/ 
    void validate(GbgUnit unit, CoordinateImpl from, CoordinateImpl to);
}