import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

/**
 * @author abhanshu
 * This class is a template for implementation of
 * HW1 for CS540 section 2
 */
/**
 * Data structure to store each node.
 */
class Location {
    private int x;
    private int y;
    private Location parent;

    public Location(int x, int y, Location parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Location getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Location) {
            Location loc = (Location) obj;
            return loc.x == x && loc.y == y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + x);
        hash = 31 * (hash + y);
        return hash;
    }
}

public class KingsKnightmare {
    //represents the map/board
    private static boolean[][] board;
    //represents the goal node
    private static Location king;
    //represents the start node
    private static Location knight;
    //y dimension of board
    private static int n;
    //x dimension of the board
    private static int m;
    //enum defining different algo types
    enum SearchAlgo{
        BFS, DFS, ASTAR;
    }

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            //loads the input file and populates the data variables
            SearchAlgo algo = loadFile(args[0]);

            if (algo != null) {
                switch (algo) {
                    case DFS :
                        executeDFS();
                        break;
                    case BFS :
                        executeBFS();
                        break;
                    case ASTAR :
                        executeAStar();
                        break;
                    default :
                        break;
                }
            }
        }
    }

    /**
     * Implementation of Astar algorithm for the problem
     */
    private static void executeAStar() {
        //TODO: Implement A* algorithm in this method
        // All path costs = 3, h = manhattan distance

        // Arraylist to store actual expanded nodes
        ArrayList<Location> expanded = new ArrayList<Location>();

        // Make a stack for the frontier
        PriorityQ<Location> frontier = new PriorityQ<>();

        // Send start node to recursive helper
        aStarHelper(knight, expanded, frontier);

    }
    private static void aStarHelper(Location curr, ArrayList<Location> expanded, PriorityQ<Location> frontier) {

        // If curr is a goal, stop
        if(curr.getX() == king.getX() && curr.getY() == king.getY()){
            ArrayList<Location> toPrint = new ArrayList<>();
            Location temp = curr;
            while(temp != null){
                toPrint.add(temp);
                temp = temp.getParent();
            }
            for(int a = toPrint.size() -1; a >= 0; a--){
                System.out.println(toPrint.get(a));
            }
            System.out.println("Expanded Nodes: " + expanded.size());
            return;
        }
        else{
            // Mark the current node as expanded in expanded set
            expanded.add(new Location(curr.getX(), curr.getY(), curr.getParent()));
        }

        // Add all children to arraylist
        ArrayList<Location> children = new ArrayList<>();
        children.add(new Location(curr.getX() + 2, curr.getY() + 1, curr)); // Child 1
        children.add(new Location(curr.getX() + 1, curr.getY() + 2, curr)); // Child 2
        children.add(new Location(curr.getX() - 1, curr.getY() + 2, curr)); // Child 3
        children.add(new Location(curr.getX() - 2, curr.getY() + 1, curr)); // Child 4
        children.add(new Location(curr.getX() - 2, curr.getY() - 1, curr)); // Child 5
        children.add(new Location(curr.getX() - 1, curr.getY() - 2, curr)); // Child 6
        children.add(new Location(curr.getX() + 1, curr.getY() - 2, curr));  // Child 7
        children.add(new Location(curr.getX() + 2, curr.getY() - 1, curr)); // Child 8

        // For each child
        for (int i = 0; i < children.size(); i++) {

            Location child = children.get(i);
            int x = child.getX();
            int y = child.getY();
            boolean valid = false;
            int depth = 0;
            int score = 0;

            Location temp = child;
            while (temp.getParent() != null) {
                depth++;
                temp = temp.getParent();
            }

            // Compute its score
            int gn = 3 * depth; // g(n) (3 * depth of node)
            int hn = Math.abs(x - king.getX()) + Math.abs(y - king.getY()); // h(n)
            score = gn + hn; // f(n)

            // If on the board and not an obstacle
            if (x >= 0 && x < m && y >= 0 && y < n && !board[y][x]) {
                // Check if in expanded
                boolean inExpanded = false;
                for (int j = 0; j < expanded.size(); j++) {
                    if ((expanded.get(j).getX() == x && expanded.get(j).getY() == y)) {
                        inExpanded = true;
                        break;
                    }
                }
                // If not in expanded, check frontier
                if (!inExpanded) {
                    if (frontier.exists(child)) {
                        // If lower score than one in frontier, delete old one and valid = true
                        if(score < frontier.getPriorityScore(child)){
                            frontier.remove(child);
                            valid = true;
                        }
                    }
                    // If not in frontier, valid is true
                    else {
                        valid = true;
                    }
                }
            }
            // If it's valid, add to frontier
            if (valid){
                frontier.add(child, score);
            }
        }
        // If frontier still has nodes and no goal found, keep going
        if(!frontier.isEmpty()){
            aStarHelper(frontier.poll().getKey(), expanded, frontier);
        }
        // Otherwise count up expanded nodes and stop
        else{
            System.out.println("NOT REACHABLE");
            System.out.println("Expanded nodes: " + expanded.size());
        }
    }

    /**
     * Implementation of BFS algorithm
     */
    private static void executeBFS(){
        //TODO: Implement bfs algorithm in this method
    	
    	// Arraylist to store actual expanded nodes
        ArrayList<Location> expanded = new ArrayList<Location>();

        // Make a stack for the frontier
        Queue<Location> frontier = new LinkedList<>();

        // Send start node to recursive helper
        bfsHelper(knight, expanded, frontier);
    }
    
    private static void bfsHelper(Location curr, ArrayList<Location> expanded, Queue<Location> frontier) {
    	
        // Mark the current node as expanded in expanded set
        expanded.add(new Location(curr.getX(), curr.getY(), curr.getParent()));

        // Add all children to arraylist
        ArrayList<Location> children = new ArrayList<>();
        children.add(new Location(curr.getX() + 2, curr.getY() + 1, curr)); // Child 1
        children.add(new Location(curr.getX() + 1, curr.getY() + 2, curr)); // Child 2
        children.add(new Location(curr.getX() - 1, curr.getY() + 2, curr)); // Child 3
        children.add(new Location(curr.getX() - 2, curr.getY() + 1, curr)); // Child 4
        children.add(new Location(curr.getX() - 2, curr.getY() - 1, curr)); // Child 5
        children.add(new Location(curr.getX() - 1, curr.getY() - 2, curr)); // Child 6
        children.add(new Location(curr.getX() + 1, curr.getY() -2, curr));  // Child 7
        children.add(new Location(curr.getX() + 2, curr.getY() - 1, curr)); // Child 8

        // For each child, if it's legal, add to frontier
        for(int i = 0; i < children.size(); i++){

            Location child = children.get(i);
            int x = child.getX();
            int y = child.getY();
            boolean valid = true;

            // If child is a goal, stop
            if(x == king.getX() && y == king.getY()){
                ArrayList<Location> toPrint = new ArrayList<>();
                Location temp = child;
                while(temp != null){
                    toPrint.add(temp);
                    temp = temp.getParent();
                }
                for(int a = toPrint.size() -1; a >= 0; a--){
                    System.out.println(toPrint.get(a));
                }
            	System.out.println("Expanded Nodes: " + expanded.size());
                return;
            }
            
            // If on the board
            if(x >= 0 && x < m && y >= 0 && y < n) {
            	boolean a = frontier.contains(child) || board[y][x]; // True if in frontier OR an obstacle
            	boolean b = false;  // Becomes true if in expanded
            	for(int j = 0; j < expanded.size(); j++) {
            		if((expanded.get(j).getX() == x && expanded.get(j).getY() == y)) {
            			b = true;
            			break;
            		}
            	}
            	valid = !(a || b); // If in frontier or obstacle or in expanded, make false
            }
            // If not on board, make false
            else {
            	valid = false;
            }
            
            // If it's valid, push it to frontier
            if(valid) {
                frontier.add(child);
            }
        }
        // If frontier still has nodes and no goal found, keep going
        if(!frontier.isEmpty()){
            bfsHelper(frontier.remove(), expanded, frontier);
        }
        // Otherwise count up expanded nodes and stop
        else{
            System.out.println("NOT REACHABLE");
            System.out.println("Expanded nodes: " + expanded.size());
        }
    }

    /**
     * Implementation of DFS algorithm
     */
    private static void executeDFS() {
        //TODO: Implement dfs algorithm in this method
        
        // Arraylist to store actual expanded nodes
        ArrayList<Location> expanded = new ArrayList<Location>();

        // Make a stack for the frontier
        Stack<Location> frontier = new Stack<>();

        // Send start node to recursive helper
        dfsHelper(knight, expanded, frontier);

    }
    // Recursive helper
    private static void dfsHelper(Location curr, ArrayList<Location> expanded, Stack<Location> frontier){

        // Mark the current node as expanded in expanded set
        expanded.add(new Location(curr.getX(), curr.getY(), curr.getParent()));

        // Add all children to arraylist
        ArrayList<Location> children = new ArrayList<>();
        children.add(new Location(curr.getX() + 2, curr.getY() + 1, curr)); // Child 1
        children.add(new Location(curr.getX() + 1, curr.getY() + 2, curr)); // Child 2
        children.add(new Location(curr.getX() - 1, curr.getY() + 2, curr)); // Child 3
        children.add(new Location(curr.getX() - 2, curr.getY() + 1, curr)); // Child 4
        children.add(new Location(curr.getX() - 2, curr.getY() - 1, curr)); // Child 5
        children.add(new Location(curr.getX() - 1, curr.getY() - 2, curr)); // Child 6
        children.add(new Location(curr.getX() + 1, curr.getY() -2, curr));  // Child 7
        children.add(new Location(curr.getX() + 2, curr.getY() - 1, curr)); // Child 8

        // For each child, if it's legal, add to frontier
        for(int i = 0; i < children.size(); i++){

            Location child = children.get(i);
            int x = child.getX();
            int y = child.getY();
            boolean valid = true;

            // If child is a goal, stop
            if(x == king.getX() && y == king.getY()){
                ArrayList<Location> toPrint = new ArrayList<>();
                Location temp = child;
                while(temp != null){
                    toPrint.add(temp);
                    temp = temp.getParent();
                }
                for(int a = toPrint.size() -1; a >= 0; a--){
                    System.out.println(toPrint.get(a));
                }
            	System.out.println("Expanded Nodes: " + expanded.size());
                return;
            }
            
            
            // If on the board
            if(x >= 0 && x < m && y >= 0 && y < n) {
            	boolean a = frontier.contains(child) || board[y][x]; // True if in frontier OR an obstacle
            	boolean b = false;  // Becomes true if in expanded
            	for(int j = 0; j < expanded.size(); j++) {
            		if((expanded.get(j).getX() == x && expanded.get(j).getY() == y)) {
            			b = true;
            			break;
            		}
            	}
            	valid = !(a || b); // If in frontier or obstacle or in expanded, make false
            }
            // If not on board, make false
            else {
            	valid = false;
            }
            
            // If it's valid, push it to frontier
            if(valid) {
                frontier.push(child);
            }
        }
        // If frontier still has nodes and no goal found, keep going
        if(!frontier.isEmpty()){
            dfsHelper(frontier.pop(), expanded, frontier);
        }
        // Otherwise count up expanded nodes and stop
        else{
            System.out.println("NOT REACHABLE");
            System.out.println("Expanded nodes: " + expanded.size());
        }
    }


    /**
     *
     * @param filename
     * @return Algo type
     * This method reads the input file and populates all the
     * data variables for further processing
     */
    private static SearchAlgo loadFile(String filename) {
        File file = new File(filename);
        try {
            Scanner sc = new Scanner(file);
            SearchAlgo algo = SearchAlgo.valueOf(sc.nextLine().trim().toUpperCase());
            n = sc.nextInt();
            m = sc.nextInt();
            sc.nextLine();
            board = new boolean[n][m];
            for (int i = 0; i < n; i++) {
                String line = sc.nextLine();
                for (int j = 0; j < m; j++) {
                    if (line.charAt(j) == '1') {
                        board[i][j] = true;
                    } else if (line.charAt(j) == 'S') {
                        knight = new Location(j, i, null);
                    } else if (line.charAt(j) == 'G') {
                        king = new Location(j, i, null);
                    }
                }
            }
            sc.close();
            return algo;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
