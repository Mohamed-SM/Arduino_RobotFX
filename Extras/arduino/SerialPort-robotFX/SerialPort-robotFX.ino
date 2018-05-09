#include <VarSpeedServo.h>

VarSpeedServo base;
VarSpeedServo shoulder;
VarSpeedServo elbow;
VarSpeedServo pinch;

int potpin = A0;

int baseVal = 90;
int shoulderVal = 90;
int elbowVal = 100;
int pinchVal = 130;

int basePin = 7;
int shoulderPin = 8;
int elbowPin = 12;
int pinchPin = 13;

int UPButton = 2;
int DOWNButton = 5;
int LEFTButton = 3;
int RIGHTButton = 4;

int buttons[] = {2,3,4,5,7,8,12,13};

boolean serialConnected = false;
boolean serialMode = false;
boolean potMode = false;
boolean buttonMode = false;


String msg;

void goHome(){
  moveTo(90,64,168,170);
}

void moveTo(long bVal,int sVal,int eVal, int pVal){

  baseVal = map(bVal,-10,200,0,180);
  shoulderVal = sVal;
  elbowVal = map(eVal,-30,180,0,180);
  pinchVal = pVal;
  
  shoulder.slowmove(shoulderVal,30);
  base.slowmove(baseVal,30);
  elbow.slowmove(elbowVal,60);
  pinch.write(pinchVal);
}

void changeMode(int mode){
  switch(mode){
    case 1:
      serialMode = false;
      potMode = true;
      buttonMode = false;
      break;
    case 2:
      serialMode = false;
      potMode = false;
      buttonMode = true;
      break;
    default:
      serialMode = true;
      potMode = false;
      buttonMode = false;
  }
  digitalWrite(A2,LOW);
  for(int i = 0 ; i < 5; i++){
    digitalWrite(A1,LOW);
    delay(100);
    digitalWrite(A1,HIGH);
    delay(100);
  }
}

void buttonChangeMode(){
  while( digitalRead(LEFTButton) == HIGH){

      if(digitalRead(UPButton) == HIGH){
        changeMode(0);
        break;
      }
      
      if(digitalRead(RIGHTButton) == HIGH){
        changeMode(1);
        break;
      }

      if(digitalRead(DOWNButton) == HIGH){
        changeMode(2);
        break;
      } 
    }
}

void serialModeGo(){
  while(Serial.available() == 0 && serialMode){
      if(!serialConnected){

        digitalWrite(A2,LOW);
        digitalWrite(A1,LOW);
        delay(500);
        digitalWrite(A1,HIGH);
        delay(500);

        buttonChangeMode();

        
      }else{
  
        digitalWrite(A1,LOW);
        digitalWrite(A2,LOW);
        delay(500);
        digitalWrite(A2,HIGH);
        delay(500);
        
      }
    }
    
    
    msg = Serial.readString();
    
    if(msg.substring(msg.length()-5,msg.length()).equals("start")){
      
      digitalWrite(A2,HIGH);
      digitalWrite(A1,LOW);
      serialConnected = true;
    
    }
    else if(msg.equals("stop")){

      Serial.println("gouing home");
      goHome();
      serialConnected = false;
      Serial.println("stoping");
      
    
    }else{
      
      digitalWrite(A2,HIGH);
      Serial.print("msg : ");
      Serial.println(msg);
      
      if(msg.length() == 15){
        String strbaseval = msg.substring(0,3);
        baseVal = strbaseval.toInt();
        Serial.print("base : ");
        Serial.print(baseVal);  
        
        String strshoulder = msg.substring(4,7);
        shoulderVal = strshoulder.toInt();
        Serial.print(", shoulder : ");
        Serial.print(shoulderVal);  
        
        String strelbow = msg.substring(8,11);
        elbowVal = strelbow.toInt();
        Serial.print(", elbowVal : ");
        Serial.print(elbowVal);
        
        String strpinch = msg.substring(12,15);
        pinchVal = strpinch.toInt();
        Serial.print(", pinchVal : ");
        Serial.println(pinchVal);

        moveTo(baseVal,shoulderVal,elbowVal,pinchVal);
        
      }
          
      delay(1000);
    }
}

