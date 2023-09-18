# Guidelines & Overview 
## Domain specific configuration app
<br>

### <u>Smjernice</u>:

##### 1. Prerequisites {#one} 
Preporučam da svatko od vas pokuša osposobiti "Hello World" web aplikaciju prema primjeru [Spring Boot React CRUD][1]. Ideja iza toga je da svi vidite kako izgleda "cjelokupna" slika nekakve web aplikacije, backend/fronted dijelovi, gdje je baza u toj priči i sl. kako bi znali gdje radite svoj dio te gdje/na kojim dijelovima kolege rade.<br><br>

##### 2. Backend {#two}
Iako primjer [Spring Boot React CRUD][1] prikazuje kako osposobiti Spring backend, sve što vam dodatno bude trebalo ili ako želite znati što točno pojedini dio radi i kako, tu je [Spring Documentation/101][2] kao službena Spring dokumentacija.<br><br>

##### 3. Frontend {#three} 
Kako biste se upoznali s React-om ne vidim bolji pristup od službenog tutorial-a [React 101 step-by-step][3] ili [React 101][4] (ovisno o tome što više preferirate).<br><br>

##### 4. Monaco editor {#four} 
Svu potrebnu dokumentaciju, odnosno kako korisiti sam editor je moguće pronaći na [React Monaco Editor][5] s dobrom količinom primjera. Za potrebe IntelliSense feature-a primjer je moguće pronaći na [Custom React Monaco Editor with custom validation, highlighter, formatter, etc.][7].<br><br>

##### 5. Tablica (data table) {#five}
Tablica (data table) koja će se koristi za prikaz podataka predlažem da bude neka od već postojećih, kao što je npr. [React Data Table][8] pa je proširite po potrebi, ili koristite isti [repozitorij][9] kako biste pronašli nekakvu drugu verziju tablice ili općenito React komponente.<br>
Također, možete pogledati i korisiti npr. [AdminLTE3 Theme][10] kao tablicu i općenito sve na toj stranici kao temu i gotove komponente. [Free Themes][11] sadrži neke od beslatnih admin tema koje sadrže sve komponente koje će vam trebati.<br><br>

##### 6. Database {#six} 
Preporučam za početak (mock) H2 bazu kao što je u primjeru [Spring Boot React CRUD][1] dok bi se prototip spojio na MySQL bazu s tablicom koja bi izgledala ovako:<br>

| ID (int) | Type (char(255)) | Name (char(255)) | Active (boolean/tinyint(1)) | Content (longblob) |
| --- | --- | --- | --- | --- |
| 1 | Type1 | Type1Name1 | 0 | whole_configuration_1v1 |
| 2 | Type2 | Type2Name1 | 1 | whole_configuration_2 |
| 3 | Type1 | Type1Name2 | 1 | whole_configuration_1v2 |

(*ID - je ujedno i verzija konfiguracije; spremanje nove ili izmijenjene konfiguracije znači novi redak u tablici s auto-inkrementalnim ID-em tako da se nikad ne mogu pojaviti dva ista ID-a.*)<br><br>

##### 7. {#seven} 
S obzirom da je ovo prototip te radi bolje raspodjele taskova, predlažem da web editor ipak dio posla odradi sam. To bi značilo npr. da backend odradi nekakvu "skuplju" validaciju (što ovdje baš i neće biti slučaj, dok bi u stvarnosti bio) dok bi klijent/browser odradio stvari kao što je bojanje koda/konfiguracije (npr. drugačije bojanje property-a naspram vrijednosti te komentara). Code completion može biti velik problem te je usko vezan uz domenu (što vama nije bitno), pa bih za početak postavio cilj code completion-a na to da editor sam automatski zatvori vitičastu zagradu (kad se prvi puta unese otvorena).
Backend bi se onda služio za provjeru ispravnosti koda/konfiguracije gdje bi provjerio, za početak, jesu li sve vitičaste zagrade zatvorene. Nakon što bi dobio od frontend-a konfiguraciju u HTTP request-u koji bi se slao na "Save" i/ili nakon što bi korisnik prestao tipkati plus neki t (npr. pet sekundi).<br><br>

##### 8. Validacija {#eight} 
Pravi pristup validaciji bi bila izrada gramtike, odnosno tokena, lexer-a i parser-a kao što možete vidjeti na [Parsing in Java][12]. Međutim, to je poprilično velika tema, ali s obzirom na jednostavnost konfiguracija u ovom projektu (u nastavku), jednostavna prototip verzija je sasvim dovoljna za detekciju ne zatvorenih vitičastih zagrada.<br><br>

