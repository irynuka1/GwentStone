324CA - COITU SEBASTIAN-TEODOR

#### În acest readme voi descrie modul în care am implementat tema, urmând fluxul programului.

## Implementare

Implementarea temei începe în clasa `Main`, unde se preiau datele de intrare în `inputData`. De aici, extrag colecția de deck-uri pentru fiecare jucător și setez contorul de jocuri jucate și câștigate de fiecare jucător. În continuare, se iterează prin toate jocurile primite la input și se apelează metoda `initGame` (din clasa `Game`) pentru a inițializa și finaliza jocul.

### Structura fișierelor + Logica Jocului

#### CARDINPUT (pachetul `fileio`)
- A fost modificat pentru implementarea unui copy constructor care realizează o copie a cărții.

#### CARDWRAPPER (pachetul `fileio`)
- A fost implementat pentru a crea obiecte ce conțin informații despre cărți pentru output.

#### STARTGAMEINPUT (pachetul `fileio`)
- A fost modificat prin schimbarea tipului câmpurilor `playerOneHero` și `playerTwoHero` din `CardInput` în `Hero`.

#### HERO (pachetul `heroes`)
- Este similar clasei `CardInput`, însă îi lipsesc câteva câmpuri care nu sunt necesare eroului, precum și copy constructor-ul.

#### HEROWRAPPER (pachetul `heroes`)
- Asemenea `CardWrapper`, a fost implementat pentru a crea obiecte ce conțin informații despre eroi pentru output.

#### GAME (pachetul `gamelogic`)
- Conține toată logica jocului și toate acțiunile ce pot fi realizate pe parcursul unui joc.
- Se creează o copie a deck-ului fiecărui jucător pentru jocul respectiv, pentru a nu modifica deck-ul original; însă eroul este direct cel primit de la input (nu se face o copie), deoarece modificarea acestuia nu afectează jocurile ulterioare.
- Tabla de joc este reprezentată de o matrice de 4x5 de cărți (`CardInput[][]`).
- După inițializarea jocului, se apelează metoda `startGame` pentru a începe realizarea acțiunilor. Această metodă iterează prin acțiunile jocului curent și apelează metoda `playerActions`, care selectează acțiunea ce urmează să fie realizată printr-un switch din clasa `ActionSelector` (pachetul `gamelogic`).
- Fiecare metodă apelată de `ActionSelector` este implementată în clasa `Game`.
- La finalul turei unui jucător (`endPlayerTurn`), se apelează metoda `resetStatus` (din clasa `Helpers`) pentru a reseta statusul fiecărei cărți de pe tabla de joc, iar variabilele aferente sunt incrementate. Pentru verificarea unei runde noi se folosește contorul `newRoundCheck`.
- Pentru afișarea deck-ului unui jucător (`getPlayerDeck`), se folosește metoda `getDeck`, care returnează un sub-array de cărți din deck-ul jucătorului (de la numărul de cărți din mână până la sfârșitul deck-ului).
- Similar, pentru afișarea cărților din mână (`getCardsInHand`), se folosește metoda `getHand`, care returnează un sub-array de cărți din deck (de la începutul deck-ului până la numărul de cărți din mână).
- Metoda `placeCard` folosește `isCardPlaced` pentru a verifica dacă o carte poate fi plasată pe tabla de joc.
- Metodele `cardUsesAttack` și `cardUsesAbility` folosesc `wrapperHelper` pentru a crea obiectele ce urmează a fi puse la output.
- Restul metodelor de acțiune respectă instrucțiunile din enunț și nu necesită detaliere suplimentară. Se folosesc metode ajutătoare din clasele `Helpers`, `AbilitySelector` și `HeroAbilitySelector`.
- Finalul jocului (când un erou este omorât) este setat prin flag-ul `gameEnded`, care permite doar realizarea acțiunilor menționate în enunț pentru acest caz.

#### HELPERS (pachetul `gamelogic`)
- Conține atât metode ajutătoare pentru realizarea acțiunilor, cât și metode ce implementează abilitățile cărților și ale eroilor.
- Consider că JavaDoc-ul este suficient de explicativ pentru fiecare metodă.

#### ABILITYSELECTOR (pachetul `gamelogic`)
- Realizează selecția abilității ce urmează să fie folosită pe baza numelui acesteia cu ajutorul unui switch.

#### HEROABILITYSELECTOR (pachetul `gamelogic`)
- Realizează selecția abilității eroului ce urmează să fie folosită pe baza numelui acesteia cu ajutorul unui switch.

## Feedback

Per total, tema a fost destul de interesantă și m-a ajutat să mă familiarizez cu limbajul Java și cu o parte din conceptele de OOP. Deși nu am implementat moșteniri sau interfețe, am considerat că rezolvarea se poate realiza mai ușor fără acestea.

Inițial, a durat puțin până am înțeles cum să implementez tema și de unde să încep, însă pe parcursul implementării devenea din ce în ce mai ușor. Am avut o problemă cu flag-ul `gameEnded` (static) pe care l-am adăugat mai târziu în program și am uitat să îl adaug și în metoda `initNewGame`, ce îl seta la `false` la începutul fiecărui joc. Acest lucru afecta toate testele ce urmau după testul 8 (acesta fiind primul care termina un joc). De asemenea, testele erau rulate într-o ordine aleatorie (7, 16, 12...), ceea ce m-a indus în eroare când încercam să rezolv problema menționată. După ce am ordonat testele, am reușit să îmi dau seama la ce test apărea problema.
