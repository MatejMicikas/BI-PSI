## BI-PSI
# <a name="domaci_uloha_c_1_-_tcp_server" id="domaci_uloha_c_1_-_tcp_server">Domácí úloha - TCP server</a>

## <a name="anotace" id="anotace">Anotace</a>

<div class="level2">

Cílem úlohy je vytvořit vícevláknový server pro TCP/IP komunikaci a implementovat komunikační protokol podle dané specifikace. Pozor, implementace klientské části není součástí úlohy! Klientskou část realizuje testovací prostředí.
</div>

## <a name="zadani" id="zadani">Zadání</a>

<div class="level2">

Vytvořte server pro automatické řízení vzdálených robotů. Roboti se sami přihlašují k serveru a ten je navádí ke středu souřadnicového systému. Pro účely testování každý robot startuje na náhodných souřadnicích a snaží se dojít na souřadnici [0,0]. Na cílové souřadnici musí robot vyzvednout tajemství. Po cestě k cíli mohou roboti narazit na překážky, které musí obejít. Server zvládne navigovat více robotů najednou a implementuje bezchybně komunikační protokol.
</div>

## <a name="detailni_specifikace" id="detailni_specifikace">Detailní specifikace</a>

<div class="level2">

Komunikace mezi serverem a roboty je realizována plně textovým protokolem. Každý příkaz je zakončen dvojicí speciálních symbolů „\a\b“. (Jsou to dva znaky '\a' a '\b'.) Server musí dodržet komunikační protokol do detailu přesně, ale musí počítat s nedokonalými firmwary robotů (viz sekce Speciální situace).

Zprávy serveru:

  <table class="inline">
    <tbody>
      <tr class="row0">
        <th class="col0">Název</th>
        <th class="col1">Zpráva</th>
        <th class="col2">Popis</th>
      </tr>
      <tr class="row1">
        <td class="col0">SERVER_CONFIRMATION</td>
        <td class="col1"><16-bitové číslo v decimální notaci>\a\b</td>
        <td class="col2">Zpráva s potvrzovacím kódem. Může obsahovat maximálně 5 čísel a ukončovací sekvenci \a\b.</td>
      </tr>
      <tr class="row2">
        <td class="col0">SERVER_MOVE</td>
        <td class="col1">102 MOVE\a\b</td>
        <td class="col2">Příkaz pro pohyb o jedno pole vpřed</td>
      </tr>
      <tr class="row3">
        <td class="col0">SERVER_TURN_LEFT</td>
        <td class="col1">103 TURN LEFT\a\b</td>
        <td class="col2">Příkaz pro otočení doleva</td>
      </tr>
      <tr class="row4">
        <td class="col0">SERVER_TURN_RIGHT</td>
        <td class="col1">104 TURN RIGHT\a\b</td>
        <td class="col2">Příkaz pro otočení doprava</td>
      </tr>
      <tr class="row5">
        <td class="col0">SERVER_PICK_UP</td>
        <td class="col1">105 GET MESSAGE\a\b</td>
        <td class="col2">Příkaz pro vyzvednutí zprávy</td>
      </tr>
      <tr class="row6">
        <td class="col0">SERVER_LOGOUT</td>
        <td class="col1">106 LOGOUT\a\b</td>
        <td class="col2">Příkaz pro ukončení spojení po úspěšném vyzvednutí zprávy</td>
      </tr>
      <tr class="row7">
        <td class="col0">SERVER_OK</td>
        <td class="col1">200 OK\a\b</td>
        <td class="col2">Kladné potvrzení</td>
      </tr>
      <tr class="row8">
        <td class="col0">SERVER_LOGIN_FAILED</td>
        <td class="col1">300 LOGIN FAILED\a\b</td>
        <td class="col2">Nezdařená autentizace</td>
      </tr>
      <tr class="row9">
        <td class="col0">SERVER_SYNTAX_ERROR</td>
        <td class="col1">301 SYNTAX ERROR\a\b</td>
        <td class="col2">Chybná syntaxe zprávy</td>
      </tr>
      <tr class="row10">
        <td class="col0">SERVER_LOGIC_ERROR</td>
        <td class="col1">302 LOGIC ERROR\a\b</td>
        <td class="col2">Zpráva odeslaná ve špatné situaci</td>
      </tr>
       <tr class="row11">
        <td class="col0">SERVER_KEY_OUT_OF_RANGE_ERROR</td>
        <td class="col1">303 KEY OUT OF RANGE\a\b</td>
        <td class="col2">Key ID není v očekávaném rozsahu</td>
      </tr>
    </tbody>
  </table>

