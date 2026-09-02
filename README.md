# filoverf-rsel-med-TCP-i-java

## Gruppe 
Import Gang

 

Navne: Jannik, Nicklas Stage

## Løsningen 

 

Beskriv kort FileClient, FileServer og protokollen. 

 

## AI-agent 

 

### Plan 

 

Hvad foreslog AI? 
den gav os en plan for hvordan vi skulle lave vores filoverførselsprogram, med forslag til hvordan vi kunne implementere klienten og serveren, samt hvordan protokollen skulle se ud.
den foreslog også at vi skulle lave en testplan, og hvordan vi kunne teste vores program.
og filserver og filclient skulle implementeres i java, og at vi skulle bruge TCP til at sende filer mellem klienten og serveren.
samt StreamReader og StreamWriter til at læse og skrive filer. og nogen feijlhåndtering, og at vi skulle bruge en buffer til at sende filen i mindre bidder.

Hvad ændrede I selv i planen? 
vi droppet intejgrations test og simpelifiseret dens forslag så det var nemmer at forstå da den gav os en lang liste med mange flere trin 
 

### Implementering 

 

Hvor hjalp Agent jer mest? 

 

### Kritisk vurdering 

 

Et AI-forslag vi fulgte: 

 

Et AI-forslag vi ændrede eller afviste: 

 

Hvorfor? 

 

## Test 

 

| Test | Resultat |
|---|---|
| Normal fil | |
| Stor fil | |
| Ukendt fil | |
| ../hemmelig.txt | |
| Server ikke startet | | 

 

## Peer review 

 

Vigtigste feedback fra den anden gruppe: 

 

Hvad ændrede vi efter reviewet? 

 

Hvad valgte vi eventuelt ikke at ændre – og hvorfor? 

 

## Refleksion 

 

1. Hvor var AI mest nyttig? 

2. Hvornår skulle I være kritiske over for AI? 

3. Hvordan kontrollerede I, at AI-genereret kode faktisk virkede?

### Trin 1 - Forstå opgaven
Hvad er serverens ansvar?
- Serverens ansvar er at lytte efter andmodeninger fra klienter

Hvad er klientens ansvar?  
- Klientens ansvar er at sende anmodening ud til serverne

Hvad sendes som tekst?  
- Kommandoen 

Hvad sendes som bytes? 
- Filen 

Hvornår afsluttes forbindelsen? 
- Der afsluttes forbindelse når clienten har modtaget  sin anmodning 
