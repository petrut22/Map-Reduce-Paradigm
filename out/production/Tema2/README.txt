Stingescu Andrei Petrut 334CC

Tema a fost efectuata in Java, folosind conceptele de programare in paralel, astfel am utilizat interfata
ExecutorService pentru executarea de task-urilor asincrone, în background, în mod concurent, pe baza modelului
Replicated Workersetapa Map + etapa de Procesare.

In prima parte a temei, am citit cele 3 argumente date in consola si anume: numarul de threaduri, fisierul
de intrare si fisierul de iesire. Dupa citirea datelor am folosit interfata ExecutorService pentru etapa Map si anume:
Determinam lungimea fisierului cu string-uri si initiam taskurile care trebuiau efectuate de workeri.

- Task-ul dat este reprezentat de clasa MyRunnable care deschide fisierul si citeste caractere de la un offset dat
ca parametru in clasa Tema2, pentru o anumita dimensiune a fragmentului. In cazul in care caracterul situat de pe pozitia
offset - 1 este o litera, inseamna ca fragmentul incepe cu un cuvant incomplet si trebuie ignorat. Dupa ce citeam
tot fragmentului, verificam daca se termina cu un cuvant incomplet. Daca conditia era indeplinita citeam tot acel
cuvant si il adaugam in fragment. Citirea caracterelor din fisier era efectuata, astfel incat ca intotdeauna
counter-ul sa nu depaseasca lungimea fisierului sau dimensiunea fragmentului. Pasul urmator consta in scoaterea
tuturor cuvintelor din fragment si se determina pentru fiecare cuvant lungimea acestuia. Apoi se actualizeaza map-ul
cu informatiile obtinute + cuvantul cu lungimea maximala. Rezultatele din fiecare task de la Map sunt salvate intr-un
vector de tip RTask.

- RTask este o clasa care contine mapa in care se afla dictionarul fiecarui fragment, cuvantul cu lungime maximala si
numele fisierului din care face parte. Metoda cea mai importanta din aceasta clasa este addMap, care adauga o cheie,
astfel verifica daca exista cheia respectiva si daca da, doar incrementeaza valoarea, daca nu, se adauga valoarea 1
la acea cheie. Celelalte metode sunt folosite pentru actualizarea valorilor + obtinerea acestora.

- In etapa de Combinare, pentru fiecare fisier se aduna toate rezultatele obtinute la etapa map si se obtine un
dictionar mai mare de cuvinte + o lista cu cuvintele maximale din fiecare fragment. Rezultatele pentru fiecare
fisier le-am adaugat intr-un vector de tip ReducedTask(fiind de asemenea lista de taskuri pentru etapa urmatoare)
care este asemenator cu RTask, diferenta consta in faptul ca de data aceasta voi avea in clasa ReducedTask o
lista de cuvinte maximale.

-In etapa de Procesare, se creeaza noi taskuri pentru ExecutorService, fiecare worker va lua anumite taskuri si le va
rezolva. Task-ul pentru aceasta etapa va fi reprezentat de clasa Process care va accesa dictionarul fiecarui fisier si va
determina rangul pe baza formulei din cerinta temei cu ajutorul functiei Fibonacci. Rezultatele finale vor fi adaugate
intr-un vector de tip Result care contine rangul fiecarui fisier si un string cu rezultatul final.

-In final se ordoneaza toate rezultatele in functie de rang, in ordine descrescatoare si vor fi afisate in fisierul de iesire.




