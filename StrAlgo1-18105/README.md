# Tugas Besar Strategi Algoritma 1
__Penerapan Algoritma Greedy di Permainan _Tower Defence___

*Kelompok Gulag Survivor*
Anggota:
- Faris Muhammad Kautsar (13518105)
- Gregorius Jovan Kresnadi (13518135)
- Muhammad Fauzan Rafi Sidiq Widjonarto (13518147)

## How to Run (not compiled, dengan file .java)
 1. _Pull_ repository Github dari [Entelect Tower Defence Game 2018](https://github.com/EntelectChallenge/2018-TowerDefence) ke folder lokal
 2. Setting starter-bot, Masuk ke folder `starter-pack/starter-bot/java/src/main/java/za/co/entelect/challenge` untuk mengganti file `Bot.java` dengan file `StrAlgo-XXXXX.java` yang disertakan
 3. Lakukan hal yang sama dengan reference-bot, di `starter-pack/reference-bot/java/src/main/java/za/co/entelect/challenge`
 4. Di folder `starter-pack/starter-bot/java`, edit `pom.xml` menggunakan IntelliJ, klik icon 'Maven' di kanan layar, lalu klik lifecycle > install. Hal ini akan membuat file bernama `jar-starter-bot-with-dependencies.jar` di folder `starter-pack/starter/bot/java/target`
 5. Pada OS **Windows**, jalankan `run.bat` di folder `starter-pack`. Pada OS Linux, jalankan di terminal di path `rootdir/starter-pack` perintah `make run` untuk menjalankan permainan.

## How to Run (compiled, dengan file .jar)
 1. _Pull_ repository Github dari [Entelect Tower Defence Game 2018](https://github.com/EntelectChallenge/2018-TowerDefence) ke folder lokal
 2. Setting starter-bot, Masuk ke folder `starter-pack/starter-bot/java`. Buat folder target, dan edit file `bot.json` dengan format berikut:
```
{
	"author": "Sebuah Nama",
	"email": "example@example.com",
	"nickName": "NicknameA",
	"botLocation": "/target",
	"botFileName": "<NamaFileJar>.jar",
	"botLanguage": "java"
} 
```
 3. Lakukan hal yang sama dengan reference-bot di `starter-pack/reference-bot/java`
 4. Pada OS **Windows**, jalankan `run.bat` di folder `starter-pack`. Pada OS Linux, jalankan di terminal di path `rootdir/starter-pack` perintah `make run` untuk menjalankan permainan.
