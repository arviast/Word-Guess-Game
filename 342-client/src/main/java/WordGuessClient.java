import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WordGuessClient extends Application {
	// members
	// ...
	private TextField ipField, portNum, charGuess;
	private HBox introHbox,catBox;
	private VBox gameVbox, guessVBox;
	private Button loginButton, animalCat, sportCat, foodCat, sendBut, playAgain;
	private Pane introPane,gamePane;
	private EventHandler<ActionEvent> toTheGame;
	private Text ip, port, welcome, categories, wordToGuess;
	private ListView<String> listView;
	private Client clientConnection;
	private String guessWord = "";
	private Boolean forcedWord = false;

	// check if its valid word
	// ...
	public boolean isValidWord(String wordCheck) {
		int countLength = 0;
		for(char x: wordCheck.toCharArray()) {
			if(Character.isAlphabetic(x)) {
				countLength++;
			}
		}
		
		if(countLength == wordCheck.length()) {
			return true;
		}
		return false;
	}
	
	// main game scene
	// ...
	public Scene gameScene() {
		ip = new Text();
		ip.setFill(Color.WHITE);
		port = new Text();
		port.setFill(Color.WHITE);
		gameVbox = new VBox(5, ip, port);
		
		welcome = new Text("Word Guessing Game");
		welcome.setFill(Color.WHITE);
		welcome.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		welcome.setLayoutX(180);
		welcome.setLayoutY(50);
		
		categories = new Text("Choose your category:");
		categories.setFill(Color.WHITE);
		categories.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		categories.setLayoutX(210);
		categories.setLayoutY(90);
		
		// animal category button
		animalCat = new Button("Animals");
		animalCat.setOnAction(e-> {
			sendBut.setDisable(false);
			animalCat.setDisable(true);
			sportCat.setDisable(true);
			foodCat.setDisable(true);
			clientConnection.guessClass = new GuessInfo();
			clientConnection.guessClass.categChosen = true;
			clientConnection.guessClass.infoCategories = "Animals";
			clientConnection.send(clientConnection.guessClass);

		});
		
		// sport category button
		sportCat = new Button("Sport");
		sportCat.setOnAction(e-> {
			sendBut.setDisable(false);
			animalCat.setDisable(true);
			sportCat.setDisable(true);
			foodCat.setDisable(true);
			clientConnection.guessClass = new GuessInfo();
			clientConnection.guessClass.categChosen = true;
			clientConnection.guessClass.infoCategories = "Sports";
			clientConnection.send(clientConnection.guessClass);

		});
		
		// food category button
		foodCat = new Button("Food");
		foodCat.setOnAction(e-> {
			sendBut.setDisable(false);
			animalCat.setDisable(true);
			sportCat.setDisable(true);
			foodCat.setDisable(true);
			clientConnection.guessClass = new GuessInfo();
			clientConnection.guessClass.categChosen = true;
			clientConnection.guessClass.infoCategories = "Food";
			clientConnection.send(clientConnection.guessClass);

		});
		
		// hbox connects all the categories
		catBox = new HBox(15,animalCat, foodCat, sportCat);
		catBox.setLayoutX(210);
		catBox.setLayoutY(120);
		
		// " _ _ _ _ _ " client guess word text
		wordToGuess = new Text("");
		wordToGuess.setFill(Color.WHITE);
		wordToGuess.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
		wordToGuess.setLayoutX(200);
		wordToGuess.setLayoutY(220);
		
		// char input textBox
		charGuess = new TextField();
		charGuess.setMinWidth(220);
		charGuess.setPromptText("Input char or word");
		
		// send char or word button
		sendBut = new Button("Send");
		sendBut.setOnAction(e->{
			if( ((charGuess.getText().length() > 1) && (isValidWord(charGuess.getText())) || 
					(forcedWord && (isValidWord(charGuess.getText())))))    {
				clientConnection.guessClass = new GuessInfo();
				clientConnection.guessClass.wordChosen = true;
				clientConnection.guessClass.clientToServerWord = charGuess.getText();
				clientConnection.guessClass.resetSignal = 0;
				clientConnection.send(clientConnection.guessClass);
			}
			
			else if(Character.isLetter(charGuess.getText().charAt(0)) && (charGuess.getText().length() == 1)) {
				clientConnection.guessClass = new GuessInfo();
				clientConnection.guessClass.categChosen = false;
				clientConnection.guessClass.charChosen = true;
				clientConnection.guessClass.guess = charGuess.getText().charAt(0);
				clientConnection.guessClass.resetSignal = 0;
				clientConnection.send(clientConnection.guessClass);
			}

			else {
				charGuess.setText("Invalid char");
			}
		});
		
		// play again button
		playAgain = new Button("Play Again");
		playAgain.setDisable(true);
		playAgain.setOnAction(e->{
			sendBut.setDisable(false);
			animalCat.setDisable(false);
			foodCat.setDisable(false);
			sportCat.setDisable(false);
			sendBut.setDisable(true);
			guessWord = "";
			wordToGuess.setText(guessWord);
			charGuess.setText("Input character or word");
			listView.getItems().clear();
			playAgain.setDisable(true);
			clientConnection.guessClass = new GuessInfo();
			clientConnection.guessClass.resetSignal = 1;
			clientConnection.send(clientConnection.guessClass);
		});
		
		// vbox connects guess box and some other buttons
		guessVBox = new VBox(25, wordToGuess, charGuess,sendBut, playAgain);
		guessVBox.setLayoutX(190);
		guessVBox.setLayoutY(160);
		guessVBox.setAlignment(Pos.CENTER);
		
		// listview that prints some infos
		listView = new ListView<>();
		listView.setLayoutY(370);
		listView.setLayoutX(110);
		listView.setPrefSize(400, 220);
		
		// main pane
		gamePane = new Pane(gameVbox,welcome,categories, catBox, listView, guessVBox);
		Image image = new Image("background_intro2.jpg");
		BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
												BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
		Background background = new Background(backgroundImage);
		gamePane.setBackground(background);
		return new Scene(gamePane,600,600);
	}
	
	// intro scene
	// ...
	public Scene introScene() {
		ipField = new TextField("127.0.0.1");
		portNum = new TextField("5555");
		introHbox = new HBox(30,ipField, portNum);
		introHbox.setLayoutX(150);
		introHbox.setLayoutY(200);
		
		// login Button
		loginButton = new Button("Next");
		loginButton.setLayoutX(150);
		loginButton.setLayoutY(240);
		loginButton.setOnAction(toTheGame);
		
		// Intro Pane with background
		introPane = new Pane(introHbox, loginButton);
		Image image = new Image("background_intro.jpg");
		BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
												BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(1.0, 1.0, true, true, false, false));
		Background background = new Background(backgroundImage);
		introPane.setBackground(background);
		
		return new Scene(introPane,600,600);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("(Client) Word Guess!!!");
		HashMap<String,Scene> sceneMap = new HashMap<String,Scene>();
		sceneMap.put("game", gameScene());
		
		// to the game event handler
		toTheGame = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				ip.setText("Your IP: " + ipField.getText());
				port.setText("Your Port: " + portNum.getText());
				
				primaryStage.setScene(sceneMap.get("game"));
				clientConnection = new Client(data->{
					Platform.runLater(()->{
						listView.getItems().add(data.toString());
						int listSizeForScroll = listView.getItems().size()-1;
						
						// if client used all 6 turns he has to guess the word
						// ...
						String tempString = listView.getItems().get(listSizeForScroll);
						if(tempString.contains("You must guess the word")) {
							forcedWord = true;
						}
						
						// if client lost the game
						// ...
						if(tempString.contains("You lost the game.") || tempString.contains("You won the game")) {
							clientConnection.guessClass.categChosen = false;
							clientConnection.guessClass.charChosen = false;
							clientConnection.guessClass.wordChosen = false;
							charGuess.setText("Click Play Again or Exit");
							animalCat.setDisable(true);
							foodCat.setDisable(true);
							sportCat.setDisable(true);
							sendBut.setDisable(true);
							playAgain.setDisable(false);
						}
						

						// client GUI modifications after client chooses category
						// ...
						if(clientConnection.guessClass.categChosen) {
				
							int listSize = listView.getItems().size()-1;
							String temp1 = listView.getItems().get(listSize);
							
							// its getting only 1 digit
							int temp2 = Character.getNumericValue(temp1.charAt(temp1.length()-1));
							
							for(int i = 0; i < temp2; i++) {
								guessWord = guessWord + "_ ";
							}
							
							wordToGuess.setText(guessWord);
							forcedWord = false;
						}
						
						// if char sent to the Server side
						// ...
						if(clientConnection.guessClass.charChosen) {
							int listSize = listView.getItems().size()-1;
							String temp1 = listView.getItems().get(listSize);
							
							for(int i = 33; i < temp1.length(); i++) {
								if(Character.isDigit(temp1.charAt(i)) && (!temp1.contains("not"))) {
									char[] myTempArray = guessWord.toCharArray();
									myTempArray[(temp1.charAt(i)-'0')*2-2] = charGuess.getText().charAt(0);
									guessWord = String.valueOf(myTempArray);
									wordToGuess.setText(guessWord);
 								}
							}
							
							// if no blanks -> word guessed correct
							// ...
							if(!guessWord.contains("_")) {
								clientConnection.guessClass.wordChosen = true;
							}
						}
						
						// if word to sent to Server Side
						// ...
						if(clientConnection.guessClass.wordChosen) {
							int listSize = listView.getItems().size()-1;
							String temp1 = listView.getItems().get(listSize-1);
							String temp2 = listView.getItems().get(listSize);
							
							String myWords[] = temp1.split(" ");
							int sizeOfwords = myWords.length;
							
							// if guess was correct
							if(temp2.contains("Correct") && !(temp1.contains("is wrong"))) {
								//building string
								StringBuilder str = new StringBuilder();
								for(char z : myWords[sizeOfwords-1].toCharArray()) {
									str.append(z+" ");
								}
								
								wordToGuess.setText(str.toString());
								sendBut.setDisable(true);
								animalCat.setDisable(false);
								foodCat.setDisable(false);
								sportCat.setDisable(false);
								guessWord = "";
								// reset
								clientConnection.guessClass.wordChosen = false;
							}
							
							else {
								// guess is wrong
								animalCat.setDisable(false);
								foodCat.setDisable(false);
								sportCat.setDisable(false);
								sendBut.setDisable(true);
								guessWord = "";
								wordToGuess.setText(guessWord);
							}
							
							if(temp2.contains("Animals")) {
								animalCat.setDisable(true);
							}
							
							if(temp2.contains("Food")) {
								foodCat.setDisable(true);
							}
							
							if(temp2.contains("Sports")) {
								sportCat.setDisable(true);
							}		
						}
						
						listView.scrollTo(listSizeForScroll);
									});
					}, ipField.getText(), Integer.parseInt(portNum.getText()));
				
				clientConnection.start();
			}
		};
		
		sceneMap.put("intro", introScene());
		primaryStage.setScene(sceneMap.get("intro"));
		primaryStage.show();
	}

}
