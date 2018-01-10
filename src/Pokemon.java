/*
   Pokemon.java   2017 12 12
   Adam Mehdi   2018 01 08

   Pokemon class that controls what the pokemon
   in the main class can do. Handles pokemon actions
   such as attacking and getting information from the pokemon
   such as their name.
*/

public class Pokemon {
    //In the .txt file, pokemon data is organized like so:
    //<name>,<hp>,<type>,<resistance>,<weakness>,<num attacks>,[<attack name>, <energy cost>,<damage>,<special>]
    private String name;
    private int health;
    private int maxHealth;
    private int energy = 50;
    private String type;
    private String resistance;
    private String weakness;
    private int attackCount;
    private String[] attackNames;
    private String[] attackSpecials;
    private int[] attackCosts;
    private int[] attackDamage;
    private boolean stunned = false;
    private boolean disabled = false;
    private boolean awake = true;


    Pokemon(String stats){
        String[] statsArray = stats.split(",");
        name = statsArray[0];
        health = Integer.parseInt(statsArray[1]);
        maxHealth = Integer.parseInt(statsArray[1]);
        type = statsArray[2];
        resistance = statsArray[3];
        weakness =  statsArray[4];
        attackCount = Integer.parseInt(statsArray[5]);
        attackNames = new String[attackCount];
        attackSpecials = new String[attackCount];
        attackCosts = new int[attackCount];
        attackDamage = new int[attackCount];
        //Organizes attacks into arrays where each array holds an attribute of all attacks like attack cost
        for(int i = 0; i < attackCount; i++){
            attackNames[i] = statsArray[6 + 4*i];
            attackCosts[i] = Integer.parseInt(statsArray[7 + 4*i]);
            attackDamage[i] = Integer.parseInt(statsArray[8 + 4*i]);
            attackSpecials[i] = statsArray[9 + 4*i];
        }
    }

    //This will cause the pokemon to attack another pokemon with a specific attack number
    //Will return true if attack is possible, otherwise false
    public boolean attack(Pokemon enemy, int attackNumber){

        int damage; //Damage of current attack being used in attack method

        if(this.energy < this.attackCosts[attackNumber] || this.stunned){
            return false;
        }
        this.energy -= this.attackCosts[attackNumber]; //Uses pokemon energy depending on cost of attack
        damage = this.attackDamage[attackNumber]; //Sets current damage

        //Tests for resistances and weaknesses
        if(this.type.equals(enemy.weakness)){
            damage *= 2;
        }
        else if(this.type.equals(enemy.resistance)){
            damage /= 2;
        }

        if(this.disabled){ //Reduces damage by 10 if disabled to minimum of 0
            damage = Math.max(0, damage - 10);
        }
        String attackName = this.attackSpecials[attackNumber];

        if(attackName.equals(" ")){ //Regular attack, no special
            enemy.setHealth(enemy.health - damage);
            System.out.printf("%s did %d damage!%n", this.name, damage);
        }

        else if(attackName.equals("stun")) { //Enemy cant retreat or attack
            enemy.setHealth(enemy.health - damage); //Damages enemy
            System.out.printf("%s did %d damage!%n", this.name, damage);
            if(Main.chance(50)) { //50% chance of stunning enemy
                enemy.setStunned(true);
                System.out.printf("%s is now stunned!%n", enemy.name);
            }
        }

        else if(attackName.equals("wild card")) { //Attack that doesn't always work
            if(Main.chance(50)) { //50% chance of damaging enemy
                enemy.setHealth(enemy.health - damage); //Damages enemy
                System.out.printf("%s did %d damage!%n", this.name, damage);
            }
        }

        else if(attackName.equals("wild storm")) { //Attacks, and if attack is succesful, attacks again, and continues until attack fails
            double randomNum = Math.random();
            //Instead of simulating to get number of attacks, takes the probability of each number of attacks happening
            //0 hits has 50%, 1 hit has 25%, 2 hits has 12.5%, 3 hits has 6.25%, etc.
            if(randomNum == 0){ //Random double between 0 and 1 (0 inclusive) has basically 0 chance of happening, but technically possible, and would signify infinite attacks
                enemy.setHealth(0); //Does infinite damage
                System.out.printf("%s did infinite damage!%n", this.name);
            }
            else{
                int wildStormDamage = damage * (int)Math.floor(-Math.log(randomNum)); //Attack damage * attack quantity
                enemy.setHealth(enemy.health - wildStormDamage); //Damages enemy
                System.out.printf("%s did %d damage!%n", this.name, wildStormDamage);
            }
        }

        else if(attackName.equals("disable")) { //Enemy pokemon gets disabled status making it do less damage
            enemy.setHealth(enemy.health - damage); //Damages enemy
            System.out.printf("%s did %d damage!%n", this.name, damage);
            if(Main.chance(50)) { //50% chance of stunning enemy
                enemy.setDisabled(true);
                System.out.printf("%s is now disabled!%n", enemy.name);
            }
        }

        else if(attackName.equals("recharge")) { //Gives pokemon energy when attacking opponent
            enemy.setHealth(enemy.health - damage); //Damages enemy
            System.out.printf("%s did %d damage!%n", this.name, damage);
            System.out.printf("%s gained 20 energy!%n", this.name);
            this.energy = Math.min(50, this.energy + 20);
        }

        return true;
    }

