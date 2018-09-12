# EC_Assignment
Practical assignment of the course Evolutionary Computing at the VU

## General architecture
The program is structured in multiple modules. The main module is called algorithm.

The basic idea is to seperate the configuration of different algorithm variations from the actual code. Therefore, each experiment consists of a Configuration object (see module "configuration") and an Evaluation function (see module "evaluation").
The Configuration defines the actual parts of the evolutionary algorithm. For details, see README file of the module "configuration".