import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("SELAMAT DATANG DI RUSH HOUR PUZZLE SOLVER!");
        System.out.println("==========================================");
        System.out.println();

        State goal = null;
        Scanner scanner = new Scanner(System.in);
        String filePath;

        while (true) {
            System.out.print("Masukkan path test case(.txt): ");
            filePath = scanner.nextLine();
            System.out.println("");
            if (filePath.isEmpty()) {
                System.out.println("Path tidak boleh kosong! Coba lagi.");
                continue;
            }
            if (!Reader.readInputFile(filePath)) {
                System.out.println("Gagal membaca file test case atau file tidak ada.\n");
                continue;
            } else {
                break;
            }
        }
        
        System.out.println("==========================================");
        System.out.println("Pilih algoritma pencarian:");
        System.out.println("1. Greedy Best First Search");
        System.out.println("2. Uniform Cost Search (UCS)");
        System.out.println("3. A*");
        System.out.println("Masukkan algoritma yang ingin digunakan: ");
        String algorithmMethod;
        while (true) {
            algorithmMethod = scanner.nextLine();
            if (algorithmMethod.isEmpty()) {
                System.out.println("Pilihan algoritma tidak boleh kosong! Coba lagi.");
                continue;
            }
            if (!algorithmMethod.equals("1") && !algorithmMethod.equals("2") && !algorithmMethod.equals("3")) {
                System.out.println("Algoritma tidak valid.\n");
                continue;
            } else {
                break;
            }         
        }

        int heuristicMethod = 0;
        System.out.println("");
        System.out.println("==========================================");
        if (algorithmMethod.equals("1") || algorithmMethod.equals("2")) {
            System.out.println("Pilih Heuristik:");
            System.out.println("1. Manhattan Distance");
            System.out.println("2. Blocking Vehicles");
            
        while (true) {
            try {
                heuristicMethod = scanner.nextInt(); // Read integer input
                scanner.nextLine(); // Consume the leftover newline character
                if (heuristicMethod != 1 && heuristicMethod != 2) {
                    System.out.println("Heuristik tidak valid.\n");
                    continue;
                } else {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Input tidak valid! Masukkan angka 1 atau 2.\n");
                scanner.nextLine(); // Clear invalid input
            }
        }

        long startTime = System.currentTimeMillis();
        if (algorithmMethod.equals("1")) {
            System.out.println("Menggunakan algoritma Greedy Best First Search.\n");
            goal = Algorithm.greedy(Reader.getInitialState(), Reader.rows, Reader.cols, heuristicMethod);
        } else if (algorithmMethod.equals("2")) {
            System.out.println("Menggunakan algoritma Uniform Cost Search (UCS).\n");
            goal = Algorithm.ucs(Reader.getInitialState(), Reader.rows, Reader.cols);
        } else {
            System.out.println("Menggunakan algoritma A*.\n");
            goal = Algorithm.aStar(Reader.getInitialState(), Reader.rows, Reader.cols, heuristicMethod);
        }
        if (goal != null) {
            System.out.println("Solution found!");
            printSolutionPath(goal);
        } else {
            System.out.println("No solution found.");
        }

        long endTime = System.currentTimeMillis();

        // Output
        System.out.println("Banyak gerakan yang diperiksa: " + Algorithm.getNodesVisited() +"\n");
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms\n");

        if (goal != null) {
            // Prompt menyimpan hasil
            System.out.println("Apakah anda ingin menyimpan hasilnya? (ya/tidak)");
            String save_solusi = scanner.nextLine();
            if (save_solusi.equals("ya")) {
                System.out.println("Masukkan nama file untuk menyimpan hasil: ");
                String output_filename = scanner.nextLine();
                outputFile(goal, "test/solution/" + output_filename + ".txt");
                System.out.println("");
                System.out.println("Solusi berhasil disimpan pada test/solution!");
            }
            System.out.println();
        }
        scanner.close();
    }
    }
    public static void printSolutionPath(State state) {
        Stack<State> path = new Stack<>();
        while (state != null) {
            path.push(state);
            state = state.getParent();
        }

        int step = 1;
        while (!path.isEmpty()) {
            State s = path.pop();
            if (s.getParent() != null) {
                System.out.print("Gerakan " + step + ": ");
                System.out.println(s.getMovedPiece() + " - " + s.getMovedPieceDirection());
                step++;
            } else {
                System.out.println("Papan awal: ");
            }
            System.out.println(s.getBoard(Reader.rows, Reader.cols));
        }
    }

    public static void outputFile(State state, String filenames){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filenames))) {
            Stack<State> path = new Stack<>();
            while (state != null) {
                path.push(state);
                state = state.getParent();
            }

            int step = 1;
            while (!path.isEmpty()) {
                State s = path.pop();
                if (s.getParent() != null) {
                    writer.write("Gerakan " + step + ": ");
                    writer.write(s.getMovedPiece() + " - " + s.getMovedPieceDirection());
                    writer.newLine();
                    step++;
                } else {
                    writer.write("Papan awal: ");
                    writer.newLine();
                }
                
                writer.write(s.getBoardPlain(Reader.rows, Reader.cols));
                writer.newLine();
            }
        } catch (IOException e){
            System.out.println("error!");
            e.printStackTrace();
        }
    }
}

