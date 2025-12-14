package TicTacToe;

import java.io.*;
import java.util.*;

public class DatasetLoader {

    public static class Sample {
        public int[] features;  // 6 features
        public int label;       // +1 or -1
    }

    public static List<Sample> loadDataset(String csvPath) throws Exception {
        List<Sample> data = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(csvPath));
        String line;
        boolean firstRow = true;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // skip header
            if (firstRow) {
                firstRow = false;
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length < 7) continue;

            int label = Integer.parseInt(parts[0].trim());
            int f6 = Integer.parseInt(parts[1].trim());
            int f5 = Integer.parseInt(parts[2].trim());
            int f4 = Integer.parseInt(parts[3].trim());
            int f3 = Integer.parseInt(parts[4].trim());
            int f2 = Integer.parseInt(parts[5].trim());
            int f1 = Integer.parseInt(parts[6].trim());

            Sample s = new Sample();
            s.label = label;
            // put in order: [f1, f2, f3, f4, f5, f6]
            s.features = new int[]{ f1, f2, f3, f4, f5, f6 };

            data.add(s);
        }

        br.close();
        return data;
    }
}
