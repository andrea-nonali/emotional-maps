# Emotional Maps
Emotional Maps is a project developed for an imaginary startup. In this startup there is an infrastructure used to collect the level of satisfcation of people in pre-determined places, called POI (points of interest). The goal of this repository is to create a program that, using the proper data strutures, efficiently calculates the emotional state of every POI, relatively to a time window.


# Main data structure 
      
HashMap of Red Black tree, or a java TreeSet. Events with the same year are stored in the same TreeSet and the HashMap collects all of them. 

### Complexity measures
Importing events:
O(1) to find the corresponding TreeSet of an event
O(log n) to add an event. Where n is the number of data in the TreeSet
total = O(log n)
	
Creating an Emotional Map takes O(n + k). Where: 
[first event of TreeSet]< n < [upper bound date]; 
k = [number of years to iterate through ]
