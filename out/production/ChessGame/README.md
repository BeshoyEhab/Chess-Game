### Player:
1. name (String)
2. color (String or boolean)
3. pieces (list of Strings or Integers or Pieces)
4. left time (float)
5. __'delete piece' takes "Piece" and delete it from list of "pieces"__
> _make setters and getters to all variables_

### Piece:
1. piece_id (String or int)
2. color (String or boolean)
3. position (String: "D3" or Integers: 43 or list of Integers)
    >_note that when using Integers "A,B,C,D,E,F,G,H" will be "10,20,30,40,50,60,70,80" like "D3" == "43"_
4. initial position (like position)
5. normal moves (list of moves "assume that current position is (x=0,y=0) \[x from right to left, y from up to down\]" Ex. Pawn will be ((0,1),(0,2),(1,0),(-1,0)) )
6. __'allowed moves' takes "list of positions that will be in Game class" and return list of moves like normal moves__
>_like Player you will make setters and getters_

### Game:
1. Board (it's already made)
2. width (Integer)
3. height (Integer)
4. colors (color1 (list of integers(R,G,B)), color2 (list of integers(R,G,B)))
5. player piece (selected piece by current player)
6. history (list of positions)
7. __'assistant' takes "allowed moves" and make dots in available indexes__
8. __'add to history' takes position and add it to history__
9. __'check validate' takes "allowed moves" from piece and the place player want piece go and possible checks on king returns boolean__
10. __'check' run every move, and it checks if player's king is under check returns boolean__
11. __'check mate' if king is under check and can't move to any place returns boolean__
12. __'end game' it ends the game and show how win or tie (if player ends his time he will lose)__
>_like others make setters and getters_