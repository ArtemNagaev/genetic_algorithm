package controlllers;

import static java.lang.Math.abs;

import com.sun.jdi.Value;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.Pair;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import main.ValueComparator;


public class Controller implements Initializable {
  @FXML
  TextField function;
  @FXML
  TextField output;
  @FXML
  TextField population;
  @FXML
  ComboBox direction;
  @FXML
  TextField iterations;
  @FXML
  TextArea answer;
  @FXML
  TextField accuracy;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    direction.getItems().removeAll(direction.getItems());
    direction.getItems().addAll("min", "max");
    direction.getSelectionModel().select("min");
  }

  //функция генерации случайной хромосомы
  public String randomChromosome(int size){
    String chromosome ="";
    Random rand = new Random();
    for (int i=0; i<size;i++){
      chromosome+=rand.nextInt(2);
    }
    return chromosome;
  }

  //Подсчёт значения функции
  int f(String function, List<List<String>> population_list, int index ) throws ScriptException {
    //инициализация парсера
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");
    Object result;

    //создание строки функции с параметрами
    String tmpString = function;
    int count = 0;
    for (int i = 0; i < tmpString.length(); i++) {
      if (Character.isLetter(tmpString.charAt(i))) {
        tmpString = tmpString.replace(String.valueOf(tmpString.charAt(i)),
            String.valueOf(Integer.parseInt(population_list.get(index).get(count), 2)));
//        System.out.println(population_list.get(index).get(count));
//        System.out.println(String.valueOf(Integer.parseInt(population_list.get(index).get(count), 2)));
        count++;
      }
    }
    return (int) engine.eval(tmpString);
  }

  //Функция скрещивания
  String crossoverStrings(String s1, String s2){
    String result="";
    Random rand = new Random();
    int point = rand.nextInt(s1.length());
    //System.out.println("s1: " + s1);
    //System.out.println("s2: " + s2);
    //System.out.println("point: " + point);
    for (int i = 0; i<point; i++){
      result= result + s1.charAt(i);
    }
    for (int i = point; i<s2.length(); i++){
      result=result + s2.charAt(i);
    }
    //System.out.println("result: " + result);
    return result;
  }

  //функция скрещивания особи
  List<String> crossoverSpecimen(List<String> Specimen1,List<String> Specimen2){
    String s1="";
    String s2="";
    for (int i=0;i<Specimen1.size();i++){
      s1+=Specimen1.get(i);
    }
    for (int i=0;i<Specimen2.size();i++){
      s2+=Specimen2.get(i);
    }
    String resultString = crossoverStrings(s1,s2);

    List<String> result = new ArrayList<>();

    String tmp="";
    for (int i = 1; i<=resultString.length(); i++){
      tmp+=resultString.charAt(i-1);
      if (i%6==0) {
        tmp += ";";
      }
    }
    result = Arrays.asList(tmp.split(";"));
    return result;
  }

  //функция мутации
  List<String> mutation(List<String> Specimen){
    String s1="";
    for (int i=0;i<Specimen.size();i++){
      s1+=Specimen.get(i);
    }

    Random rand = new Random();
    String tmp1 = "";
    for (int i = 0;i<s1.length();i++){
      if (rand.nextInt(100)<15){
        if (s1.charAt(i) == '1'){
          tmp1+="0";
        }
        else
          tmp1+="1";
      }
      else
        tmp1+=s1.charAt(i);
    }
    String resultString=tmp1;
    List<String> result = new ArrayList<>();

    String tmp="";
    for (int i = 1; i<=resultString.length(); i++){
      tmp+=resultString.charAt(i-1);
      if (i%6==0) {
        tmp += ";";
      }
    }
    result = Arrays.asList(tmp.split(";"));
    return Specimen;
  }

  public void parse(ActionEvent actionEvent) throws ScriptException {
    //инициализация парсера
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");
    Object result;
    String functionString = function.getText().toString();

    //подсчёт переменных
    int var_count=0;
    for (int i=0; i<functionString.length(); i++){
      if (Character.isLetter(functionString.charAt(i))){
        var_count++;
      }
    }

    //вывод числа переменных
    output.setText(String.valueOf(var_count));

    //инициализация популяции
    int populationSize = Integer.parseInt(population.getText());
    List<List<String>> population_list = new ArrayList<>();

    //генерация популяции
    for (int i = 0; i<populationSize; i++){
      population_list.add(new ArrayList<>());
      for (int j = 0; j<var_count; j++){
        population_list.get(i).add(randomChromosome(6));
      }
    }

    //вывод популяции
    for (int i = 0; i<populationSize; i++){
      for (int j = 0; j<var_count; j++){
        System.out.print(population_list.get(i).get(j) + " ");
      }
      System.out.println();
    }

    int best = f(functionString,population_list,0);


    List<List<String>> new_population_list = new ArrayList<>();
    List<Pair<Integer,Integer>> f = new ArrayList<Pair<Integer,Integer>>();

    int iteration=0;
    //Основной цикл
    for (int i = 0; i < Integer.parseInt(iterations.getText());i++) {
      iteration++;
      new_population_list.clear();
        //скрещивание
        for (int n = 0; n < population_list.size(); n++) {
          for (int j = 0; j < population_list.size(); j++) {
            new_population_list
                .add(crossoverSpecimen(population_list.get(n), population_list.get(j)));
          }
        }

      for (List<String>tmp:new_population_list
      ) {
        tmp=mutation(tmp);
      }
        //вывод популяции
      for (int k = 0; k < new_population_list.size(); k++) {
          for (int j = 0; j < var_count; j++) {
            System.out.print(new_population_list.get(k).get(j) + " ");
          }
          System.out.println();
        }
      System.out.println();

      //Вычисление значений функций
        f.clear();
        for (int j = 0;j < new_population_list.size(); j++){
          f.add(new Pair<>(j,f(functionString,new_population_list,j)));
        }
        if (direction.getValue().toString() == "max")
          f.sort(new ValueComparator().reversed());
        else
          f.sort(new ValueComparator());

        population_list.clear();
        for (int j = 0; j<populationSize; j++){
        population_list.add(new_population_list.get(f.get(j).getKey()));
        }

        if (abs(best - f.get(0).getValue()) <= Integer.parseInt(accuracy.getText())){
          break;
        }

      if (direction.getValue().toString() == "max") {
        if (best < f.get(0).getValue()) {
          best = f.get(0).getValue();
        }
      }
      else {
        if (best > f.get(0).getValue()) {
          best = f.get(0).getValue();
        }
      }
      System.out.println(best);
    }


    answer.setText("Значение функции: " + String.valueOf(f(functionString,new_population_list,0))+ " Итераций: " + String.valueOf(iteration) + " Значение переменных:\n");
    for (int i =0;i<var_count;i++){
      answer.appendText(new_population_list.get(0).get(i)+ (" ") + Integer.parseInt(new_population_list.get(0).get(i),2) + ("\n"));
    }
  }


}
