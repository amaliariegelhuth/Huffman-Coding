import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import java.util.*;
/*
Amalia Riegelhuth
amaliariegelhuth

Isabel Bulman
ibulman

CSCI 1102 Computer Science 2


*/
public class Huff {

  static boolean DEBUG=true;
  //priority queue to store all the huff trees
  static PriorityQueue <HuffTree> pq = new PriorityQueue <HuffTree>();
  static class HuffTree implements Comparable<HuffTree>{
    Node top; //top node
    int size = 1;//huff tree size
    int weight; //weight of whole tree

    HuffTree(int weight){ //constructor used when making huff trees for all of the characters
      this.weight = weight;
      top = new Node();
    }
    HuffTree(Node leftChild, Node rightChild, int weight){ //constructor used when combining trees
      top = new Node();
      top.leftChild = leftChild;
      top.rightChild = rightChild;
      this.weight = weight;
    }
    public int getWeight(){
      return weight;
    }
    public int compareTo(HuffTree tree){ //implementing comparable
      if (weight >= tree.weight){
        return 1;
      } else {
        return -1;
      }
    }

    public void traverse (Node n, String s, TreeMap<String, Info> map) {
      if (n == null) {
        return;
      }
      if (n.rightChild == null && n.leftChild == null) { //if it is a leaf, set the character's huffcode info to the string
        map.get(n.character).huffCode = s;
      }
      if (n.leftChild != null) { //if there is a left child, keep traversing with the left child and add 0 to string
        traverse(n.leftChild, s + "0", map);
      }
      if (n.rightChild != null) { //if there is a right child, keep traversing with the right child and add 1 to string
        traverse(n.rightChild, s + "1", map);
      }

    }

    class Node{
      Node rightChild;
      Node leftChild;
      Node parent;
      String character;
      int freq;

      public String toString(){
        String out = "";
        out = out + ("Right child: " + rightChild + "/n");
        out = out + ("Left child: " + leftChild + "/n");
        out = out + ("Parent: " + parent + "/n");
        out = out + ("Character: " + character + "/n");
        out = out + ("Frequency: " + freq);
        return out;
      }
    }
  }
  //stores information about the character's frequency and huffcode
  public static class Info {
    int freq;
    String huffCode;
    public Info(int f){ //constructor for when we first add the characters and their info to the map
      freq = f;

    }
    public Info(int f, String h){ //
      freq = f;
      huffCode = h;

    }
    public int getFreq() {
      return freq;
    }
    public void setFreq(int n) {
      freq = n;
    }
    public String getHuffCode() {
      return huffCode;
    }
    public void setHuffCode(String s) {
      huffCode = s;
    }
  }

  public static void createTree(TreeMap<String, Info> map){
    for (Map.Entry<String, Info> entry : map.entrySet()){ //for each entry in the frequency map, make a huff tree with weight as the frequency and set the tree's top node info to the info from the entry
      HuffTree ht = new HuffTree(entry.getValue().getFreq());
      ht.top.character = entry.getKey();
      ht.top.freq = entry.getValue().getFreq();
      pq.add(ht); //then add it to the priority queue
    }
    while (pq.size() > 1){ //for each tree in priority queue
      HuffTree t1 = pq.poll();
      HuffTree t2 = pq.poll();
      HuffTree newHT = new HuffTree(t1.top, t2.top, t1.weight + t2.weight); //make a tree from the two smaller trees and set the weight as the sum of the two small ones
      pq.add(newHT); //add this new tree to the priority queue
    }
  }

  // MAIN METHOD
  // It has to throw IOException since you are using classes that throw IOExceptions.
  public static void main (String[] args) throws IOException {

    // USING FileIOC TO READ IN A FILE
    // Here is some code that shows you how use FileIOC to read in a (not binary) file:
    // Of course, you will want to read a file provided as a command-line argument.
    FileIOC fioc = new FileIOC();
    FileReader fr = fioc.openInputFile("../samples/lincoln.txt");
    TreeMap<String, Info> frequencyMap = new TreeMap<>(); //map that holds the characters (as strings) and their frequencies
    // This lets you go through the file character by character so you can count them.
    int c;
    while ((c = fr.read()) != -1) {
      if (frequencyMap.containsKey(Character.toString((char) c))){ //if the map already has the character, just increase its frequency by 1
        Integer val = frequencyMap.get(Character.toString((char) c)).freq;
        Info i = new Info(val + 1);
        frequencyMap.put(Character.toString((char) c), i );
      }else{ //add the character and in the map with a frequency of 1
        Info i = new Info(1);
        frequencyMap.put(Character.toString((char) c), i);
      }
      // This would be a good place for STEP 1, putting the code that keeps track of
      // the frequency of each character, storing it in your HashMap member variable.

    }
    createTree(frequencyMap); //greates huff tree
    HuffTree t = pq.poll(); //takes the remaining tree out of the priority queue
    t.traverse(t.top, "", frequencyMap); //traverses the tree and sets all of the characters' huffcode




    // Here's where you want to do your STEP 2. Don't forget that you
    // will want to create a separate class for HuffTree outside this class.

    // USING FileIOC TO WRITE OUT TO A BINARY FILE

    // Here is some code that shows you how to use FileIOC to write out a binary file.
    // Below are just some examples of what you can do with BinaryOut. You don't need
    // to use all of this code, of course.

    // BTW, this is where you'll be doing STEP 3.

    // FileIOC uses the S&W BinaryOut class, which lets you write to binary a file.
    // FileIOC will automatically open a file with the same name as your input file
    // but it will replace .txt with .zip.
    BinaryOut bo = fioc.openBinaryOutputFile();

    // The BinaryOut class has a write() method that can print out all the
    // primitive data types to binary. Here are some examples, below.

    // This line prints out the "signature" two bytes that begin one of our zip files.
    // A short is a two-byte datatype, so use a short.
    short s = 0x0bc0;
    bo.write(s);
    bo.write(frequencyMap.size());


    for (Map.Entry<String, Info> entry : frequencyMap.entrySet()){
      bo.write(entry.getKey().charAt(0), 8); //for each entry key, writes it in 8 bits
      bo.write(entry.getValue().getFreq()); //writes the values frequency
    }
    FileIOC newfioc = new FileIOC();
    FileReader newfr = newfioc.openInputFile("../samples/lincoln.txt");
    while ((c = newfr.read()) != -1) { //goes through all the characters and translates them to huffcode which is translated by the methods to hex
      String huff = frequencyMap.get(Character.toString((char) c)).getHuffCode();
      int huffInt = Integer.parseInt(huff, 2);
      bo.write(huffInt, huff.length());
    }

    // Suppose after you build your Huffman binary tree, the code for T
    // ends up being 101. Here's one way you can print that out to the binary file.
    // String t = "101";
    // int i = Integer.parseInt(t, 2);
    // bo.write(i, t.length());


    // A boolean is the only type that has two values, so you can pass in a boolean
    // to the write() method of the BinaryOut class to print out a single bit.
    // Here's how you could write out 101 using individual bits.
    // bo.write(true);
    // bo.write(false);
    // bo.write(true);
    // You have to close the file, just the way you would in Python.
    fr.close();


    // One last thing: files have to be written in bytes not bits. The BinaryOut
    // write() method will bunch your bits together into bytes for you, but
    // when you write out your compressed file, so you might end up with some
    // number of bits that isn't divisible by 8. Thus, at the end of your file,
    // you need to "flush" the output, as shown below. flush() will add 0s at
    // the end of your file until you complete a byte.
    bo.flush();

    // Do this to close the output stream.
    bo.close();


  }

}
