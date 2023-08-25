import java.util.ArrayList;

public class studentAI extends Player {
    private int maxDepth;


    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    // Use alpha beta search to update the data member move.
    // Cutoff at maxDepth (will be no greater than 15)
    public void move(BoardState state) {
        move = alphabetaSearch(state, maxDepth);
    }

    // Board state for current player (max player)
    // Return the best move that leads to the state with maximum SBE value for current player
    // Return the move with the smallest index in case of tie
    // Value returned should be [0.5] with 0 being leftmost pit
    public int alphabetaSearch(BoardState state, int maxDepth) {

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int v = Integer.MIN_VALUE;
        int bestMove = 0;

        for(int i = 0; i < 6; i++){
            if(state.isLegalMove(1, i)){
                v = Math.max(v, minValue(state.applyMove(1,i), maxDepth, maxDepth - 1, alpha, beta));
            }
            if(v > alpha){
                alpha = v;
                bestMove = i;
            }
        }
        return bestMove;
    }

    // Searches for minimax value associated with best move for MAX player.
    public int maxValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {

        // Get the successors and current v value
        int v = Integer.MIN_VALUE;
        ArrayList<BoardState> successors = new ArrayList<>();

        // If max depth, return SBE of it
        if(currentDepth == 0){
            return sbe(state);
        }

        // Add legal successors
        for(int i = 0; i < 6; i++){
            if(state.isLegalMove(1, i)){
                successors.add(state.applyMove(1,i));
            }
        }

        // If no legal successors, return SBE
        if(successors.size() == 0){
            return sbe(state);
        }

        // For each of the successors, value = the max of calling minValue on them
        for(BoardState successor: successors){
            v = Math.max(v, minValue(successor, maxDepth, currentDepth - 1, alpha, beta));
            // If the current child's value is greater than beta, prune other children (don't check)
            if(v >= beta){
                return v;
            }
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    // Searches for the minimax value for the best move for the MIN player
    public int minValue(BoardState state, int maxDepth, int currentDepth, int alpha, int beta) {

        // Get the successors and current v value
        int v = Integer.MAX_VALUE;
        ArrayList<BoardState> successors = new ArrayList<>();

        // If max depth, return SBE of it
        if(currentDepth == 0){
            return sbe(state);
        }

        // Add legal successors
        for(int i = 0; i < 6; i++){
            if(state.isLegalMove(2, i)){
                successors.add(state.applyMove(2,i));
            }
        }

        // If no legal successors, return SBE
        if(successors.size() == 0){
            return sbe(state);
        }

        // For each of the successors, value = the max of calling minValue on them
        for(BoardState successor: successors){
            v = Math.min(v, maxValue(successor, maxDepth, currentDepth - 1, alpha, beta));
            // If the current child's value is greater than beta, prune other children (don't check)
            if(v <= alpha){
                return v;
            }
            beta = Math.min(beta, v);
        }
        return v;
    }

    // Return: number of stones in storehouse of current player - number of stones in opponent's storehouse (player 1 is current player)
    public int sbe(BoardState state){
    	return state.score[0] - state.score[1];
    }
}