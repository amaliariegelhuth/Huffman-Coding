import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import edu.princeton.cs.algs4.BinaryIn;
import edu.princeton.cs.algs4.BinaryOut;
import java.util.*;


public class Huff {
  HuffTree ht = new HuffTree(0);
  static boolean DEBUG=true;

  PriorityQueue<HuffTree> pq = new PriorityQueue<>();
  class HuffTree implements Comparable<HuffTree>{
    Node top = new Node();
    int size = 1;
    int weight;
    HuffTree(int weight){
      this.weight = weight;

    }

    HuffTree(Node rightChild, Node leftChild, int weight, int size){
      // System.out.println("toppppp:" + top);

      this.top.rightChild = rightChild;
      // System.out.println("toppppp:" + top);
      this.top.leftChild = leftChild;
      this.weight = weight;
      this.size = size;
    }

    public int compareTo(HuffTree tree){
      if (weight >= tree.weight){
        return 1;
      }else {
        return -1;
      }
    }
public String toString(){
  // traverse(top, "");
  return (top.toString()  + "weight:" + weight);
}
    // got this method from the hints, not completely sure how it relates to PS
    public void traverse(Node n, String s) {
      if (n == null) {
        return;
      }
      if (n.rightChild == null && n.leftChild ==null) {
        // System.out.print("CHAR:" + n.character);
        s =  n.character + " " + s;
        frequencyMap.get(n.character).huffCode = s;


      }
      if (n.leftChild != null) {
        // System.out.println(n.leftChild, s + "0");
        traverse(n.leftChild, s + "1");


      }
      if (n.rightChild != null) {
        // System.out.println(n.rightChild, s + "1");
        traverse(n.rightChild, s + "0");
      }
      // System.out.println("THIS IS S:" + s);
  // System.out.println( s);
  //     return s;
    }


    class Node{
      Node rightChild;
      Node leftChild;
      Node parent;
      String character;
      int freq;
      // public void Node(Node rightChild, Node leftChild, Node parent, String character, int freq){
      //   this.rightChild = rightChild;
      //   this.leftChild = leftChild;
      //   this.parent = parent;
      //   this.character = character;
      //   this.freq = freq;
      // }
      public String toString(){
        String out = "";
        // out = out + ("Right child: " + rightChild.toString() + "\n");
        // out = out + ("Left child: " + leftChild.toString() + "\n");
        // out = out + ("Parent: " + parent.toString() + "\n");
        out = out + ("Character: " + character + "\n");
        out = out + ("Frequency: " + freq);
        return out;
      }
    }



  }
  // use this instead of integer
  class Info {
    int freq;
    String huffCode;
    Info(int f){
      freq = f;
      // huffCode = hc;
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

  public void createTree(TreeMap<String, Info> map){
    for (Map.Entry<String, Info> entry : map.entrySet()){
      // System.out.println(entry.getKey());
      // Node n;
      // n.character = entry.getKey();
      // n.freq = entry.getValue();
      // HuffTree ht;
      ht = new HuffTree(entry.getValue().freq);
      ht.top.character = entry.getKey();
      ht.top.freq = entry.getValue().freq;
      pq.add(ht);

    }

    while (pq.size() > 1){
      HuffTree t1 = pq.poll();
      HuffTree t2 = pq.poll();
      HuffTree newHT = new HuffTree(t1.top,t2.top,t1.weight + t2.weight, t1.size + t2.size);
      t1.top.parent = newHT.top;
      t2.top.parent = newHT.top;
      pq.add(newHT);
      // System.out.println(pq.toString());

    }
    HuffTree ht = pq.poll();
    ht.traverse(ht.top, "");
  }
  public Info makeNewInfo(int f){
    Info i = new Info(f);
    return i;
  }
  TreeMap<String, Info> frequencyMap = new TreeMap<>();

  // MAIN METHOD
  // It has to throw IOException since you are using classes that throw IOExceptions.
  public static void main (String[] args) throws IOException {
    Huff h = new Huff();

    // USING FileIOC TO READ IN A FILE
    // Here is some code that shows you how use FileIOC to read in a (not binary) file:
    // Of course, you will want to read a file provided as a command-line argument.
    FileIOC fioc = new FileIOC();
    FileReader fr = fioc.openInputFile("../samples/mississippi.txt");
    // This lets you go through the file character by character so you can count them.
    int c;
    while ((c = fr.read()) != -1) {

      // Example of something to do: print out each character.
      // System.out.println((char) c);
      if (h.frequencyMap.containsKey(Character.toString((char) c))){

        Integer val = h.frequencyMap.get(Character.toString((char) c)).freq;
        Info i = h.makeNewInfo(val + 1);
        h.frequencyMap.put(Character.toString((char) c), i );
      }else{
        Info i = h.makeNewInfo(1);
        h.frequencyMap.put(Character.toString((char) c), i);
      }
      // This would be a good place for STEP 1, putting the code that keeps track of
      // the frequency of each character, storing it in your TreeMap member variable.

    }
    h.createTree(h.frequencyMap);
    for (Map.Entry<String, Info> entry : h.frequencyMap.entrySet()){
      System.out.println(entry.getValue().huffCode);

    }

    /*
    PriorityQueue <HuffTree> pq = new PriorityQueue <HuffTree>();
    for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()){
      HuffTree ht = new HuffTree(n, n.freq);
      pq.add(ht);
      */
    // }

    // while (pq.size() > 1) {
    //   HuffTree t1 = pq.poll();
    //   HuffTree t2 = pq.poll();
    //   // I don't know what the top Node would be??
    //   HuffTree t = new HuffTree(Node n, t1.weight() + t2.weight());
    //   pq.add(t);
    // }

    // HuffTree t = pq.poll()
    // t.traverse(t.top);

    // for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()){
    //
    // }




// System.out.println(frequencyMap.toString());
// for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()){
//   System.out.println(entry.getKey());
//   // Node n;
//   // n.character = entry.getKey();
//   // n.freq = entry.getValue();
//   // HuffTree ht;
//   // ht = new HuffTree();
//   ht.top.character = entry.getKey();
//   ht.top.freq = entry.getValue();
// }
    // You have to close the file, just the way you would in Python.
    fr.close();


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


    // Suppose after you build your Huffman binary tree, the code for T
    // ends up being 101. Here's one way you can print that out to the binary file.
    String t = "101";
    int i = Integer.parseInt(t, 2);
    bo.write(i, t.length());


    // A boolean is the only type that has two values, so you can pass in a boolean
    // to the write() method of the BinaryOut class to print out a single bit.
    // Here's how you could write out 101 using individual bits.
    bo.write(true);
    bo.write(false);
    bo.write(true);


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