Zprávy klienta:

  <table class="inline">
    <tbody>
      <tr class="row0">
        <th class="col0">Název</th>
        <th class="col1">Zpráva</th>
        <th class="col2">Popis</th>
        <th class="col3">Ukázka</th>
        <th class="col4">Maximální délka</th>
      </tr>
      <tr class="row1">
        <td class="col0">CLIENT_USERNAME</td>
        <td class="col1"><user name>\a\b</td>
        <td class="col2">Zpráva s uživatelským jménem. Jméno může být libovolná sekvence znaků kromě kromě dvojice \a\b a nikdy nebude shodné s obsahem zprávy CLIENT_RECHARGING.</td>
        <td class="col3">Umpa_Lumpa\a\b</td>
        <td class="col4">12</td>
      </tr>
      <tr class="row2">
        <td class="col0">CLIENT_KEY_ID</td>
        <td class="col1"><Key ID>\a\b</td>
        <td class="col2">Zpráva obsahující Key ID. Může obsahovat pouze celé číslo o maximálně třech cifrách.</td>
        <td class="col3">2\a\b</td>
        <td class="col4">5</td>
      </tr>
      <tr class="row3">
        <td class="col0">CLIENT_CONFIRMATION</td>
        <td class="col1"><16-bitové číslo v decimální notaci>\a\b</td>
        <td class="col2">Zpráva s potvrzovacím kódem. Může obsahovat maximálně 5 čísel a ukončovací sekvenci \a\b.</td>
        <td class="col3">1009\a\b</td>
        <td class="col4">7</td>
      </tr>
      <tr class="row4">
        <td class="col0">CLIENT_OK</td>
        <td class="col1">OK <x> <y>\a\b</td>
        <td class="col2">Potvrzení o provedení pohybu, kde _x_ a _y_ jsou souřadnice robota po provedení pohybového příkazu.</td>
        <td class="col3">OK -3 -1\a\b</td>
        <td class="col4">12</td>
      </tr>
      <tr class="row5">
        <td class="col0">CLIENT_RECHARGING</td>
        <td class="col1">RECHARGING\a\b</td>
        <td class="col2">Robot se začal dobíjet a přestal reagovat na zprávy.</td>
        <td class="col3"></td>
        <td class="col4">12</td>
      </tr>
      <tr class="row6">
        <td class="col0">CLIENT_FULL_POWER</td>
        <td class="col1">FULL POWER\a\b</td>
        <td class="col2">Robot doplnil energii a opět příjímá příkazy.</td>
        <td class="col3"></td>
        <td class="col4">12</td>
      </tr>
      <tr class="row7">
        <td class="col0">CLIENT_MESSAGE</td>
        <td class="col1"><text>\a\b</td>
        <td class="col2">Text vyzvednutého tajného vzkazu. Může obsahovat jakékoliv znaky kromě ukončovací sekvence \a\b.</td>
        <td class="col3">Haf!\a\b</td>
        <td class="col4">100</td>
      </tr>
    </tbody>
  </table>

Časové konstanty:

  <table class="inline">
    <tbody>
      <tr class="row0">
        <th class="col0">Název</th>
        <th class="col1">Hodota [s]</th>
        <th class="col2">Popis</th>
      </tr>
      <tr class="row1">
        <td class="col0">TIMEOUT</td>
        <td class="col1">1</td>
        <td class="col2">Server i klient očekávají od protistrany odpověď po dobu tohoto intervalu.</td>
      </tr>
      <tr class="row2">
        <td class="col0">TIMEOUT_RECHARGING</td>
        <td class="col1">5</td>
        <td class="col2">Časový interval, během kterého musí robot dokončit dobíjení.</td>
      </tr>
    </tbody>
  </table>
  
