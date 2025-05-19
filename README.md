# Tucil3_13523060_13523070

## IF2211 Strategi Algoritma
Tucil 3 Stima Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding

Dipersiapkan oleh:

| Nama                          | NIM      |
|:-----------------------------:|:--------:|
| Angelina Efrina Prahastaputri | 13523060 |
| Sebastian Hung Yansen         | 13523070 |

## Penjelasan Singkat Program
<p align="justify"> Algoritma divide and conquer adalah algoritma yang menggunakan pendekatan solusi dengan membagi persoalan yang akan diselesaikan menjadi persoalan yang lebih kecil atau subpersoalan sehingga lebih mudah dipecahkan dan independen. Setelah subpersoalan tersebut diselesaikan, solusinya digabungkan kembali untuk memperoleh solusi keseluruhan.Program ini mengimplementasikan algoritma kompresi gambar menggunakan metode Quadtree. Metode ini membagi gambar menjadi blok-blok yang lebih kecil berdasarkan ambang batas error (threshold). Jika error dalam suatu blok melebihi threshold atau melebihi minimum block size, blok tersebut dibagi menjadi empat sub-blok hingga mencapai ukuran minimum atau error berada di bawah threshold. Hasil kompresi disimpan sebagai gambar baru, dan proses kompresi divisualisasikan dalam bentuk GIF animasi yang menunjukkan hasil kompresi pada setiap kedalaman Quadtree. Metode pengukuran error yang digunakan ada 4 yaitu variance, mean absolute deviation (MAD), max pixel difference (MPD), dan entropy.</p>

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
