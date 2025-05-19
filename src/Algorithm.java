import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Algorithm {
    private static int nodesVisited = 0;

    public static int getNodesVisited() {
        return nodesVisited;
    }

    // Greedy Best First Search
    // hanya menggunakan h(n)
    public static State greedy(State initial, int rows, int cols, int heuristic) {
        PriorityQueue<State> queue;
        if (heuristic == 1) {
            queue = new PriorityQueue<>(Comparator.comparingInt(Algorithm::manhattanDistance));
        } else if (heuristic == 2) {
            queue = new PriorityQueue<>(Comparator.comparingInt(Algorithm::blockingVehicles));
        } else {
            throw new IllegalArgumentException("Invalid heuristic type");
        }
        Set<String> visited = new HashSet<>();

        queue.add(initial);
        nodesVisited = 0;

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());
            nodesVisited++;

            if (current.isGoal()) {
                return current;
            }

            for (State next : current.generateNextStates(rows, cols)) {
                if (!visited.contains(next.toString())) {
                    queue.add(next);
                }
            }
        }

        return null;
    }

    // Uniform Cost Search
    // hanya menggunakan g(n)
    public static State ucs(State initial, int rows, int cols) {
        PriorityQueue<State> frontier = new PriorityQueue<>(Comparator.comparingInt(State::getCost));
        Set<String> visited = new HashSet<>();

        frontier.add(initial); // inisialisasi dengan state awal
        nodesVisited = 0;

        while (!frontier.isEmpty()) {
            State current = frontier.poll();

            if (current.isGoal()) {
                return current;
            }

            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());
            nodesVisited++;

            for (State neighbor : current.generateNextStates(rows, cols)) {
                if (!visited.contains(neighbor.toString())) {
                    frontier.add(neighbor); // tambahkan tetangga ke queue
                }
            }
        }

        return null;
    }

    // A* Search
    // menggunakan f(n) = g(n) + h(n)
    public static State aStar(State initial, int rows, int cols, int heuristic) {
        PriorityQueue<State> queue;
        if (heuristic == 1) {
            // Use Manhattan Distance as the heuristic
            queue = new PriorityQueue<>(Comparator.comparingInt(state -> state.getCost() + manhattanDistance(state)));
        } else if (heuristic == 2) {
            // Use Blocking Vehicles Heuristic as the heuristic
            queue = new PriorityQueue<>(Comparator.comparingInt(state -> state.getCost() + blockingVehicles(state)));
        } else {
            throw new IllegalArgumentException("Invalid heuristic type");
        }
        Set<String> visited = new HashSet<>();

        queue.add(initial);
        nodesVisited = 0;

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());
            nodesVisited++;

            if (current.isGoal()) {
                return current;
            }

            for (State next : current.generateNextStates(rows, cols)) {
                if (!visited.contains(next.toString())) {
                    queue.add(next);
                }
            }
        }

        return null;
    }

    // Heuristic functions
    private static int manhattanDistance(State state) {
        Piece primary = state.getPieces().get(0);
        
        // Right exit
        if (Reader.hasRightExit()) {
            int endCol = primary.getCol() + primary.getLength() - 1;
            int rowDistance = Math.abs(primary.getRow() - Reader.getExitRow());
            int colDistance = (state.getCols() - 1) - endCol;
            return rowDistance + colDistance;
        } 
        // Left exit
        else if (Reader.hasLeftExit()) {
            int rowDistance = Math.abs(primary.getRow() - Reader.getExitRow());
            int colDistance = primary.getCol();
            return rowDistance + colDistance;
        }
        // Top exit
        else if (Reader.hasTopExit()) {
            int rowDistance = primary.getRow();
            int colDistance = Math.abs(primary.getCol() - Reader.getExitCol());
            return rowDistance + colDistance;
        }
        // Bottom exit
        else if (Reader.hasBottomExit()) {
            int endRow = primary.getRow() + (primary.isHorizontal() ? 0 : primary.getLength() - 1);
            int rowDistance = (state.getRows() - 1) - endRow;
            int colDistance = Math.abs(primary.getCol() - Reader.getExitCol());
            return rowDistance + colDistance;
        }
        return 0;
    }

    private static int blockingVehicles(State state) {
        List<Piece> pieces = state.getPieces();
        Piece primary = pieces.get(0);
        int blockingCount = 0;
        int distanceComponent = 0;
        
        // For right exit
        if (Reader.hasRightExit()) {
            int primaryRow = primary.getRow();
            int endCol = primary.getCol() + primary.getLength() - 1;
            
            // Count vehicles blocking the path between the primary piece and exit
            for (int i = 1; i < pieces.size(); i++) {
                Piece p = pieces.get(i);
                // Check if piece is in the way horizontally
                if (p.getRow() == primaryRow && p.getCol() > endCol) {
                    blockingCount++;
                }
            }
            
            // Add distance component
            distanceComponent = Math.abs(primaryRow - Reader.getExitRow()) + 
                            ((state.getCols() - 1) - endCol);
        } 
        // For left exit
        else if (Reader.hasLeftExit()) {
            int primaryRow = primary.getRow();
            int startCol = primary.getCol();
            
            // Count vehicles blocking the path to the left
            for (int i = 1; i < pieces.size(); i++) {
                Piece p = pieces.get(i);
                // Check if piece is in the way horizontally
                if (p.getRow() == primaryRow && p.getCol() < startCol) {
                    blockingCount++;
                }
            }
            
            // Add distance component
            distanceComponent = Math.abs(primaryRow - Reader.getExitRow()) + startCol;
        }
        // For top exit
        else if (Reader.hasTopExit()) {
            int primaryCol = primary.getCol();
            int startRow = primary.getRow();
            
            // Count vehicles blocking the path upward
            for (int i = 1; i < pieces.size(); i++) {
                Piece p = pieces.get(i);
                // Check if piece is in the way vertically
                if (p.getCol() == primaryCol && p.getRow() < startRow) {
                    blockingCount++;
                }
            }
            
            // Add distance component
            distanceComponent = Math.abs(primaryCol - Reader.getExitCol()) + startRow;
        }
        // For bottom exit
        else if (Reader.hasBottomExit()) {
            int primaryCol = primary.getCol();
            int endRow = primary.getRow() + (primary.isHorizontal() ? 0 : primary.getLength() - 1);
            
            // Count vehicles blocking the path downward
            for (int i = 1; i < pieces.size(); i++) {
                Piece p = pieces.get(i);
                // Check if piece is in the way vertically
                if (p.getCol() == primaryCol && p.getRow() > endRow) {
                    blockingCount++;
                }
            }
            
            // Add distance component
            distanceComponent = Math.abs(primaryCol - Reader.getExitCol()) + 
                            ((state.getRows() - 1) - endRow);
        }
        
        // Weight the blocking count more heavily than distance
        return (blockingCount * 2) + distanceComponent;
    }
}