Komunikaci s roboty lze rozdělit do několika fází:

</div>

### <a name="autentizace" id="autentizace">Autentizace</a>

<div class="level3">
  
Server i klient oba znají dvojici autentizačních klíčů (nejedná se o veřejný a soukromý klíč):

<div align="center">
  <table>
      <tbody>
        <tr class="row0">
          <th class="col0">Key ID</th>
          <th class="col1">Server Key</th>
          <th class="col2">Client Key</th>
        </tr>
        <tr class="row1">
          <td class="col0">0</td>
          <td class="col1">23019</td>
          <td class="col2">32037</td>
        </tr>
        <tr class="row2">
          <td class="col0">1</td>
          <td class="col1">32037</td>
          <td class="col2">29295</td>
        </tr>
        <tr class="row3">
          <td class="col0">2</td>
          <td class="col1">18789</td>
          <td class="col2">13603</td>
        </tr>
        <tr class="row4">
          <td class="col0">3</td>
          <td class="col1">16443</td>
          <td class="col2">29533</td>
        </tr>
        <tr class="row5">
          <td class="col0">4</td>
          <td class="col1">18189</td>
          <td class="col2">21952</td>
        </tr>
      </tbody>
  </table>
</div>

Každý robot začne komunikaci odesláním svého uživatelského jména (zpráva CLIENT_USERNAME). Uživatelské jméno múže být libovolná sekvence 18 znaků neobsahující sekvenci „\a\b“. V dalším kroku vyzve server klienta k odeslání Key ID (zpráva SERVER_KEY_REQUEST), což je vlastně identifikátor dvojice klíčů, které chce použít pro autentizaci. Klient odpoví zprávou CLIENT_KEY_ID, ve které odešle Key ID. Po té server zná správnou dvojici klíčů, takže může spočítat "hash" kód z uživatelského jména podle následujícího vzorce:

  <pre class="code">Uživatelské jméno: Mnau!

ASCII reprezentace: 77 110 97 117 33

Výsledný hash: ((77 + 110 + 97 + 117 + 33) * 1000) % 65536 = 40784</pre>

Výsledný hash je 16-bitové číslo v decimální podobě. Server poté k hashi přičte klíč serveru tak, že pokud dojde k překročení kapacity 16-bitů, hodnota jednoduše přeteče (následuje ukázka pro Key ID 0):

<pre class="code">(40784 + 54621) % 65536 = 29869</pre>

Výsledný potvrzovací kód serveru se jako text pošle klientovi ve zprávě SERVER_CONFIRM. Klient z obdrženého kódu vypočítá zpátky hash a porovná ho s očekávaným hashem, který si sám spočítal z uživatelského jména. Pokud se shodují, vytvoří potvrzovací kód klienta. Výpočet potvrzovacího kódu klienta je obdobný jako u serveru, jen se použije klíč klienta (následuje ukázka pro Key ID 0):

<pre class="code">(40784 + 45328) % 65536 = 20576</pre>

Potvrzovací kód klienta se odešle serveru ve zpráve CLIENT_CONFIRMATION, který z něj vypočítá zpátky hash a porovná jej s původním hashem uživatelského jména. Pokud se obě hodnoty shodují, odešle zprávu SERVER_OK, v opačném prípadě reaguje zprávou SERVER_LOGIN_FAILED a ukončí spojení. Celá sekvence je na následujícím obrázku:

```Klient                  Server
------------------------------------------
CLIENT_USER         --->
                    <---    SERVER_CONFIRMATION
CLIENT_CONFIRMATION --->
                    <---    SERVER_OK
                              nebo
                            SERVER_LOGIN_FAILED
```

Server dopředu nezná uživatelská jména. Roboti si proto mohou zvolit jakékoliv jméno, ale musí znát sadu klíčů klienta i serveru. Dvojice klíčů zajistí oboustranou autentizaci a zároveň zabrání, aby byl autentizační proces kompromitován prostým odposlechem komunikace.

</div>

### <a name="pohyb_robota_k_cilove_oblasti" id="pohyb_robota_k_cilove_oblasti">Pohyb robota k cílové oblasti</a>

<div class="level3">

