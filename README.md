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
Puff.jar        src/
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

Read in the input file as shown in `Huff.java` and consider each character. If the character aready exists, add 1 to its current frequency total in that character's value in the `HashMap`. Otherwise, enter 1 in its value in the `HashMap`. Again, you might want to map from chars to some class that stores information about the character, rather than just to an integer.


## Step 2: Building the binary Huffman tree
Next you are going to build a Huffman tree that you will be able to traverse to generate the Huffman code (i.e., the sequence of 1s and 0s) for each character, just as shown in class on October 22 and 24. Here is one way to do this:

1. Create  a `HuffTree` class that you can use to implement a binary tree data structure. Here are some elements it probably should contain: 
* A `Node` inner class that contains pointers to right child node, left child node, and parent node, along with a variable to store the character and a variable to store the weight. Non-leaf nodes will have null for their character variable, while leaf nodes will have null for their right and left child nodes. Note that it will be helpful to have a `toString()` method on `Node` just for sanity checking. 
* A member variable that is a pointer to the top `Node`.
* A member variable keeping track of the size. 
* `HuffTree` should implement `Comparable`, which means that it will need a `compareTo()` method. The `compareTo()` method will compare the weights of two `HuffTrees` so that you can store `HuffTrees` in a Java `PriorityQueue` object. 
* You should have a method that can traverse a `HuffTree` from its top node down to its leaf nodes in order to determine what sequences of left and right turns (i.e., 0s and 1s) that were required to arrive at each leaf node. This will be the Huffman code of the character at that leaf node.

2. For each character key in your `HashMap`, create a `HuffTree` instance. Initially, the `top` of each `HuffTree` will point at a `Node` that has null pointers for its parent, right child, and left child, and has the character variable set to the character and the weight variable set to the frequency of that character.

3. Put all of these `HuffTree`s in a Java `PriorityQueue`. (*You don't need to create your own priority queue! Use Java's implementation, which you can read about [here](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)*). You can create a `PriorityQueue` by adding elements one by one with add() or by giving it a whole Collection (e.g., ArrayList) of elements as an argument to the constructor.

4. You now have a `PriorityQueue` with one `HuffTree` for each character. While there is more than one `HuffTree` in the `PriorityQueue`, `poll()` off the two `HuffTree`s with the smallest weights, **t1** and **t2**. Construct a new `HuffTree` **t** with **t1** and **t2** as left and right children (respectively) and with weight = t1.weight() + t2.weight(). Insert the new `HuffTree` **t** into the priority queue.

5. After merging all these `HuffTree` objects, the `PriorityQueue` now contains exactly one element: the Huffman coding tree for the input text. Remove the remaning `HuffTree` from the priority queue. Recursively walk the tree recording the bit path P (i.e., the Huffman code, the sequence of 0s and 1s). I have included pseudocode for reading off the leaves of a tree at the end of this README.

* When the recursive walk arrives at a leaf with symbol A, you will know the Huffman code (i.e., the path P, the sequence of 0s and 1s) for character A. 

* In the `HashMap` storing the frequency table, you (hopefully) have keys that are chars and values that are instances of some class that stores information about a character. This class should have a variable where you can store its Huffman code, i.e., the binary path P of 0s and 1s. Update character A's Huffman code in the `HashMap` frequency table to record the binary path P that led from the top to leaf A.

6. The `HashMap` storing the frequency table will now have the information required to write the variable length codes to the binary output file.

## Step 3: Writing to a binary file
You will use the S&W `BinaryOut` class, an instance of which is created by the `FileIOC` class in `Huff.java`, to print out to the compressed binary file. 

1. Open the binary output file. (Code for this is included in `Huff.java`.)

2. Write out the signature two-byte code (0x0bc0), which identifies the file as our special zip file. (This and all of the following steps that involve writing out to the binary file will use the overloaded `write()` method  in the `BinaryOut` class, as shown in the `Huff.java` code I have provided.)

3. Write out, as an integer (32 bits, i.e., 4 bytes), the number of chars (keys) in the frequency table `HashMap`.

