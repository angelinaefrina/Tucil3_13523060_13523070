import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Algorithm {
    private static int nodesVisited = 0;

    public static int getNodesVisited() {
        return nodesVisited;
    }

    // Greedy Best First Search
    // hanya menggunakan h(n)
    public static State greedy(State initial, int rows, int cols) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(Algorithm::heuristic));
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
    public static State aStar(State initial, int rows, int cols) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::getTotalCost));
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

    // Heuristic function
    private static int heuristic(State state) {
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
}
