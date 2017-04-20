package treemaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by lick on 08.04.17.
 */
public class CustomFieldParts {
    private String name;
    private int comparisonCount;
    private List<Double> ratios = new ArrayList<>();

    public CustomFieldParts(CustomTextField ctf) {
        String text = ctf.getText();
        String[] textParts = text.split(":");

        name = textParts[0].trim();

        Scanner scanner = new Scanner(textParts[1].trim());
        scanner.useDelimiter(" ");

        while (scanner.hasNext()) {
            String num = scanner.next();
            ratios.add(Double.parseDouble(num.trim()));
        }

        comparisonCount = (1+(int)Math.sqrt(1+8*ratios.size()))/2;
    }

    public String getName() {
        return name;
    }

    public int getComparisonCount() {
        return comparisonCount;
    }

    public List<Double> getRatios() {
        return ratios;
    }

    @Override
    public String toString() {
        return "CustomFieldParts{" +
                "name='" + name + '\'' +
                ", comparisonCount=" + comparisonCount +
                ", ratios=" + ratios +
                '}';
    }
}