Robot se může pohybovat pouze rovně (SERVER_MOVE) a je schopen provést otočení na místě doprava (SERVER_TURN_RIGHT) i doleva (SERVER_TURN_LEFT). Po každém příkazu k pohybu odešle potvrzení (CLIENT_OK), jehož součástí je i aktuální souřadnice. Pozice robota není serveru na začátku komunikace známa. Server musí zjistit polohu robota (pozici a směr) pouze z jeho odpovědí. Z důvodů prevence proti nekonečnému bloudění robota v prostoru, má každý robot omezený počet pohybů (pouze posunutí vpřed). Počet pohybů by měl být dostatečný pro rozumný přesun robota k cíli. Následuje ukázka komunkace. Server nejdříve pohne dvakrát robotem kupředu, aby detekoval jeho aktuální stav a po té jej navádí směrem k cílové souřadnici [0,0].

```
Klient                  Server
------------------------------------------
                <---    SERVER_MOVE
CLIENT_CONFIRM  --->
                <---    SERVER_MOVE
CLIENT_CONFIRM  --->
                <---    SERVER_MOVE
                          nebo
                        SERVER_TURN_LEFT
                          nebo
                        SERVER_TURN_RIGHT
```

Těsně po autentizaci robot očekává alespoň jeden pohybový příkaz - SERVER_MOVE, SERVER_TURN_LEFT nebo SERVER_TURN_RIGHT! Nelze rovnou zkoušet vyzvednout tajemství. Po cestě k cíli se nachází mnoho překážek, které musí roboti překonat objížďkou. Pro překážky platí následující pravidla:

<ul>
  <li>Překážka okupuje vždy jedinou souřadnici.</li>
  <li>Je zaručeno, že každá překážka má všech osm okolních souřadnic volných (tedy vždy lze jednoduše objet).</li>
  <li>Je zaručeno, že překážka nikdy neokupuje souřadnici [0,0].</li>
  <li>Pokud robot narazí do překážky více než dvacetkrát, poškodí se a ukončí spojení.</li>
</ul>

Překážka je detekována tak, že robot dostane pokyn pro pohyb vpřed (SERVER_MOVE), ale nedojde ke změně souřadnic (zpráva CLIENT_OK obsahuje stejné souřadnice jako v předchozím kroku). Pokud se pohyb neprovede, nedojde k odečtení z počtu zbývajících kroků robota.

</div>

### <a name="vyzvednuti_tajneho_vzkazu" id="vyzvednuti_tajneho_vzkazu">Vyzvednutí tajného vzkazu</a>

<div class="level3">

Poté, co robot dosáhne cílové souřadnice [0,0], tak se pokusí vyzvednout tajný vzkaz (zpráva SERVER_PICK_UP). Pokud je robot požádán o vyzvednutí vzkazu a nenachází se na cílové souřadnici, spustí se autodestrukce robota a komunikace se serverem je přerušena. Při pokusu o vyzvednutí na cílově souřadnici reaguje robot zprávou CLIENT_MESSAGE. Server musí na tuto zprávu zareagovat zprávou SERVER_LOGOUT. (Je zaručeno, že tajný vzkaz se nikdy neshoduje se zprávou CLIENT_RECHARGING, pokud je tato zpráva serverem obdržena po žádosti o vyzvednutí jedná se vždy o dobíjení.) Poté klient i server ukončí spojení. Ukázka komunikace s vyzvednutím vzkazu:

```Klient                  Server
------------------------------------------
                <---    SERVER_PICK_UP
CLIENT_MESSAGE  --->
                <---    SERVER_MOVE
CLIENT_OK       --->
                <---    SERVER_PICK_UP
CLIENT_MESSAGE  --->
                <---    SERVER_TURN_RIGHT
CLIENT_OK       --->
                <---    SERVER_MOVE
CLIENT_OK       --->
                <---    SERVER_PICK_UP
CLIENT_MESSAGE  --->
                <---    SERVER_LOGOUT</pre>
```
</div>

### <a name="dobijeni" id="dobijeni">Dobíjení</a>

<div class="level3">

