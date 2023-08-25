import java.io.File;
import java.util.*;

class Edge implements Comparable<Edge>{
    public int end;
    public int weight;

    public Edge(int end, int weight){
        this.end = end;
        this.weight = weight;
    }

    public int compareTo(Edge edge){
        return weight - edge.weight;
    }
}

public class Mice {

    public static ArrayList<Edge>[] graph;
    public static int numNodes;
    public static int goalNodeID;
    public static int maxTimeUnits;
    public static int numEdges;

    public static void main(String[] args) throws java.lang.Exception{
        try{
            Scanner input = new Scanner(new File("inputs/mice_input.txt"));

            numNodes = input.nextInt();
            goalNodeID = input.nextInt()-1;
            maxTimeUnits = input.nextInt();
            numEdges = input.nextInt();
            graph = new ArrayList[numNodes];

            // Each node has an arraylist of edges
            for(int i = 0; i < numNodes; i++){
                graph[i] = new ArrayList<>();
            }

            // Reverse the edges and add them
            for(int i = 0; i < numEdges; i++) {
                int startID = input.nextInt()-1;
                int endID = input.nextInt()-1;
                int weight = input.nextInt();
                graph[endID].add(new Edge(startID, weight));
            }

            System.out.println(mice(new int[numNodes], goalNodeID, maxTimeUnits));
            
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
    private static int mice(int[] distances, int start, int goal){

        // Keep track of whether a node has been visited or not
        int[] visited = new int[distances.length];
        for(int i = 0; i < visited.length; i++){
            visited[i] = 0;
        }

        // Initialize all distances to max
        for(int i = 0; i < distances.length; i++){
            distances[i] = Integer.MAX_VALUE;
        }

        // Start dijkstra's
        int miceMadeIt = 0;
        PriorityQueue<Edge> queue = new PriorityQueue<>();
        queue.offer(new Edge(start,0));

        while(!queue.isEmpty()){
            Edge curr = queue.poll();

            // Already visited
            if(visited[curr.end] == 1){
                continue;
            }
            // Otherwise mark it visited and its edge cost
            visited[curr.end] = 1;
            distances[curr.end] = curr.weight;

            // If is too far, stop
            if(distances[curr.end] > goal){
                break;
            }

            // Otherwise it's valid
            miceMadeIt++;
            for(Edge e : graph[curr.end]) {
                if (visited[e.end] == 0) {
                    queue.offer(new Edge(e.end, e.weight + curr.weight));
                }
            }
        }
        return miceMadeIt;
    }
}
