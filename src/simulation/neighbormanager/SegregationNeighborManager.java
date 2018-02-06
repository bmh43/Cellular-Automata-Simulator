package simulation.neighbormanager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import simulation.cell.*;
import simulation.grid.*;


/**
 * Manages neighbors of current segregation grid 
 * 
 * @author Katherine Van Dyk
 *
 */
public class SegregationNeighborManager extends NeighborManager {

	private int VACANT = 2; 

	/**
	 * Returns a random vacant cell
	 * 
	 * @param neighbors: Arraylist of all cell's neighbors 
	 * @return
	 */
	public SegregationCell findVacantCell(Grid g) {
		ArrayList<SegregationCell> vacant = new ArrayList<SegregationCell>();
		Random rand = new Random();
		for(Cell[] row : g.getCells()) {
			for(Cell cell : row) {
				SegregationCell sCell = (SegregationCell) cell;
				if(!sCell.getMove() && sCell.getState() == VACANT) vacant.add(sCell);
			}
		}
		if(vacant.size() > 0) return vacant.get(rand.nextInt(vacant.size()));
		else return null;
	}

	/**
	 * Returns all neighbors of Cell c
	 * 
	 * @param c
	 * @param g
	 * @return
	 */
	private Cell[] getNeighbors(Cell c, Grid g) {
		ArrayList<Cell> neighbors = new ArrayList<Cell>();
		ArrayList<Cell> agents = new ArrayList<Cell>();
		neighbors.addAll(Arrays.asList(NSEWCells(c , g)));
		neighbors.addAll(Arrays.asList(diagonalCells(c ,g)));
		for(Cell neighbor: neighbors) {
			if(neighbor.getState() != VACANT) {
				agents.add(neighbor);
			}
		}
		return agents.toArray(new Cell[agents.size()]);
	}

	/**
	 * Returns number of cells in same group
	 * 
	 * @param neighbors
	 * @param c
	 * @return
	 */
	private int neighborCount(Cell c, Grid g) {
		Cell[] neighbors = getNeighbors(c, g);
		int count = 0;
		for(Cell neighbor : neighbors) {
			if(neighbor.getState() == c.getState()) count++;
		}
		return count;
	}

	/**
	 * Returns satisfaction level of cell object
	 * 
	 * @param c: cell object whose satisfaction is being evaluated
	 * @param g: current grid state
	 * @return double representing cell satisfaction
	 */
	public double getNeighborSatisfaction(Cell c, Grid g) {
		return (double) neighborCount(c, g) / getNeighbors(c, g).length;
	}

}