package za.co.entelect.challenge;

import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.BuildingType;
import za.co.entelect.challenge.enums.PlayerType;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.ArrayList;

import static za.co.entelect.challenge.enums.BuildingType.ATTACK;
import static za.co.entelect.challenge.enums.BuildingType.DEFENSE;
import static za.co.entelect.challenge.enums.BuildingType.ENERGY;

public class Bot {
    private static final String NOTHING_COMMAND = "";
    private GameState gameState;
    private GameDetails gameDetails;
    private int gameWidth;
    private int gameHeight;
    private Player myself;
    private Player opponent;
    private List<Building> buildings;
    private List<Missile> missiles;

    /**
     * Constructor
     *
     * @param gameState the game state
     **/
    public Bot(GameState gameState) {
        this.gameState = gameState;
        gameDetails = gameState.getGameDetails();
        gameWidth = gameDetails.mapWidth;
        gameHeight = gameDetails.mapHeight;
        myself = gameState.getPlayers().stream().filter(p -> p.playerType == PlayerType.A).findFirst().get();
        opponent = gameState.getPlayers().stream().filter(p -> p.playerType == PlayerType.B).findFirst().get();

        buildings = gameState.getGameMap().stream()
                .flatMap(c -> c.getBuildings().stream())
                .collect(Collectors.toList());

        missiles = gameState.getGameMap().stream()
                .flatMap(c -> c.getMissiles().stream())
                .collect(Collectors.toList());
    }

    /**
     * Run
     *
     * @return the result
     public String run() {
         if (isUnderAttack()) {
             return defendRow();
            } else if (hasEnoughEnergyForMostExpensiveBuilding()) {
                return buildRandom();
            } else {
                return greedyAttack();
            }
        }
    **/
        
    /**
     * Run
     *
     * @return the result
     **/
    public String run() {
        if (!energyColumnComplete()) {
            return completeEnergyBuilding();
        } else if (isUnderAttack()) {
            return defendRow();
        } else {
            return greedyAttack();
        }
    }
    
    private List<Integer> checkLane(Player p){
        Predicate<Building> isAttack = b -> b.buildingType == ATTACK;
        Predicate<Building> isAlsoDef = b -> b.buildingType == DEFENSE;
        Predicate<Building> isEnergy = b -> b.buildingType == ENERGY;
        Predicate<Building> all = isAttack.or(isAlsoDef).or(isEnergy);
        
        List<Integer> holder = new ArrayList<Integer>();
        
        for(int i = 0; i < gameHeight; i++){
            int hold = getAllBuildingsForPlayerRow(p.playerType, all, i).size();
            holder.add(hold);
        }
        
        return holder;
    }
    
    private boolean energyColumnComplete() {
        Predicate<Building> isEnergy = b -> b.buildingType == ENERGY;
        return getAllBuildingsForPlayerColumn(myself.playerType,isEnergy, 0).size() == 5;
    }
    
    private String completeEnergyBuilding() {
        for (int i=0; i < 5; i++) {
            if (isCellEmpty(0, i) && canAffordBuilding(ENERGY))
                return placeBuildingInRowFromBack(ENERGY, i);
        } 
        return "";
    }
    
    private boolean enemyOpening() {
        List<Integer> enemyData = checkLane(opponent);
        boolean open = false;
        int i = 0;
        while (!open && i < enemyData.size()) {
            if (enemyData.get(i) == 0) {
                open = true;
            }
            i++;
        }
        return open;
    }


    private String greedyAttack(){
        List<Integer> enemyData = checkLane(opponent);
        List<Integer> myData = checkLane(myself);
        int thinnest = 0;

        for(int i = 0; i < enemyData.size(); i++){  
            if ((enemyData.get(i) < enemyData.get(thinnest)) && (myData.get(i) < 8)){
                    thinnest = i;
                }
        }
        if (canAffordBuilding(ATTACK)) {
            return placeBuildingInRowFromBack(ATTACK, thinnest);
        }
        else {
            return doNothingCommand();
        } 
    }

    private boolean isCellEmpty(int x, int y) {
        Optional<CellStateContainer> cellOptional = gameState.getGameMap().stream()
                .filter(c -> c.x == x && c.y == y)
                .findFirst();

        if (cellOptional.isPresent()) {
            CellStateContainer cell = cellOptional.get();
            return cell.getBuildings().size() <= 0;
        } else {
            System.out.println("Invalid cell selected");
        }
        return true;
    }

    private String placeBuildingInRowFromBack(BuildingType buildingType, int y) {
        for (int i = 0; i < gameWidth / 2; i++) {
            if (isCellEmpty(i, y)) {
                return buildingType.buildCommand(i, y);
            }
        }
        return "";
    }

