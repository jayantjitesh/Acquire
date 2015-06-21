# Acquire
==================================
Acquire is a implementation of board game with the same name.
For more details about the game read : https://en.wikipedia.org/?title=Acquire

This game has two components:

1.**Server** : Once started it will wait for miniumn number of players to join. The port and minimum players required for a game can be passed while starting the server (remote enforcer). The server is responsible to monitor all the moves and decide the winner at the end

2.**Client** : It's a remote player that can connect to and remote server. It will play based on the strategy used like greedy, random, odrered etc.


Project structure:
----------------------------------
- **Lib** : External jar used 
- ** basics** : The basic component need for the game and can be used by client or server both. It contains component like Hotel Chain, Location etc.
- **board** : All the actions assocaited with the board like validating a move
- **game** : The actual representation of game. It will be used to simulate the current and future state of the game
- **state** : The instance of game 

- **enforcer** :  The Server that will run the game.  Main class is AcquireMain
- **remote-enforcer** : The proxy server to run the game for remote client 

- **player** : The player with some basic strategy like random, ordered and greedy
- **remote-player** : A proxy player to connect to a remote server

- **game-tester-code** : The testbed to test the functionality of the game
- **harness-helper** : Helper used by the game-testet-code
- **protocol** : Xml utilities used to test the remote proxies


