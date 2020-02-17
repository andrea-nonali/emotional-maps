# Laboratorio_A
This was my firts project at the University and it is called Emotional Maps.


DESCRIPTION

    • GOAL
      
We are given a text file with strings that represents emotional states in some points of Milan. The goal is to parse this strings and store their value in a data structure sorted by date. In addition under your request we create an Emotional Map: a percentage representation of the events that we have stored from a date to another. 


    • DATA STRUCTURE 
      
The data structure we used is an HashMap of Red Black tree (java TreeSet). Events with the same year are stored in the same TreeSet and the HashMap collect all of them. 

    • PERFORMANCE
      
The import of events takes:

O(1) to find the corresponding TreeSet of an event;
O(log n) to add an event. Where n is the number of data in the TreeSet;
total = O(log n).

In the test we did it takes about 7 seconds to parse and store 1 milion events.

	
Creating an Emotional Map takes O(n + k). Where: 

[first event of TreeSet]< n < [upper bound date]; 
k = [number of years to iterate through ]
	
As you can see in "Manuale Tecnico" we manage to create an Emotional Map of about 1 milion events in 372 milliseconds.
