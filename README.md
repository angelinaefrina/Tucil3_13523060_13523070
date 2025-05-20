# Tucil3_13523060_13523070

## IF2211 Strategi Algoritma
Tucil 3 Stima Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding

Dipersiapkan oleh:

| Nama                          | NIM      |
|:-----------------------------:|:--------:|
| Angelina Efrina Prahastaputri | 13523060 |
| Sebastian Hung Yansen         | 13523070 |

## Penjelasan Singkat Program
<p align="justify"> Algoritma pathfinding adalah algoritma yang digunakan dalam proses eksplorasi graf untuk menentukan jalur atau path paling optimal dari suatu titik tertentu menuju titik atau tujuan tertentu (atau dalam konteks tugas kecil ini adalah dari start hingga finish permainan) dengan mempertimbangkan cost, constraints, obstacles, dan lain-lain. Program ini mengimplementasikan algoritma Uniform Cost Search, algoritma Greedy Best First Search, dan algoritma A* untuk penyelesaian puzzle Rush Hour. Digunakan dua heuristik yakni Manhattan Distance dan Blocking Vehicles. Input program berupa konfigurasi awal puzzle berupa file .txt dan pilihan algoritma pathfinding beserta heuristik yang digunakan. Output program berupa hasil yakni solusi konfigurasi puzzle tahap per tahap (yang dapat disimpan dalam file .txt), banyak gerakan (node) yang diperiksa, dan waktu pencarian solusi</p>

## Requirement Program
1. Bahasa Pemrograman : Java
2. Pustaka yang Digunakan : java.awt.image, javax.imageio
3. Input : file teks (.txt)

## Cara Menjalankan Program
1. Clone Repository
Lakukan clone repository ke lokal menggunakan perintah:
```
git clone <URL REPOSITORI>
```
2. Pastikan sekarang sudah di direktori repositori
```
git cd Tucil3_13523060_13523070
```
3. Jalankan perintah berikut di terminal untuk mengkompilasi file:
```
javac -d bin src/Algorithm.java src/Main.java src/Piece.java src/Reader.java src/State.java
```
4. Jalankan perintah berikut di terminal untuk menjalankan program:
```
java -cp bin Main
```
