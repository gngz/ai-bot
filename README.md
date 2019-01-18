# Survivor Bot
Survivor bot is the project of Artificial Intelligence Course at University of Madeira - Year 2018/2019

Made by:
 - Gonçalo Passos
 - Michael Cavaleiro
 - Alexandru Borta
 - Duarte Marco
 - Joel Remesso

## Movement Heuristic Function


> With this heuristic function is possible make the robot be able to move with minimum number of steps and privilege the movement by the center when robot can't go by the shortest path. 


$`h(x) = a_0 * d(x) +  a_1 * c(x)`$ is the heuristic function.

$`d(x) = ABS(x_1 - x_2) + ABS(y_1 - y_2) `$ is the minimal number of steps between testing point and objective point.

$`c(x) = ABS(x_1 - 3,5) + ABS(y_1 - 3,5) - 1`$ is the minimal number of steps between testing point and nearest of 4 center points.

Testing Point $`= (x_1,y_1)`$

Objective Point $`= (x_2,y_2)`$

$`x`$ is the testing point variable.

$`a_0`$ is the d(x) weight. By default is 1,5.

$`a_1`$ is the c(x) weight. By default is 0,5.





## Project Phases 

* [x]  Phase 1 - Minimal Movements
* [x]  Phase 2 - Final Project