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

  static PriorityQueue <HuffTree> pq = new PriorityQueue <HuffTree>();
  static class HuffTree implements Comparable<HuffTree>{

    Node top;
    int size = 1;
    int weight;

    HuffTree(int weight){
      this.weight = weight;
    }
    HuffTree(Node leftChild, Node rightChild, int weight){
      top = new Node();
      top.leftChild = leftChild;
      top.rightChild = rightChild;
      this.weight = weight;
    }
    public int compareTo(HuffTree tree){
      if (weight >= tree.weight){
        return 1;
      } else {
        return -1;
      }
    }

    public String traverse (Node n, String s) {
      if (n == null) {
        return "";
      }
      if (n.rightChild == null && n.leftChild == null) {
        return s;
      }
      if (n.leftChild != null) {
        traverse(n.leftChild, s + "0");
      }
      if (n.rightChild != null) {
        traverse(n.rightChild, s + "1");
      }
      return "";
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
  // use this instead of integer
  public static class Info {
    int freq;
    String huffCode;
    public Info(int f, String hc){
      freq = f;
      huffCode = hc;
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
    for (Map.Entry<String, Info> entry : map.entrySet()){
      System.out.println(entry.getKey());
      HuffTree ht = new HuffTree(entry.getValue().getFreq());
      ht.top.character = entry.getKey();
      ht.top.freq = entry.getValue().getFreq();
      pq.add(ht);
    }
    while (pq.size() > 1){
      HuffTree t1 = pq.poll();
      HuffTree t2 = pq.poll();
      // Node n1 = t1.top;
      // Node n2 = t2.top;
      HuffTree newHT = new HuffTree(t1.top, t2.top, t1.weight + t2.weight);
      pq.add(newHT);
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
    TreeMap<String, Info> frequencyMap = new TreeMap<>();
    // This lets you go through the file character by character so you can count them.
    int c;
    while ((c = fr.read()) != -1) {

      // Example of something to do: print out each character.
      System.out.println((char) c);
      if (frequencyMap.containsKey(Character.toString((char) c))){
        int val = frequencyMap.get(Character.toString((char) c)).getFreq();
        frequencyMap.put(Character.toString((char) c), new Info(val + 1, ""));
      }else{
        frequencyMap.put(Character.toString((char) c), new Info(1, ""));
      }
      // This would be a good place for STEP 1, putting the code that keeps track of
      // the frequency of each character, storing it in your HashMap member variable.

    }

    createTree(frequencyMap);
    HuffTree t = pq.poll();
    String codeList = t.traverse(t.top, "");
    for (int i = 0; i < codeList.length(); i++){
      HuffTree holder = t;
      // Node pholder = t.top;
      String code = "";
      while (t.top.character == null){
        if (codeList.substring(i, i + 1).equals("0")){
          t.top = t.top.leftChild;
          code = code + "0";
        } else {
          t.top = t.top.rightChild;
          code = code + "1";
        }
      }
      frequencyMap.put(t.top.character, new Info(t.top.freq, code));
    }


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
    bo.write(frequencyMap.size());

    bo.write(frequencyMap.size());
    for (Map.Entry<String, Info> entry : frequencyMap.entrySet()){
      bo.write(entry.getKey(), 8);
      bo.write(entry.getValue().getFreq());
    }
    while ((c = fr.read()) != -1) {
      String huffc = frequencyMap.get(Character.toString((char) c)).getHuffCode();
      if (huffc.equals("0")){
        bo.write(false);
      }
      if (huffc.equals("1")) {
        bo.write(true);
      }
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
