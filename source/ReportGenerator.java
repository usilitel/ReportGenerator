package ru.lesson.lessons.ReportGenerator.source;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ReportGenerator {

    // Переделать. Брать параметры из настроечного файла.
    int PAGE_WIDTH = 32;
    int PAGE_HEIGH = 12;
    int[] columnWidth = {8, 7, 7};
    String[] columnTitle = {"Номер", "Дата", "ФИО"};

    String dataFile = "C:/projects/java-courses/src/main/java/ru/lesson/lessons/ReportGenerator/docs/source-data.txt";
    //String dataFile = "../docs/source-data.txt";

    ArrayList<ArrayList<ArrayList<String>>> arrayListReport; // массив cо всеми данными отчета. [строка].[столбец].[номер строки в ячейке]



    public static void main(String[] args){
        new ReportGenerator();
    }


    public ReportGenerator(){
        readTextFile(dataFile);

    }




    // переводим текстовый файл в массив arrayListReport (1 строка в массиве = 1 строка в ячейке отчета)
    public void readTextFile(String textFile) {
        ArrayList<String> rowSource; // массив c исходными данными из одной строки (ячейки отчета разделены по элементам массива)
        ArrayList<ArrayList<String>> arrayListRow; // массив c исходными данными из одной строки (ячейки отчета разделены по элементам массива)
        ArrayList<ArrayList<String>> arrayListRowJoined; // массив c исходными данными из одной строки (ячейки отчета разделены по элементам массива)
        ArrayList<String> arrayListTemp; // массив-буфер


        arrayListReport = new ArrayList<ArrayList<ArrayList<String>>>();


        // записываем заголовки в массив
        arrayListRow = new ArrayList<ArrayList<String>>();
        for(int i=0;i<columnTitle.length;i++){
            arrayListTemp = new ArrayList<String>();
            arrayListTemp.add(columnTitle[i]);
            arrayListRow.add(arrayListTemp);
        }
        arrayListReport.add(0, arrayListRow);


        try{
            FileInputStream fstream = new FileInputStream(textFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            while ((strLine = br.readLine()) != null){
                //System.out.println("-------------");
                //System.out.println(strLine);

                rowSource = StringToArrayList(strLine, "\t"); // массив ячеек (ячейка в виде строки)

                arrayListRow = ArrayListToArrayListDim2(rowSource); // массив ячеек (ячейка в виде ArrayList<String>)

                arrayListRowJoined = new ArrayList<ArrayList<String>>();
                for (int i=0;i<arrayListRow.size();i++){
                    SplitBigWords(arrayListRow.get(i), columnWidth[i]); // расставляем переносы
                    arrayListTemp = JoinWords(arrayListRow.get(i), columnWidth[i]); // стыкуем слова
                    arrayListRowJoined.add(i,arrayListTemp);
                }

                arrayListReport.add(arrayListRowJoined);
            }

        }catch (IOException e){
            System.out.println("Ошибка при доступе к файлу " + textFile);
        }


        // выводим содержание массива cо всеми данными отчета
        for (int i1=0;i1<arrayListReport.size();i1++){
            for (int i2=0;i2<arrayListReport.get(i1).size();i2++){
                for (int i3=0;i3<arrayListReport.get(i1).get(i2).size();i3++){
                    System.out.println(arrayListReport.get(i1).get(i2).get(i3));
                }
                System.out.println("--");
            }
            System.out.println("-----");
        }


    }





    // преобразуем ArrayList<String> в ArrayList<ArrayList<String>>
    public ArrayList<ArrayList<String>> ArrayListToArrayListDim2(ArrayList<String> arrayListDim1) {

        ArrayList<String> arrayListTemp; // = new ArrayList<String>();
        ArrayList<ArrayList<String>> arrayListDim2 = new ArrayList<ArrayList<String>>();

        for (int i=0;i<arrayListDim1.size();i++) {
            arrayListTemp = SplitStringToWords(arrayListDim1.get(i));
            arrayListDim2.add(arrayListTemp);
        }

        return arrayListDim2;
    }


    // преобразуем строку в ArrayList
    public ArrayList<String> StringToArrayList(String stringSource, String delimiter) {
        String[] arrayRowSource = stringSource.split("\t");
        List listRowSource = Arrays.asList(arrayRowSource);
        ArrayList<String> rowSource = new ArrayList(listRowSource);
        return rowSource;
    }


    // разделяем строку на массив отдельных слов
    public ArrayList<String> SplitStringToWords(String s){
        boolean isWordPartPrevious=true;
        boolean isWordPartCurrent=true;
        String charCurrent="";
        String wordCurrent="";
        ArrayList<String> wordsSplitted =  new ArrayList<String>();

        for(int i=0;i<s.length();i++){
            charCurrent=String.valueOf(s.charAt(i));
            isWordPartCurrent=charCurrent.matches("[a-zA-Zа-яА-Я0-9]");

            if ((isWordPartCurrent!=isWordPartPrevious) || (isWordPartCurrent==false)){ // смена слова ИЛИ конец строки - записываем слово в массив
                wordsSplitted.add(wordCurrent);
                wordCurrent=charCurrent;
            }
            else
            {
                wordCurrent+=charCurrent;
            }
            isWordPartPrevious=isWordPartCurrent;

            if ((i==s.length()-1)){ // конец строки - записываем слово в массив
                wordsSplitted.add(wordCurrent);
            }
        }

        return wordsSplitted;
    }


    // расставляем переносы в больших словах (которые полностью не влезают в строку)
    public ArrayList<String> SplitBigWords(ArrayList<String> words, int columnWidth){

        int i=0;
        while(i<words.size()){
            if(words.get(i).length()>columnWidth){
                words.add(i+1,words.get(i).substring(columnWidth));
                words.set(i,words.get(i).substring(0,columnWidth));
            }
            i++;
        }

        return words;
    }

    // объединяем слова в строки (получаем набор строк для одной ячейки)
    public ArrayList<String> JoinWords(ArrayList<String> words, int columnWidth){

        int i=1;
        String stringWords=words.get(0);
        String nextWord;
        ArrayList<String> wordsJoined = new ArrayList<String>();

        if(words.size()==1){wordsJoined.add(stringWords);}

        while(i<words.size()){
            nextWord=words.get(i);
            if((stringWords.length() + nextWord.length() <= columnWidth)) { // если слово влезает в колонку - то увеличиваем фразу
                stringWords=stringWords+nextWord;
            }
            else{ // если слово не влезает в колонку - то добавляем фразу в массив и начинаем ее заново
                wordsJoined.add(stringWords);
                stringWords=nextWord.trim();
            }

            if(i==words.size()-1){ // добавляем последнюю фразу в массив
                wordsJoined.add(stringWords);
            }

            i++;
        }

        return wordsJoined;
    }



}
