# Minesweeper game
This is the repository of the minesweeper game project I designed during my Java course at Mines de Saint Etienne.
The game is designed in French (code comments as well).

## How the game works
The minesweeper game is playable in two differents modes: offline and online.   

For the offline mode, you only need to run the `demineur.jar` file to play a fun minesweeper game alone. Just run the file and enjoy !

For the online mode, you also need to run the `serveur.jar` file.    
* First, run the `serveur.jar` file,
* Then, run one to several times the `demineur.jar` file (as many times as they are players),
* Then, each player can join the game by clicking the "connexion" button on its window,
* Once all the players are connected, you can start an online game by clicking "Démarrer partie" on the server window.

_NOTE: If you already started a game, no player can join anymore unless you restart the server by clicking "Redémarrer serveur" on the server window. Now, every player can join again, and new players as well._

## Running the app
To run the minesweeper game, you can simply download the [demineur.jar file](out/artifacts/demineur_jar/demineur.jar) and [server.jar file](out/artifacts/serveur_jar/serveur.jar).   

Once downloaded, just run the jar files to play the game (you need at least java 12 version). 

#### How to install Java 12
Follow one of these guides to install java 12 version on your machine:
* [For Ubuntu users](http://ubuntuhandbook.org/index.php/2019/03/install-oracle-java-12-ubuntu-18-04-16-04/),
* [For Windows users](https://java.tutorials24x7.com/blog/how-to-install-openjdk-12-on-windows).

#### How to run a jar file :
```java -jar demineur.jar```

## Notes on the code :
As the code was developed for a school project in a limited period of time and for education purpose, no unit tests have been written.

## Documentation (JavaDoc) :
Documentation of the code can be found in the `docs/` folder.  
To see a nice version of the documentation:
* Clone the repository on your machine `git clone https://github.com/hugoseri/demineur.git`,
* open [index.html](docs/index.html) file located in `docs/` in a web browser,
* Explore !

