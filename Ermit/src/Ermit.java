import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.pow;

public class Ermit {
    private final String file;

    public Ermit(String file) {
        this.file = file;
        if (new File(file).exists())
            EventQueue.invokeLater(this::display);
        else
            System.out.println("file not found");
    }


    private void display() {
        JFrame f = new JFrame("Ermit Spline Frame");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ChartPanel chartPanel = new ChartPanel(null);
        chartPanel.setPreferredSize(new Dimension(640, 480));
        f.add(chartPanel);
        try {
            chartPanel.setChart(createResult());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private JFreeChart createResult() throws IOException {
        Double[] yf = readSignal(file);
        Double[] xf = sizeToList(yf);
        Double[] xv = arange(xf);
        Double[] ermit_x = arange(xf);
        Double[] ermit_y = Arrays.copyOfRange(ermitSpline(yf, xf, xv), 0, ermit_x.length);

        MatlabChart fig = new MatlabChart();
        fig.plot(ermit_x, ermit_y, "b", 1.5f, "Ermit");
        fig.plot(xf, yf, "r", 1.5f, "Signal");
        fig.RenderPlot();
        fig.title("Ermit spline");
        fig.xlabel("X");
        fig.ylabel("Y");
        fig.grid("on", "on");
        fig.legend("northwest");
        fig.font("Helvetica", 15);
//        fig.saveas("MyPlot.jpeg", 1366, 768);

        return fig.chart;
    }

    private Double[] arange(Double[] value) {
        ArrayList<Double> result = new ArrayList<>();
        for (int x = 1; x < value.length - 2; x++)
            for (int i = 1; i < 100; i++)
                result.add((value[x] + (i * 0.01)));
        return result.toArray(new Double[0]);
    }

    private Double[] ermitSpline(Double[] yf, Double[] xf, Double[] xv) {
        ArrayList<Double> result = new ArrayList<>();
        for (int j = 1; j < (xf.length - 2); j++) {
            for (Double aDouble : xv) {
                if (aDouble >= xf[j] && aDouble <= xf[j + 1]) {
                    double t = (aDouble - xf[j]) / (xf[j + 1] - xf[j]);
                    double f1 = (pow(1 - t, 2) * (1 + (2 * t)));
                    double f2 = pow(t, 2) * (3 - (2 * t));
                    double f3 = t * pow((1 - t), 2);
                    double f4 = -pow(t, 2) * (1 - t);

                    result.add(f1 * yf[j] + f2 * yf[j + 1] + f3 * (yf[j + 1] - yf[j]) + f4 * (yf[j + 2] - yf[j + 1]));
                }
            }
        }
        return result.toArray(new Double[0]);
    }

    private Double[] readSignal(String file) throws IOException {
        return Files.readAllLines(Paths.get(file)).stream().map(Double::parseDouble).toArray(Double[]::new);
    }

    private Double[] sizeToList(Double[] f) {
        Double[] r = new Double[f.length];
        for (int i = 0; i < f.length; i++)
            r[i] = (double) (i + 1);
        return r;
    }

}