4. Next write out the symbol frequency information. For each key in the symbol table, write the key (i.e., the character) using one byte and write its integer frequency using 4 bytes. Remember, a char type is really just a value that corresponds to the decimal ASCII code for that char. To write out a char using only one byte, you can use the version of `write()` in `BinaryOut` that takes two arguments: a char and the number of bits in the char that you want to print out.

5. Reopen the input file, and process it character by character.

6. For each character in the input file, look up its bit pattern in the frequency table and write it out to the binary file. You have two options for doing this using the `write()` method in `BinaryOut`:

* You can proceed through the string of 0s and 1s, print out each as a boolean: false for 0 and true for 1.

* You can convert the string of 0s and 1s to an int using `Integer.parseInt()`, with the first argument being the String of 0s and 1s and the second argument being the int radix, 2. This will create an int that corresponds to the value expressed by string of 0s and 1s in binary. You can then call the `write()` method of `BinaryOut` with two arguments: the int you just created and the number of bits you want to print, which will be length of the original string.

7. Close the file.

---

## Helpful Hints

#### Working with binary files: hexdump

The Huff and Puff programs write and read binary files, so it will be helpful to have a tool that lets you view the contents of binary files so you can make sure you are printing out the correct bits and bytes.

On a Mac or Unix system, there is a command line utility called `hexdump` that you can use to take any file and print out its hexadecimal representation. Suppose you used the `Puff.jar` to compress the sample file `lincoln.txt`. From a command line you can type:

`hexdump lincoln.zip` 

and the hexadecimal version of the file will appear, as demonstrated in class on Monday, October 29.

If you're using Windows, have a look around the web for hex viewers and editors. In the latest version of Windows, there may be a utility included as part of the powershell, so give it a try from the terminal in Atom.

If you're having trouble wrapping your head around all the different bases (decimal, binary, and hex), check out this very useful online conversion tool:

https://www.rapidtables.com/convert/number/

And don't forget to find a handy ASCII table for when you are reading your hexdump and you want to make sure you've written out the right characters.

http://www.asciitable.com


#### Bits of variable length

Huffman codes are *variable length* codes. For example, the letter `'A'` may be represented by the 3-bit string `101` while the letter `'B'` may be represented by the 2-bit string `11`. Remember: you can't just take the string of 0s and 1s for a code, convert it to an int and print it out. If you do, you will be unnecessarily adding lots of extra zeros at the front in order to make it 32 bits. You are trying to write the fewest bits possible to your binary file. 

Some options for printing out only the bits you need are discussed above in item 6 of Step 3.

#### The char data type and ASCII representations

Both the Huff and Puff programs will require a table data structure (a `HashMap`) that allows them to look up information about symbols (i.e., characters) that occur in the input text. As you know, characters are represented by small integers. For example, the ASCII-assigned integer representation of the letter 'A' is 65. Find an ASCII table (like [this one](http://www.asciitable.com)) that includes both decimal and hex values for each character so that you can make sure you're doing everything right. 

Java has a 16-bit (2-byte) char data type. When you write out to the binary file, you are required to use only 8 bits in your frequency table. I discuss a way to write out a 16-bit char to 8 bits in the binary file above, in part 4 of Step 3. 

#### Recursively printing out the leaves of a tree

To print out the leaves of a binary tree you can have a void method whose argument is a node. You can call the method originally by providing the argument top.

```
if the node is null
  return

if the leftchild and rightchild are null
  you are at a leaf, so print out the character value of that node
  
if the left child is not null
  call the method on the left child
  
if the right child is not null
  call the method on the right child
  
```

To actually get the string of zeros and ones, you'll need to have an additional argument that is a string where you can store the 0s and 1s. When you call the method recursively, you'll add 0 or 1 to the string argument, depending on whether you have a left child or a right child.

---

## Final words

This is hard, so get started early. If you can do this, you are doing great!

When you turn your code in, write lots of comments. Please don't ask me about null pointer exception without trying to figure them out yourself by printing stuff out to the screen.
