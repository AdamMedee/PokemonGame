/*
   Main.java   2017 12 12
   Adam Mehdi   2018 01 08

   Main class for pokemon game. User picks 4 pokemon
   and in random succession battles multiple other
   pokemon that they did not choose until either the
   user has all their pokemon faint or they
   defeat all enemy pokemon. 
*/

import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args){
        Scanner stdin = new Scanner(System.in); //Used to get input
        Random rand = new Random(); //Random object

        ArrayList<Pokemon> PokemonList = new ArrayList<Pokemon>();
        int gameStart; //Used to prompt user to start the game
        int pokemonCount; //Number of pokemon in the game
        Pokemon userPokemon[]; //Array of all pokemon user picked
        Pokemon enemyPokemon[]; //Array of pokemon the user has
        int pokemonNumberChosen; //The pokemon number user chooses at beginning of the game
        int turn; //How many times the turns have rotated in a battle to keep track of whos turn it is
        boolean lost; //Whether or not user has lost yet
        int userPokemonChosen; //Of the 4 pokemon, the index of the pokemon chosen for the user
        int userAction; //Action user picks during battle
        String userName; //Players name in game
        int userAttack; //Attack number user picks


        while(true){
            //Creates the pokemon arraylist
            PokemonList.clear();
            try {
                Scanner inFile = new Scanner(new BufferedReader(new FileReader("pokemon.txt")));
                pokemonCount = Integer.parseInt(inFile.nextLine());
                for (int i = 0; i < pokemonCount; i++){
                    Pokemon tempPoke = new Pokemon(inFile.nextLine());
                    PokemonList.add(tempPoke);
                }
            }
            //Catches IO error that could occur
            catch(IOException e){
                break;
            }

            //Get user to enter 1 to start the game
            do {
                try {
                    System.out.println("Enter 1 to start the game!");
                    gameStart = stdin.nextInt();
                } catch (Exception e) {
                    gameStart = 0;
                }
            }
            while(gameStart != 1);


            //Get users name for the game
            do {
                try {
                    System.out.println("Enter your name: ");
                    userName = stdin.next();
                } catch (Exception e) {
                    userName = "";
                    stdin.next();
                }
            }
            while(userName.equals(""));
            textPurge();

            userPokemon = new Pokemon[4]; //Empty array to hold the players pokemon
            //Asks the user to choose 4 pokemon
            for(int i = 0; i < 4; i++){
                for(int j = 0; j < pokemonCount; j++){
                    System.out.printf("%-2d.)%-20s", j+1, PokemonList.get(j).getName());
                    if((j+1) % 4 == 0){
                        System.out.print("\n");
                    }
                }
                System.out.print("\n");

                //Gets input of pokemon number picked by user
                do {
                    pokemonNumberChosen = 0; //Resets gamestart is user entered invalid value
                    try {
                        System.out.println("Enter the number of the pokemon you wish to add to your deck. If you want to see the information about that pokemon, enter the negative of that number.");
                        pokemonNumberChosen = stdin.nextInt();
                    } catch (Exception e) {
                        stdin.next();
                    }
                }
                while(!(Math.abs(pokemonNumberChosen) - 1 <= pokemonCount && Math.abs(pokemonNumberChosen) - 1 >= 0));

                if(pokemonNumberChosen < 0){
                    //Will take number user input and change it to list index of that pokemon
                    pokemonNumberChosen *= -1;
                    pokemonNumberChosen -= 1;

                    //Will print all pokemon information
                    PokemonList.get(pokemonNumberChosen).printPokemonInformation();

                    System.out.println("\n\nEnter anything to continue."); //Allows user to read stats and continue when comtorable
                    stdin.next(); //Waits for any input
                    i -= 1; //Since the user didnt pick a pokemon, sets the for loop counter back
                    textPurge();
                }

                //If user entered a positive value, it will be checked if it's already been picked and if not, add it to the list
                else{
                    //Will take number user input and change it to list index of that pokemon
                    pokemonNumberChosen -= 1;

                    //Checks if pokemons already been picked
                    if(PokemonList.get(pokemonNumberChosen).equals(userPokemon[0]) || PokemonList.get(pokemonNumberChosen).equals(userPokemon[1]) || PokemonList.get(pokemonNumberChosen).equals(userPokemon[2]) || PokemonList.get(pokemonNumberChosen).equals(userPokemon[3])){
                        i -= 1; //Doesnt count as a pick so sets back counter
                        textPurge();
                        System.out.println("That pokemon has already been chosen, pick a different pokemon!");
                    }
                    else{
                        userPokemon[i] = PokemonList.get(pokemonNumberChosen);
                    }
                }
            }

            //Takes all pokemon not picked and will use them to fight the pokemon of the user
            int enemyIndex = 0;
            enemyPokemon = new Pokemon[pokemonCount - 4];
            for(int i = 0; i < pokemonCount; i++){
                if(!Arrays.asList(userPokemon).contains(PokemonList.get(i))){
                    enemyPokemon[enemyIndex] = PokemonList.get(i);
                    enemyIndex++;
                }
            }
            Collections.shuffle(Arrays.asList(enemyPokemon)); //Randomizes order user will fight the pokemon

            //All the battles the user must go through start here
            turn = 0;
            lost = false;
            for(int i = 0; i < pokemonCount - 4; i++) {

                if(lost){ //Does not start new round if user has lost
                    break;
                }

                Pokemon enemyPokemonFought = enemyPokemon[i]; //Choose pokemon the user will be fighting

                //Update values of pokemon for user after battle
                for (int j = 0; j < 4; j++) {
                    userPokemon[j].roundOver();
                }
                //List of numbers to randomize enemy pokemon attacks
                int[] attackNumbers = new int[enemyPokemonFought.getAttackCount()];
                for(int j = 0; j < enemyPokemonFought.getAttackCount(); j++){
                    attackNumbers[j] = j;
                }

                //Start of a single battle
                turn += rand.nextInt(2); //Will add 1 or 0 to the turn to randomize who starts the battle

                do {
                    try {
                        System.out.printf("%s, choose your pokemon! Enter pokemon number:%n", userName);
                        for(int j = 0; j < 4; j++){
                            System.out.printf("%d.) %s%n", j + 1, userPokemon[j].getName());
                        }
                        userPokemonChosen = stdin.nextInt() - 1;
                        textPurge();
                        //Checks if new pokemon chosen is awake
                        if(!userPokemon[userPokemonChosen].getAwake()){
                            System.out.printf("%s is not awake!%n", userPokemon[userPokemonChosen]);
                            userPokemonChosen = -1; //Makes userpokemonchosen invalid so user is asked again
                        }
                    } catch (Exception e) {
                        stdin.next(); //Flushes invalid input
                        System.out.println("Invalid Input!");
                        userPokemonChosen = 0;
                    }
                }
                while(!(0 <= userPokemonChosen && userPokemonChosen <= 3)); //Must be from 1 - 4

                int rounds = 0; //Keeps track of when to give pokemon 10 energy

                //Start of the pokemon battle
                System.out.printf("%s has entered the battlefield!%n%n", enemyPokemonFought.getName());
                while (true) {
                    //Users turn to use their pokemon and attack the cpu
                    if(turn % 2 == 0){

                        //Get user to enter their action
                        do {
                            try {
                                System.out.println("Choose an action:\n1.) Attack\n2.) Retreat\n3.) Pass\n4.) Information");
                                userAction = stdin.nextInt();
                                textPurge();
                            } catch (Exception e) {
                                userAction = 0;
                                System.out.println("Invalid input");
                            }
                        }
                        while(!(0 < userAction && userAction < 5));

                        //Goes through all possible actions
                        //Tells user to pick an attack
                        if(userAction == 1){
                            do {
                                try {
                                    System.out.println("Choose an attack:");
                                    for(int j = 0; j < userPokemon[userPokemonChosen].getAttackCount(); j++){
                                        System.out.printf("%d.) %s%n", j + 1, userPokemon[userPokemonChosen].getAttackNames()[j]);
                                    }
                                    userAttack = stdin.nextInt() - 1;
                                    textPurge();
                                } catch (Exception e) {
                                    userAttack = 0;
                                    System.out.println("Invalid input");
                                }
                            }
                            while(!(0 <= userAttack && userAttack <= userPokemon[userPokemonChosen].getAttackCount() - 1));

                            //If attack is valid, attacks and prints attack message
                            System.out.println(userPokemon[userPokemonChosen].giveAttackMessage(userAttack));
                            if(!userPokemon[userPokemonChosen].attack(enemyPokemonFought, userAttack)){
                                turn -= 1;
                            }

                        }
                        else if(userAction == 2){ //User picks new pokemon to use
                            do {
                                try {
                                    System.out.printf("%s, choose your pokemon! Enter pokemon number:%n", userName);
                                    for(int j = 0; j < 4; j++){
                                        System.out.printf("%d.) %s%n", j + 1, userPokemon[j].getName());
                                    }
                                    userPokemonChosen = stdin.nextInt() - 1;
                                    textPurge();
                                    //Checks if new pokemon chosen is awake
                                    if(!userPokemon[userPokemonChosen].getAwake()){
                                        System.out.printf("%s is not awake!%n", userPokemon[userPokemonChosen].getName());
                                        userPokemonChosen = -1; //Makes userpokemonchosen invalid so user is asked again
                                    }
                                } catch (Exception e) {
                                    stdin.next(); //Flushes invalid input
                                    System.out.println("Invalid Input!");
                                    userPokemonChosen = 0;
                                }
                            }
                            while(!(0 <= userPokemonChosen && userPokemonChosen <= 3)); //Must be from 1 - 4
                        }
                        else if(userAction == 3){
                            System.out.printf("%s uses PASS!%n", userPokemon[userPokemonChosen].getName());
                        }
                        else if(userAction == 4){
                            //Print information of both pokemon
                            textPurge();
                            enemyPokemonFought.printInBattleInformation();
                            userPokemon[userPokemonChosen].printInBattleInformation();
                            turn -= 1; //Doesnt use a turn so allows user to go again
                        }

                        //Unstuns pokemon if theyve been stunned
                        for(int j = 0; j < 4; j++){
                            userPokemon[userPokemonChosen].setStunned(false);
                        }

                        //Checks if pokemons been killed
                        enemyPokemonFought.checkAwake();
                        if (!enemyPokemonFought.getAwake()) {
                            System.out.printf("%s has defeated %s!%n", userName, enemyPokemonFought.getName());
                            break;
                        }
                    }

                    //cpus turn to battle the user
                    else{
                        Collections.shuffle(Arrays.asList(attackNumbers)); //Randomizes possible attacks
                        for(int j = 0; j < enemyPokemonFought.getAttackCount(); j++){
                            if(enemyPokemonFought.attack(userPokemon[userPokemonChosen], j)){
                                System.out.printf("%s uses %s!%n", enemyPokemonFought.getName(), enemyPokemonFought.getAttackNames()[j]);
                                break;
                            }
                            if(j == enemyPokemonFought.getAttackCount() - 1){
                                System.out.printf("%s uses PASS!%n", enemyPokemonFought.getName());
                            }
                        }
                        enemyPokemonFought.setStunned(false);
                    }

                    turn++; //Next iteration, go to the next players turn
                    System.out.println(""); //Empty line for aesthetics
                    rounds++;

                    if(rounds % 2 == 0){ //If round over, recover all pokemons' energy
                        enemyPokemonFought.energyRecover();
                        for(int j = 0; j < 4; j++){
                            userPokemon[j].energyRecover();
                        }
                    }

                    userPokemon[userPokemonChosen].checkAwake(); //Checks if current pokemon has fainted
                    if(!userPokemon[userPokemonChosen].getAwake()){
                        System.out.printf("%s has fainted! They must retreat!%n", userPokemon[userPokemonChosen].getName());

                        //Checks if all pokemon have fainted
                        if (!(userPokemon[0].getAwake() || userPokemon[1].getAwake() || userPokemon[2].getAwake() || userPokemon[3].getAwake())) {
                            lost = true;
                            break;
                        }

                        //Gets input for the new pokemon
                        do {
                            try {
                                System.out.printf("%s, choose your pokemon! Enter pokemon number:%n", userName);
                                for(int j = 0; j < 4; j++){
                                    System.out.printf("%d.) %s%n", j + 1, userPokemon[j].getName());
                                }
                                userPokemonChosen = stdin.nextInt() - 1;
                                textPurge();
                                //Checks if new pokemon chosen is awake
                                if(!userPokemon[userPokemonChosen].getAwake()){
                                    System.out.printf("%s is not awake!%n", userPokemon[userPokemonChosen].getName());
                                    userPokemonChosen = -1; //Makes userpokemonchosen invalid so user is asked again
                                }
                            } catch (Exception e) {
                                stdin.next(); //Flushes invalid input
                                System.out.println("Invalid Input!");
                                userPokemonChosen = 0;
                            }
                        }
                        while(!(0 <= userPokemonChosen && userPokemonChosen <= 3)); //Must be from 1 - 4
                    }
                }

                if(lost){
                    //Losig game messages
                    System.out.println("Oh no, all your pokemon have fainted! The battle arena was just too difficult...");
                    System.out.println("Well, theres always next time! Enter 1 to play again.");
                    break;
                }
            }

            //If the code gets to this point, check if player has won
            if(!lost){
                System.out.printf("Congratulations %s, the new pokemon arena champion!%n", userName);
                System.out.println("Prove your worth again? Enter 1 to play again.");
            }
        }
    }

    public static boolean chance(int percentSucc){
        return Math.random()*100 < percentSucc;
    }

    //Moves all text up by printing a lot of empty lines
    public static void textPurge(){
        for(int i = 0; i < 100; i++) {
            System.out.println("");
        }
    }

}
