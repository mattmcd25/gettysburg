My goal with the design was to have GettysburgEngine be a true Facade, in the sense that it is purely a
public interface for the client. No actual computations are done in the GettysburgEngine; instead, it 
delegates all actions to the subsystems as necessary. This is a pattern I followed all throughout my code,
aiming to have the logic done only in the class that actually contains the information in true object-
oriented style. This led to what I believe is a very clean design, where it is very evident what each
function does. 


Overview of added classes and packages:
engine.common:
	The main addition here is the BattleResolver class. A new BattleResolver is created whenever
	a battle needs to be resolved. This handles the selection of which battle result to return,
	and knows how to act to actually put these results into effect on the board.
	Other additions include helper functions to CoordinateImpl similar to those created for the
	HexCoordinate project, and concrete implementations of BattleDescriptor and BattleResolution.

engine.state:
	Used to manage the state of the game. the "GbgGameState" class is effectively a combination
	of GameStatus, GameStep, and the current turn. It is used as a state pattern to delegate
	responsibility based on the current game step. In this way, almost all conditional logic for
	steps is removed, and instead each state knows how to handle itself. This means that move steps
	know to throw exceptions when resolve battle is closed, and battle steps know how to resolve the
	battle.
	GbgGameBoard is used to store the board, and perform all actions related to units. This involves
	moving, placing, removing, turning, battles, and zones of control. 

engine.validation:
	Used to validate actions performed in other places in the application. BattleValidator, 
	MoveValidator, and TurnValidator are all Functional Interfaces generated in the ValidatorFactory.
	These are made use of in the game states, in resolveBattle, moveUnit, and setUnitFacing, 
	respectively. The Path and Pathfinder classes are used to find a valid path during moveUnit
	validation.


Design Patterns Used:
	Creational:
		Factory: Coordinates, BattleDescriptors, Units, Pathfinders, GameStates
		Object Pool: Storing and reusing Coordinates so they are each only built once
		Singleton: Creating a single instance of game engine that is accessible anywhere
	Structural:
		Facade: GettysburgEngine is a facade that simply delegates responsibility to subsystems
	Behavioral:
		Iterator: Built in to Java collections
		State: Game states based on GameStep so they can handle themselves
		Strategy: To allow expansion of pathfinding algorithms	
		Template: To allow game states to slightly modify base functionality with hooks	
Maybe others?