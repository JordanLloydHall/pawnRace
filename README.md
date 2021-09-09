# Pawn Race
This is the code that was made for the AI Pawn Race competition that Imperial College London held in December 2020. It is written in Kotlin targetting the JVM. During the 5 days that I had to develop the AI, I wrote the game engine, an optimized Linear Algebra library, and a library for evolving neural network based heuristic aproximators for the game states.

The anatomy of the AI is as follows:
 - A threaded implementation of a MiniMax algorithm with Alpha-Beta pruning.
 - The heuristics function is the output of a neural network, that determines the neural network's percieved advantage in the game state.
 - A tournament-based selection regime for training the neural networks via crossover and mutation.
 
The AI worked very well, and worked very well in the competition. some things I learned from the competition to carry forward:
 - Spend more time optimising the linear algebra library, as speed quickly became the biggest issue during training and the competition.
 - Use memoization - a lot of the board states being evaluated were evaluated several times which is expensive. It would have been better to use a transposition table.
 - Training time is important - I only trained the neural networks in the last day of the competition. I should have let it train for 3 days without interruption.

## Contributing
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.
## Authors

* **Jordan Hall** - *Active Developer* - [PlatinumNinja72](https://github.com/PlatinumNinja72)
