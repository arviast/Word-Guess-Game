import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;

class GuessTest {

	GuessInfo test = new GuessInfo();
	ClientData testData = new ClientData();

	@Test
	void LetterExists() {
		assertEquals("23", test.checkLetterExists("pool", 'o'));
	}

	//Guess all letters
	@Test
	void LetterExists2(){
		String testWord = "plus";
		String location = test.checkLetterExists(testWord, 'p');
		location += test.checkLetterExists(testWord, 'l');
		location += test.checkLetterExists(testWord, 'u');
		location += test.checkLetterExists(testWord, 's');

		assertEquals("1234", location);
	}

	//Word with same letter next to each other
	@Test
	void LetterExists3(){
		String testWord = "loop";
		String location = test.checkLetterExists(testWord, 'l');
		location += test.checkLetterExists(testWord, 'o');
		location += test.checkLetterExists(testWord, 'p');

		assertEquals("1234", location);
	}

	//location string should not be in order
	@Test
	void LetterExists4(){
		String testWord = "banana";
		String location = test.checkLetterExists(testWord, 'b');
		location += test.checkLetterExists(testWord, 'a');
		location += test.checkLetterExists(testWord, 'n');

		assertEquals("124635", location);
	}

	@Test
	void LetterDoesNotExist(){
		assertEquals("999", test.checkLetterExists("pool", 'q'));
	}

	@Test
	void PsuedoGame(){
		String testWord = "banana";
		String location = test.checkLetterExists(testWord, 'n');
		location += test.checkLetterExists(testWord, 'q'); //Wrong guess
		location += test.checkLetterExists(testWord, 'b');
		location += test.checkLetterExists(testWord, 'a');

		assertEquals("359991246", location);
	}

	@RepeatedTest(9)
	void GetRandomWord(){
		ArrayList<String> FoodExpected = new ArrayList<>( Arrays.asList("calamari", "caviar", "escargot", "hummus", "jambalaya", "nectarine", "peanuts", "quiche", "zucchini"));
		String wordExists = test.getWord(test.Food);
		assertTrue(FoodExpected.contains(wordExists));
	}

	//Tests to see if the random words are being generated
	@RepeatedTest(2) //Repeated test twice just to make sure its not a coincidence
	void RandomWord(){
		ArrayList<String> FoodExpected = new ArrayList<>( Arrays.asList("calamari", "caviar", "escargot", "hummus", "jambalaya", "nectarine", "peanuts", "quiche", "zucchini"));
		String randWord = test.getWord(test.Food);
		int RandomWordCount = 0;
		for(int i = 0; i < 10; i++) { //Runs the random word generator 10 times. Probability for having the same word 10 times in a row is less than 1 in a billion
			String randWord2 = test.getWord(test.Food);
			if(randWord.equals(randWord2)){
				RandomWordCount++;
			}
		}
		assertNotEquals(10, RandomWordCount); //If the same word is repeated 10 times, you should buy a lottery ticket or something is wrong
	}

	//Grabs a random word and makes guesses.
	@Test
	void RandomWordGuess(){
		ArrayList<Character> guessTest = new ArrayList<>( Arrays.asList('a', 'e', 'i', 'o', 'u', 'y', 'w', 'm', 't')); //Guesses arraylist
		String randWord = test.getWord(test.Food);
		String location = "";
		String allWrongGuess = "999999999999999999999999999";

		for(int i = 0; i < guessTest.size(); i++){
			location += test.checkLetterExists(randWord, guessTest.get(i)); //Should never contain only 999
		}

		assertNotEquals(allWrongGuess, location);
	}

	//Checks to see if the ClientDataReset method is working properly
	@Test
	void ClientDataReset(){
		testData.turns = 2;
		testData.solvedCat = 3;
		testData.solvedCats.add("Hello World");
		testData.Words.add("Pudding");
		testData.numberOfFail.add(12);
		testData.resetClient();

		ArrayList<Integer> ExpectedNumOfFail = new ArrayList<>( Arrays.asList(3,3,3));
		assertEquals(6, testData.turns);
		assertEquals(0, testData.solvedCat);
		assertTrue(testData.solvedCats.isEmpty());
		assertTrue(testData.Words.isEmpty());
		assertEquals(ExpectedNumOfFail ,testData.numberOfFail);
	}
}