Každý z robotů má omezený zdroj energie. Pokud mu začne docházet baterie, oznámí to serveru a poté se začne sám ze solárního panelu dobíjet. Během dobíjení nereaguje na žádné zprávy. Až skončí, informuje server a pokračuje v činnosti tam, kde přestal před dobíjením. Pokud robot neukončí dobíjení do časového intervalu TIMEOUT_RECHARGING, server ukončí spojení.

```Klient                    Server
------------------------------------------
CLIENT_USER         --->
                    <---    SERVER_CONFIRMATION
CLIENT_RECHARGING   --->

      ...

CLIENT_FULL_POWER   --->
CLIENT_CONFIRMATION --->
                    <---    SERVER_OK
                              nebo
                            SERVER_LOGIN_FAILED
                      .
                      .
                      .
```

Další ukázka:

```Klient                  Server
------------------------------------------
                    .
                    .
                    .
                  <---    SERVER_MOVE
CLIENT_CONFIRM    --->
CLIENT_RECHARGING --->

      ...

CLIENT_FULL_POWER --->
                  <---    SERVER_MOVE
CLIENT_CONFIRM    --->
                    .
                    .
                    .
```

</div>

## <a name="chybove_situace" id="chybove_situace">Chybové situace</a>

<div class="level2">

Někteří roboti mohou mít poškozený firmware a tak mohou komunikovat špatně. Server by měl toto nevhodné chování detekovat a správně zareagovat.

</div>

### <a name="chyba_pri_autentizaci" id="chyba_pri_autentizaci">Chyba při autentizaci</a>

<div class="level3">

Pokud je ve zprávě CLIENT_KEY_ID Key ID, který je mimo očekávaný rozsah (tedy číslo, které není mezi 0-4), tak na to server reaguje chybovou zprávou SERVER_KEY_OUT_OF_RANGE_ERROR a ukončí spojení. Za číslo se pro zjednodušení považují i záporné hodnoty. Pokud ve zprávě CLIENT_KEY_ID není číslo (např. písmena), tak na to server reaguje chybou SERVER_SYNTAX_ERROR.

Pokud je ve zprávě CLIENT_CONFIRMATION číselná hodnota (i záporné číslo), která neodpovídá očekávanému potvrzení, tak server pošle zprávu SERVER_LOGIN_FAILED a ukončí spojení. Pokud se nejedná vůbec o čistě číselnou hodnotu, tak server pošle zprávu SERVER_SYNTAX_ERROR a ukončí spojení.

</div>

### <a name="syntakticka_chyba" id="syntakticka_chyba">Syntaktická chyba</a>

<div class="level3">

Na syntaktickou chybu reagauje server vždy okamžitě po obdržení zprávy, ve které chybu detekoval. Server pošle robotovi zprávu SERVER_SYNTAX_ERROR a pak musí co nejdříve ukončit spojení. Syntakticky nekorektní zprávy:

*   <div class="li">Příchozí zpráva je delší než počet znaků definovaný pro každou zprávu (včetně ukončovacích znaků \a\b). Délky zpráv jsou definovány v tabulce s přehledem zpráv od klienta.</div>

*   <div class="li">Příchozí zpráva syntakticky neodpovídá ani jedné ze zpráv CLIENT_USERNAME, CLIENT_CONFIRMATION, CLIENT_OK, CLIENT_RECHARGING a CLIENT_FULL_POWER.</div>

Každá příchozí zpráva je testována na maximální velikost a pouze zprávy CLIENT_CONFIRMATION, CLIENT_OK, CLIENT_RECHARGING a CLIENT_FULL_POWER jsou testovány na jejich obsah (zprávy CLIENT_USERNAME a CLIENT_MESSAGE mohou obsahovat cokoliv).

</div>

### <a name="logicka_chyba" id="logicka_chyba">Logická chyba</a>

<div class="level3">

Logická chyba nastane pouze při nabíjení - když robot pošle info o dobíjení (CLIENT_RECHARGING) a po té pošle jakoukoliv jinou zprávu než CLIENT_FULL_POWER nebo pokud pošle zprávu CLIENT_FULL_POWER, bez předchozího odeslání CLIENT_RECHARGING. Server na takové situace reaguje odesláním zprávy SERVER_LOGIC_ERROR a okamžitým ukončením spojení.

</div>

