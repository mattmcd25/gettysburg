package student.gettysburg.engine.validation;

import gettysburg.common.BattleDescriptor;

@FunctionalInterface
public interface BattleValidator {
	/** Perform a validation check on the battle to resolve **/ 
    void validate(BattleDescriptor bd);
}