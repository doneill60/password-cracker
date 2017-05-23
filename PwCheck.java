import java.io.*;
import java.util.Scanner;
import java.io.IOException;

public class PwCheck{
	//R here is the branching factor 
	int R=42;
	private final DLBNode root= new DLBNode();
	private final Node[] trieRoot = new Node[R];	
	public static void main(String[] args){
		PwCheck d = new PwCheck();
		if(args[0].equals("-find")){
			d.createDLB();
			d.createSwaps();
			d.create();
		}
		if(args[0].equals("-check")){
			d.check();
		}
	}
	/*
	*	Reads in a user password and reports on how long it would take to find using the generator called prior
	*/
	public void check(){
		boolean userWantsMore = true;
		Scanner input;
		boolean found = false;
		StringBuilder answer = new StringBuilder();
		StringBuilder userPassword = new StringBuilder();
		StringBuilder modified = new StringBuilder();
		int i,j,counter;
		Node foundNode;
		while(userWantsMore){
			System.out.println("Please enter a password");
			input = new Scanner(System.in);
			//clear the stringbuilders
			userPassword.setLength(0);
			modified.setLength(0);
			answer.setLength(0);	
			userPassword.append(input.nextLine());
			//make sure input is lowercase
			modified.append(userPassword.toString().toLowerCase());
			//level
			j = 0;
			i = 0;
			found = false;
			Node current[]= trieRoot;
			while(i<current.length){
				if(current[i].getCurrent() == modified.charAt(j)){
					//if found go to next level of the trie
					current = current[i].next;
					//move index variable to next character in user input
					j++;
					//mark as found
					found = true;
					//if at the end report the time
					if(found && j==4){
						foundNode = current[i].getCurrentNode();
						System.out.println("Found in " + foundNode.time+ " ns");
					}
				}
				//else increment and set found to false
				i++;
				found  = false;
			}
			if(!found){
				//count the number of additional passwords up to 10 that need to be provided
				counter = 0;
				//back up
				i=i-1;
				//take first index of every closest match in trie
				j=0;
				while(counter < 11){
					while(current[i].hasChild()){
						current[i] = current[i].next[j];
					}
					//now travel along last array
					foundNode=current[j].getCurrentNode();
					j++;
					System.out.println(foundNode.value + " " + foundNode.time + " ns");
					counter++;
				}
			}
			System.out.println("Would you like to try another password?\n");
			input = new Scanner(System.in);
			answer.append(input.nextLine());
			if(answer.toString().equals("no")){
				break;
			}
		}
	}
	/* 	
	*	Method to swap letters with "pseudo-letters" i.e. swapping 3 with e
	*/
	public void createSwaps(){
		Iterator it = new Iterator();
		boolean atTop = false;
		boolean atRight = false;
		DLBNode nextNode = null;
		DLBNode swapChar = null;
		DLBNode previousNode = null;
		//traverse to bottom left first then make your way up
		while(it.hasChild()){
			it.checkChild();
		}
		while(!atTop){
			while(!atRight){
				//convert uppercase dictionary elements to lowercase
				if(it.getCurrent()> 'z'){
					it.setCurrent((char) (it.getCurrent() ^ 0x20));
				}
				if(it.getCurrent()=='t'){
					//save currents previous node
					previousNode = it.getCurrentNode();
					//save currents next node
					nextNode = previousNode.rightSibling;
					//create new node to share t's children
					swapChar = new DLBNode('7');
					//add it to the right of the current node
					previousNode.rightSibling = swapChar;
					//set new nodes right to be the next node saved above
					swapChar.rightSibling = nextNode;
					//kidnap the previous nodes children
					swapChar.child = previousNode.child;
					
				}
				if(it.getCurrent()=='a'){
					previousNode = it.getCurrentNode();
					nextNode = previousNode.rightSibling;
					swapChar = new DLBNode('4');
					previousNode.rightSibling = swapChar;
					swapChar.rightSibling = nextNode;
					swapChar.child = previousNode.child;
				}
				if(it.getCurrent()=='o'){
					previousNode = it.getCurrentNode();
					nextNode = previousNode.rightSibling;
					swapChar = new DLBNode('0');
					previousNode.rightSibling = swapChar;
					swapChar.rightSibling = nextNode;
					swapChar.child = previousNode.child;
				}
				if(it.getCurrent()=='e'){
					previousNode = it.getCurrentNode();
					nextNode = previousNode.rightSibling;
					swapChar = new DLBNode('3');
					previousNode.rightSibling = swapChar;
					swapChar.rightSibling = nextNode;
					swapChar.child = previousNode.child;
				}
				if(it.getCurrent() == 'i' || it.getCurrent() == 'l'){
					previousNode = it.getCurrentNode();
					nextNode = previousNode.rightSibling;
					swapChar = new DLBNode('1');
					previousNode.rightSibling = swapChar;
					swapChar.rightSibling = nextNode;
					swapChar.child = previousNode.child;
				}
				if(it.getCurrent()=='s'){
					previousNode = it.getCurrentNode();
					nextNode = previousNode.rightSibling;
					swapChar = new DLBNode('$');
					previousNode.rightSibling = swapChar;
					swapChar.rightSibling = nextNode;
					swapChar.child = previousNode.child;
				}
				if(!it.hasSibling()){
					atRight=true;
				}
				else{
					it.checkSibling();
				}
			}
			if(!it.hasParent()){
				atTop=true;
			}
			it.checkParent();
		}	
	}
	/*
	*	Adds some of the most common passwords to prune from guesses since common words
	*	are not valid passwords under guidelines.
	*/
	public void createDLB(){
		DLBNode cur;
		DLBNode parent;
		DLBNode next;
		try{
			File file = new File("./Dictionary.txt");
			//open stream to read from dictionary file
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			cur = root;
			while(is.available()>0){
				//read in one character
				char c = (char)is.read();
				while(1==1){
					//if empty it was meant to be written over
					if(cur.value == '\0'){
						//make sure youre not at the end of the word
						if(c == '\n'){
							//if at any point you encounter this case
							//that means that you can eliminate all subtrees
							//and collapse current linked list into one element
							//if a smaller dictionary word is a subset of a bigger
							//dictionary word, its useless to continue checking the big one
							cur = cur.parent;
							//delimiter for DLB
							cur.child = new DLBNode('~');
							cur.child.parent = cur;
							//start back again
							cur = root;
							break;
						}
						//overwrite the "blank" space
						cur.value = c;
						cur.child = new DLBNode();
						cur.child.parent  = cur;
						cur = cur.child;
						break;
					}
					if(cur.value != c){
						if(cur.rightSibling != null){
							cur = cur.rightSibling;
							continue;
						}
						else{
							//node not in DLB - create it
							cur.rightSibling = new DLBNode(c);
							cur.rightSibling.parent = cur.parent;
							//set right sibling to be the new node
							cur = cur.rightSibling;
							//new entry always goes to the right and then down with the next entry
							cur.child = new DLBNode();
							cur.child.parent = cur;
							cur = cur.child;
							break;	
						}
					}
					//at right node
					else{
						//make sure it isnt null
						if(cur.child == null){
							cur.child = new DLBNode();
							cur.child.parent = cur;
						}
						//go to it
						cur = cur.child;
						break;
					}
				}
			}
		
		}
		catch(FileNotFoundException ex){
			ex.printStackTrace(System.out);
			System.exit(0);
		}
		catch(IOException e){
			e.printStackTrace(System.out);
			System.exit(0);
		}
		
	}
	private class DLBNode{
			public DLBNode parent;
			public char value;
			public DLBNode rightSibling;
			public DLBNode child;
			public DLBNode(char c){
				this.value = c;
			}
			public DLBNode(){};
	}
	private class Iterator{
		private DLBNode cur = root;
		public boolean hasSibling() { return cur.rightSibling != null; }
		public boolean hasChild() { return cur.child != null; }
		public boolean hasParent() { return cur.parent != null; }
		
