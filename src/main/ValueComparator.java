package main;

import java.util.Comparator;
import javafx.util.Pair;

public class ValueComparator implements Comparator<Pair<Integer,Integer>> {

  @Override
  public int compare(Pair<Integer,Integer> o1, Pair<Integer,Integer> o2) {
    return o1.getValue().compareTo(o2.getValue());
  }
}