void potModeGo(){
  
  digitalWrite(A1,LOW);
  digitalWrite(A2,HIGH);
  
  while (digitalRead(basePin) == HIGH) {
    digitalWrite(A1,HIGH);
    digitalWrite(A2,LOW);
    baseVal = analogRead(potpin);
    baseVal = map(baseVal, 0, 1023, 0, 180);
    base.slowmove (baseVal,90);
    //base.write(val);
    Serial.print("moving base : ");
    Serial.println(baseVal);
    delay(1);
  }
  
  while (digitalRead(shoulderPin) == HIGH) {
    digitalWrite(A1,HIGH);
    digitalWrite(A2,LOW);
    shoulderVal = analogRead(potpin);
    shoulderVal = map(shoulderVal, 0, 1023, 80, 180);
    shoulder.slowmove (shoulderVal,30);
    //shoulder.write(val);
    Serial.print("moving shoulder : ");
    Serial.println(shoulderVal);
    delay(1);
  }


  while (digitalRead(elbowPin) == HIGH) {
    digitalWrite(A1,HIGH);
    digitalWrite(A2,LOW);
    elbowVal = analogRead(potpin);
    elbowVal = map(elbowVal, 0, 1023, 50, 180);
    elbow.write(elbowVal);
    Serial.print("moving elbow : ");
    Serial.println(elbowVal);
    delay(1);
  }


  while (digitalRead(pinchPin) == HIGH) {
    digitalWrite(A1,HIGH);
    digitalWrite(A2,LOW);
    pinchVal = analogRead(potpin);
    pinchVal = map(pinchVal, 0, 1023, 120, 180);
    pinch.write(pinchVal);
    Serial.print("moving pinch : ");
    Serial.println(pinchVal);
    delay(1);
  }
}

void buttonModeGo(){
  digitalWrite(A1,LOW);
  digitalWrite(A2,HIGH);
  
  while (digitalRead(basePin) == HIGH) {
    digitalWrite(A1,LOW);
    if(digitalRead(LEFTButton) ==HIGH && baseVal < 180){
       digitalWrite(A1,HIGH);
       base.write(baseVal++);
       delay(30);
    }
    
    if(digitalRead(RIGHTButton) == HIGH && baseVal > 00){
      digitalWrite(A1,HIGH);
      base.write(baseVal--);
      delay(30);
    }
    Serial.print("moving base : ");
    Serial.println(baseVal);
    delay(1);
  }
  
  while (digitalRead(shoulderPin) == HIGH) {
    digitalWrite(A1,LOW);
    if(digitalRead(UPButton) ==HIGH && shoulderVal > 90){
       digitalWrite(A1,HIGH);
       shoulder.write(shoulderVal--);
       delay(30);
    }
    
    if(digitalRead(DOWNButton) == HIGH && shoulderVal < 180){
      digitalWrite(A1,HIGH);
      shoulder.write(shoulderVal++);
      delay(30);
    } 
    Serial.print("moving shoulder : ");
    Serial.println(shoulderVal);
 
  }


  while (digitalRead(elbowPin) == HIGH) {
    digitalWrite(A1,LOW);
    if(digitalRead(UPButton) ==HIGH && elbowVal > 45){
       digitalWrite(A1,HIGH);
       elbow.write(elbowVal--);
       delay(10);
    }
    
    if(digitalRead(DOWNButton) == HIGH && elbowVal < 180){
      digitalWrite(A1,HIGH);
      elbow.write(elbowVal++);
      delay(10);
    }
    Serial.print("moving elbow : ");
    Serial.println(elbowVal);
    
  }


  while (digitalRead(pinchPin) == HIGH) {
    digitalWrite(A1,LOW);
    if(digitalRead(RIGHTButton) ==HIGH && pinchVal < 180){
       digitalWrite(A1,HIGH);
       pinch.write(pinchVal+=1);
       delay(5);
    }
    
    if(digitalRead(LEFTButton) == HIGH && pinchVal > 120){
      digitalWrite(A1,HIGH);
      pinch.write(pinchVal-=1);
      delay(5);
    }
    Serial.print("moving elbow : ");
    Serial.println(pinchVal);
    delay(1);
  }
}

void setup() {

  for(int i  = 0 ; i < 8 ; i++){
    pinMode(buttons[i],INPUT);
  }

  Serial.begin(115200);
  
  pinMode(13,OUTPUT);
  pinMode(A1,OUTPUT);
  pinMode(A2,OUTPUT);
  
  while (!Serial) {
     // wait for serial port to connect. Needed for native USB port only
  }
  Serial.println("Connection Ok");
 
  base.attach(6);
  shoulder.attach(9);
  elbow.attach(10);
  pinch.attach(11);
  
  goHome();
  delay(100);

  changeMode(0);
}

void loop() {

  buttonChangeMode();
  
  if(serialMode){
    serialModeGo();
  }

  if(potMode){
    potModeGo();
  }
  
  if(buttonMode){
    buttonModeGo();
  }
}

