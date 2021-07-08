<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the NuriCheat and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
***
***
***
*** To avoid retyping too much info. Do a search and replace for the following:
*** Stikinit, NuriCheat, twitter_handle, email, project_title, project_description
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![GitHub followers][github-shield]][github-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Stikinit/NuriCheat">
  </a>

  <h1 align="center">NuriCheat</h1>

  <p align="center">
    This project was developed as part of the Digital Systems class in my Software Engineering Major. 
    <br />The goal of the project was to create an Andorid app that could solve Nurikabe puzzles by detecting a 5x5 grid in a photo/image. 
    <br /> 
    <br />
    <a href="#demo">View Demo</a>
    ·
    <a href="https://github.com/Stikinit/NuriCheat/issues">Report Bug</a>
    ·
    <a href="https://github.com/Stikinit/NuriCheat/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[Nurikabe](https://www.puzzle-nurikabe.com) is played on a NxN grid that is detected from a photo or image by the app and then converted to a simple NxN matrix. It is then solved by a strategy based algorithm and shown on screen. <br>
These requirements make it a three step project:
* **Detection**: the image is firstly analized using Image Processing operations supported by the OpenCV for Java library
* **Conversion**: the result is then converted to a matrix using a [Convolutional Neural Network](https://github.com/Stikinit/NuriCheat/tree/master/app/src/main/ml) that was pre-trained using the famous [MNIST](https://en.wikipedia.org/wiki/MNIST_database) dataset for digit recognition.
* **Solution**: Finally the solution is found using a sequence of complex [strategies](https://www.conceptispuzzles.com/index.aspx?uri=puzzle/nurikabe/techniques) based on the puzzle's rules and constraints.

<span id="demo">The following is a video of the typical usage of the app:</span>
<br/><br/>
<img src="https://github.com/Stikinit/NuriCheat/blob/master/DocsAndResources/NuriCheatDemo.gif" width="25%" height="25%" alt="EditorGIF"/>



### Built With

* [Android Studio](https://developer.android.com/studio)
* [OpenCV for Java](https://opencv.org/)
* [MNIST-trained CNN](https://tfhub.dev/tensorflow/tfgan/eval/mnist/logits/1)

<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites
* Git
* Android Studio

### Installation

1. Install Git at [Download Git](https://git-scm.com/download).
2. Clone the repo.
   ```sh
   git clone https://github.com/Stikinit/NuriCheat
   ```
3. Download the latest version of Android Studio.
4. Download [OpenCV Java version 3.4.10](https://sourceforge.net/projects/opencvlibrary/files/3.4.10/opencv-3.4.10-android-sdk.zip/download)
5. Add the project directory on Android Studio: File > New > Import Project > Select Project Directory from Explorer.
6. Install OpenCV Java by following [these steps](https://medium.com/android-news/a-beginners-guide-to-setting-up-opencv-android-library-on-android-studio-19794e220f3c).



<!-- USAGE EXAMPLES -->
## Usage

To use the application either:
* Run the app using the simulated Android Studio Device.
* Install the app on your own device (Android 7.0 or more).



<!-- ROADMAP -->
## Roadmap

See the [open issues](https://github.com/Stikinit/NuriCheat/issues) for a list of proposed features (and known issues).

### Open Issues
* The cells of the in-game grid can be modified freely by selecting them.
* The digit recognition is not always perfect on the number 2.

### Future developments
* Bigger grid.
* Optimization of the solving algorithm.
* Recognition of numbers with multiple digits.
* Choice for immediate photo from camera or image selection from gallery.



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See [`LICENSE`](https://github.com/Stikinit/NuriCheat/blob/master/LICENSE) for more information.



<!-- CONTACT -->
## Contact

Gabriele Marconi - gabry.thestikinit@gmail.com
Project Link: [https://github.com/Stikinit/NuriCheat](https://github.com/Stikinit/NuriCheat)






<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/Stikinit/NuriCheat.svg?style=for-the-badge
[contributors-url]: https://github.com/Stikinit/NuriCheat/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/Stikinit/NuriCheat.svg?style=for-the-badge
[forks-url]: https://github.com/Stikinit/NuriCheat/network/members
[stars-shield]: https://img.shields.io/github/stars/Stikinit/NuriCheat.svg?style=for-the-badge
[stars-url]: https://github.com/Stikinit/NuriCheat/stargazers
[issues-shield]: https://img.shields.io/github/issues/Stikinit/NuriCheat.svg?style=for-the-badge
[issues-url]: https://github.com/Stikinit/NuriCheat/issues
[license-shield]: https://img.shields.io/github/license/Stikinit/NuriCheat.svg?style=for-the-badge
[license-url]: https://github.com/Stikinit/NuriCheat/blob/master/LICENSE.txt
[github-shield]: https://img.shields.io/github/followers/Stikinit.svg?style=social&label=Follow
[github-url]: https://github.com/Stikinit

