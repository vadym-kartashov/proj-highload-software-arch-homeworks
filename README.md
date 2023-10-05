# hla20 Data Structures and Algorithms

## BST and Counting Sort
Implement class for Balanced Binary Search Tree that can insert, find and delete elements. <br/>
Generate 100 random datasets and measure complexity <br/>
Implement Counting Sort algorithm <br/>
Figure out when Counting Sort doesn’t perform. <br/>
### Binary search tree
Implementation is provided under [BalanceBinarySearchTree](src/main/java/org/vkartashov/collections/BalancedBinarySearchTree.java).
Testing is done on 4 different datasets:
1) Sorted data is inserted into tree
2) Reverse sorted data is inserted into tree
3) Sorted data is shuffled and inserted into tree
4) Random data is inserted into tree
#### Search Min Value from range (1% out of N)
![SearchMinValue0%](img/SearchMinValue0%25.jpg)
#### Search Avg Value (25% out of N)
![SearchAvgValue25%](img/SearchAvgValue25%25.jpg)
#### Search Mid Value from range (50% out of N)
![SearchMidValue50%](img/SearchMidValue50%25.jpg)
#### Search Max Value from range (100% out of N)
![SearchMaxValue100%](img/SearchMaxValue100%25.jpg)
#### Search avg value after rebalancing trees (25% out of N)
![SearchAvgValue25Balanced](img/SearchAvgValue25%25Balanced.jpg)
### Counting Sort
Counting sort performs bad in case if there is a big range of values in array
Data is tested on 2 different datasets:
1) Dataset where range of possible values is less than size of dataset (Best case)
2) Dataset where range of possible values is bigger than size of dataset (Worst case)
####
![countingSort](img/countingSort.jpg)