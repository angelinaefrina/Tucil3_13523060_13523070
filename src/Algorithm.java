import java.util.*;

public class Algorithm {

    public static State greedy(State initial, int rows, int cols) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(Algorithm::heuristic));
        Set<String> visited = new HashSet<>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());

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

    public static State ucs(State initial, int rows, int cols) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::getCost));
        Set<String> visited = new HashSet<>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());

            if (current.isGoal()) {
                return current;
            }

            for (State next : current.generateNextStates(rows, cols)) {
                if (!visited.contains(next.toString())) {
                    queue.add(next);
                }
            }
        }

        return null; // No solution found
    }

    public static State aStar(State initial, int rows, int cols) {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::getTotalCost));
        Set<String> visited = new HashSet<>();

        queue.add(initial);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            if (visited.contains(current.toString())) continue;
            visited.add(current.toString());

            if (current.isGoal()) {
                return current;
            }

            for (State next : current.generateNextStates(rows, cols)) {
                if (!visited.contains(next.toString())) {
                    queue.add(next);
                }
            }
        }

        return null; // No solution found
    }

    // Heuristic function
    private static int heuristic(State state) {
        Piece primary = state.getPieces().get(0);
        int endCol = primary.getCol() + primary.getLength() - 1;
        return state.getCols() - 1 - endCol;
    }
}
