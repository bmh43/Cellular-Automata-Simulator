package simulation.ruleSet;
import neighbormanager.WaTorNeighborManager;
import simulation.cell.*;
import simulation.grid.Grid;

/**
 * RULES:
 * Fish: move randomly to free neighboring cells
 * Once breed time is up, new fish is born in free neighboring cell
 * Shark: move randomly to neighboring cells that are free/occupied by shark
 * If fish, fish is eaten and energy increases, shark loses energy w/ every 
 * time step, dies if it reaches 0
 * 
 * @param fishBreedTime
 * @param sharkBreedTime
 * @param sharkInitEnergy
 * @param fishEnergy
 * @author Katherine Van Dyk
 * @author Ben Hodgson
 */
public class WaTorRuleset implements Ruleset {
	private int FISH = 0;
	private int SHARK = 1; 
	private int VACANT = 2;
	private Grid GRID;
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
	 * Sets grid to current grid 
	 * 
	 * @param g: Current simulation grid
	 */
	@Override
	public void setGrid(Grid g) {
		GRID = g;
	}

	/**
	 * Processes all cells in current grid
	 */
	@Override
	public void processCells() {
		for(Cell[] row : (Cell[][]) GRID.getCells()) {
			for(Cell cell : row) {
				WaTorCell wCell = (WaTorCell) cell;
				if(wCell.getState() == FISH) {
					wCell.incrementBreedingTime();
					checkBreedingTime(wCell);
					moveFish(wCell);
				}
				else if(wCell.getState() == SHARK) {
					wCell.decrementEnergy();
					if(checkEnergy(wCell) == VACANT) wCell.setState(VACANT);
					else moveShark(wCell);
				}
				else if(!wCell.getMove()) {
					wCell.setState(wCell.getState());
				}
			}
		}
		cleanMove();
		updateStates();
	}

	/**
	 * Moves fish cell on grid
	 * 
	 * @param fish
	 */
	private void moveFish(WaTorCell fish) {
		WaTorCell freeNeighbor = NEIGHBOR_MANAGER.vacantNeighbor(fish, GRID);
		if(freeNeighbor == null) {
			fish.setState(fish.getState());
			return;
		}
		else {
			swapCells(fish, freeNeighbor);
		}
	}

	/**
	 * Moves shark cell on grid
	 * 
	 * @param shark
	 */
	private void moveShark(WaTorCell shark) {
		WaTorCell freeNeighbor = NEIGHBOR_MANAGER.vacantOrFishNeighbor(shark, GRID);
		if(freeNeighbor == null) {
			shark.setState(shark.getState());
			return;
		}
		else if(freeNeighbor.getState() == FISH) {
			shark.incrementEnergy();
			freeNeighbor.setState(VACANT);
		}
		swapCells(shark, freeNeighbor);
	}

	/**
	 * Checks energy of shark cell
	 * 
	 * @param shark
	 */
	private int checkEnergy(WaTorCell shark) {
		int E = shark.getEnergy();
		if(E > SHARK_BREEDENERGY) {
			giveBirth(shark);
		}
		else if(E <= 0) {
			return VACANT;
		}
		return SHARK;
	}

	/**
	 * Checks if fish should give birth
	 * 
	 * @param fish
	 */
	private void checkBreedingTime(WaTorCell fish) {
		if(fish.getBreedingTime() > FISH_BREEDTIME) {
			giveBirth(fish);
		}
	}

	/**
	 * Method that lets cell c give birth
	 * 
	 * @param cell: Fish cell giving birth
	 */
	private void giveBirth(WaTorCell cell) {
		WaTorCell baby = NEIGHBOR_MANAGER.vacantNeighbor(cell, GRID);
		if(baby != null) {
			baby.setState(VACANT);
			baby.reset();
			baby.setState(cell.getState());
			baby.setMove(true);
			GRID.addCell(baby);
		}
	}

	/**
	 * Changes all setMoves() to false (so that cells don't overwrite each other)
	 */
	private void cleanMove() {
		for(int r = 0; r < GRID.getXSize(); r++) {
			for(int c = 0; c < GRID.getYSize(); c++) {
			//	System.out.println("X , Y"+ r + " " + c);
			//	System.out.println((((WaTorCell) GRID.getCell(r,c)).getNextState()));
				((WaTorCell) GRID.getCell(r,c)).setMove(false);
			}
		}
	}
	
	/**
	 * Updates cell states all at once
	 */
	public void updateStates() {
		for(int r = 0; r < GRID.getXSize(); r++) {
			for(int c = 0; c < GRID.getYSize(); c++) {
				WaTorCell cell = (WaTorCell) GRID.getCell(r, c);
				cell.updateState();
			}
		}
	}

	/**
	 * Swaps attributes of any two cells
	 * @param a
	 * @param b
	 */
	private void swapCells(WaTorCell a, WaTorCell b) {
		// Switch state
		int aState = a.getState();		
		a.setState(b.getState());
		b.setState(aState);
	
		// Switch energy
		int aEnergy = a.getEnergy();
		a.setEnergy(b.getEnergy());
		b.setEnergy(aEnergy);
		
		// Switch breeding time 
		int aTime = a.getBreedingTime();
		a.setBreedingTime(b.getBreedingTime());
		b.setBreedingTime(aTime);
		
		// Set to move
		a.setMove(true);
		b.setMove(true);
		if(a.getState() == SHARK) {
			System.out.println("Shark coords " + b.getX() + " " + b.getY());
		}
	
		GRID.addCell(a);
		GRID.addCell(b);
	}

}