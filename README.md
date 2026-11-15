# Лаборатори 10 — Definition-Use хосууд (DataFlow)

**Нэр:** Түмэннасан Мөнхцэцэн  
**Оюутны код:** *B232270025*  

## Орчин

```
> java -version
java version "22.0.1" 2024-04-16
Java(TM) SE Runtime Environment (build 22.0.1+8-16)
Java HotSpot(TM) 64-Bit Server VM (build 22.0.1+8-16, mixed mode, sharing)

> mvn -version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: C:\Program Files\apache-maven-3.9.9-bin\apache-maven-3.9.9
Java version: 22.0.1, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-22
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

`mvn verify` (эцсийн): **Tests run: 16, Failures: 0** — `results/mvn-verify-final.txt`

---



## Хэсэг A — `gcd` DU хосууд

Файлын мөр (`DataFlowTargets.java`): def `x`,`y` — **13**; `while` **15**; `tmp` **16**; `x` **17**; `y` **18**; `return` **20**.


| Хувьсагч | Def мөр | Use мөр | Төрөл | Def-clear зам  | Тест ID                                      | Хамрагдсан |
| -------- | ------- | ------- | ----- | -------------- | -------------------------------------------- | ---------- |
| x        | 13      | 16      | c-use | 13→15(T)→16    | `gcd_oneIteration`, `gcd_multipleIterations` | тийм       |
| x        | 13      | 20      | c-use | 13→15(F)→20    | `gcd_noLoop_yZero`                           | тийм       |
| x        | 17      | 16      | c-use | 17→18→15(T)→16 | `gcd_multipleIterations`                     | тийм       |
| x        | 17      | 20      | c-use | 17→18→15(F)→20 | `gcd_multipleIterations`                     | тийм       |
| y        | 13      | 15      | p-use | 13→15          | `gcd_noLoop_yZero`, бусад тест               | тийм       |
| y        | 13      | 16      | c-use | 13→15(T)→16    | `gcd_oneIteration`                           | тийм       |
| y        | 18      | 15      | p-use | 18→15          | `gcd_multipleIterations`                     | тийм       |
| y        | 18      | 16      | c-use | 18→15(T)→16    | `gcd_multipleIterations`                     | тийм       |
| tmp      | 16      | 18      | c-use | 16→17→18       | `gcd_oneIteration`, `gcd_multipleIterations` | тийм       |


**Лекцэд дутуу байсан хос:** `y (18,16)` — давталтын дараагийн эргэн тойрогт `y`-ийн шинэ def-ийг `x % y` дээр c-use хийдэг.

### Тестүүд (`GcdTest.java`)

`mvn test -Dtest=GcdTest` → **Tests run: 4, Failures: 0**.


| Тест                     | Хамарсан DU хос (файлын мөр)                       |
| ------------------------ | -------------------------------------------------- |
| `gcd_noLoop_yZero`       | x(13,20), y(13,15)                                 |
| `gcd_oneIteration`       | x(13,16), y(13,16), tmp(16,18)                     |
| `gcd_multipleIterations` | x(17,16), x(17,20), y(18,15), y(18,16), tmp(16,18) |
| `gcd_zeroX_entersLoop`   | x(13,16) (`x=0` үеийн c-use)                       |


- `gcd(7,0)` дангаараа **x(13,20)** хосыг (давталтгүй) хамарна.
- `gcd(12,8)` **2 удаа** давталт хийж **x(17,·)**, **y(18,·)**, **tmp(16,18)** хосуудыг хамарна.

---



## Хэсэг B — `bonus`



### B1 — CFG

![bonus CFG](cfg-bonus.png)

For-each (мөр 38): нөхцөл — `attendance`-д дараагийн элемент байгаа эсэх (**attendance**-ийн p-use); алхам — дараагийн элементийг `a`-д оноох (**a**-ийн def).

### B2 — Def / Use (товч)


| Мөр        | Def                   | Use                                                    |
| ---------- | --------------------- | ------------------------------------------------------ |
| 31         | attendance, threshold | —                                                      |
| 32         | —                     | p-use attendance, threshold                            |
| 35–37      | present, streak, best | c-use present (40), p-use streak (42), p-use best (42) |
| 38         | a                     | p-use attendance, p-use a (39)                         |
| 40         | present               | c-use present                                          |
| 41         | streak                | c-use streak (41), p-use (42)                          |
| 43         | best                  | p-use best (42, 53)                                    |
| 46         | streak (kill)         | — өмнөх streak def-үүдийг **устгана** (ж: L41)          |
| 49, 51, 54 | score                 | c-use score (54), return (56)                          |
| 50         | —                     | p-use present                                          |
| 53         | —                     | p-use best                                             |




### B3 — DU хүснэгт (гол хосууд)

Kill: **мөр 46** `streak = 0` нь streak-ийн өмнөх def-ийг (жишээ нь L41) устгана; kill-ийн дараа хуучин def-ээс use руу def-clear зам байхгүй тул ийм хослолыг DU хос гэж тоолохгүй.


| Хувьсагч   | Def | Use | Төрөл          | Kill / тэмдэглэл | Тест ID                                                       |
| ---------- | --- | --- | -------------- | ---------------- | ------------------------------------------------------------- |
| attendance | 31  | 32  | p-use          | —                | `invalid_nullAttendance`                                      |
| attendance | 31  | 38  | p-use          | for-each         | `fullScore_thresholdAndStreakBonus`                           |
| threshold  | 31  | 32  | p-use          | —                | `invalid_nullAttendance`                                      |
| threshold  | 31  | 50  | p-use          | —                | `fullScore_...`, `edge_empty_...`                             |
| present    | 35  | 40  | c-use          | —                | `fullScore_...`                                               |
| present    | 40  | 50  | p-use          | loop             | `streakKill_...`                                              |
| streak     | 36  | 41  | c-use          | —                | `fullScore_...`                                               |
| streak     | 41  | 42  | p-use          | —                | `bestUnchanged_whenStreakNotGreater`                          |
| best       | 37  | 42  | p-use          | —                | `fullScore_...`                                               |
| best       | 43  | 53  | p-use          | —                | `bonusOnly_belowThreshold_longStreak`, `streakLongerThanFive` |
| a          | 38  | 39  | p-use          | —                | `fullScore_...`                                               |
| score      | 49  | 56  | c-use          | return           | `zeroScore_allAbsent`                                         |
| score      | 51  | 54  | c-use          | —                | `fullScore_...`                                               |
| score      | 54  | 56  | c-use          | —                | `fullScore_...`                                               |


**Биелэшгүй / DU биш хос (жишээ):** (streak, L41→L46) — L46 нь use биш, kill def; (streak, L46→L42) — kill-ийн дараа def-clear замгүй.

### B4 — All-definitions тестүүд

**Яагаад 5 тест вэ?** `score`-ийн гурван def (L49, L51, L54) нэг гүйцэтгэлд бүгдийг use (return, L54)-д хүргэж чадахгүй; streak-ийн kill (L46) тусдаа зам; `null` болон хоосон массив тусдаа. Тиймээс **5-аас бага болохгүй**.

`mvn test -Dtest=GcdTest,BonusTest$AllDefinitions` → **Tests run: 9, Failures: 0** (`results/mvn-verify-alldefs.txt`).


| Тест ID                             | Def-үүдийг хамарна                                  |
| ----------------------------------- | --------------------------------------------------- |
| `fullScore_thresholdAndStreakBonus` | attendance, a, present, streak, best, score L51/L54 |
| `zeroScore_allAbsent`               | present, score L49→return                           |
| `streakKill_midStreakBreak`         | streak L46 (kill)                                   |
| `invalid_nullAttendance`            | attendance/threshold p-use L32                      |
| `edge_empty_thresholdZero`          | for-each 0 удаа, threshold L50                      |




### B5 — JaCoCo (all-defs шат)

`results/jacoco-alldefs.csv`: **BRANCH_MISSED=1**, BRANCH_COVERED=15; LINE_MISSED=1 (далд конструктор).

All-defs хангагдсан ч branch 100% биш — нэг predicate-ийн салаа (жишээ нь `streak > best`-ийн F, `best >= 5`-ийн F) all-DU-pairs тестээр нөхөгдөнө.

### B4 (үргэлжлэл) — All-DU-pairs тестүүд

`mvn test -Dtest=BonusTest$AllDuPairs` → **Tests run: 7, Failures: 0**. Бүтэн `mvn verify` → **Tests run: 16**.


| Тест ID                                 | Нэмэлт DU / branch           |
| --------------------------------------- | ---------------------------- |
| `baseOnly_exactThreshold`               | threshold L50, score L51     |
| `bonusOnly_belowThreshold_longStreak`   | best L53, streak L41→L43     |
| `bestUnchanged_whenStreakNotGreater`    | L42 false салалт             |
| `longStreakBroken_middle`               | L53, threshold хангаагүй     |
| `streakLongerThanFive`                  | present + streak урт         |
| `invalid_negativeThreshold`             | threshold < 0                |
| `m2_kill_bestMustTrackStreakNotPresent` | L43 буруу use (M2 kill)      |




### B5 (үргэлжлэл) — JaCoCo all-DU-pairs


| Тайлан                       | BRANCH_MISSED | BRANCH_COVERED | LINE_MISSED | LINE_COVERED |
| ---------------------------- | ------------- | -------------- | ----------- | ------------ |
| `results/jacoco-alldefs.csv` | 1             | 15             | 1           | 23           |
| `results/jacoco-final.csv`   | 0             | 16             | 1           | 23           |


All-defs үед branch **15/16**; бүх 16 тестийн дараа **16/16**. `LINE_MISSED=1` — 7-р мөр, далд конструктор (хэвийн). Branch 100% байсан ч DU хүснэгтэд биелэшгүй хос үлдэж болно — **branch ≠ DU** (лекц 10).

---



## Хэсэг C — Fault seeding

`DataFlowTargets.java` эх хувилбарыг мутаци бүрийн дараа `git restore`-оор буцаасан (эцэст commit-д өөрчлөлтгүй).


| ID  | Мутаци                               | Төрөл           | Үр дүн (`results/`)                                                                                                                       |
| --- | ------------------------------------ | --------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| M1  | `streak = streak + 1` устгасан       | Missing update  | **5** тест унав — `mvn-test-M1.txt` (ж: `fullScore_thresholdAndStreakBonus` expected 8, was 5; `bonusOnly_...` expected 3, was 0)         |
| M2  | `best = streak` → `best = present`   | Wrong use       | **1** тест унав — `m2_kill_...` (expected 5, was 8); `mvn-test-M2.txt`, `mvn-test-M2-kill.txt` |
| M3  | `int total = present + best;` нэмсэн | Dead definition | тест **унасангүй** — `mvn-test-M3.txt` BUILD SUCCESS; IDE: *Variable 'total' is never used*                                               |


**M2 тайлбар:** `{1,1,0,1,1,1}`, `threshold=0` — зөв код `best=3` (нийт оноо 5), M2 дээр L43 `best=present` болох үед `best=5` (+3) → 8. `streak > best` нь **хатуу** (`>`) тул зарим оролтоор алдаа нуугдана; kill тест `present` ба `streak`-ийг ялгана.

**M3 тайлбар:** Гүйцэтгэлийн тест илрүүлдэггүй; static analysis (IDE, SpotBugs) dead store гэж анхааруулна.

---



## Дүгнэлт

DU анализ def→use зам, kill, def-clear-ийг шууд заадаг тул урсгалын алдаа (M1 — streak def дутуу, M2 — буруу use) ихэнхдээ илэрдэг; JaCoCo branch 100% үүнийг бүрэн орлохгүй (M2 зарим сценариор амьд үлдэж, M3 тестэд огт илрэхгүй). Хамгийн их цаг bonus-ийн DU хүснэгт, CFG, all-defs-ийн таван тестийн үндэслэл, M2 kill тестэнд орсон. Fault seeding-ийн үр дүнгээс M1 нь DU чиглэлийн тестийн давуу талыг, M3 нь static analysis-ийн шаардлагыг харуулна.

---



## Нэмэлт даалгавар (AI өнцөг)

`bonus()`-ийн эх кодыг AI туслахад өгч «бүх DU хосын хүснэгт» гэж аваад өөрийн B3 хүснэгттэй мөр мөрөөр тулгаж үзсэн. (*Энэ хэсгийг commit-д оруулаагүй — идэвх/оролцооны даалгавар.*)

### AI-ийн жишээ гаралт (товч)

AI ихэвчлэн **present, streak, best, score, a**-ийн олон def×use-ийг автоматаар үржүүлж гаргадаг: ж `streak(41,46)` c-use, `streak(46,41)` урвуу зам, `attendance(31,56)`, `score(49,51)` гинж гэх мэт. For-each-ийг `L38 def a → L39 use` гэж зөв дурдаж чаддаг ч **attendance p-use** (давталтын нөхцөл) ба **давталт бүрийн `a` def**-ийг задлан тайлбарлах нь бага. **Kill (L46)**-ийг ховор тэмдэглэсэн ч kill-ийн дараа **шинэ** streak def (L36/L41) ямар use руу явдгийг замтай нь заагаагүй.

### Тулгалт


| Төрөл        | AI                                                                              | Миний B3                                                            |
| ------------ | ------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| Алдсан / сул | `score(51,56)`; `threshold(31,50)`; `streak(36,41)`; iteration-ийн олон `a` def | `score` гурван def; `edge_empty` / exception тестүүд                |
| Илүү / буруу | `streak(41,46)`-ийг use гэж; зарим замгүй хос                                   | L41→L46 **биелэшгүй** (L46 — kill def); kill-ээр тасарсан хос — DU биш |
| Kill         | Ерөнхий тайлбар                                                                 | L46 kill тусгай; post-kill def-clear                                |
| Биелэшгүй    | Ихэнхдээ байхгүй                                                                | Тусдаа мөр/тайлбар                                                  |




### Дүгнэлт (AI vs гар анализ)

AI-ийн хүснэгт **хэмжээсээр** том боловч удирдлагын урсгал (for-each, kill, олон def) дээр алдана. **L46**-ийг streak-ийн use гэж бичих нь буруу — энэ нь kill def. Миний хүснэгт kill болон def-clear замгүй хосыг DU гэж тоолохгүй гэж тодорхойлсон. **Score**-ийн гурван def-ийг нэгтгэж бичих нь all-defs тестийн үндэслэлийг сулруулдаг; би тус бүрийг өөр замаар тестлэсэн. For-each дээр attendance ба `a` use-ийг ялгах нь CFG болон B2-оор илүү тодорхой. AI биелэшгүй хосыг ихэвчлэн заадаггүй; хүн kill-ийн дараах def→use хос хоосон эсэхийг шийддэг. AI-г **эхлэл болгон** ашиглаж, kill / def-clear / биелэшгүй ялгалыг **гараар баталгаажуулах** нь зөв — branch 100% байсан ч DU-ийн нарийн шалгалт (ж: M2-ийн нууцлагдсан алдаа) үүнийг батална.