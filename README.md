# Problem Set 7: Huffman coding

## Due Tuesday, November 5 @ 11:59pm

[Click here for an up-to-date version of this README](https://github.com/BC-CSCI-1102-F18-MW/ps7/blob/master/README.md) 

---

## Problem Set Overview

This is a pair problem set. Find one partner to work with. If you do not have a partner, email me, and I will try match you up with someone else who is also looking for a partner. **Please identify both team members in the comments at the top of your files.**

### Goals

In this problem set, you will team up with one other classmate to design and develop a program called `**Huff.java**` which will perform Huffman compression of a text file. In the next problem set (PS8), you will work together to develop a program called `**Puff.java**` which will perform the corresponding decompression.

Algorithms of this kind are sometimes called *codecs*. Huffman coding is a *lossless compression* algorithm that achieves a  respectable rate of compression. In this problem set you will design and develop the Huff program. After completing this problem set and the next, your team's pair of programs should have the property that for every text file `f.txt`

```
> cp f.txt original.txt      # makes a copy of f.txt and call it original.txt
> java Huff f.txt            # creates compressed file f.zip
> java Puff f.zip            # creates uncompressed file f.txt
> diff f.txt original.txt    # compares two files to see if they are different.
>                            # no message means no differences
```
### Files included with this repository

In addition to a hidden `.gitignore` file, this folder contains:

```
Huff.jar        README.md       samples/
Puff.jar        img/            src/
```

The `src` directory contains `FileIO.java`, an interface (ADT) for reading input and writing output, and an implementation of that interface called `FileIOC.java`. You can use this class for reading in the file you want to compress and for writing out the compressed file. (You will also use this class to read in a compressed file and write it out to uncompressed format in the next problem set.) I demonstrate how to use this class in the `Huff.java` file provided.

In addition, the `src` directory contains well commented starter code for `Huff.java`. You might not use all this code, but it will help you read in a file character by character and write out to a binary file. 

The `samples` directory contains a few test files that you can use to test your code.

The file `Huff.jar` is a Java archive containing a reference implementation of the *compression* half of the codec. The file `Puff.jar` is a Java archive containing a reference implementation of the *decompression* half of the codec. These `.jar` files contain only compiled Java classes. You will not be able to view the actual code without sneaky efforts.

You can use these .jar files to test whether your code is doing what it should be doing. If you can compress a file with *your* `Huff.java` implementation, and decompress it with the included `Puff.jar` file, then your code works. You can also inspect the output of `Huff.jar` and compare it to the output of your code for `Huff.java`.

Without writing any code, you can try out either half of the codec from a command line as follows.

```
> cp samples/lincoln.txt .              # make a copy of lincoln.txt in your current directory
> java -jar Huff.jar lincoln.txt        # this produces file lincoln.zip
> rm lincoln.txt                        # this deletes the text file you just zipped
> java -jar Puff.jar lincoln.zip        # this produces a new file lincoln.txt
> diff lincoln.txt samples/lincoln.txt  # compare the two files
>                                       # no differences :)
```

### Three tasks

There three tasks involved in this problem set:

1. Reading in a text file and keeping track of how many times you see each character in order to create a frequency table.

2. Building a binary tree that you will then traverse to determine the bit sequence representing each character in your text (i.e., the Huffman coding for your input text), as demonstrated in class.

3. Writing this information, along with a Huffman encoded version of the original text, to a binary file, which will be your compressed file. 

I provide some code to get you started, but this will probably be a difficult problem set. You should start working on it right away. 

You should think carefully about the overall design of the program because much of what you create for this problem set might be shared with the Puff program you will be writing for PS8. Obviously, the shared parts should be encapsulated in separate files with appropriate functions and documentation so that you can use these shared parts in both the Huff and Puff programs.


## Step 1: Creating a frequency table
Create a `HashMap` in `Huff.java` that will serve as your frequency table. It will map a character to information about that character. You should consider creating a class for storing information about a character (e.g., frequency, Huffman code, length of the Huffman code) that can serve as the values in this `HashMap`. 

Read in the input file as shown in `Huff.java` and consider each character. If the character aready exists, add 1 to its current frequency total in that character's value in the `HashMap`. Otherwise, enter 1 in its value in the `HashMap`. Again, you might want to map from characters (or `int`s representing characters) to some class that stores information about the character.


## Step 2: Building the binary Huffman tree
Next you are going to build a Huffman tree that you will be able to traverse to generate the Huffman code (i.e., the sequence of 1s and 0s) for each character, just as shown in class on October 22 and 24. Here is one way to do this:

1. Create  a `HuffTree` class that you can use to implement a binary tree data structure. Here are some elements it probably should contain: 
* A `Node` inner class that contains pointers to right child node, left child node, and parent node, along with a variable to store the character and a variable to store the weight. It will also be helpful to have a `toString()` method on `Node` just for sanity checking.
* A member variable that is a pointer to the top `Node`.
* A member variable keeping track of the size. 
* `HuffTree` should implement `Comparable`, which means that it will need a `compareTo()` method. The `compareTo()` method will compare the weights of two `HuffTree`s so that you can store `HuffTree`s in a Java `PriorityQueue` object. 
* You should have a method that can traverse a `HuffTree` from its top node down to its leaf nodes in order to determine what sequences of left and right turns (i.e., 0s and 1s) were required to arrive at each leaf node. This will be the Huffman code of the character at that leaf node.

2. For each character in your `HashMap`, create a `HuffTree` instance. Initially, the `top` of each `HuffTree` will point at a `Node` that has null pointers for its parent, right child, and left child, and has the character variable set to the character and the weight variable set to the frequency of that character.

3. Put all of these `HuffTree`s in a Java `PriorityQueue`. (*You don't need to create your own priority queue! Use Java's implementation, which you can read about [here](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)*). You can create a `PriorityQueue` by adding elements one by one with add() or by giving it a whole Collection (e.g., ArrayList) of elements as an argument to the constructor.

4. You now have a `PriorityQueue` with one `HuffTree` for each character. While there is more than one `HuffTree` in the `PriorityQueue`, `poll()` off the two `HuffTree`s with the smallest weights, **t1** and **t2**. Construct a new `HuffTree` **t** with **t1** and **t2** as left and right children (respectively) and with weight = t1.weight() + t2.weight(). Insert the new `HuffTree` **t** into the priority queue.

5. After merging all these `HuffTree`s, the `PriorityQueue` now contains exactly one element: the Huffman coding tree for the input text. Remove the remaning `HuffTree` from the priority queue. Recursively walk the coding tree recording the bit path P (i.e., the Huffman code, the sequence of 0s and 1s). 

* When the recursive walk arrives at a leaf with symbol A, you will know the Huffman code (i.e., the path P, the sequence of 0s and 1s) for character A. 

* In the `HashMap` storing the frequency table, you (hopefully) have keys that are characters and values that are instances of some class that stores information about a character. This class should have a variable where you can store its Huffman code, i.e., the binary path P of 0s and 1s. Update character A's Huffman code in the `HashMap` frequency table to record the binary path P that led from the top to leaf A.

6. The `HashMap` storing the frequency table now has the information required to write the variable length codes to the binary output file.

## Step 3: Writing to a binary file
You will use the S&W `BinaryOut` class, an instance of which is created by the `FileIOC` class in `Huff.java`, to print out to the compressed binary file. 

1. Open the binary output file. (Code for this is included in `Huff.java`.)

2. Write out the signature two-byte code (0x0bc0), which identifies the file as our special zip file. (This and all of the following steps that involve writing out to the binary file will use the overloaded `write()` method  in the `BinaryOut` class, as shown in the `Huff.java` code I have provided.)

3. Write out, as an integer (32 bits, i.e., 4 bytes), the number of symbols in the frequency table HashMap.

4. Next write out the symbol frequency information. For each key in the symbol table, write the key (i.e., the character) using one byte (i.e., as a char) and write its integer frequency using 4 bytes (i.e., as an int).

5. Reopen the input file.

6. For each occurrence of a character in the input file, look up its bit pattern in the frequency table and write it out to the binary file. You have two options for doing this using the `write()` method in `BinaryOut`.

* You can proceed through the string of 0s and 1s, print out each as a boolean: false for 0 and true for 1.

* You can convert the string of 0s and 1s to an int using `Integer.parseInt`, with the first argument being the string of 0s and 1s and the second argument being the radix, 2. This will create an int that corresponds to the value expressed by string of 0s and 1s in binary. You can then call the `write()` method of `BinaryOut`


#### Working with Binary Files

The Huff and Puff programs write and read binary files. So it will be helpful to have a tool that lets you view the contents of binary files. 

On a Mac or Unix system, there is a command line utility called `hexdump` that you can use to take any file and print out its hexadecimal representation. Suppose you used the `Puff.jar` to compress the sample file `lincoln.txt`. From a command line you can type:

`hexdump lincoln.zip` 

and the hexadecimal version 

If you're using Windows (or if emacs isn't on your system or doesn't excite you) you can always troll around on the web for free hex-editors. I found <a href="http://download.cnet.com/HxD-Hex-Editor/3000-2352_4-10891068.html">HxD</a> for Windows and <a href="http://www.macupdate.com/app/mac/6455/hexeditor">HexEditor</a> for MacOS. I tried the latter and it worked well enough. I didn't try the former but the reviews seemed OK.

You'll find a `FileIO` ADT in the `src` directory. There are four routines there to support the IO required of both your programs. 


#### Working with Bits and Variable Length Patterns of Bits

Huffman coding represents text using variable length bit strings. Short bit strings can be represented in high-level programming languages like Java by using values of type `int`. Ints in Java are 32 bits long. Dr. Java's *Interactions* window is an excellent tool for experimenting with binary representations. Note the use of the `String.format` function together with the hexadecimal field specifier `%x`.

![interactions](./img/interactions.jpeg)

We can turn bits on and move them around using bit-wise or and multiplication (or division) by 2.

#### An ADT for Variable Length Strings of Bits

Huffman codes are *variable length* codes. For example, the letter `'A'` may be represented by the 3-bit string `101` while the letter `'B'` may be represented by the 2-bit string `11`. So in order to represent a variable length bit string in Java it will be convenient to pair the int described above with a second integer specifying the length of the string of bits.

Of course, when we have two values that are related in this way, it usually makes sense to think about encapsulating them in an ADT with appropriate operations. As you know, in Java, an ADT can be specified by an interface and implemented by a class.

#### Symbol Tables --- Representing Information about Input Symbols

Both the Huff and Puff programs will require a table data structure that allows them to look up information about symbols (i.e., characters) that occur in the input text. Tables that associate symbols with information are usually called *symbol tables*. This is the subject of section 3.1 of our textbook.  For the purposes of this project, the symbols are characters. As you know, characters are represented by small integers, usually 8 or 16 bits. For example, the ASCII-assigned integer representation of the letter 'A' is 65. This is a base 10 numeral, it is more common to use its hexadecimal equivalent 0x41. (Note that 0x41 = 4 x 16^1 + 1 x 16^0 = 4 x 16 + 1 x 1 = 64 + 1 = 65.)  In order to print the character associated with one of these numbers, the number would need to be associated with the **char** type.  For most purposes in this application though, you will find it more convenient to work with the characters in the source file as **int**s or even wrapped as **Integer**s.

There are many ways to implement symbol tables in Java. For the purposes of this problem set, you'll want to look at the `java.util.Map<K, V>` interface. You can use any implementing class that you would like but I recommend using the `java.util.HashMap<K, V>` implementation.

What information will you need to store in the symbol table? Two different pieces of information about each input symbol will be required in the Huff program. First, an integer **frequency** will need to be computed that represents the number of occurrences of the given symbol in the input text. The second piece of information required for each symbol is the **binary bit pattern** assigned to the symbol by the algorithm. This latter piece will ultimately be written to the output file.

The frequency information is easily computed from the input file simply by reading the characters in the file and counting their occurrences. The frequency information will be needed in order to construct the Huffman coding tree as described below.

The binary bit pattern can be represented as an object encapsulating the pair of ints as described above. Since we now have another pair of related values (symbol frequency and bit string), it again makes sense to think of encapsulating these two items in an ADT of some kind.

#### Huffman Trees

Another important ingredient for the program is the Huffman coding tree.  A Huffman tree is a simple binary tree data structure in which each node has an integer weight. Huffman trees are `Comparable`: one tree is compared against another by comparing their weights.

What other fields are required? Leaf nodes require an additional integer symbol field while interior nodes (i.e., non-leaf nodes) require two Huffman trees left and right. In order to simplify the trees it's probably best to just have one tree node type with all 4 fields. When processing leaf nodes the symbol and weight fields would be used and the left and right fields would be ignored. For interior nodes weight, left and right fields would be used and the symbol field would be ignored.

I would like to stress the importance of having a reasonable `toString` function for Huffman trees. It will be essential for debugging.

As we have discussed, the Huffman coding tree can be constructed from the information in the symbol table using a priority queue. See the description of the algorithm below.

#### Priority Queues

See the [Java JRE documentation](http://docs.oracle.com/javase/7/docs/api/index.html?overview-summary.html) of the `java.util.PriorityQueue<E>` class.

You're done! Give your code a once over to make sure that it looks great, then push it to the GitHub master repo.
