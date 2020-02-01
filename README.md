# Battleships

An implementation of classic paper game that is also knows as Battleship / Sea Battle.
Written in plain Java, networking functions are realized by sockets.

## Quick start

To run the application:
* build using `mvn install`
* run the application with following parameters:
   * `-mode [server|client]` - server - accepts connections from clients; client - connects to server
   * `-port N` - port number on which application will be listening for incoming connections in server mode
   or communicate with server in client mode
   * `-map map-file` - path to the file storing a map (proper map format is required)
   * `-host ip-address` - (optional) IPv4 address that the application running in:
         * server mode will be available to clients (default: the private IP address of computer in its network)
         * client mode will try to connect to (default: localhost)

After running two instances of the application (one as a server and one as a client) you can start the battle!

## Map format

The map is a 10x10 grid describing ships placement.
* Rows are labeled by letters A through J (top to bottom), columns - by numbers 1 through 10 (left to right).
* Water is labeled by `.`, ships are labeled by `#`.
* The board has to contain:
  * 4 ships of size of 1 cell,
  * 3 ships of size of 2 cells,
  * 2 ships of size of 3 cells,
  * 1 ship of size of 4 cells.
  
Ships of size of 3 and 4 cells can be "curved", but every segment of such ship has to be connected with
at least one other segment by a common side.

Two different ships cannot have any mutual edges.

Ships can be placed vertically and horizontally, but not diagonally.

### Example map
`map.txt`

```
..#.......
#......#..
#..#......
..##......
......##..
.##.......
.........#
..##...#..
.##....#.#
.......#..
```
This particular map contains i.a. four one-masted ships placed on positions: A3, B8, G10 and I10.

# Specification (PL)

