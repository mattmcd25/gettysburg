package student.gettysburg.engine.validation;

import java.util.LinkedList;

import student.gettysburg.engine.common.CoordinateImpl;

public class Path {
    private final LinkedList<CoordinateImpl> path = new LinkedList<CoordinateImpl>();

    /** 
     * Builds the path in reverse order, last to first. 
     */
    public void buildPath(CoordinateImpl n){
        path.addFirst(n);
    }

    /**
     * @return the distance in terms of movement factor
     */
    public int getMoveDistance() {
    	return this.path.size() - 1;
    }
    
//    /**
//     * Convert path to string, for verification purposes
//     */
//    public String toString() {
//        StringBuilder str = new StringBuilder("Path: ");
//        for (CoordinateImpl n : path) {
//            str.append(n).append(", ");
//        }
//        return str.toString();
//    }

//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof Path) {
//            Path other = (Path) o;
//            if(path.size() != other.path.size()) {
//                return false;
//            }
//            for (int i = 0; i < path.size(); i++) {
//                if(!path.get(i).equals(other.path.get(i))) {
//                    return false;
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
}