		public char checkSibling() {
			cur = cur.rightSibling;
			return cur.value;
		}
		
		
		public boolean checkTilde(){
			if(cur.child.value == '~'){
				return true;
			}
			return false;
		}
		
		public void checkParent(){
			cur=cur.parent;
		}
		
		public char getCurrent(){
			return cur.value;			
		}
		
		public void setCurrent(char val){
			cur.value = val;
		}
		
		public DLBNode getCurrentNode(){
			return cur;
		}
		
		public char checkChild() {
			if(!hasChild()) { return '\0'; }
			cur = cur.child;
			return cur.value;
		}
		
		public char levelUp() {
			if(!hasParent()) { return '\0'; }
			cur = cur.parent;
			return cur.value;
		}
		
	}

	public boolean searchDict(Iterator it, char value){
		char current;
		while(it.hasSibling()){
			current = it.checkSibling();
			if(current == value){
				if(it.checkTilde()){
					return true;
				}
			}
		}
		it=null;
		return false;
	}
	/*
	*	Creates an R way trie of all valid passwords
	*/
	public void create(){
		//dictionaries for letters, numbers and special characters in various combinations
		char letnumsp[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','!','@','$','^','_','*'};
		char letnum[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9'};
		char letsp[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','!','@','$','^','_','*'};
		char numsp[] = {'0','1','2','3','4','5','6','7','8','9','!','@','$','^','_','*'};
		char let[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
		Node [] current=trieRoot;
		int letters=0;
		int numbers=0;
		int spchars=0;
		long total = 0;
		int passwordCount=0;
		long start, finish;
		//create new iterator for each level to start checking in the dictionary from then on for dictionary
		//words that are subsets of other words
		Iterator one,two,three,four,five;
		StringBuffer currentPassword = new StringBuffer();
		//buffered writer to write all passwords to
		BufferedWriter passwords = null;
		try{
			passwords = new BufferedWriter(new FileWriter("./all_passwords.txt"));
		}
		catch(Exception e){
			e.printStackTrace(System.out);
		}
		for(int i = 0; i<R; i++){
			//redefine new start time for each 
			start = System.nanoTime();			
			current[i] = new Node();
			//cant hit any requirements here dont check
			current[i].value=letnumsp[i];
			
			//create new iterator for level one
			one = new Iterator();
			
			if(searchDict(one, current[i].value)){
				//put place marker there so you can jump over it later
				current[i].value= '\0';
				continue;
			}
			
			currentPassword.append(current[i].value);
			//update letter, number, and spchar count if its in the appropriate index
			if(i<26){
				letters++;
			}
			if(i>=26 && i<36){
				numbers++;
			}
			if(i>=36){
				spchars++;
			}
			
			//if next array reference doesnt exist, create it
			if(current[i].next == null){
				current[i].next = new Node[R];
			}
			//go to it
			current = current[i].next;
			for(int j = 0; j<R; j++){
				//cant hit any requirements here dont check
				current[j] = new Node();
				current[j].value=letnumsp[j];
				//create new iterator
				two = new Iterator();
				//if the first iterator hasnt died check if it found a word
				if(one!=null){
					if(searchDict(one, current[j].value)){
						current[j].value= '\0';
						System.out.println("It happened");
						continue;
					}
				}
				//search the dictionary for the new iterator
				if(searchDict(two, current[j].value)){
					current[j].value= '\0';
					System.out.println("It happened");
					continue;
				}
				//if it hasnt skipped over the current index, add it to the final password
				currentPassword.append(current[j].value);
				if(j<26){
					letters++;
				}
				if(j>=26 && j<36){
					numbers++;
				}
				if(j>=36){
					spchars++;
				}
				if(current[j].next == null){
					//either special characters or numbers may have been satisfied here
					if(numbers>=2){
						R=32;
					}
					if(spchars>=2){
						R=36;
					}
					current[j].next = new Node[R];
				}
				current = current[j].next;
				
				//third level
				//r may have been altered so we need to check
				for(int k = 0; k<R; k++){
					if(R==42){
						current[k] = new Node();
						current[k].value=letnumsp[k];
					}
					else if(R==32){
						current[k] = new Node();
						current[k].value=letsp[k];
					}
					else{
						current[k] = new Node();
						current[k].value=letnum[k];
					}
					three = new Iterator();
					if(one!=null){
						if(searchDict(one, current[k].value)){
							current[k].value= '\0';
							System.out.println("It happened");
							continue;
						}
					}
					if(two!=null){
						if(searchDict(one, current[k].value)){
							current[k].value= '\0';
							System.out.println("It happened");
							continue;
						}
					}
					if(searchDict(three, current[k].value)){
						current[k].value= '\0';
						System.out.println("It happened");
						continue;
					}
					currentPassword.append(current[k].value);
					if(k<26){
						letters++;	
					}
					//only check if you have a number if the numbers requirement hasnt been satisfied
					if(R!=32){
						if(k>=26 && k<36){
							numbers++;
						}
					}
					//only check if you have a spchar if the spchars requirement hasnt been satisfied
					if(R!=36){
						if(k>=36){
							spchars++;
						}
					}
					if(current[k].next == null){
						//either letters or numbers or special characters may have been satisfied here
						if(numbers>=2){
							R=32;
						}
						if(spchars>=2){
							R=36;
						}
						if(letters>=3){
							R=16;
						}
						current[k].next = new Node[R];
					}
					current = current[k].next;
					for(int l = 0; l<R; l++){
						//4th level
						//r may have been altered so we need to check
						if(R==42){
							current[l] = new Node();
							current[l].value=letnumsp[l];
						}
						else if(R==16){
							current[l] = new Node();
							current[l].value=numsp[l];
						}
						else if(R==32){
							current[l] = new Node();
							current[l].value=letsp[l];
						}
						else{
							current[l] = new Node();
							current[l].value=letnum[l];
						}
						four = new Iterator();
						if(one!=null){
							if(searchDict(one, current[l].value)){
								current[l].value= '\0';
								System.out.println("It happened");
								continue;
							}
						}
						if(two!=null){
							if(searchDict(one, current[l].value)){
								current[l].value= '\0';
								System.out.println("It happened");
								continue;
							}
						}
						if(three!=null){
							if(searchDict(one, current[l].value)){
								current[l].value= '\0';
								System.out.println("It happened");
								continue;
							}
						}
						if(searchDict(four, current[l].value)){
							current[l].value= '\0';
							System.out.println("It happened");
							continue;
						}
						currentPassword.append(current[l].value);
						if(R!=16){
							if(l<26){
								letters++;
							}
						}
						//only check if you have a number if the numbers requirement hasnt been satisfied
						if(R!=32){
							
							if(l>=26 && l<36){
								numbers++;
							}
						}
						//only check if you have a spchar if the spchars requirement hasnt been satisfied
						if(R!=36){
							if(l>=36){
								spchars++;
							}
						}
						if(current[l].next == null){
							//either letters or numbers or special characters may have been satisfied here
							if(numbers>=2 && spchars>=2){
								R = 26;
							}
							if(numbers>=2){
								R=32;
							}
							if(spchars>=2){
								R=36;
							}
							if(letters>=3){
								R=16;
							}
							current[l].next = new Node[R];
						}
						current = current[l].next;
						for(int m = 0; m<R; m++){
							//5th level
							//r may have been altered so we need to check
							if(R==42){
								current[m] = new Node();
								current[m].value=letnumsp[m];
							}
							if(R==16){
								current[m] = new Node();
								current[m].value=numsp[m];
							}
							if(R==32){
								current[m] = new Node();
								current[m].value=letsp[m];
							}
							if(R==26){
								current[m] = new Node();
								current[m].value=let[m];
							}
							if(R==36){
								current[m] = new Node();
								current[m].value=letnum[m];
							}
							five = new Iterator();
							if(one!=null){
								if(searchDict(one, current[m].value)){
									current[m].value= '\0';
									System.out.println("It happened");
									continue;
								}
							}
							if(two!=null){
								if(searchDict(one, current[m].value)){
									current[m].value= '\0';
									System.out.println("It happened");
									continue;
								}
							}
							if(three!=null){
								if(searchDict(one, current[m].value)){
									current[m].value= '\0';
									System.out.println("It happened");
									continue;
								}
							}
							if(four!=null){
								if(searchDict(one, current[m].value)){
									current[m].value= '\0';
									System.out.println("It happened");
									continue;
								}
							}
							if(searchDict(five, current[m].value)){
								current[m].value= '\0';
								System.out.println("It happened");
								continue;
							}
							currentPassword.append(current[m].value);
							//since weve made it this far increment the password counter
							passwordCount++;
							try{
								finish = System.nanoTime();
								total = finish -start;
								current[m].time = total;
								passwords.write(currentPassword.toString() + ", "  + " ns\n");
							}
							catch(Exception e){
								e.printStackTrace(System.out);
							}
							if(R!=16){
								if(m<26){
									letters++;
								}
							}
							//only check if you have a number if the numbers requirement hasnt been satisfied
							if(R!=32 || R!= 26){
								
								if(m>=26 && m<36){
									numbers++;
								}
							}
							//only check if you have a spchar if the spchars requirement hasnt been satisfied
							if(R!=36 || R!= 26){
								if(R==42){
									if(m>=36){
										spchars++;
									}
								}
								if(R==32){
									if(m>=26){
										spchars++;
									}
								}
							}
							//delete last char so next for loop can use it
							currentPassword.deleteCharAt(4);
						}
						currentPassword.deleteCharAt(3);
					}
					currentPassword.deleteCharAt(2);
				}
				currentPassword.deleteCharAt(1);
			}
			currentPassword.deleteCharAt(0);
		}
	System.out.println("Passwords: " + passwordCount);
	}
	private class Node {
		char value;
		Node[] next;
		long time;
		public char getCurrent(){
			return value;
		}
		public Node getCurrentNode(){
			return this;
		}
		public boolean hasChild(){
			return next!= null;
		}
	}
}