    //Gives a message when the pokemon attacks
    public String giveAttackMessage(int attackNumber){
        if(this.energy < this.attackCosts[attackNumber]){
            return String.format("%s does not have enough energy to use %s!", this.name, this.attackNames[attackNumber]);
        }
        else if(this.stunned){
            return String.format("%s is stunned!", this.name);
        }
        else{
            return String.format("%s uses %s!", this.name, this.attackNames[attackNumber]);
        }
    }

    //At the end of each round, the pokemon will have their energy refreshed and be healed if they're still awake
    public void roundOver(){
        this.energy = 50;
        if(this.health > 0){
            this.health = Math.min(this.maxHealth, this.health + 20);
        }
        this.disabled = false;
    }

    //Displays pokemon information
    public void printPokemonInformation(){
        //Prints all pokemon info
        System.out.printf("The pokemons name is: %s%n", this.name);
        System.out.printf("The pokemons max health is: %s%n", this.maxHealth);
        System.out.printf("The pokemons type is: %s%n", this.type);
        System.out.printf("The pokemons resistance is: %s%n", this.resistance);
        System.out.printf("The pokemons weakness is: %s%n", this.weakness);
        System.out.printf("The pokemons has %d attack(s). They are:%n", this.attackCount);
        for(int j = 0; j < this.attackCount; j++){
            System.out.printf("%s attack does %d damage and needs %d energy.", this.attackNames[j], this.attackDamage[j], this.attackCosts[j]);
            if(this.attackSpecials[j].equals(" ")){
                System.out.println("This is a normal attack");
            } else {System.out.printf("This is a(n) %s attack.%n", this.attackSpecials[j]);}
        }
    }

    //Print pokemon info
    public void printInBattleInformation(){
        System.out.printf("--------------------------------------%n");
        System.out.printf("| NAME: %-29s|%n", this.name);
        System.out.printf("| TYPE: %-29s|%n", this.type);
        System.out.printf("| WEAKNESS: %-25s|%n", this.weakness);
        System.out.printf("| RESISTANCE: %-23s|%n", this.resistance);
        System.out.printf("| ENERGY: %-2d             HP: %-3d/%-3d |%n", this.energy, this.health, this.maxHealth);
        System.out.printf("--------------------------------------%n");
    }

    //All pokemon getters to return values to be used in main class
    public String getName(){
        return this.name;
    }

    public int getAttackCount() {
        return this.attackCount;
    }

    public String[] getAttackNames() {
        return this.attackNames;
    }

    public boolean getAwake(){
        return this.awake;
    }

    //These will be pokemon setters to change the value of the pokemons attributes
    public void setHealth(int newHealth){ this.health = newHealth; }

    public void energyRecover(){
        this.energy = Math.min(50, this.energy + 10);
    }

    public void setStunned(boolean stun){
        this.stunned = stun;
    }

    public void setDisabled(boolean disable){
        this.disabled = disable;
    }

    //Updates if pokemon is awake
    public void checkAwake(){
        if(this.health <= 0){
            this.awake = false;
        }
    }

}
