package main;

import java.util.Comparator;
import javafx.util.Pair;

public class ValueComparator implements Comparator<Pair<Integer,Double>> {

  @Override
  public int compare(Pair<Integer,Double> o1, Pair<Integer,Double> o2) {
    return o1.getValue().compareTo(o2.getValue());
  }
}
