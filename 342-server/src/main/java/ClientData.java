import java.util.ArrayList;

public class ClientData
	{
		// data members
		char guessChar;
		int turns;
		int catIndex;
		int solvedCat = 0;
		String solveByChar;
		String currentCategory;
		String word;
		
		ArrayList<String> solvedCats = new ArrayList<String>();
		ArrayList<String> Words = new ArrayList<String>();
		ArrayList<Integer> numberOfFail = new ArrayList<Integer>();
		
		// constructor
		ClientData()
		{
			turns = 6;
			word = "";
		}	
		
		// resets client data
		void resetClient() {
			turns = 6;
			solvedCat = 0;
			solvedCats = new ArrayList<String>();
			Words = new ArrayList<String>();
			numberOfFail = new ArrayList<Integer>();
	    	numberOfFail.add(3);
	    	numberOfFail.add(3);
	    	numberOfFail.add(3);
		}
	}