import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.PrintWriter;
import java.io.File;

public class Payroll{

  //String[] csvBookingList = {"C:/Users/scott/Desktop/Payroll/LondonStudio/bel.CSV"};
  //String[] csvMemberList = {"C:/Users/scott/Desktop/Payroll/LondonStudio/mem.CSV"};
  //String[] csvTimeClockList = {};
  //String csvFillingFile = "C:/Users/scott/Desktop/Payroll/LondonStudio/inp.CSV";
  String[] csvBookingList = {"C:/Users/scott/Desktop/Payroll/thisweek/belS.CSV", "C:/Users/scott/Desktop/Payroll/thisweek/belN.CSV"};
  String[] csvMemberList = {"C:/Users/scott/Desktop/Payroll/thisweek/memS.CSV", "C:/Users/scott/Desktop/Payroll/thisweek/memN.CSV"};
  String[] csvTimeClockList = {"C:/Users/scott/Desktop/Payroll/thisweek/timeS.CSV", "C:/Users/scott/Desktop/Payroll/thisweek/timeN.CSV"};
  String csvFillingFile = "C:/Users/scott/Desktop/Payroll/thisweek/CPNInput.CSV";
  String csvBookingFile;
  String csvMemberFile;
  String csvTimeClockFile;
  BufferedReader fillingFile = null;
  BufferedReader bookingFile = null;
  BufferedReader memberFile = null;
  BufferedReader timeClockFile = null;
  String fillingLine = "";
  String bookingLine = "";
  String memberLine = "";
  String timeClockLine = "";

  String cvsSplitBy = ",";

  boolean instructorSignUpPay;
  boolean pastButNotLoggedPay;
  boolean noShowPay;
  boolean cancelledOutsidePolicyPay;
  boolean cancelledByAdminPay;
  boolean cancelledWithinRulesPay;
  double normalHourlyPay = 12.00;
  boolean displayTotalByStudio;
  boolean displayOverallTotal;

  String storeNumber;
  int numberOfStudios;
  String currentLocation;

  String[][] timePayTableInput = new String[200][4];

  String[][] basePayTable = new String[50][3];
  String[][] groupClassPayTable = new String[50][15];
  String[][] privateClassPayTable = new String[50][5];
  String[][] introClassPayTable = new String[50][15];

  String location;
  String dateRange;

  int bookingEventIndex;
  int bookingDetailIndex;
  int bookingDateIndex;
  int bookingStartIndex;
  int bookingWithIndex;
  int bookingStatusIndex;
  int customerFirstIndex;
  int customerLastIndex;
  int bookingLocation;

  String[][] bEvents = new String[40000][7];
  String[][] classes = new String[2000][7];

  String[][] finalTable = new String[2000][8];

  int bECounter = 0;
  int cPCounter = 0;

  int bEventCounter = 0;
  int classesCounter = 0;

  String[] memberList = new String[600];

  String[][] totals;

  ArrayList<String> teacherNames = new ArrayList<String>();

  String[][][] indTot;

  public int getStoreCount(){
    List<String> stud = new ArrayList<>();
    for(int i = 0;  i < basePayTable.length; i++){
      stud.add(basePayTable[i][2]);
    }
    Set<String> hs1 = new LinkedHashSet<>(stud);
    List<String> stud2 = new ArrayList<>(hs1);
    stud2.removeAll(Collections.singleton(null));
    numberOfStudios = stud2.size();
    return stud2.size();
  }

  public void setFiles(int index){
    csvBookingFile = csvBookingList[index];
    csvMemberFile = csvMemberList[index];
    if(csvTimeClockList.length > 0){
      csvTimeClockFile = csvTimeClockList[index];
    }
  }

  public void readFillingCSVFile(){
    try{

      fillingFile = new BufferedReader(new FileReader(csvFillingFile));
      System.out.println("Filling File worked!");
    }catch(IOException ex){
        ex.printStackTrace();
    }
  }

  public void readBookingCSVFile(){
    try{
      bookingFile = new BufferedReader(new FileReader(csvBookingFile));
      System.out.println("Booking File worked!");
    }catch(IOException ex){
          ex.printStackTrace();
    }
  }

  public void readMemberCSVFile(){
    try{
      memberFile = new BufferedReader(new FileReader(csvMemberFile));
      System.out.println("Member File worked!");
    }catch(IOException ex){
          ex.printStackTrace();
    }
  }

  public void readTimeClockFile(){
    try{
      if(csvTimeClockList.length > 0){
        timeClockFile = new BufferedReader(new FileReader(csvTimeClockFile));
        System.out.println("Time Clock File worked!");
      }else{
        System.out.println("No Time Clock Files");
      }
    }catch(IOException ex){
          ex.printStackTrace();
    }
  }