### <a name="timeout" id="timeout">Timeout</a>

<div class="level3">

Protokol pro komunikaci s roboty obsahuje dva typy timeoutu:

*   <div class="li">TIMEOUT - timeout pro komunikaci. Pokud robot nebo server neobdrží od své protistrany žádnou komunikaci (nemusí to být však celá zpráva) po dobu tohoto časového intervalu, považují spojení za ztracené a okamžitě ho ukončí.</div>

*   <div class="li">TIMEOUT_RECHARGING - timeout pro dobíjení robota. Po té, co server přijme zprávu CLIENT_RECHARGING, musí robot nejpozději do tohoto časového intervalu odeslat zprávu CLIENT_FULL_POWER. Pokud to robot nestihne, server musí okamžitě ukončit spojení.</div>

</div>

## <a name="specialni_situace" id="specialni_situace">Speciální situace</a>

<div class="level2">

Při komunikaci přes komplikovanější síťovou infrastrukturu může docházet ke dvěma situacím:

*   <div class="li">Zpráva může dorazit rozdělena na několik částí, které jsou ze socketu čteny postupně. (K tomu dochází kvůli segmentaci a případnému zdržení některých segmentů při cestě sítí.)</div>

*   <div class="li">Zprávy odeslané brzy po sobě mohou dorazit téměř současně. Při jednom čtení ze socketu mohou být načteny obě najednou. (Tohle se stane, když server nestihne z bufferu načíst první zprávu dříve než dorazí zpráva druhá.)</div>

Za použití přímého spojení mezi serverem a roboty v kombinaci s výkonným hardwarem nemůže k těmto situacím dojít přirozeně, takže jsou testovačem vytvářeny uměle. V některých testech jsou obě situace kombinovány.

Každý správně implementovaný server by se měl umět s touto situací vyrovnat. Firmwary robotů s tímto faktem počítají a dokonce ho rády zneužívají. Pokud se v protokolu vyskytuje situace, kdy mají zprávy od robota předem dané pořadí, jsou v tomto pořadí odeslány najednou. To umožňuje sondám snížit jejich spotřebu a zjednodušuje to implementaci protokolu (z jejich pohledu).

</div>

## <a name="optimalizace_serveru" id="optimalizace_serveru">Optimalizace serveru</a>

<div class="level2">

Server optimalizuje protokol tak, že nečeká na dokončení zprávy, která je očividně špatná. Například na výzvu k autentizaci pošle robot pouze část zprávy s uživatelským jménem. Server obdrží např. 22 znaků uživatelského jména, ale stále neobdržel ukončovací sekvenci \a\b. Vzhledem k tomu, že maximální délka zprávy je 20 znaků, je jasné, že přijímaná zpráva nemůže být validní. Server tedy zareaguje tak, že nečeká na zbytek zprávy, ale pošle zprávu SERVER_SYNTAX_ERROR a ukončí spojení. V principu by měl postupovat stejně při vyzvedávání tajného vzkazu.

V případě části komunikace, ve které se robot naviguje k cílovým souřadnicím očekává tři možné zprávy: CLIENT_OK, CLIENT_RECHARGING nebo CLIENT_FULL_POWER. Pokud server načte část neúplné zprávy a tato část je delší než maximální délka těchto zpráv, pošle SERVER_SYNTAX_ERROR a ukončí spojení. Pro pomoc při optimalizaci je u každé zprávy v tabulce uvedena její maximální velikost.

</div>

## <a name="ukazka_komunikace" id="ukazka_komunikace">Ukázka komunikace</a>

<div class="level2">

<pre class="code">C: "Umpa_Lumpa\a\b"
S: "15045\a\b"
C: "5752\a\b"
S: "200 OK\a\b"
S: "102 MOVE\a\b"
C: "OK 0 1\a\b"
S: "102 MOVE\a\b"
C: "OK 0 2\a\b"
S: "103 TURN LEFT\a\b"
C: "OK 0 2\a\b"
S: "102 MOVE\a\b"
C: "OK -1 2\a\b"
S: "102 MOVE\a\b"
C: "OK -2 2\a\b" 
S: "104 TURN RIGHT\a\b"
C: "OK -2 2\a\b" 
S: "104 TURN RIGHT\a\b"
C: "OK -2 2\a\b" 
S: "105 GET MESSAGE\a\b" 
C: "Tajny vzkaz.\a\b"
S: "106 LOGOUT\a\b"</pre>

