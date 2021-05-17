package chrysostom.model.chart;

import chrysostom.model.entities.Anaphora;
import chrysostom.model.AnaphoraDictionary;
import chrysostom.model.Statistics;
import chrysostom.model.Text;

import java.util.List;

public class ChartFactory
{
    public static Chart createChart(AnaphoraDictionary dictionary, String text) {
        Chart chart = new Chart();
        for (String name : dictionary.getNames()) {
            Anaphora anaphora = dictionary.getByName(name);
            List<Double> coordinates = Statistics.getCoordinates(anaphora, new Text(text));
            chart.add(name, new DataRay(coordinates, anaphora.getColor()));
        }
        return chart;
    }
}
