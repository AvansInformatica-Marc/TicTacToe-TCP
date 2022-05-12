<div align="center">

  # TicTacToe-TCP
  Basic tic tac toe demo application: a simple multiplayer game using TCP sockets, the Kotlin programming language and Jetpack/Jetbrains Compose Desktop for the UI.

</div>

# :notebook_with_decorative_cover: Table of Contents
- [About the Project](#star2-about-the-project)
  * [Screenshots](#camera-screenshots)
  * [Tech Stack](#space_invader-tech-stack)
  * [Features](#dart-features)
  * [Color Reference](#art-color-reference)
  * [Environment Variables](#key-environment-variables)
- [Getting Started](#toolbox-getting-started)
  * [Prerequisites](#bangbang-prerequisites)
- [Acknowledgements](#gem-acknowledgements)

## :star2: About the Project
### :camera: Screenshots
![Screenshot: Tic tac toe game where it is the current player's turn](/docs/screenshot1.webp?raw=true "Screenshot: Tic tac toe game where it is the current player's turn")
![Screenshot: Tic tac toe game where it is the opponent's turn](/docs/screenshot2.webp?raw=true "Screenshot: Tic tac toe game where it is the opponent's turn") 

### :space_invader: Tech Stack
An application written in Kotlin (JVM target), using Jetbrains Compose for Desktop. Also uses:
* [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Ktor](https://ktor.io/) for [non-blocking sockets](https://ktor.io/docs/servers-raw-sockets.html)
* [Kotlinx serialization](https://kotlinlang.org/docs/serialization.html) - [protocol buffers](https://developers.google.com/protocol-buffers/) for sending typed messages in binary format

### :dart: Features
* Playing the tic tac toe game

### :art: Color Reference
| Color | Hex (light mode) | Hex (dark mode)
| --- | --- | --- |
| Primary Color | ![#6200EE](https://via.placeholder.com/16/6200EE.webp?text=+) #6200EE | ![#BB86FC](https://via.placeholder.com/16/BB86FC.webp?text=+) #BB86FC |
| Primary Variant Color | ![#3700B3](https://via.placeholder.com/16/3700B3.webp?text=+) #3700B3 | ![#3700B3](https://via.placeholder.com/16/3700B3.webp?text=+) #3700B3 |
| Secondary Color | ![#03DAC6](https://via.placeholder.com/16/03DAC6.webp?text=+) #03DAC6 | ![#03DAC6](https://via.placeholder.com/16/03DAC6.webp?text=+) #03DAC6 |
| Secondary Variant Color | ![#018786](https://via.placeholder.com/16/018786.webp?text=+) #018786 | ![#03DAC6](https://via.placeholder.com/16/03DAC6.webp?text=+) #03DAC6 |

### :key: Environment Variables
This project doesn't have environment variables as of now.

## 	:toolbox: Getting Started
### :bangbang: Prerequisites
This project uses Gradle as build tool. Intellij IDEA is the recommended IDE to deploy or test this application.

## :gem: Acknowledgements
- [Awesome Readme Template](https://github.com/Louis3797/awesome-readme-template)
