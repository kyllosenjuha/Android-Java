#include <DS3232RTC.h>
#include <LiquidCrystal_PCF8574.h>
#include <Wire.h>

LiquidCrystal_PCF8574 lcd(0x3F); // set the LCD address to 0x3F for a 16 chars and 2 line display
DS3232RTC myRTC;

const int RELE_1 = 4;
const int RELE_2 = 5;
const int RELE_3 = 6;
const int RELE_4 = 7;

int RELE_STATUS[] = {101, 102, 103, 104};

void setup() {

  Serial.begin(9600);
 
  lcd.begin(16, 2);
  lcd.setBacklight(255);
  lcd.home();
  lcd.clear();
  
  myRTC.begin();
  
  setSyncProvider(myRTC.get);   // the function to get the time from the RTC
  if(timeStatus() != timeSet) {
     Serial.println("Unable to sync with the RTC");
  } else {
     Serial.println("RTC has set the system time");
  }
  
  pinMode(RELE_1, OUTPUT);
  pinMode(RELE_2, OUTPUT);
  pinMode(RELE_3, OUTPUT);
  pinMode(RELE_4, OUTPUT);
  
  digitalWrite(RELE_1, LOW);
  digitalWrite(RELE_2, LOW);
  digitalWrite(RELE_3, LOW);
  digitalWrite(RELE_4, LOW);
  
  
}

void loop() 
{
  turnRelsOnOff();
  digitalClockDisplay(); 
}

int weekday(int year, int month, int day)
/* Calculate day of week in proleptic Gregorian calendar. Sunday == 0. */
{
  int adjustment, mm, yy;
  
  if (year<2000) 
     year+=2000;
  
  adjustment = (14 - month) / 12;
  mm = month + 12 * adjustment - 2;
  yy = year - adjustment;
  
  return (day + (13 * mm - 1) / 5 + yy + yy / 4 - yy / 100 + yy / 400) % 7;
}

void printStatus()
{
  for(int i=0; i<4; i++)
  {
    Serial.print((char) RELE_STATUS[i]);
  }
}

void turnRelsOnOff()
{
  byte codeWord = 0;
  
  if (Serial.available() > 0) {
    
    codeWord = Serial.read();
    
    // RELE 1, 'a' = ON
    if(codeWord == 97) {
      digitalWrite(RELE_1, HIGH);
      RELE_STATUS[0] = 97;
      printStatus();
    }

    // RELE 2, 'b' = ON
    else if(codeWord == 98) {
      digitalWrite(RELE_2, HIGH);
      RELE_STATUS[1] = 98;
      printStatus();
    }

    // RELE 3, 'c' = ON
    else if(codeWord == 99) {
      digitalWrite(RELE_3, HIGH);
      RELE_STATUS[2] = 99;
      printStatus();
    }

    // RELE 4, 'd' = ON
    else if(codeWord == 100) {
      digitalWrite(RELE_4, HIGH);
      RELE_STATUS[3] = 100;
      printStatus();
    }

    // RELE 1, 'e' = OFF
    else if (codeWord == 101) {
      digitalWrite(RELE_1, LOW);
      RELE_STATUS[0] = 101;
      printStatus();
    }

    // RELE 2, 'f' = OFF
    else if (codeWord == 102) {
      digitalWrite(RELE_2, LOW); 
      RELE_STATUS[1] = 102;
      printStatus();
    }

    // RELE 3, 'g' = OFF
    else if (codeWord == 103) {
      digitalWrite(RELE_3, LOW);
      RELE_STATUS[2] = 103;
      printStatus();
    }

    // RELE 4, 'h' = OFF
    else if (codeWord == 104) {
      digitalWrite(RELE_4, LOW);
      RELE_STATUS[3] = 104; 
      printStatus();
    }   

    else if (codeWord == 122) {
      printStatus();
    } 
  }
}

void digitalClockDisplay()
{   
    String nameOfTheDay = "";
    float temperatureInCelsius = 0;
    int numberOfTheDay = weekday();
    
    switch(numberOfTheDay)
    {
      case 2:
        nameOfTheDay = "Mon";
        break;
      case 3:
        nameOfTheDay = "Tue";
        break;
      case 4:
        nameOfTheDay = "Wed";
        break;
      case 5:
        nameOfTheDay = "Thu";
        break;
      case 6:
        nameOfTheDay = "Fri";
        break;
      case 7:
        nameOfTheDay = "Sat";
        break;
      case 1:
        nameOfTheDay = "Sun";
        break;
      default:
        nameOfTheDay = "   ";
        break;
    }
  
    lcd.setCursor(0, 0);

    lcd.print(nameOfTheDay);
    lcd.print(" ");
     
    printDigitsLCD(day());
    lcd.print('.');
    printDigitsLCD(month());
    lcd.print('.');
    lcd.print(year());
    
    lcd.setCursor(0, 1);

    printDigitsLCD(hour());
    lcd.print(':');
    printDigitsLCD(minute());
    lcd.print(':');
    printDigitsLCD(second());
    lcd.print(" ");
    
    temperatureInCelsius = myRTC.temperature() / 4.0;

    if(temperatureInCelsius >= 0.0)
    {
      lcd.print("+");
    }
    
    lcd.print(temperatureInCelsius);
    
    if(temperatureInCelsius > -10.0 && temperatureInCelsius < 10.0)
    {
      lcd.print("C ");
    }
    else 
      {
        lcd.print("C");
      }  
}

void printDigitsLCD(int digits)
{
    // utility function for digital clock display: prints preceding colon and leading 0
  
    if(digits < 10)
    lcd.print('0');
    lcd.print(digits);
}
