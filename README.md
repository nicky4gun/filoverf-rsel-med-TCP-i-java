# filoverf-rsel-med-TCP-i-java

## Gruppe 
Import Gang

 

Navne: Jannik, Nicklas Stage

## Løsningen 

 

Beskriv kort FileClient og FileServer. 

## FileClient 
Prøver at finde en fil  
Gemmer filen i downloades 
### Består af connectToserver, sendRequest, saveReceivedFile 

### connectToServer 
Klienten prøver at connect to sever ved at lave en ny Socket som består af en HOST og en PORT 
den har en time out  til hvis serveren ikke er oppe 
efter for teller den at den har forbundet til HOST og PORT
og returner socket 

### sendRequest
Klienten sender en filnavn til serveren med navent på filen 
som sender en request  
og får en response 

### saveReceivedFile
Filen bliver sat i mappen der hedder downloads. 
Hvis den download mappe ikke findes så opretter den en downloade mappe. 


## FileServer 
Leder efter denne fil  
Sender filen til Klienten 

### Består af handleClient, sendFile, parseFileName, sendError 
 
### handleClient
- Modtager filnavnet 
- Validere filnavet

### sendFile
Den sender file til clienten med angivet størrelse på filen 


### parseFileName

Tjekker om filnavnet mangler sender den tilbage null vis blank eller null  
Tjekker om den har GET og dens length 

### sendError
Sender en error message ved fejl


## AI-agent 

 

### Plan 

 

**Hvad foreslog AI?**

Den gav os en plan for hvordan vi skulle lave vores filoverførselsprogram, med forslag til hvordan vi kunne implementere klienten og serveren, samt hvordan protokollen skulle se ud.

Den foreslog også at vi skulle lave en testplan, og hvordan vi kunne teste vores program.
og filserver og filclient skulle implementeres i java, og at vi skulle bruge TCP til at sende filer mellem klienten og serveren.
samt StreamReader og StreamWriter til at læse og skrive filer. og nogen feijlhåndtering, og at vi skulle bruge en buffer til at sende filen i mindre bidder.

**Hvad ændrede I selv i planen?**

Vi droppet intejgrations test og simpelifiseret dens forslag så det var nemmer at forstå da den gav os en lang liste med mange flere trin 
 

### Implementering 

 

Hvor hjalp Agent jer mest? 

 tids messigt var AI helt klart en god hjelp 
den var også god til at komme med en forsklaring af hvad Ai ville lave i hvært bullet point 

### Kritisk vurdering 

 

**Et AI-forslag vi fulgte:** 

- PrintWriter kan skjule IO-fejl; log eller tjek flush/fejl ved skrivning

**Et AI-forslag vi ændrede eller afviste:** 
- Server blokerer per klient (ingen tråde/poole) — sænk ventetid for samtidige klienter ved at håndtere hver accept i en ny tråd/Executor.
 

**Hvorfor?** 
1. Vil gerne være sikker på at kunne fange og håndtere alle fejl 
2. Vores program er ikke designet til at skulle have flere klienter, og derfor giver det ikke mening at lave programet om så det passer til flere clienter 
 

## Test 

 

| Test                | Resultat                           |
|---------------------|------------------------------------|
| Normal fil          | OK + gemmes i downloads            |
| Stor fil            | OK + hele filen gemmes i downloads |
| Ukendt fil          | Serverfejl: Filen findes ikke      |
| ../hemmelig.txt     | ERROR: Ugyldigt filnavn            |
| Server ikke startet | Connection refused                 | 

 

## Peer review 

 

Vigtigste feedback fra den anden gruppe: 

 

Hvad ændrede vi efter reviewet? 

 

Hvad valgte vi eventuelt ikke at ændre – og hvorfor? 

 

## Refleksion 

 

1. Hvor var AI mest nyttig? 

2. Hvornår skulle I være kritiske over for AI? 

3. Hvordan kontrollerede I, at AI-genereret kode faktisk virkede?

### Trin 1 - Forstå opgaven
**Hvad er serverens ansvar?**
- Serverens ansvar er at lytte efter anmodninger fra klienter

**Hvad er klientens ansvar?**  
- Klientens ansvar er at sende anmodning ud til serverne

**Hvad sendes som tekst?**  
- Kommandoen 

**Hvad sendes som bytes?** 
- Filen 

**Hvornår afsluttes forbindelsen?**
- Der afsluttes forbindelse når clienten har modtaget  sin anmodning 
