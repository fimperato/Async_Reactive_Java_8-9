package it.imperato.test.java8.lambda.t01.newapproach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.imperato.test.java8.lambda.t01.City;
import it.imperato.test.java8.lambda.t01.IConditionTester;
 
public class MessageSender4 {
 
    List<City> iscritti = Arrays.asList(
       new City("Mario", "Rossi", "M", 35, "italiana"),
       new City("Lucy", "Parker", "F", 22, "inglese"),
       new City("Gianni", "Bianchi", "M", 20, "italiana"),
       new City("Fabio", "Marchi", "M", 40, "italiana"),
       new City("John", "Simpson", "M", 18, "USA"),
       new City("Adele", "Fabi", "F", 20, "italiana")
    );
 
     public List<City> getIscrittiFiltratiPer(IConditionTester<City> pred){
      List<City> persone = new ArrayList<City>();
      for (City p : iscritti)
          if (pred.test(p))
              persone.add(p);
 
      return persone;
    }
 
    public void sendMessage(String msg, List<City> persone){
       // Logica di Invio messaggio
       // .........
 
       System.out.println("Inviato messaggio a "+persone.size()+" iscritti");
     }
 
    public static void main(String[] args) {
      MessageSender4 ms = new MessageSender4();
 
      // ITests
      IConditionTester<City> allGiovaniDonne = p -> p.getSesso().equals("F") && p.getEta() > 17 && p.getEta() < 30;
      IConditionTester<City> allMaschi = p -> p.getSesso().equals("M");
      IConditionTester<City> allStranieri = p -> !p.getNazionalita().equals("italiana");
 
      // ------------ invio messaggio per giovani donne ---------------
      ms.sendMessage("messaggioX", ms.getIscrittiFiltratiPer(allGiovaniDonne));
 
      // ------------ invio messaggio per iscritti maschi ---------------
      ms.sendMessage("messaggioY", ms.getIscrittiFiltratiPer(allMaschi));
 
      // ------------ invio messaggio per iscritti stranieri ---------------
      ms.sendMessage("messaggioZ", ms.getIscrittiFiltratiPer(allStranieri));
    } 
 
}
