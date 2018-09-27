package student.gettysburg.engine.validation;

import java.util.HashMap;
import java.util.LinkedList;

import gettysburg.common.GbgUnit;
import student.gettysburg.engine.common.CoordinateImpl;

public interface Pathfinder {
	/** 
	 * Find the shortest path for the corresponding unit from point A to B 
	 */ 
	public Path findPath(GbgUnit unit, CoordinateImpl from, CoordinateImpl to);
	
	/** 
	 * Return a Pathfinder instance of the specified type 
	 */
	public static Pathfinder getPathfinder(String type) {
		switch(type) {
			case "AStar": return new AStarPathfinder();
			default: return null;
		}
	}
	
	/** 
	 * Construct the path based on the HashMap of edges explored 
	 */
	default Path constructPath(HashMap<CoordinateImpl, CoordinateImpl> cameFrom, CoordinateImpl end) {
        Path p = new Path();
        p.buildPath(end);
        CoordinateImpl current = end;

        while(cameFrom.keySet().contains(current)){
            current = cameFrom.get(current);
            p.buildPath(current);
        }
        return p;
    }
}

class AStarPathfinder implements Pathfinder {

	/**
	 * Find the shortest path using the A* algorithm.
	 * Based off pseudocode found online.
	 * Originally written by me from my Software Engineering project, and modified slightly.
	 */
	@Override
	public Path findPath(GbgUnit unit, CoordinateImpl start, CoordinateImpl goal) {
        LinkedList<CoordinateImpl> closedSet;
        LinkedList<CoordinateImpl> openSet;
        HashMap<CoordinateImpl, CoordinateImpl> cameFrom;
        HashMap<CoordinateImpl, Double> gScore;
        HashMap<CoordinateImpl, Double> fScore;

        closedSet = new LinkedList<>();
        openSet = new LinkedList<>();
        openSet.add(start);

        fScore = new HashMap<>();
        fScore.put(start, heuristicCost(start, goal));

        gScore = new HashMap<>();
        gScore.put(start, 0.0);

        cameFrom = new HashMap<>();

        while(!openSet.isEmpty()){
        	CoordinateImpl current = openSet.getFirst();
            for (CoordinateImpl n : openSet) {
                if (fScore.get(n) < fScore.get(current)) {
                    current = n;
                }
            }

            if(current.equals(goal)){
            	return constructPath(cameFrom, current);
            }
            openSet.remove(current);
            closedSet.add(current);

            for(CoordinateImpl n: current.getAllowedNeighbors(unit, goal)){
                if(closedSet.contains(n)) continue;
                double score = gScore.get(current) + heuristicCost(current, n);
                if(!openSet.contains(n)) openSet.add(n);
                else if(score >= gScore.get(n)) continue;

                cameFrom.put(n, current);
                gScore.put(n, score);
                fScore.put(n, score + heuristicCost(n, goal));
            }
        }
        return null;
	}
	
	/**
	 * Calculate the heuristic distance cost between two points 
	 */
    private double heuristicCost(CoordinateImpl start, CoordinateImpl goal){
       return Math.abs(start.getX() - goal.getX())
               + Math.abs(start.getY() - goal.getY());
    }
}