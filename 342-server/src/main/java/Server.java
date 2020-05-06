import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class Server{
	// data members
	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ArrayList<ClientData> allData = new ArrayList<>();
	TheServer server;
	private Consumer<Serializable> callback;
	public int port;
	GuessInfo info = new GuessInfo();
	String tempString = null;
	StringBuilder _letterPos;
	
	// constructor for Server
	Server(Consumer<Serializable> call, int portNum){
		port = portNum;
		callback = call;
		server = new TheServer();
		server.start();
	}
	
	public class TheServer extends Thread{
		public void run() {
			try(ServerSocket mysocket = new ServerSocket(port);){
		    System.out.println("Server is waiting for a client! ");
		  
		    while(true) {
				ClientThread c = new ClientThread(mysocket.accept(), count);
				callback.accept("client has connected to server: " + "client #" + count);
				clients.add(c);
				c.start();
				allData.add(c.temp);
				count++;
			    }
			}//end of try
				catch(Exception e) {
					callback.accept("Server socket did not launch");
				}
			}//end of while
		}
	
		// nested clientThread class
		class ClientThread extends Thread{
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			ClientData temp;
			
			// constructor
			ClientThread(Socket s, int count){
				temp = new ClientData();
				this.connection = s;
				this.count = count;	
				
		    	temp.numberOfFail.add(3);
		    	temp.numberOfFail.add(3);
		    	temp.numberOfFail.add(3);
					
			}
			
			// update every clients listview
			public void updateClients(String message) {
				for(int i = 0; i < clients.size(); i++) {
					ClientThread t = clients.get(i);
					try {
					 t.out.writeObject(message);
					}
					catch(Exception e) {}
				}
			}
			
			// update only one clients listview
			public void updateClient(String message, int whichClient) {
				ClientThread t = clients.get(whichClient);
				try {
					 t.out.writeObject(message);
					}
				catch(Exception e) {}
			}
			
			public void run(){
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				
				updateClient("You are Player " + count, count-1);
					
				 while(true) {
					    try {
					    	GuessInfo guessTemp = (GuessInfo) in.readObject();
					    	
					    	// reset all the client data on play again button
					    	info.resetSignal = guessTemp.resetSignal;
					    	if(info.resetSignal == 1) {
					    		allData.get(count-1).resetClient();
					    	}
					    	
					    	// everything happening after category is chosen
					    	if(guessTemp.categChosen) {
								info.infoCategories = guessTemp.infoCategories;
								allData.get(count-1).solveByChar = null;
								_letterPos = new StringBuilder();
								
								// creates array with 3 words from different 3 categories
								allData.get(count-1).Words = new ArrayList<String>();
								String wordOne = info.getWord(info.Animals);
								String wordTwo = info.getWord(info.Food);
								String wordThree = info.getWord(info.Sports);
								
								allData.get(count-1).Words.add(wordOne);
								allData.get(count-1).Words.add(wordTwo);
								allData.get(count-1).Words.add(wordThree);
								
								if(info.infoCategories.equals("Animals")) {
									tempString = allData.get(count-1).Words.get(0);
									allData.get(count-1).catIndex = 0;
									allData.get(count-1).currentCategory = "Animals";
								}
								
								else if(info.infoCategories.equals("Food")) {
									tempString = allData.get(count-1).Words.get(1);
									allData.get(count-1).catIndex = 1;
									allData.get(count-1).currentCategory = "Food";
								}
								
								else if(info.infoCategories.equals("Sports")) {
									tempString = allData.get(count-1).Words.get(2);
									allData.get(count-1).catIndex = 2;
									allData.get(count-1).currentCategory = "Sports";
								}
								
								allData.get(count-1).word = tempString;
								
								// update client and server
					    		updateClient("Client" + count  + ": Length of Word is " + tempString.length(), count-1);
					    		callback.accept("client:" + count + " chose category " + info.infoCategories + 
					    						"\n                 word to guess: " + allData.get(count-1).word);
					    	}
					    	
					    	// when char is sent from the client
					    	if(guessTemp.charChosen) {
					    		allData.get(count-1).guessChar = guessTemp.guess;
					    		char x = allData.get(count-1).guessChar;
					    		// integer that saves all positions
					    		int letterPos = Integer.parseInt(info.checkLetterExists(allData.get(count-1).word, x));
					    		// if char doesn't exist
					    		if(letterPos == 999) {
					    			allData.get(count-1).turns -= 1;
							    	if(allData.get(count-1).turns == 0) {
							    		updateClient("You must guess the word. You dont have any more turns.", count-1);
							    	}
							    	else {
							    		updateClient("Chosen character " + allData.get(count-1).guessChar + " not in the word. You have " + allData.get(count-1).turns + " turns left.", count-1);
							    	}
					    		}
					    		else {
					    			if(!(_letterPos.toString().contains(String.valueOf(letterPos)))) {
					    				_letterPos.append(String.valueOf(letterPos));
					    			}
					    			else {
					    				System.out.println("duplicate");
					    			}
					    			updateClient("Guessed correct, char locations: " + letterPos, count-1);
					    		}
					    	}
					    	
					    	// guessed word by guessing all the chars
					    	if(allData.get(count-1).word.length() == _letterPos.length()) {
					    		guessTemp.wordChosen = true;
					    		guessTemp.clientToServerWord = allData.get(count-1).word;
					    	}
					    	
					    	// if client sent a word to the server
					    	if(guessTemp.wordChosen) {
					    		info.clientToServerWord = guessTemp.clientToServerWord;
					    		// if correct guess
					    		if(allData.get(count-1).word.equals(info.clientToServerWord)) {
					    			StringBuilder strBuild = new StringBuilder(); 
					    			allData.get(count-1).solvedCats.add(allData.get(count-1).currentCategory);
					    			
					    			for(String x : allData.get(count-1).solvedCats) {
					    				strBuild.append(" " + x);
					    			}
					    			
					    			allData.get(count-1).solvedCat++;
					    			updateClient("Word is: " + allData.get(count-1).word,count-1);
					    			updateClient("Correct Guesses on" + strBuild.toString(),count-1);
					    			callback.accept("client:" + count + " guessed correct word");
					    		}
					    		
					    		// wrong guess
					    		else {
					    			int w = allData.get(count-1).numberOfFail.get(allData.get(count-1).catIndex);
					    			allData.get(count-1).numberOfFail.set(allData.get(count-1).catIndex, w-1);
					    			
					    			StringBuilder strBuild2 = new StringBuilder(); 		
					    			for(String l : allData.get(count-1).solvedCats) {
					    				strBuild2.append(" " + l);
					    			}
					    			
					    			if(w-1 == 0) {
					    				updateClient("You lost the game. GoodBye !!!", count-1);
					    				callback.accept("client:" + count + " lost the game");
					    			}
					    			else {
					    				updateClient("Your word guess is wrong! You have left " + (w-1) + " word guesses",count-1);
					    				callback.accept("client:" + count + " guessed wrong");
						    			if(strBuild2.toString().length() == 0) {
						    				updateClient("Correct Guesses on nothing yet",count-1);
						    			}
						    			else {
						    				updateClient("Correct Guesses on" + strBuild2.toString(),count-1);
						    			}
					    			}
			
					    		}
					    		allData.get(count-1).turns = 6;			// reset number of turns on every new word
					    		
					    		// if solvedCat = 3 client won the game
						    	if(allData.get(count-1).solvedCat == 3) {
						    		updateClient("You won the game. Congrats !!!", count-1);
						    		callback.accept("client:" + count + " won the game");
						    	}
					    	}
					    	
					    	}
					    catch(Exception e) {
					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					    	updateClients("Client #"+count+" has left the server!");
					    	clients.remove(this);
					    	break;
					    }
					}
				}//end of run
		}//end of client thread
}


	
	

	