<sub>_Autorem oryginalnej specyfikacji jest [@areksokolowski](https://gitlab.com/areksokolowski)._
_Została ona nieco zmodyfikowana ze względu na poszerzenie funkcjonalności aplikacji._</sub>

## Gra w okręty

Należy napisać aplikację do gry w okręty przez sieć.

Aplikacja łączy się z inną aplikacją i rozgrywa partię gry w okręty.

### Parametry uruchomieniowe
Aplikacja obługuje następujące parametry:
* `-mode [server|client]` - wskazuje tryb działania (jako serwer - przyjmuje połączenie, jako klient - nawiązuje połączenie z serwerem)
* `-port N` - port, na którym aplikacja ma się komunikować.
* `-map map-file` - ścieżka do pliku zawierającego mapę z rozmieszczeniem statków (format opisany w sekcji Mapa).
* `-host ip-address` - (opcjonalny) adres IP:
    * hosta, z którym aplikacja ma się połączyć w trybie klienta (domyślnie localhost),
    * pod którym aplikacja ma być dostępna w trybie serwera (domyślnie adres prywatny w sieci komputera).

### Mapa
* Mapa jest planszą 10x10 zawierającą opis położenia okrętów.
* `.` oznacza wodę, `#` oznacza okręt.
* Wiersze oznacza się literami od A do J (z góry na dół), kolumny - liczbami od 1 do 10 (od lewej do prawej).
* Plansza powinna zawierać:
  * 4 okręty o rozmiarze 1,
  * 3 okręty o rozmiarze 2,
  * 2 okręty o rozmiarze 3,
  * 1 okręt o rozmiarze 4.
* Okręty o rozmiarach 3-4 mogą być "łamane", ale poszczególne segmenty muszą się łączyć przynajmniej jednym bokiem.
* Dwa okręty nie mogą się ze sobą stykać bokami.

Przykładowa mapa poniżej:
```
..#.......
#......#..
#..#......
..##......
......##..
.##.......
.........#
..##...#..
.##....#.#
.......#..
```
Objaśnienie: na tej mapie, okręty 1-masztowe znajdują się na pozycjach: A3, B8, G10 oraz I10.

### Protokół komunikacji
* Komunikacja odbywa się z użyciem protokołu TCP, z kodowaniem UTF-8.
* Klient i serwer wysyłają sobie na przemian _wiadomość_, która składa się z 2 części: _komendy_ i _współrzędnych_, odzielonych znakiem `;`, i zakończonych znakiem końca linii `\n`.
  * Format wiadomości: `komenda;współrzędne\n`
  * Przykład wiadomości: `MISS;D6\n`
* Komendy i ich znaczenie:
  * _start_
    * komenda inicjująca rozgrywkę.
    * Wysyła ją klient tylko raz, na początku.
    * Przykład: `START;A1\n`
  * _pudło_
    * odpowiedź wysyłana, gdy pod współrzędnymi otzymanymi od drugiej strony nie znajduje się żaden okręt.
    * Przykład: `MISS;A1\n`
  * _trafiony_
    * opowiedź wysyłana, gdy pod współrzędnymi otzymanymi od drugiej strony znajduje się okręt, i nie jest to jego ostatni dotychczas nie trafiony segment.
    * Przykład: `HIT;A1\n`
  * _trafiony zatopiony_
    * opowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony znajduje się okręt, i trafiono ostatni jeszcze nie trafiony segment tego okrętu.
    * Przykład: `SUNK;A1\n`
  * _ostatni zatopiony_
    * opowiedź wysyłana, gdy pod współrzędnymi otrzymanymi od drugiej strony znajduje się okręt, i trafiono ostatni jeszcze nie trafiony segment okrętu całej floty w tej grze.
    * Jest to ostatnia komenda w grze. Strona wysyłająca ją przegrywa.
    * Przy tej komendzie nie podaje się współrzędnych strzału (już nie ma kto strzelać!).
    * Przykład: `LAST_SUNK\n`
* Możliwe (choć strategicznie nierozsądne) jest wielokrotne strzelanie w to samo miejsce. Należy wtedy odpowiadać zgodnie z aktualnym stanem planszy:
  * `MISS` w razie pudła,
  * `HIT` gdy okręt już był trafiony w to miejsce, ale nie jest jeszcze zatopiony,
  * `SUNK` gdy okręt jest już zatopiony.
* Obsługa błędów:
  * W razie otrzymania niezrozumiałej komendy lub po 1 sekundzie oczekiwania, należy ponownie wysłać swoją ostatnią wiadomość.
  * Po 3 nieudanej próbie, należy wyświelić komunikat `Communication error` i zakończyć działanie aplikacji.

### Działanie aplikacji
* W czasie działania, aplikacja powinna wyświetlać wszystkie wysyłane i otrzymywane wiadomości.
* Po zakończeniu rozgrywki, aplikacja powinna wyświetlić:
  * Informację o wyniku rozgrywki (wygrana, przegrana)
  * W razie wygranej - pełną mapę przeciwnika,
  * W razie przegranej - mapę przeciwnika, z zastąpieniem nieznanych pól znakiem `?`. _Uwaga_: pola sąsiadujące z zatopionym okrętem należy uznać za odkryte (nie może się na nich znajdować inny okręt).
  * Pusty wiersz
  * Swoją mapę, z dodatkowymi oznaczeniami: `~` - pudła przeciwnika, `@` - celne strzały przeciwnika.

Przykład mapy przeciwnika z przegranej sesji:
```
..#..??.?.
#.????.#..
#....??...
..##....?.
?.....##..
??#??.....
..?......#
..##...#..
.##....#.#
.......#..
```

Przykład swojej mapy po grze (wygranej - nie wszytkie okręty zatopione):
```
~~@~~.~~~.
@..~.~.@.~
#.~#..~.~.
..##..~..~
..~.~.@@..
.#@~..~...
.~.~.~.~.@
~.##.~.#~~
.##~..~~~~
..~.~.~~~.
```

### Zasady zaliczenia:
Zadanie nie ma testów automatycznych, ani nawet określonej struktury projektu (należy ją zrobić samemu).

Zaliczenie będzie polegało na rozegraniu kilku partii pomiędzy uczestnikami zajęć na następnym laboratiorium.