    /**
     * Build random building
     *
     * @return the result
     **/
    private String buildRandom() {
        List<CellStateContainer> emptyCells = gameState.getGameMap().stream()
                .filter(c -> c.getBuildings().size() == 0 && c.x < (gameWidth / 2))
                .collect(Collectors.toList());

        if (emptyCells.isEmpty()) {
            return doNothingCommand();
        }

        CellStateContainer randomEmptyCell = getRandomElementOfList(emptyCells);
        BuildingType randomBuildingType = getRandomElementOfList(Arrays.asList(BuildingType.values()));

        if (!canAffordBuilding(randomBuildingType)) {
            return doNothingCommand();
        }

        return randomBuildingType.buildCommand(randomEmptyCell.x, randomEmptyCell.y);
    }

    /**
     * Has enough energy for most expensive building
     *
     * @return the result
     **/
    private boolean hasEnoughEnergyForMostExpensiveBuilding() {
        return gameDetails.buildingsStats.values().stream()
                .filter(b -> b.price <= myself.energy)
                .toArray()
                .length == 3;
    }



    /**
     * Defend row
     *
     * @return the result
     **/
    private String defendRow() {
        for (int i = 0; i < gameHeight; i++) {
            int opponentAttacking = getAllBuildingsForPlayerRow(PlayerType.B, b -> b.buildingType == ATTACK, i).size();
            int myDefense = getAllBuildingsForPlayerRow(PlayerType.A, building -> building.buildingType == DEFENSE, i).size();
            if ((opponentAttacking >= 2) && canAffordBuilding(DEFENSE) && (myDefense <= 1)) {
                return placeBuildingInRowFromFront(DEFENSE, i);
            }
        }
        return "";
    }

    /**
     * Place building in row y nearest to the front
     *
     * @param buildingType the building type
     * @param y            the y
     * @return the result
     **/
    private String placeBuildingInRowFromFront(BuildingType buildingType, int y) {
        for (int i = (gameWidth / 2) - 1; i >= 0; i--) {
            if (isCellEmpty(i, y)) {
                return buildingType.buildCommand(i, y);
            }
        }
        return "";
    }

    /**
     * Checks if this is under attack
     *
     * @return true if this is under attack
     **/
    private boolean isUnderAttack() {
        //if enemy has two or more attack buildings on a single row
        for (int i = 0; i < gameHeight; i++) {
            int opponentAttacks = getAllBuildingsForPlayerRow(PlayerType.B, building -> building.buildingType == ATTACK, i).size();
            int myDefense = getAllBuildingsForPlayerRow(PlayerType.A, building -> building.buildingType == DEFENSE, i).size();
            if ((myDefense == 0) && (opponentAttacks >= 2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Do nothing command
     *
     * @return the result
     **/
    private String doNothingCommand() {
        return NOTHING_COMMAND;
    }

    /**
     * Place building in row
     *
     * @param buildingType the building type
     * @param y            the y
     * @return the result
     **/
    private String placeBuildingInRow(BuildingType buildingType, int y) {
        List<CellStateContainer> emptyCells = gameState.getGameMap().stream()
                .filter(c -> c.getBuildings().isEmpty()
                        && c.y == y
                        && c.x < (gameWidth / 2) - 1)
                .collect(Collectors.toList());

        if (emptyCells.isEmpty()) {
            return buildRandom();
        }

        CellStateContainer randomEmptyCell = getRandomElementOfList(emptyCells);
        return buildingType.buildCommand(randomEmptyCell.x, randomEmptyCell.y);
    }

    /**
     * Get random element of list
     *
     * @param list the list < t >
     * @return the result
     **/
    private <T> T getRandomElementOfList(List<T> list) {
        return list.get((new Random()).nextInt(list.size()));
    }

    private boolean getAnyBuildingsForPlayer(PlayerType playerType, Predicate<Building> filter, int y) {
        return buildings.stream()
                .filter(b -> b.getPlayerType() == playerType
                        && b.getY() == y)
                .anyMatch(filter);
    }

    private List<Building> getAllBuildingsForPlayerRow(PlayerType playerType, Predicate<Building> filter, int y) {
        return gameState.getGameMap().stream()
                .filter(c -> c.cellOwner == playerType && c.y == y)
                .flatMap(c -> c.getBuildings().stream())
                .filter(filter)
                .collect(Collectors.toList());
    }

    private List<Building> getAllBuildingsForPlayerColumn(PlayerType playerType, Predicate<Building> filter, int x) {
        return gameState.getGameMap().stream()
                .filter(c -> c.cellOwner == playerType && c.x == x)
                .flatMap(c -> c.getBuildings().stream())
                .filter(filter)
                .collect(Collectors.toList());
    }

    // private boolean extensiveBuildingChecker(PlayerType playerType, Predicate<Building> filter, int x, int y) {
    //     return buildings.stream()
    //             .filter(b -> b.getPlayerType() == playerType
    //                     && b.getX() == x && b.getY() == y)
    //             .anyMatch(filter);
    // }

    /**
     * Can afford building
     *
     * @param buildingType the building type
     * @return the result
     **/
    private boolean canAffordBuilding(BuildingType buildingType) {
        return myself.energy >= gameDetails.buildingsStats.get(buildingType).price;
    }
}