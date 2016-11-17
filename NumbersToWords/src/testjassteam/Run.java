/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testjassteam;

import convert.text.NumberFormat;

/* @autor Andrei Kalosha


 Перевод числа в цифровой записи в строковую. Например 134345 будет "сто тридцать четыре
 тысячи триста сорок пять".  * Учесть склонения - разница в окончаниях (к примеру, две и два). 
 Алгоритм должен работать для 10^63(дециллиард),соответственно, значения степеней - миллион, 
 тысяча, миллиард и т.д.- должны браться их справочника, к примеру, текстового файла.  
 Обязательно создать Data Driven Test (я, как пользователь, должен иметь возможность ввести 
 множество наборов 1.число 2.правильный эталонный результат, тест самостоятельно проверяет 
 все наборы и говорит, что неверное),  который доказывает, что Ваш алгоритм работает правильно. 
 Использовать JUnit.
 */

public class Run {


    public static void main(String[] args) {
  //   NumberFormat d=new NumberFormat("ru");
     
     NumberFormat d=new NumberFormat("en");
     d.loadDirectory();
     System.out.print(d.format("0.0"));
    
     
    }
    
}