##### 9. Struktura domenski specifične konfiguracije: {#nine} 
```
Node {
    # comment1
    Sub-Node-1-1 = Value
    Sub-Node-1-2 {
        Sub-Node-2-1 = Value
        # comment2
        Sub-Node-2-2 {
            ...Sub-Node-N {

            }
        }
        Sub-Node-2-3 {
            # comment3
            Value1
        }
        Sub-Node-2-4 {
            Value1, Value2, ...ValueN
        }
        Sub-Node-2-5 {
            Value1,
            Value2, # comment4
            ...ValueN
        }
    }
}
```
Primjer prema tablici u bazi podataka ([6](#six)):
| ID (int) | Type (char(255)) | Name (char(255)) | Active (boolean/tinyint(1)) | Content (longblob) |
| --- | --- | --- | --- | --- |
| 1 | Type1 | Type1Name1 | 0 | whole_configuration_1v1 |
| 2 | Type2 | Type2Name1 | 1 | whole_configuration_2 |
| 3 | Type1 | Type1Name2 | 1 | whole_configuration_1v2 |
```
Type1 {
    Type1Name1 {
        Property1 = Value1
        Property2 = Value2
        # comment1
        Property3 {
            Value3
        }
    }
}
```
<br>

#### 10. Cilj {#ten} 
Cilj projekta, potrebno je izraditi REST servis u Spring-u koji se spaja na MySQL bazu podataka iz koje izčitava postojeće domenski specifične konfiguracije i pošalje ih u HTTP response-u na HTTP request koji dobije od React/JS frontend-a nakon što korisnik u browser-u unese određen URL (URL aplikacije). Popis konfiguracija je moguće vidjeti u nekakvoj tablici u browser-u, te je iste moguće pretražiti ili sortirati. Za potrebe tablice sam sadržaj konfiguracije nije potrebno poslati iz backend-a, već samo podatke kao što je tip, naziv, verzija i status aktivnosti. Nakon što se odabere neka od konfiguracija iz tablice, otvara se Monaco editor sa sadržajem konfiguracije koju je frontend zatražio od backend-a koristeći verziju odabrane konfiguracije (što je ujedno i ID konfiguracije u bazi podataka). Sam editor ima mogućnost bojanja sintakse (razlika između property-a, vrijednosti i komentara), te periodično ili na poseban gumb aktivira validaciju. Validacija se aktivira tako da frontent pošalje backend-u u HTTP request-u trenutni sadržaj konfiguracije koji backend validira i vrati nazad frontend-u (u HTTP response-u) s nekakvim indikatorom u samom sadržaju koja zagrada nije zatvorena kako bi editor to mogao "izcrtati" (ako uopće postoji ikakav problem sa zagradam). U slučaju da korisnik u editor-u unese otvorenu vitičastu zagradu, ista se odmah mora zatvoriti na sljedećem retku na istoj razini (uvlačenje), code completion. U trenutku kad korisnik pritisne tipku "Save" radi se identična validacija kao ranije opisana, samo što se dodatno stvara novi redak u tablici s tom konfiguracijom (nije moguće, u bazi podataka, editirati već dodanu konfiguraciju).
Ostale mogućnosti možemo dogovoriti po potrebi, ali bi prvenstveno uključivali proširenu validaciju, opciju dodavanja nove konfiguracije i brisanje postojeće, aktiviranje/deaktiviranje konfiguracije, formu za odabir baze podataka, import/export i sl.<br>

---

### <u>Predložena raspodjela</u>:

1. Spring backend (CRUD, communication with frontend, etc.)
2. Validation (Grammar, Lexer, Parser for domain specific configurations)
3. React frontend (UI controls, searchable data table) + JS communication with backend
4. Custom Monaco editor as React component with syntax highlighter and code completion

---

### <u>Literatura</u>:

1. [Spring Boot React CRUD][1]
2. [Spring Documentation/101][2]
3. [React 101 step-by-step][3]
4. [React 101][4]
5. [React Monaco Editor][5]
6. [Monaco Editor][6]
7. [Custom React Monaco Editor with custom validation, highlighter, formatter, etc.][7]
8. [React Data Table][8]
9. [React/JS packages repo][9]
10. [AdminLTE3 theme][10]
11. [Free themes][11]
12. [Parsing in Java][12]

---

Originalnalni tekst s email-a mozete vidjeti/skinuti [ovdje][gno_original].

[1]: <https://www.baeldung.com/spring-boot-react-crud> (Spring Boot React CRUD)
[2]: <https://docs.spring.io/spring-boot/docs/current/> (Spring Documentation/101)
[3]: <https://reactjs.org/docs/hello-world.html> (React 101 step-by-step)
[4]: <https://reactjs.org/tutorial/tutorial.html> (React 101)
[5]: <https://www.npmjs.com/package/@monaco-editor/react> (React Monaco Editor)
[6]: <https://microsoft.github.io/monaco-editor/> (Monaco Editor)
[7]: <https://blog.expo.dev/building-a-code-editor-with-monaco-f84b3a06deaf> (Custom React Monaco Editor with custom validation, highlighter, formatter, etc.)
[8]: <https://www.npmjs.com/package/react-data-table-component> (React Data Table)
[9]: <https://www.npmjs.com/package/> (React/JS packages repo)
[10]: <https://adminlte.io/themes/v3/pages/tables/data.html> (AdminLTE3 theme)
[11]: <https://adminlte.io/blog/free-admin-panels/> (Free themes)
[12]: <https://tomassetti.me/parsing-in-java/> (Parsing in Java)
[gno_original]: <https://gitlab.com/avukalov/enea-dsc-app/-/blob/master/Docs/GuidelinesAndOverview_original.rtf> (original from e-mail)

