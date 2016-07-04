# RiskGame
CLI Risk game in Scala, combining an object structure with functional 
dynamics and data flow.   
  
Mutability is ugly, lambda is the new sexy. 
  
## Dependencies:
* Scala 2.11.5
* SBT 0.13.7 (Scala build tool)

## Todo
  
### Short term
* See //TODO comments in src/main/scala
* Simplify classes (Circular dependencies: troops don't need to see their country)
* Clean main function to run the game
* Move tests from main to test folder

### Maybe later
* Basic GUI
* One-turn AI (taking a situation map as input and returning a decision)