</div>

## <a name="testovani" id="testovani">Testování</a>

<div class="level2">

K testování je připraven obraz operačního systému Tiny Core Linux, který obsahuje tester domácí úlohy. Obraz je kompatibilní s aplikací VirtualBox.

</div>

### <a name="tester" id="tester">Tester</a>

<div class="level3">

Stáhněte a rozbalte obraz. Výsledný soubor spusťte ve VirtualBoxu. Po spuštění a nabootování je okamžitě k dispozici shell. Tester se spouští příkazem _tester_:

<pre class="code">tester <číslo portu> <vzdálená adresa> [čísla testů]</pre>

Prvním parametrem je číslo portu, na kterém bude naslouchat váš server. Následuje parametr se vzdálenou adresou serveru. Pokud je váš server spuštěn na stejném počítači jako VirtualBox, použijte adresu defaultní brány. Postup je naznačen na následujícím obrázku:

![Tester](https://github.com/MatejMicikas/BI-PSI/blob/main/testing-image-example.png)

Výstup je poměrně dlouhý, proto je výhodné přesměrovat jej příkazu less, ve kterém se lze dobře pohybovat, nebo lze použít klávesovou kombinaci "Shift₊PageUp" nebo "Shift+PageDown" pro pohyb ve.- výstupu nahoru nebo dolu (historie je však krátká, nelze se posunout moc daleko nazpět).

Pokud není zadáno číslo testu, spustí se postupně všechny testy. Testy lze spouštět i jednotlivě. Následující ukázka spustí testy 2, 3 a 8:

<pre class="code">tester 3999 10.0.2.2 2 3 8 | less</pre>

</div>

#### <a name="mozne_problemy_v_operacnim_systemu_windows" id="mozne_problemy_v_operacnim_systemu_windows">Možné problémy v operačním systému windows</a>

<div class="level4">

V některých instalací <acronym title="Operating System">OS</acronym> Windows bývá problém se standardní konfigurací virtuálního stroje. Pokud se nedaří spojit tester ve virtuálce s testovaným serverem v hostitelském operačním systému, tak použijte následující postup:

*   <div class="li">U vypnuté virtuálky s testerem změňte nastavení síťového adaptéru z NAT na Host-only network.</div>

*   <div class="li">V hostitelském <acronym title="Operating System">OS</acronym> by se mělo objevit síťové rozhraní patřící VirtualBoxu. To lze zjistit z příkazové řádky příkazem _ipconfig_. IP adresa tohoto rozhraní bude pravděpodobně 192.168.56.1/24.</div>

*   <div class="li">Ve virtuálce s testerem je teď nutné ručně nastavit IP adresu síťovému rozhraní eth0:</div>

sudo ifconfig eth0 192.168.56.2 netmask 255.255.255.0

*   <div class="li">Nyní je možné spustit tester, ale jako cílovou adresu zadejte IP adresu síťového rozhraní v hostitelském <acronym title="Operating System">OS</acronym>:</div>

tester 3999 192.168.56.1

</div>

### <a name="prehled_testu" id="prehled_testu">Přehled testů</a>

#### <a name="idealni_situace" id="idealni_situace">Ideální situace</a>

<div class="level4">

Test 1 pošle validní data pro autentizaci a jeho robot se po prvním pohybu nachází na cílových souřadnicích a očekává vyzvednutí tajného vzkazu.

</div>

#### <a name="kontrola_autentizace" id="kontrola_autentizace">Kontrola autentizace</a>

<div class="level4">

Testy 2 až 4 ověřují, zda server správně kontroluje chyby při autentizaci. (Neplatný potvrzovací kód, speciální znaky v uživatelském jméně…)

</div>

#### <a name="kontrola_osetreni_specialnich_situaci" id="kontrola_osetreni_specialnich_situaci">Kontrola ošetření speciálních situací</a>

<div class="level4">

Testy 5 až 7 kontrolují správné reakce na speciální situace (segmentace a spojování zpráv).

</div>

#### <a name="kontrola_detekce_syntaktickych_chyb" id="kontrola_detekce_syntaktickych_chyb">Kontrola detekce syntaktických chyb</a>

<div class="level4">

Testy 8 až 14 ověřují detekci syntaktických chyb.

</div>

#### <a name="kontrola_detekce_komunikacniho_timeoutu" id="kontrola_detekce_komunikacniho_timeoutu">Kontrola detekce komunikačního timeoutu</a>

<div class="level4">

Testy 15 a 16 ověřují, zda server správně timeoutuje a ukončuje spojení.

</div>

#### <a name="kontrola_optimalizace_serveru" id="kontrola_optimalizace_serveru">Kontrola optimalizace serveru</a>

<div class="level4">

Testy 17 až 21 kontrolují, zda je server správně optimalizován.

</div>

#### <a name="kontrola_navigace_robota" id="kontrola_navigace_robota">Kontrola navigace robota</a>

<div class="level4">

Testy 20 až 24 kontrolují, zda server dokáže navést robota do cíle. Pozor! Roboti mohou chybovat a občas neprovedou posun vpřed.

</div>

#### <a name="kontrola_reakce_na_dobijeni_robota" id="kontrola_reakce_na_dobijeni_robota">Kontrola reakce na dobíjení robota</a>

<div class="level4">

Testy 25 až 28 kontrolují, zda server správně reaguje na dobíjení robota.

</div>

#### <a name="kontrola_paralelniho_zpracovani" id="kontrola_paralelniho_zpracovani">Kontrola paralelního zpracování</a>

<div class="level4">

Test 29 spustí tři testovací instance paralelně.

</div>

#### <a name="testovani_nahodne_generovanymi_situacemi" id="testovani_nahodne_generovanymi_situacemi">Testování náhodně generovanými situacemi</a>

<div class="level4">

Test 30 vytváří validní, ale náhodně generovanou komunikaci. Tento test je použit při finálním testu.

</div>

#### <a name="konecna_kontrola" id="konecna_kontrola">Konečná kontrola</a>

<div class="level4">

Tento test se spustí automaticky po úspěšném dokončení všech předchozích testů. Spustí se paralelně 3 instance testu 30.

</div>

### <a name="ke_stazeni" id="ke_stazeni">Ke stažení</a>

<div class="level3">

Aplikace VirtualBox: [https://www.virtualbox.org/wiki/Downloads](https://www.virtualbox.org/wiki/Downloads "https://www.virtualbox.org/wiki/Downloads")

Tester: [Všechny verze testeru ke stažení](https://drive.google.com/drive/folders/1QzPyzZeLNWZhjtbaTGehyNu-zgHcInta "https://www.virtualbox.org/wiki/Downloads](https://drive.google.com/drive/folders/1QzPyzZeLNWZhjtbaTGehyNu-zgHcInta")

Adresář z předchozího linku obsahuje adresáře s vývojovými verzemi testeru. V každém adresáři označeném jako vX (kde X je číslo verze) naleznete následující soubory:

    BI-PSI_tester_2021_vX.ova - virtuálka s testerem
    psi-tester-2021-vX_x64.bz2 - verze pro linux 64-bit
    psi-tester-2021-vX_x86.bz2 - verze pro linux 32-bit
    psi-tester-2021-vX_arm.bz2 - verze pro arm (použitelné pro uživatele MacBook s procesorem M1 - čtěte dále)

Uživatelé MacBooku s procesorem M1 mají trochu komplikovanější spouštění. Společnost Apple bohužel nedává moc možností jak zkompilovat C++ kód kdekoliv jinde, než v operačním systému MacOS. Tester zkompilovaný pro procesory ARM je kompatiblní s architekturou M1, ale protože je zkompilovaný pro linux, nelze jej nativně spustit. Doporučený způsob testování je použít VirtualBox (viz link výše) a v něm nainstalujte libovolnou linuxou distribuci pro architekturu ARM. Tester pro arm by měl v takto nainstalovaném systému fungovat. (Bohužel nemůžeme připravit image pro ARM, protože nemáme stroj s potřebnou architekturou.)

</div>
