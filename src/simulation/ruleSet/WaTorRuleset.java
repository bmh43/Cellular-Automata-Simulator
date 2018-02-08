package simulation.ruleSet;
import simulation.cell.*;
import simulation.neighbormanager.WaTorNeighborManager;

/**
 * WaTor Predator-Prey Simulation
 * 
 * @param fishBreedTime
 * @param sharkBreedTime
 * @param sharkInitEnergy
 * @param fishEnergy
 * @author Katherine Van Dyk
 * @author Ben Hodgson
 */
public class WaTorRuleset extends Ruleset {
	private int FISH = 0;
	private int SHARK = 1; 
	private int VACANT = 2;
	private int FISH_BREEDTIME;
	private int SHARK_BREEDENERGY;
	private WaTorNeighborManager NEIGHBOR_MANAGER = new WaTorNeighborManager();

	/**
	 * Constructor that sets simulation parameters
	 * 
	 * @param fishBreedTime
	 * @param sharkBreedEnergy
	 */
	public WaTorRuleset(int fishBreedTime, int sharkBreedEnergy) {
		this.FISH_BREEDTIME = fishBreedTime;
		this.SHARK_BREEDENERGY = sharkBreedEnergy;
		this.NEIGHBOR_MANAGER = new WaTorNeighborManager();
	}

	/**
	 * Returns next state of all cells that need to be processed
	 * 
	 * @param: c, cell to be processed
	 */
	@Override
	public int processCell(Cell c) {
		WaTorCell wCell = (WaTorCell) c;
		if(wCell.getState() == SHARK) {
			if(checkEnergy(wCell)) {		
				wCell.reset();
				return VACANT;
			}
			else {
				return moveShark(wCell);
			}
		}
		else if(wCell.getState() == FISH) {
			if(checkBreedingTime(wCell)) {
				wCell.reset();
				return FISH;
			}
			else { 
				return moveFish(wCell);
			}
		}
		else {
			return wCell.getState();
		}
	}

	/**
	 * Moves fish cell on grid
	 * 
	 * @param fish
	 */
	private int moveFish(WaTorCell fish) {
		fish.incrementBreedingTime();
		WaTorCell freeNeighbor = NEIGHBOR_MANAGER.vacantNeighbor(fish, GRID);
		if(freeNeighbor != null) {
			swapCells(fish, freeNeighbor);
			freeNeighbor.setState(FISH);
			freeNeighbor.setMove(true);
			return VACANT;
		}
		return FISH;
	} 

	/**
	 * Moves shark cell on grid
	 * 
	 * @param shark
	 */
	private int moveShark(WaTorCell shark) {
		shark.decrementEnergy();
		WaTorCell freeNeighbor = NEIGHBOR_MANAGER.vacantOrFishNeighbor(shark, GRID);
		if(freeNeighbor == null) {
			return SHARK;
		}
		else if(freeNeighbor.getState() == FISH) {
			shark.incrementEnergy();
		}
		swapCells(shark, freeNeighbor);
		freeNeighbor.setState(SHARK);
		freeNeighbor.setMove(true);
		return VACANT;
	}

	/**
	 * Checks energy of shark cell
	 * 
	 * @param shark
	 */
	private boolean checkEnergy(WaTorCell shark) {
		int E = shark.getEnergy();
		if(E > SHARK_BREEDENERGY) {
			giveBirth(shark);
			return true;
		}
		else if(E <= 0) {
			kill(shark);
			return true;
		}
		return false;
	}

	/**
	 * Kills specific cell
	 * 
	 * @param shark
	 */
	private void kill(WaTorCell cell) {
		cell.reset();
		cell.setState(VACANT);
		cell.setMove(true);
	}

	/**
	 * Checks if fish should give birth
	 * 
	 * @param fish
	 */
	private boolean checkBreedingTime(WaTorCell fish) {
		if(fish.getBreedingTime() > FISH_BREEDTIME) {
			giveBirth(fish);
			return true;
		}
		return false;
	}

	/**
	 * Method that lets cell c give birth
	 * 
	 * @param cell: Fish cell giving birth
	 */
	private void giveBirth(WaTorCell cell) {
		WaTorCell baby = NEIGHBOR_MANAGER.vacantNeighbor(cell, GRID);
		if(baby != null) {
			baby.reset();
			baby.setState(cell.getState());
			baby.setMove(true);
		}
	}

	/**
	 * Swaps attributes of any two cells
	 * @param a
	 * @param b
	 */
	private void swapCells(WaTorCell a, WaTorCell b) {
		int aEnergy = a.getEnergy();
		a.setEnergy(b.getEnergy());
		b.setEnergy(aEnergy);

		int aTime = a.getBreedingTime();
		a.setBreedingTime(b.getBreedingTime());
		b.setBreedingTime(aTime);
	}
}