  public void fillPayTables(){
    try{
      int count = 0;
      if(basePayTable.length == 0){
        count = 0;
      }else{
        for(int p = 0; p < basePayTable.length; p++){
          if(basePayTable[p][0]==null){
            count = p;
            p = basePayTable.length;
          }
        }
      }

      fillingFile.readLine();
      while(!(fillingLine = fillingFile.readLine()).contains("End Questions")){
        fillingLine = fillingLine.replaceAll("\"", "");
        if(fillingLine.substring(0,2).equals(",,")){
          break;
        }
        //Checks to make sure line isn't completely empty (,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,)
        String[] line = fillingLine.split(cvsSplitBy);
        if(line[0].contains("(2)")){
          instructorSignUpPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(3)")){
          pastButNotLoggedPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(4)")){
          noShowPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(5)")){
          cancelledWithinRulesPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(6)")){
          cancelledByAdminPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(7)")){
          cancelledOutsidePolicyPay = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(8)")){
          normalHourlyPay = Double.parseDouble(line[1]);
        }else if(line[0].contains("(9)")){
          displayTotalByStudio = Boolean.parseBoolean(line[1]);
        }else if(line[0].contains("(10)")){
          displayOverallTotal = Boolean.parseBoolean(line[1]);
        }
        //Checks to see if most of the elements on the line are filled (or if it is a pay table)
        if(!(fillingLine.contains(",,,,,,,,,,,,,,,,"))){
          basePayTable[count][0] = line[1];
          basePayTable[count][1] = line[2];
          basePayTable[count][2] = line[0];

          int secondaryCounter = 0;
          //Fills group class array
          groupClassPayTable[count][14] = line[0];
          groupClassPayTable[count][0] = line[1];
          while(secondaryCounter < 13){
            groupClassPayTable[count][secondaryCounter+1] = line[secondaryCounter+4];
            secondaryCounter++;
          }

          secondaryCounter = 0;
          //Fills private/semi-private class array
          privateClassPayTable[count][4] = line[0];
          privateClassPayTable[count][0] = line[1];
          while(secondaryCounter < 3){
            privateClassPayTable[count][secondaryCounter+1] = line[secondaryCounter+18];
            secondaryCounter++;
          }

          secondaryCounter = 0;
          //Fills intro class array
          introClassPayTable[count][14] = line[0];
          introClassPayTable[count][0] = line[1];
          while(secondaryCounter < 13){
            introClassPayTable[count][secondaryCounter+1] = line[secondaryCounter+22];
            secondaryCounter++;
          }
        }
      count++;
      }

    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void fillMemberArray(){
    try{
      int firstNameIndex = -1;
      int lastNameIndex = -1;
      int counter = 0;

      //Finds beginning line of member file         ex. Member List: Club Pilates South Naperville (5923) - 394 Active on 7/23/2018
      boolean beginning = false;
      while(!beginning){
        memberLine = memberFile.readLine();
        if(memberLine.contains("Member List") || memberLine.contains("Active on")){
          beginning = true;
        }
      }
      storeNumber = memberLine.substring(memberLine.indexOf("(")+1, memberLine.indexOf(")"));

      //Finds the line with indexes on it then finds the first name and last name index
      boolean indexes = false;
      while(!indexes){
        memberLine = memberFile.readLine();
        if(memberLine.contains("First Name") || memberLine.contains("Last Name")){
          indexes = true;
          String[] split = memberLine.split(cvsSplitBy);
          for(int i = 0; i < split.length; i++){
            if(split[i].contains("First Name")){
              firstNameIndex = i;
            }else if(split[i].contains("Last Name")){
              lastNameIndex = i;
            }
          }
        }
      }


      while((memberLine = memberFile.readLine()) != null){
        memberLine = memberLine.replaceAll("\"", "");
        String[] line = memberLine.split(cvsSplitBy);
        if(line.length > 10){
          memberList[counter] = line[firstNameIndex] + "" + line[lastNameIndex];
        }
        counter++;
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void fillTimeClockArray(){
    try{
      int counter = 0;
      for(int y = 0; y < timePayTableInput.length; y++){
        if(timePayTableInput[y][0] == null){
          counter = y;
          y = timePayTableInput.length;
        }
      }
      if(csvTimeClockList.length > 0){
        timeClockLine = timeClockFile.readLine();
        timeClockLine = timeClockFile.readLine();
        while((timeClockLine = timeClockFile.readLine()) != null){
          timeClockLine = timeClockLine.replaceAll("\"", "");
          String[] line = timeClockLine.split(cvsSplitBy);
          timePayTableInput[counter][0] = line[0];
          timePayTableInput[counter][1] = line[5];
          try{
            timePayTableInput[counter][2] = line[6].substring(2);
          }catch(StringIndexOutOfBoundsException ee){
            timePayTableInput[counter][2] = "0";
          }
          timePayTableInput[counter][3] = currentLocation;
          counter++;
        }
      }
      //for(int u = 0; u < timePayTableInput.length; u++){
        //System.out.println(timePayTableInput[u][0] +": " + timePayTableInput[u][1] + ": " + timePayTableInput[u][3]);
      //}
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void findBookingIndexes(){
    try{
      int count = 1;
      while(count < 5){
        bookingLine = bookingFile.readLine();
        if(count == 2){
          String loc = bookingLine.substring(0, bookingLine.indexOf("["));
          currentLocation = loc;
          if(location == null){
            location = loc;
          }else{
            location += ("/ " + loc);
          }
        }
        if(count == 4){
          String date = bookingLine.substring(0, bookingLine.lastIndexOf("/") + 5);
          dateRange = date;
        }
        count += 1;
      }


      bookingLine = bookingFile.readLine();
      String[] column = bookingLine.split(cvsSplitBy);

      //Sets booking indexes
      for(int i = 0; i < column.length; i++){
        if(column[i].contains("Booking Event")){
          bookingEventIndex = i;
        }
        if(column[i].contains("Booking Detail")){
          bookingDetailIndex = i;
        }
        if(column[i].contains("Booking Date")){
          bookingDateIndex = i;
        }
        if(column[i].contains("Booking Start")){
          bookingStartIndex = i;
        }
        if(column[i].contains("Booking With")){
          bookingWithIndex = i;
        }
        if(column[i].contains("Current Status")){
          bookingStatusIndex = i;
        }
        if(column[i].contains("Customer First Name")){
          customerFirstIndex = i;
        }
        if(column[i].contains("Customer Last Name")){
          customerLastIndex = i;
        }
        if(column[i].contains("BookingLocation")){
          bookingLocation = i;
        }
      }
    }catch(IOException e1){
      e1.printStackTrace();
    }
  }

  public void fillBookingEventArray(){
    try{
      //Adds all booking events to array
      while((bookingLine = bookingFile.readLine()) != null){
        String[] row = bookingLine.split(cvsSplitBy);
        if(row != null){
          if(row[bookingEventIndex].contains("Booking Completed") ||
             (row[bookingEventIndex].contains("Past But Not Logged") && pastButNotLoggedPay) ||
             (row[bookingEventIndex].contains("No-Show") && noShowPay) ||
             ((row[bookingEventIndex].contains("Booking Canceled") && row[bookingStatusIndex].contains("Cancelled Outside Policy Rules - Session Lost")) && cancelledOutsidePolicyPay) ||
             ((row[bookingEventIndex].contains("Booking Canceled") && row[bookingStatusIndex].contains("Cancelled Within Policy Rules")) && cancelledWithinRulesPay) ||
             ((row[bookingEventIndex].contains("Booking Canceled") && row[bookingStatusIndex].contains("Cancelled By Admin")) && cancelledByAdminPay)
             ){

            bEvents[bECounter][0] = row[bookingDetailIndex];
            bEvents[bECounter][1] = row[bookingDateIndex];
            bEvents[bECounter][2] = row[bookingStartIndex];
            bEvents[bECounter][3] = row[bookingWithIndex];
            bEvents[bECounter][4] = "1";
            if(row[bookingDetailIndex].contains("Intro")){
              for(int i = 0; i < memberList.length; i++){
                if((row[customerFirstIndex] + "" + row[customerLastIndex]).equals(memberList[i])){
                  bEvents[bECounter][5] = "1";
                }
              }
            }
            bEvents[bECounter][6] = row[bookingLocation];
            bECounter += 1;
            bEventCounter += 1;
          }
        }
      }
    }catch(IOException e){
      e.printStackTrace();
    }
  }

  public void fillClassesArray(){
    for(int x = 0; x < bEvents.length; x++){
      if(bEvents[x][0] != null){
        //First Case
        if(x == 0){
          classes[x][0] = bEvents[x][0];
          classes[x][1] = bEvents[x][1];
          classes[x][2] = bEvents[x][2];
          classes[x][3] = bEvents[x][3];
          classes[x][4] = bEvents[x][4];
          classes[x][5] = bEvents[x][5];
          classes[x][6] = bEvents[x][6];
          classesCounter += 1;
        }else{
          boolean sameClassChecker = false;
          for(int y = 0; y < classes.length; y++){
            if(classes[y][0] != null){
              if(classes[y][0].equals(bEvents[x][0]) && classes[y][1].equals(bEvents[x][1]) && classes[y][2].equals(bEvents[x][2]) && classes[y][3].equals(bEvents[x][3])){
                if(Integer.parseInt(classes[y][4]) < 12){
                  classes[y][4] = "" + (Integer.parseInt(classes[y][4]) + 1) + "";
                }
                if(bEvents[x][5]!= null){
                  if(classes[y][5] == null){
                    classes[y][5] = "0";
                  }
                  classes[y][5] = "" + (Integer.parseInt(classes[y][5]) + 1) + "";
                }
                sameClassChecker = true;
                y = classes.length;
              }
            }else{
              y = classes.length;
            }
          }

          if(sameClassChecker == false){
            for(int z = 0; z < classes.length; z++){
              if(classes[z][0] == null){
                classes[z][0] = bEvents[x][0];
                classes[z][1] = bEvents[x][1];
                classes[z][2] = bEvents[x][2];
                classes[z][3] = bEvents[x][3];
                classes[z][4] = bEvents[x][4];
                classes[z][5] = bEvents[x][5];
                classes[z][6] = bEvents[x][6];
                z = classes.length;
                classesCounter += 1;
              }
            }
          }
        }
      }else{
        x = bEvents.length;
      }
    }
  }

  public void displayClasses(){
     System.out.println(bEventCounter);
     for(int i = 0; i < classes.length; i++){
       if(classes[i][0] != null && classes[i][3].contains("Holly") && classes[i][1].contains("11-05")){
         System.out.println(classes[i][0] + "---" + classes[i][1] + "---" + classes[i][2] + "---" + classes[i][3] + "---" + classes[i][4] + "---" + classes[i][5] + "---" + classes[i][6]);
       }
     }
     int bEC = 0;
     for(int r = 0; r < bEvents.length; r++){
       if(bEvents[r][0] != null){
         bEC++;
       }
     }
     System.out.println("Unka;;;;;;;;;;;;;;;" + bEC);
     System.out.println("Scott::::::::::::" + classesCounter);
   }

  public void getPay(){
    for(int x = 0; x < classes.length; x++){
      if(classes[x][0] != null){
        if(classes[x][0].substring(0,2).equals("CP")){
          for(int i = 0; i < groupClassPayTable.length; i++){
            if(classes[x][3].equals(groupClassPayTable[i][0]) && (classes[x][6].equals(groupClassPayTable[i][14]) || groupClassPayTable[i][14].equals("ALL"))){
              finalTable[cPCounter][0] = classes[x][0];
              finalTable[cPCounter][1] = classes[x][1];
              finalTable[cPCounter][2] = classes[x][2];
              finalTable[cPCounter][3] = classes[x][3];
              finalTable[cPCounter][4] = classes[x][4];
              if(classes[x][4] != null){
                finalTable[cPCounter][5] = groupClassPayTable[i][(Integer.parseInt(classes[x][4]))+1];
              }else{
                finalTable[cPCounter][5] = groupClassPayTable[i][0];
              }
              finalTable[cPCounter][7] = classes[x][6];
              cPCounter +=1;
            }
          }
        }else if(classes[x][0].contains("Private")){
          for(int q = 0; q < privateClassPayTable.length; q++){
            if(classes[x][3].equals(privateClassPayTable[q][0]) && (classes[x][6].equals(privateClassPayTable[q][4]) || groupClassPayTable[q][4].equals("ALL"))){
              finalTable[cPCounter][0] = classes[x][0];
              finalTable[cPCounter][1] = classes[x][1];
              finalTable[cPCounter][2] = classes[x][2];
              finalTable[cPCounter][3] = classes[x][3];
              finalTable[cPCounter][4] = classes[x][4];
              finalTable[cPCounter][5] = privateClassPayTable[q][(Integer.parseInt(classes[x][4]))];
              finalTable[cPCounter][7] = classes[x][6];
              cPCounter += 1;
            }
          }
        }else if(classes[x][0].contains("Intro")){
          for(int q = 0; q < introClassPayTable.length; q++){
            if(classes[x][3].equals(introClassPayTable[q][0]) && (classes[x][6].equals(introClassPayTable[q][14]) || introClassPayTable[q][14].equals("ALL"))){
              finalTable[cPCounter][0] = classes[x][0];
              finalTable[cPCounter][1] = classes[x][1];
              finalTable[cPCounter][2] = classes[x][2];
              finalTable[cPCounter][3] = classes[x][3];
              finalTable[cPCounter][4] = classes[x][4];

              if(instructorSignUpPay == true){
                //Case if instructors are payed based on how many people sign up
                if(classes[x][5] != null){
                  finalTable[cPCounter][5] = introClassPayTable[q][(Integer.parseInt(classes[x][5]))+1];
                }else{
                  finalTable[cPCounter][5] = introClassPayTable[q][1];
                }
              }else{
                //Case if instructors are payed based on how many people show up
                if(classes[x][4] != null){
                  finalTable[cPCounter][5] = introClassPayTable[q][(Integer.parseInt(classes[x][4]))+1];
                }else{
                  finalTable[cPCounter][5] = "0";
                }
              }
              if(classes[x][5] == null){
                finalTable[cPCounter][6] = "0";
              }else{
                finalTable[cPCounter][6] = classes[x][5];
              }
              finalTable[cPCounter][7] = classes[x][6];
              cPCounter += 1;
            }
          }
        }
      }
    }
  }

  public void findInstructors(){
    for(int i = 0; i < finalTable.length; i++){
      if(finalTable[i][3]!=null && !(teacherNames.contains(finalTable[i][3]))){
        teacherNames.add(finalTable[i][3]);
      }
    }
  }

  public void fixArray(){
    int counter = 0;
    for(int i = 0; i < finalTable.length; i++){
      if(finalTable[i][0] != null){
        counter+=1;
      }
    }
    String[][] ex = new String[counter][finalTable[0].length];
    int c = 0;
    for(int a = 0; c < ex.length; a++){
      if(finalTable[a][0] != null){
        for(int z = 0; z < ex[0].length; z++){
          ex[c][z] = finalTable[a][z];
        }
        c++;
      }
    }

    finalTable = ex;
  }

  public void findTotals(){

    totals = new String[teacherNames.size() + 40][13];
    totals[totals.length-1][0] = "Overall";
    for(int y = 1; y < totals[totals.length-1].length; y++){
      totals[totals.length-1][y] = "0.00";
    }

    for(int i = 0; i < teacherNames.size(); i++){
      totals[i][0] = teacherNames.get(i);
      totals[i][9] = "0.00";
      totals[i][10] = "0.00";
      for(int x = 0; x < finalTable.length; x++){
        if(totals[i][0].equals(finalTable[x][3])){
          if(finalTable[x][0].contains("CP")){
            if(totals[i][1] == null){
              totals[i][1] = "1";
              totals[i][2] = finalTable[x][5];
            }else{
              totals[i][1] = "" + ((int) (Double.parseDouble(totals[i][1]) + 1)) + "";
              totals[i][2] =  (Double.parseDouble(totals[i][2]) + Double.parseDouble(finalTable[x][5])) + "";
            }
          }else if(finalTable[x][0].equals("Private Session") || finalTable[x][0].equals("Private Training Assessment")){
            if(totals[i][3] == null){
              totals[i][3] = "1";
              totals[i][4] = finalTable[x][5];
            }else{
              totals[i][3] = "" + ((int) (Double.parseDouble(totals[i][3]) + 1)) + "";
              totals[i][4] =  (Double.parseDouble(totals[i][4]) + Double.parseDouble(finalTable[x][5])) + "";
            }
          }else if(finalTable[x][0].contains("Semi-Private")){
            if(totals[i][5] == null){
              totals[i][5] = "1";
              totals[i][6] = finalTable[x][5];
            }else{
              totals[i][5] = "" + ((int) (Double.parseDouble(totals[i][5]) + 1)) + "";
              totals[i][6] = (Double.parseDouble(totals[i][6]) + Double.parseDouble(finalTable[x][5])) + "";
            }
          }else if(finalTable[x][0].contains("Intro")){
            if(totals[i][7] == null){
              totals[i][7] = "1";
              totals[i][8] = finalTable[x][5];
            }else{
              totals[i][7] = "" + ((int) (Double.parseDouble(totals[i][7]) + 1)) + "";
              totals[i][8] =  (Double.parseDouble(totals[i][8]) + Double.parseDouble(finalTable[x][5])) + "";
            }
          }
          for(int sc = 0; sc < timePayTableInput.length; sc++){
            if(timePayTableInput[sc][0] != null){
              if(timePayTableInput[sc][0].equals(finalTable[x][3])){
                totals[i][9] = "" + (Double.parseDouble(timePayTableInput[sc][1])/60) + "";
                totals[i][10] =  "" +((Double.parseDouble(timePayTableInput[sc][1])/60) * (Double.parseDouble(timePayTableInput[sc][2])))+ "";
              }
            }
          }
        }
      }
    }


    for(int q = 0; q < totals.length; q++){
      for(int u = 0; u < totals[0].length; u++){
        if(totals[q][u] == null){
          totals[q][u] = "0";
        }
      }
    }

  }

  public void findTotalsLastColumn(){
    for(int v = 0; v < totals.length; v++){
      if(!(totals[v][1].equals("X")) || !(totals[v][3].equals("X")) || !(totals[v][5].equals("X")) || !(totals[v][7].equals("X"))){
        totals[v][11] = "" + ((int)(Double.parseDouble(totals[v][1]) + Double.parseDouble(totals[v][3]) + Double.parseDouble(totals[v][5]) + Double.parseDouble(totals[v][7]))) + "";
        totals[v][12] = "" + (Double.parseDouble(totals[v][2]) + Double.parseDouble(totals[v][4]) + Double.parseDouble(totals[v][6]) + Double.parseDouble(totals[v][8]) + Double.parseDouble(totals[v][10])) + "";
      }
    }

    for(int t = 0; t < totals.length-1; t++){
      for(int s = 1; s < totals[0].length; s++){
        if(!(totals[t][s].equals("X"))){
          totals[totals.length-1][s] = "" + (Double.parseDouble(totals[totals.length-1][s]) + Double.parseDouble(totals[t][s]));
        }
      }
    }
  }

  public void addNonInstructor(){
    //Find next available spot in totals array (after all instructors)
    int begin = 0;
    for(int w = 0; w < totals.length; w++){
      if(totals[w][0].equals("0")){
        begin = w;
        w = totals.length;
      }
    }

    ArrayList<String> copy = (ArrayList<String>) teacherNames.clone();
    String[][] notInstructors = new String[30][3];

    //Checks if a person in the time pay table is in the instructor array copy
    int counter = 0;
    for(int z = 0; z < timePayTableInput.length; z++){
      boolean wordChecker = true;
      for(int e = 0; e < copy.size(); e++){
        if(timePayTableInput[z][0] != null){
          if(timePayTableInput[z][0].contains(copy.get(e))){
            //Case if name is in instructor array (copy)
            wordChecker = false;
            e = copy.size();
          }
        }
      }
      //Case if name is not in instructor array (copy)
      if(wordChecker == true && timePayTableInput[z][0] != null && !(timePayTableInput[z][1].equals("0"))){
        //If instructor has a pay rate
        if(!(timePayTableInput[z][2].equals("???"))){
          notInstructors[counter][0] = timePayTableInput[z][0];
          notInstructors[counter][1] = (Double.parseDouble(timePayTableInput[z][1])/60) + "";
          notInstructors[counter][2] = ((Double.parseDouble(timePayTableInput[z][1])/60) * (Double.parseDouble(timePayTableInput[z][2]))) + "";
          counter += 1;
        //If instructor does not have a pay rate
        }else{
          notInstructors[counter][0] = timePayTableInput[z][0];
          notInstructors[counter][1] = (Double.parseDouble(timePayTableInput[z][1])/60) + "";
          notInstructors[counter][2] = ((Double.parseDouble(timePayTableInput[z][1])/60) * normalHourlyPay) + "";
        }
      }
    }

    //Adds all non-instructors to totals
    for(int u = 0; u < notInstructors.length; u++){
      if(notInstructors[u][0] != null){
        totals[begin][0] = notInstructors[u][0];
        totals[begin][1] = "X";
        totals[begin][2] = "X";
        totals[begin][3] = "X";
        totals[begin][4] = "X";
        totals[begin][5] = "X";
        totals[begin][6] = "X";
        totals[begin][7] = "X";
        totals[begin][8] = "X";
        totals[begin][9] = notInstructors[u][1];
        totals[begin][10] = notInstructors[u][2];
        totals[begin][11] = "X";
        totals[begin][12] = notInstructors[u][2];
        begin += 1;
      }
    }
  }

  public void write(){
    try{
      File file = new File("C:/Users/scott/Desktop/Payroll/output.txt");
      file.getParentFile().mkdirs();
      PrintWriter writer = new PrintWriter(file);
      writer.println(location.replaceAll(",",""));
      writer.println(dateRange.replaceAll(",",""));
      writer.println("");
      writer.println("");
      for(int i = 0; i < teacherNames.size(); i++){
        if(teacherNames.get(i) != null){
          writer.println("*"+teacherNames.get(i));
          writer.println("");
          writer.println("Group Classes");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("CP")){
                  writer.println(finalTable[x][0] + "---" + finalTable[x][1] + "---" +finalTable[x][2] + "---" +finalTable[x][4] + "---$" +finalTable[x][5]);
                }
              }
            }
          }
          for(int q = 0; q < totals.length; q++){
            if(teacherNames.get(i).equals(totals[q][0])){
              writer.println("----- Group Classes Total: " + totals[q][1] + "   Group Classes: $" + totals[q][2]);
            }
          }
          writer.println("");
          writer.println("Private Classes");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].equals("Private Session")){
                  writer.println(finalTable[x][0] + "---" + finalTable[x][1] + "---" +finalTable[x][2] + "---" +finalTable[x][4] + "---$" +finalTable[x][5]);
                }
              }
            }
          }
          for(int w = 0; w < totals.length; w++){
            if(teacherNames.get(i).equals(totals[w][0])){
              writer.println("----- Private Classes Total: " + totals[w][3] + "   Private Classes Pay: $" + totals[w][4]);
            }
          }
          writer.println("");
          writer.println("Semi-Private Classes");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("Semi-Private")){
                  writer.println(finalTable[x][0] + "---" + finalTable[x][1] + "---" +finalTable[x][2] + "---" +finalTable[x][4] + "---$" +finalTable[x][5]);
                }
              }
            }
          }
          for(int e = 0; e < totals.length; e++){
            if(teacherNames.get(i).equals(totals[e][0])){
              writer.println("----- Semi-Private Classes Total: " + totals[e][5] + "   Semi-Private Classes Pay: $" + totals[e][6]);
            }
          }
          writer.println("");
          writer.println("Intro Classes");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("Intro")){
                  writer.println(finalTable[x][0] + "---" + finalTable[x][1] + "---" +finalTable[x][2] + "---" +finalTable[x][6] + "/" +finalTable[x][4] + "---$" +finalTable[x][5]);
                }
              }
            }
          }
          for(int u = 0; u < totals.length; u++){
            if(teacherNames.get(i).equals(totals[u][0])){
              writer.println("----- Intro Classes Total: " + totals[u][7] + "   Intro Classes Pay: $" + totals[u][8]);
            }
          }
        }
        writer.println("");
        writer.println("");
        for(int k = 0; k < totals.length; k++){
          if(teacherNames.get(i).equals(totals[k][0])){
            writer.println("----- " + teacherNames.get(i) + " Classes Total: " + totals[k][9] + "   " + teacherNames.get(i) +" Classes Pay: $" + totals[k][10]);
          }
        }
        writer.println("");
        writer.println("___________________________________________________________________________________________________________");
      }
      writer.println("");
      writer.println("Name - Group Class total - Group Class Pay - Private Class total - Private Class Pay - Semi-Private Class total - Semi-Private Class Pay - Intro Class total - Intro Class Pay - All Class total - All Class Pay -");
      for(int x = 0 ; x < totals.length; x++){
        for(int y = 0; y < totals[0].length; y++){
          writer.print(totals[x][y] + " - ");
        }
        writer.println("");
      }
      writer.close();
    }catch(IOException exx){
      exx.printStackTrace();
    }
  }

  public void writeCSV(){
    try{
      String csvOutput = "C:/Users/scott/Desktop/Payroll/payrollOutput.CSV";
      PrintWriter pw = new PrintWriter(new File(csvOutput));
      pw.write(location.replaceAll(",",""));
      pw.write("\n");
      pw.write(dateRange.replaceAll(",",""));
      pw.write("\n");
      pw.write("");
      pw.write("\n");

      for(int i = 0; i < teacherNames.size(); i++){
        if(teacherNames.get(i) != null){
          pw.write(teacherNames.get(i));
          pw.write("\n");
          pw.write("");
          pw.write("\n");
          pw.write("Group Class");
          pw.write(",");
          pw.write("Location");
          pw.write(",");
          pw.write("Date");
          pw.write(",");
          pw.write("Time");
          pw.write(",");
          pw.write("Number of People");
          pw.write(",");
          pw.write("Amount Owed");
          pw.write("\n");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("CP")){
                  pw.write(finalTable[x][0]);
                  pw.write(",");
                  pw.write(finalTable[x][7]);
                  pw.write(",");
                  pw.write(finalTable[x][1]);
                  pw.write(",");
                  pw.write(finalTable[x][2]);
                  pw.write(",");
                  pw.write(finalTable[x][4]);
                  pw.write(",");
                  pw.write("$" + finalTable[x][5]);
                  pw.write("\n");
                }
              }
            }
          }
          for(int q = 0; q < totals.length; q++){
            if(teacherNames.get(i).equals(totals[q][0])){
              pw.write("Totals (Number of Classes-Total Pay)");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write(totals[q][1]);
              pw.write(",");
              pw.write("$" + totals[q][2]);
              pw.write("\n");
            }
          }
          pw.write("");
          pw.write("\n");
          pw.write("Private Class");
          pw.write(",");
          pw.write("Location");
          pw.write(",");
          pw.write("Date");
          pw.write(",");
          pw.write("Time");
          pw.write(",");
          pw.write("Number of People");
          pw.write(",");
          pw.write("Amount Owed");
          pw.write(",");
          pw.write("\n");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && (finalTable[x][0].equals("Private Session") || finalTable[x][0].equals("Private Training Assessment"))){
                  pw.write(finalTable[x][0]);
                  pw.write(",");
                  pw.write(finalTable[x][7]);
                  pw.write(",");
                  pw.write(finalTable[x][1]);
                  pw.write(",");
                  pw.write(finalTable[x][2]);
                  pw.write(",");
                  pw.write(finalTable[x][4]);
                  pw.write(",");
                  pw.write("$" + finalTable[x][5]);
                  pw.write("\n");
                }
              }
            }
          }
          for(int w = 0; w < totals.length; w++){
            if(teacherNames.get(i).equals(totals[w][0])){
              pw.write("Totals (Number of Classes-Total Pay)");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write(totals[w][3]);
              pw.write(",");
              pw.write("$" + totals[w][4]);
              pw.write("\n");
            }
          }
          pw.write("");
          pw.write("\n");
          pw.write("Semi-Private Class");
          pw.write(",");
          pw.write("Location");
          pw.write(",");
          pw.write("Date");
          pw.write(",");
          pw.write("Time");
          pw.write(",");
          pw.write("Number of People");
          pw.write(",");
          pw.write("Amount Owed");
          pw.write("\n");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("Semi-Private")){
                  pw.write(finalTable[x][0]);
                  pw.write(",");
                  pw.write(finalTable[x][7]);
                  pw.write(",");
                  pw.write(finalTable[x][1]);
                  pw.write(",");
                  pw.write(finalTable[x][2]);
                  pw.write(",");
                  pw.write(finalTable[x][4]);
                  pw.write(",");
                  pw.write("$" + finalTable[x][5]);
                  pw.write("\n");
                }
              }
            }
          }
          for(int e = 0; e < totals.length; e++){
            if(teacherNames.get(i).equals(totals[e][0])){
              pw.write("Totals (Number of Classes-Total Pay)");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write( totals[e][5]);
              pw.write(",");
              pw.write("$" + totals[e][6]);
              pw.write("\n");
            }
          }
          pw.write("");
          pw.write("\n");
          pw.write("Intro Class");
          pw.write(",");
          pw.write("Location");
          pw.write(",");
          pw.write("Date");
          pw.write(",");
          pw.write("Time");
          pw.write(",");
          if(instructorSignUpPay){
            pw.write("People Joined/People In Class");
          }else{
            pw.write("People In Class");
          }
          pw.write(",");
          pw.write("Amount Owed");
          pw.write("\n");
          for(int x = 0 ; x < finalTable.length; x++){
            if(finalTable[x][3] != null){
              if(finalTable[x][3].equals(teacherNames.get(i))){
                if(finalTable[x][0] != null && finalTable[x][0].contains("Intro")){
                  pw.write(finalTable[x][0]);
                  pw.write(",");
                  pw.write(finalTable[x][7]);
                  pw.write(",");
                  pw.write(finalTable[x][1]);
                  pw.write(",");
                  pw.write(finalTable[x][2]);
                  pw.write(",");
                  if(instructorSignUpPay){
                    pw.write("(" + finalTable[x][6] + "/" + finalTable[x][4] + ")");
                  }else{
                    pw.write(finalTable[x][4]);
                  }
                  pw.write(",");
                  pw.write("$" + finalTable[x][5]);
                  pw.write("\n");
                }
              }
            }
          }
          for(int u = 0; u < totals.length; u++){
            if(teacherNames.get(i).equals(totals[u][0])){
              pw.write("Totals (Number of Classes-Total Pay)");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write(totals[u][7]);
              pw.write(",");
              pw.write("$" + totals[u][8]);
              pw.write("\n");
            }
          }
          pw.write("");
          pw.write("\n");
          pw.write("Time Clock");
          pw.write(",");
          pw.write("");
          pw.write(",");
          pw.write("");
          pw.write(",");
          pw.write("");
          pw.write(",");
          pw.write("Hours");
          pw.write(",");
          pw.write("Amount Owed");
          pw.write(",");
          pw.write("\n");
          for(int j = 0; j < totals.length; j++){
            if(teacherNames.get(i).equals(totals[j][0])){
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write("");
              pw.write(",");
              pw.write(totals[j][9]);
              pw.write(",");
              pw.write("$" + totals[j][10]);
              pw.write("\n");
            }
          }

        pw.write("");
        pw.write("\n");
        for(int k = 0; k < totals.length; k++){
          if(teacherNames.get(i).equals(totals[k][0])){

            pw.write(teacherNames.get(i) + " Totals");
            pw.write(",");
            pw.write("");
            pw.write(",");
            pw.write("");
            pw.write(",");
            pw.write("");
            pw.write(",");
            pw.write(totals[k][11]);
            pw.write(",");
            pw.write("$" + totals[k][12]);
            pw.write("\n");

          }
        }
        pw.write("");
        pw.write("\n");
        pw.write("");
        pw.write("\n");
        pw.write("");
        pw.write("\n");
        }
      }

      if(displayOverallTotal){
        pw.write("");
        pw.write("Overall");
        pw.write("\n");
        pw.write("Employee");
        pw.write(",");
        pw.write("Group Class Total");
        pw.write(",");
        pw.write("Group Class Pay");
        pw.write(",");
        pw.write("Private Class Total");
        pw.write(",");
        pw.write("Private Class Pay");
        pw.write(",");
        pw.write("Semi-Private Class Total");
        pw.write(",");
        pw.write("Semi-Private Class Pay");
        pw.write(",");
        pw.write("Intro Class Total");
        pw.write(",");
        pw.write("Intro Class Pay");
        pw.write(",");
        pw.write("Time Clock Hours");
        pw.write(",");
        pw.write("Time Clock Pay");
        pw.write(",");
        pw.write("All Class Total");
        pw.write(",");
        pw.write("All Class Pay");
        pw.write("\n");
        for(int x = 0 ; x < totals.length; x++){
          if(totals[x][0] != "0"){
            for(int y = 0; y < totals[0].length; y++){
              if(y != 0 && (y%2 == 0)){
                pw.write("$" + totals[x][y]);
                pw.write(",");
              }else{
                pw.write(totals[x][y]);
                pw.write(",");
              }
            }
            pw.write("\n");
          }
        }
        pw.write("\n");
        pw.write("\n");
        pw.write("\n");
      }

      if(displayTotalByStudio && numberOfStudios > 1){
        for(int sm = 0; sm < indTot.length; sm++){
          pw.write(indTot[sm][0][13]);
          pw.write("");
          pw.write("\n");
          pw.write("Employee");
          pw.write(",");
          pw.write("Group Class Total");
          pw.write(",");
          pw.write("Group Class Pay");
          pw.write(",");
          pw.write("Private Class Total");
          pw.write(",");
          pw.write("Private Class Pay");
          pw.write(",");
          pw.write("Semi-Private Class Total");
          pw.write(",");
          pw.write("Semi-Private Class Pay");
          pw.write(",");
          pw.write("Intro Class Total");
          pw.write(",");
          pw.write("Intro Class Pay");
          pw.write(",");
          pw.write("Time Clock Hours");
          pw.write(",");
          pw.write("Time Clock Pay");
          pw.write(",");
          pw.write("All Class Total");
          pw.write(",");
          pw.write("All Class Pay");
          pw.write("\n");
          for(int re = 0; re < indTot[0].length; re++){
            if(!(indTot[sm][re][12].equals("0.0"))){
              for(int go = 0; go < indTot[0][0].length-1; go++){
                if(go!=0 && go%2 == 0){
                  pw.write("$" + indTot[sm][re][go]);
                  pw.write(",");
                }else{
                  pw.write(indTot[sm][re][go]);
                  pw.write(",");
                }
              }
              pw.write("\n");
            }
          }
          pw.write("\n");
          pw.write("\n");
          pw.write("\n");
        }
      }
      pw.close();
    }catch(IOException i){
      i.printStackTrace();
    }
  }

  public void test(){
      for(int x = 0 ; x < finalTable.length; x++){
        //if(finalTable[x][0] != null  && finalTable[x][3].contains("Kelly")){
          //for(int y = 0; y < finalTable[0].length; y++){
            System.out.print(finalTable[x][1] + "--" + finalTable[x][2]);
          //}
          System.out.println("");
        //}
      }
    }

    public void test1(){
        for(int x = 0 ; x < finalTable.length; x++){
          if(finalTable[x][0]!=null && finalTable[x][0].contains("Semi")){
            for(int y = 0 ; y < finalTable[0].length; y++){
              System.out.print(finalTable[x][y] +"-");
            }
            System.out.println("");
          }
        }
      }

  public boolean findSignUpPay(){
    return instructorSignUpPay;
  }

  public void organizeFinal(){
    String[][] copyFinalTable = new String[finalTable.length][finalTable[0].length];
    for(int g = 0; g < finalTable.length; g++){
      for(int h = 0; h < finalTable[0].length; h++){
        copyFinalTable[g][h] = finalTable[g][h];
      }
    }

    boolean finished = false;
    int test = 0;
    String[][] organized = new String[copyFinalTable.length][copyFinalTable[0].length];
    int smallestIndexOrg = 0;
    int earliestDateIndex = 0;
    while(!finished){
      while(test < copyFinalTable.length){
        String[] smallest = copyFinalTable[earliestDateIndex][1].split("-");
        String[] newest = copyFinalTable[test][1].split("-");
        String smallestAMPM = copyFinalTable[earliestDateIndex][2].substring(copyFinalTable[earliestDateIndex][2].indexOf(" ")+1);
        String newestAMPM = copyFinalTable[test][2].substring(copyFinalTable[test][2].indexOf(" ")+1);
        String smallestTime = copyFinalTable[earliestDateIndex][2].substring(0,copyFinalTable[earliestDateIndex][2].indexOf(":"));
        String newestTime = copyFinalTable[test][2].substring(0,copyFinalTable[test][2].indexOf(":"));
        //System.out.println("Smallest: " + smallest[0] + "-" + smallest[1] + "-" + smallest[2] + "-" + smallestAMPM + "-" + smallestTime);
        //System.out.println("Newest: " + newest[0] + "-" + newest[1] + "-" + newest[2] + "-" + newestAMPM + "-" + newestTime);
        if(Integer.parseInt(smallest[0]) > Integer.parseInt(newest[0])){
          earliestDateIndex = test;
        }else if(Integer.parseInt(smallest[0]) == Integer.parseInt(newest[0]) &&
                 Integer.parseInt(smallest[1]) > Integer.parseInt(newest[1])
                 ){
          earliestDateIndex = test;
        }else if(Integer.parseInt(smallest[0]) == Integer.parseInt(newest[0]) &&
                 Integer.parseInt(smallest[1]) == Integer.parseInt(newest[1]) &&
                 Integer.parseInt(smallest[2]) > Integer.parseInt(newest[2])
                 ){
          earliestDateIndex = test;
        }else if(Integer.parseInt(smallest[0]) == Integer.parseInt(newest[0]) &&
                 Integer.parseInt(smallest[1]) == Integer.parseInt(newest[1]) &&
                 Integer.parseInt(smallest[2]) == Integer.parseInt(newest[2]) &&
                 (smallestAMPM.equals("PM") && newestAMPM.equals("AM"))
                 ){
          earliestDateIndex = test;
        }else if(Integer.parseInt(smallest[0]) == Integer.parseInt(newest[0]) &&
                 Integer.parseInt(smallest[1]) == Integer.parseInt(newest[1]) &&
                 Integer.parseInt(smallest[2]) == Integer.parseInt(newest[2]) &&
                 ( (smallestAMPM.equals("AM") && newestAMPM.equals("AM")) || (smallestAMPM.equals("PM") && newestAMPM.equals("PM")) ) &&
                 Integer.parseInt(smallestTime) > Integer.parseInt(newestTime)
                 ){
          earliestDateIndex = test;
        }
        test++;
      }
      for(int j = 0; j < copyFinalTable[earliestDateIndex].length; j++){
        //System.out.println(copyFinalTable[earliestDateIndex][j]);
        organized[smallestIndexOrg][j] = copyFinalTable[earliestDateIndex][j];
      }
      smallestIndexOrg = smallestIndexOrg + 1;

      String[][] copyFinalTable1 = new String[copyFinalTable.length-1][copyFinalTable[0].length];
      int counter = 0;
      for(int k = 0; counter < copyFinalTable.length-1; k++){
        if(k != earliestDateIndex){
          for(int l = 0; l < copyFinalTable[0].length; l++){
            copyFinalTable1[counter][l] = copyFinalTable[k][l];
          }
          counter = counter + 1;
        }
      }
      copyFinalTable = copyFinalTable1;
      test = 0;
      earliestDateIndex = 0;
      if(copyFinalTable.length < 1){
        finished = true;
      }
    }
    finalTable = organized;
  }

  public void findTotalByStudio(){
    indTot = new String[numberOfStudios][teacherNames.size()+1][14];
    String[] stuNames = new String[10];

    //Finds all studio names
    stuNames[0] = finalTable[0][7];
    for(int i = 1; i < finalTable.length; i++){
      int counter = 1;
      boolean foundStu = false;
      for(int x = 0; x < stuNames.length; x++){
        if(stuNames[x]!=null){
          if(finalTable[i][7].equals(stuNames[x])){
            foundStu = true;
          }
        }
      }

      if(foundStu == false && finalTable[i][7]!=null){
        stuNames[counter] = finalTable[i][7];
        counter += 1;
      }
    }

    //Filles each 2D array with all teachers names and the respective studio location
    for(int y = 0; y < indTot.length; y++){
      for(int z = 0; z < teacherNames.size(); z++){
        indTot[y][z][0] = teacherNames.get(z);
        indTot[y][z][13] = stuNames[y];
      }
      indTot[y][indTot[0].length-1][0] = "Overall";
      indTot[y][indTot[0].length-1][13] = stuNames[y];
    }

    //Add pay to each individual studio instructors
    for(int s = 0; s < finalTable.length; s++){
      for(int g = 0; g < indTot.length; g++){
        if(indTot[g][0][13]!=null && indTot[g][0][13].equals(finalTable[s][7])){
          for(int e = 0; e < indTot[0].length; e++){
            if(indTot[g][e][0].equals(finalTable[s][3])){
              if(finalTable[s][0].substring(0,2).equals("CP")){
                  if(indTot[g][e][1]==null){
                    indTot[g][e][1] = "1";
                    indTot[g][e][2] = "" + finalTable[s][5];
                  }else{
                    indTot[g][e][1] = "" + (Integer.parseInt(indTot[g][e][1]) + 1) + "";
                    indTot[g][e][2] = "" + (Double.parseDouble(indTot[g][e][2]) + Double.parseDouble(finalTable[s][5])) + "";
                  }
              }else if(finalTable[s][0].contains("Private") && !(finalTable[s][0].contains("Semi"))){
                if(indTot[g][e][3]==null){
                  indTot[g][e][3] = "1";
                  indTot[g][e][4] = "" + finalTable[s][5];
                }else{
                  indTot[g][e][3] = "" + (Integer.parseInt(indTot[g][e][3]) + 1) + "";
                  indTot[g][e][4] = "" + (Double.parseDouble(indTot[g][e][4]) + Double.parseDouble(finalTable[s][5])) + "";
                }
              }else if(finalTable[s][0].contains("Semi")){
                if(indTot[g][e][5]==null){
                  indTot[g][e][5] = "1";
                  indTot[g][e][6] = "" + finalTable[s][5];
                }else{
                  indTot[g][e][5] = "" + (Integer.parseInt(indTot[g][e][5]) + 1) + "";
                  indTot[g][e][6] = "" + (Double.parseDouble(indTot[g][e][6]) + Double.parseDouble(finalTable[s][5])) + "";
                }
              }else if(finalTable[s][0].contains("Intro")){
                if(indTot[g][e][7]==null){
                  indTot[g][e][7] = "1";
                  indTot[g][e][8] = "" + finalTable[s][5];
                }else{
                  indTot[g][e][7] = "" + (Integer.parseInt(indTot[g][e][7]) + 1) + "";
                  indTot[g][e][8] = "" + (Double.parseDouble(indTot[g][e][8]) + Double.parseDouble(finalTable[s][5])) + "";
                }
              }
            }
          }
        }
      }
    }

    //Fills each respective instructor time clock column
    for(int w = 0; w < timePayTableInput.length; w++){
      for(int yy = 0; yy < indTot.length; yy++){
        if(timePayTableInput[w][3] != null && timePayTableInput[w][3].contains(indTot[yy][0][13])){
          for(int aa = 0; aa <  indTot[0].length; aa++){
            if(indTot[yy][aa][0].equals(timePayTableInput[w][0])){
              if(!timePayTableInput[w][1].equals("0")){
                if(indTot[yy][aa][9]==null){
                  indTot[yy][aa][9] = "" + (Double.parseDouble(timePayTableInput[w][1])/60);
                  indTot[yy][aa][10] = "" + ((Integer.parseInt(timePayTableInput[w][1])/60) * Double.parseDouble(timePayTableInput[w][2]));
                }else{
                  indTot[yy][aa][9] = "" + (Double.parseDouble(indTot[yy][aa][9]) + (Double.parseDouble(timePayTableInput[w][1])) );
                  indTot[yy][aa][10] = "" + (Double.parseDouble(indTot[yy][aa][10]) + (Integer.parseInt(timePayTableInput[w][1]) * Double.parseDouble(timePayTableInput[w][2])));
                }
              }
            }
          }
        }
      }
    }

    //Adds zeros to null points
    for(int pp = 0; pp < indTot.length; pp++){
      for(int uu = 0; uu < indTot[0].length; uu++){
        for(int nn = 0; nn < indTot[0][0].length; nn++){
          if(indTot[pp][uu][nn]==null){
            indTot[pp][uu][nn] = "0";
          }
        }
      }
    }

    //Computes totals for each instructor
    for(int ii = 0; ii < indTot.length; ii++){
      for(int zz = 0; zz < indTot[0].length; zz++){
        indTot[ii][zz][11] = "" + (Integer.parseInt(indTot[ii][zz][1]) + Integer.parseInt(indTot[ii][zz][3]) + Integer.parseInt(indTot[ii][zz][5]) + Integer.parseInt(indTot[ii][zz][7]));
        indTot[ii][zz][12] = "" + (Double.parseDouble(indTot[ii][zz][2]) + Double.parseDouble(indTot[ii][zz][4]) + Double.parseDouble(indTot[ii][zz][6]) + Double.parseDouble(indTot[ii][zz][8]) + Double.parseDouble(indTot[ii][zz][10]));
      }
    }

    //Computes overall Totals
    for(int h = 0; h < indTot.length; h++){
      for(int k = 0; k < indTot[0].length-1; k++){
        indTot[h][indTot[0].length-1][1] = "" + (Integer.parseInt(indTot[h][indTot[0].length-1][1]) + Integer.parseInt(indTot[h][k][1]));
        indTot[h][indTot[0].length-1][2] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][2]) + Double.parseDouble(indTot[h][k][2]));
        indTot[h][indTot[0].length-1][3] = "" + (Integer.parseInt(indTot[h][indTot[0].length-1][3]) + Integer.parseInt(indTot[h][k][3]));
        indTot[h][indTot[0].length-1][4] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][4]) + Double.parseDouble(indTot[h][k][4]));
        indTot[h][indTot[0].length-1][5] = "" + (Integer.parseInt(indTot[h][indTot[0].length-1][5]) + Integer.parseInt(indTot[h][k][5]));
        indTot[h][indTot[0].length-1][6] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][6]) + Double.parseDouble(indTot[h][k][6]));
        indTot[h][indTot[0].length-1][7] = "" + (Integer.parseInt(indTot[h][indTot[0].length-1][7]) + Integer.parseInt(indTot[h][k][7]));
        indTot[h][indTot[0].length-1][8] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][8]) + Double.parseDouble(indTot[h][k][8]));
        indTot[h][indTot[0].length-1][9] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][9]) + Double.parseDouble(indTot[h][k][9]));
        indTot[h][indTot[0].length-1][10] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][10]) + Double.parseDouble(indTot[h][k][10]));
        indTot[h][indTot[0].length-1][11] = "" + (Integer.parseInt(indTot[h][indTot[0].length-1][11]) + Integer.parseInt(indTot[h][k][11]));
        indTot[h][indTot[0].length-1][12] = "" + (Double.parseDouble(indTot[h][indTot[0].length-1][12]) + Double.parseDouble(indTot[h][k][12]));
      }
    }
  }

  public void test4(){
    for(int i = 0; i < finalTable.length; i++){
      for(int j = 0; j < finalTable[0].length; j++){
        System.out.print(finalTable[i][j] + "   ");
      }
      System.out.println();
    }
  }


  public static void main(String[] args){
    long startTime = System.nanoTime();

    Payroll p = new Payroll();
    p.readFillingCSVFile();
    p.fillPayTables();

    int amnt = p.getStoreCount();
    System.out.println(amnt);
    int index = 0;

    for(int i = 0; i < amnt; i++){

      p.setFiles(i);

      boolean payType = p.findSignUpPay();
      if(payType){
       p.readMemberCSVFile();
       p.fillMemberArray();
      }

      p.readBookingCSVFile();

      p.findBookingIndexes();

      p.fillBookingEventArray();

      p.readTimeClockFile();
      p.fillTimeClockArray();

     }

     p.fillClassesArray();
     p.getPay();

     p.displayClasses();

     //p.test1();

     p.fixArray();

     p.findInstructors();

     p.findTotals();
     //////////
     //p.addNonInstructor();

     p.findTotalsLastColumn();
     p.test4();
     p.organizeFinal();
     //p.test();

     if(amnt > 1){
       p.findTotalByStudio();
     }

     p.writeCSV();

     //p.test3();
     //p.test();

     System.out.println("File Written!");

     long endTime = System.nanoTime();

     System.out.println("Program finished in "+(endTime - startTime)/1000000 + " Milliseconds");


    }

}
