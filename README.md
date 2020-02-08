# Laboratorio_A
This was my firts project at the University. It is called Emotional Maps.

For deeper information in this repository you can find two Manuals: Manuale Utente (for Users), Manuale Tecnico (for experts),
but they are written in Italian.
Furthermore the code has JavaDoc comments.

DESCRIPTION

    • GOAL
      
Given a text file with strings that represents emotional states in some points of Milan, the goal is to parse this strings, crete events and store it in a data structure sorted by occurrence date. Once we have stored data we create, under your request, an Emotional Map: a percentage representation of the events that we have stored from a lower bound date and an upper bound date. 

    • DATA STRUCTURE 
      
The data structure we used is an HashMap of Red Black tree (java TreeSet) with key--> year and value-->TreeSet of the year.

    • PERFORMANCE
      
The import of events takes O(1) to find the corresponding year of a event and O(log n) where n is the number of data in the corresponding year: total = O(log n).
In the test we did it takes 7 seconds to parse and store 1 milion events.
	
The creation of an Emotional Map takes O(n + k) where n is included form first event of one year and the upper bound event you give us; k is the number of year we have to iterate through.
	
As you can see in "Manuale Tecnico" we manage to create a Map of about 1 milion events in 372 milliseconds.
