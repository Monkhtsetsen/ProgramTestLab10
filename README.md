# Лаборатори 10 — Definition-Use хосууд (DataFlow)

**Нэр:** Түмэннасан Мөнхцэцэн  
**Оюутны код:** *B232270025*  

## Орчин

Төслийг **WSL2 (Ubuntu)** дээр ажиллуулсан (`/mnt/d/projects/ProgramTestLab10`). `pom.xml`-д `maven.compiler.release=17` тул OpenJDK 17 ашигласан.

```
$ java -version
openjdk version "17.0.21.0.3" 2026-10-24
OpenJDK Runtime Environment (build 17.0.21.0.3+1-1-26.04.1-Ubuntu)
OpenJDK 64-Bit Server VM (build 17.0.21.0.3+1-1-26.04.1-Ubuntu, mixed mode, sharing)

$ mvn --version
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: /mnt/c/Program Files/apache-maven-3.9.9-bin/apache-maven-3.9.9
Java version: 17.0.21.0.3, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "6.6.114.1-microsoft-standard-wsl2", arch: "amd64", family: "unix"
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
| y        | 13      | 17      | c-use | 13→15(T)→16→17 | `gcd_oneIteration`, `gcd_multipleIterations`, `gcd_zeroX_entersLoop` | тийм |
| y        | 18      | 15      | p-use | 18→15          | `gcd_multipleIterations`                     | тийм       |
| y        | 18      | 16      | c-use | 18→15(T)→16    | `gcd_multipleIterations`                     | тийм       |
| y        | 18      | 17      | c-use | 18→15(T)→16→17 | `gcd_multipleIterations`                     | тийм       |
| tmp      | 16      | 18      | c-use | 16→17→18       | `gcd_oneIteration`, `gcd_multipleIterations` | тийм       |


**Лекцэд дутуу байсан хосууд:** `y (18,16)` — дараагийн эргэлтэд `x % y` нь L18-д бичигдсэн `y`-ийг c-use хийнэ. `y (13,17)` ба `y (18,17)` — `x = y` (L17) нь `y`-ийн c-use. L13→L17 нь эхний эргэлт (`y`-г L18 дахин бичихээс өмнө). L18→L17 нь хоёр дахь эргэлт: `gcd(12,8)` дээр эхний эргэлт `y=4` гэж бичээд `y != 0` тул дараагийн `x = y` тэр def-ийг уншина. `gcd(10,5)` нэг эргэлтээр дуусдаг тул `y (18,17)`-г хамрахгүй.

### Тестүүд (`GcdTest.java`)

`mvn test -Dtest=GcdTest` → **Tests run: 4, Failures: 0**.


| Тест                     | Хамарсан DU хос (файлын мөр)                       |
| ------------------------ | -------------------------------------------------- |
| `gcd_noLoop_yZero`       | x(13,20), y(13,15)                                              |
| `gcd_oneIteration`       | x(13,16), y(13,16), y(13,17), tmp(16,18)                        |
| `gcd_multipleIterations` | x(17,16), x(17,20), y(18,15), y(18,16), y(18,17), tmp(16,18)    |
| `gcd_zeroX_entersLoop`   | x(13,16), y(13,17) (`x=0` үеийн c-use; `x = y` нь параметр `y`) |


- `gcd(7,0)` дангаараа **x(13,20)** хосыг (давталтгүй) хамарна.
- `gcd(12,8)` **2 удаа** давталт хийж **x(17,·)**, **y(18,15)**, **y(18,16)**, **y(18,17)**, **tmp(16,18)** хосуудыг хамарна.

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




### B3 — DU хүснэгт (def-clear замтай бүх хос)

**Kill:** L46 `streak = 0` нь streak-ийн L36 ба L41 def-ийг устгана. Дараах хослол def-clear замгүй тул DU биш: **streak (36,42)** ба **streak (36,43)** (L41 завсарт дахин бичнэ); **streak (46,42)** (L41 нь L46-ийг L42-оос өмнө дахин бичнэ); **streak (41,46)** (L46 use биш, шинэ def). L46-ийн дараах L42 дээрх use нь хуучин L41 биш, шинэ L41 def — хүснэгтийн **streak (41,42)** мөр.


| Хувьсагч   | Def | Use | Төрөл | Def-clear зам                              | Тест ID                                              | Хамрагдсан |
| ---------- | --- | --- | ----- | ------------------------------------------ | ---------------------------------------------------- | ---------- |
| attendance | 31  | 32  | p-use | 31→32                                      | `invalid_nullAttendance`                             | тийм       |
| attendance | 31  | 38  | p-use | 31→32(F)→35→36→37→38                       | `streakLongerThanFive`, `edge_empty_thresholdZero`   | тийм       |
| threshold  | 31  | 32  | p-use | 31→32 (`attendance != null` үед)           | `invalid_negativeThreshold`                          | тийм       |
| threshold  | 31  | 50  | p-use | 31→32(F)→…→50                              | `baseOnly_exactThreshold`, `edge_empty_thresholdZero` | тийм      |
| present    | 35  | 40  | c-use | 35→38(T)→39(T)→40                          | `baseOnly_exactThreshold`                            | тийм       |
| present    | 35  | 50  | p-use | 35→38(F)→49→50, эсвэл 39(F)→…→50 (L40 үгүй) | `edge_empty_thresholdZero`, `zeroScore_allAbsent`   | тийм       |
| present    | 40  | 40  | c-use | 40→41→42→38(T)→39(T)→40                    | `streakLongerThanFive`, `defs_score49_killThenBonus` | тийм       |
| present    | 40  | 50  | p-use | 40→…→38(F)→49→50                           | `baseOnly_exactThreshold`, `m2_kill_bestMustTrackStreakNotPresent` | тийм |
| streak     | 36  | 41  | c-use | 36→38(T)→39(T)→41                          | `baseOnly_exactThreshold`                            | тийм       |
| streak     | 41  | 41  | c-use | 41→42→38(T)→39(T)→41 (L46 алгасна)         | `streakLongerThanFive`                               | тийм       |
| streak     | 41  | 42  | p-use | 41→42                                      | `streakLongerThanFive` (T), `m2_kill_bestMustTrackStreakNotPresent` (F) | тийм |
| streak     | 41  | 43  | c-use | 41→42(T)→43                                | `streakLongerThanFive`, `bonusOnly_belowThreshold_longStreak` | тийм |
| streak     | 46  | 41  | c-use | 46→38(T)→39(T)→41                          | `defs_score49_killThenBonus`, `longStreakBroken_middle` | тийм    |
| best       | 37  | 42  | p-use | 37→38(T)→39(T)→41→42(T)                    | `baseOnly_exactThreshold`                            | тийм       |
| best       | 37  | 53  | p-use | 37→38(F)→49→50→53(F), эсвэл 39(F)→…→53(F)  | `edge_empty_thresholdZero`, `zeroScore_allAbsent`    | тийм       |
| best       | 43  | 42  | p-use | 43→38(T)→39(T)→41→42                       | `streakLongerThanFive` (T), `longStreakBroken_middle` (F) | тийм  |
| best       | 43  | 53  | p-use | 43→…→38(F)→49→50→53                        | `bonusOnly_belowThreshold_longStreak` (T), `baseOnly_exactThreshold` (F) | тийм |
| a          | 38  | 39  | p-use | 38→39                                      | `streakLongerThanFive`, `zeroScore_allAbsent`        | тийм       |
| score      | 49  | 54  | c-use | 49→50(F)→53(T)→54                          | `bonusOnly_belowThreshold_longStreak`, `defs_score49_killThenBonus` | тийм |
| score      | 49  | 56  | c-use | 49→50(F)→53(F)→56                          | `zeroScore_allAbsent`                                | тийм       |
| score      | 51  | 54  | c-use | 51→53(T)→54                                | `streakLongerThanFive`                               | тийм       |
| score      | 51  | 56  | c-use | 51→53(F)→56                                | `baseOnly_exactThreshold`, `defs_score51_reachesReturn`, `edge_empty_thresholdZero` | тийм |
| score      | 54  | 56  | c-use | 54→56                                      | `streakLongerThanFive`, `bonusOnly_belowThreshold_longStreak` | тийм |


**Биелэшгүй p-use салаа:**

- **best (37, 42 F).** L37 `best = 0` L42 дээр амьд байх үед L41 дөнгөж streak-ийг хамгийн багадаа 1 болгосон (анхны ирц, эсвэл L46-ийн дараах анхны ирц). `streak > 0` үргэлж үнэн тул L42-ийн **F** салаа энэ def-ээр гарахгүй. F салаа зөвхөн L43-ийн дараа (`best` аль хэдийн ≥ 1) гарна; тэр use нь **best (43, 42)**.
- **best (37, 53 T).** L37-ийн утга 0. `0 >= 5` худал тул L53-ийн **T** салаа энэ def-ээр хүрэхгүй. T салаа L43 дээр `streak >= 5` болсон **best (43, 53)**-оор л гарна. L37→L53 зам зөвхөн **F** (`edge_empty_thresholdZero`, `zeroScore_allAbsent`).

### B4 — All-definitions тестүүд

**Хамгийн цөөн нь 2.** `score`-ийн use зөвхөн L54 ба L56 (хоёулаа L51-ийн дараа).

- `score = 0` (L49) use-д def-clear хүрэхийн тулд L51 ажиллахгүй (`present < threshold`).
- `score = 5` (L51) use-д хүрэхийн тулд L51 заавал ажиллана.

Нэг гүйцэтгэлд L51 нь L49-ийг use-ээс өмнө устгана. Тиймээс **score@49 ба score@51-ийг нэг тест хамрахгүй**, доод хязгаар 2.

Бусад def нэг тестэд багтана. `defs_score49_killThenBonus` (`{1,1,1,1,1,0,1}`, threshold=100, оноо 3): L51 алгасна, score@49→L54, score@54→L56, L46 kill дараа streak@46→L41, present/streak/best/a/attendance/threshold-ийн def use-д хүрнэ. `defs_score51_reachesReturn` (`{1,1,1}`, threshold=3, оноо 5): score@51→L56 (`best=3<5` тул L54 алгасна).

`null`, хоосон массив, бүгд тасалсан зам all-defs-д гурав дахь тест шаардахгүй: attendance ба threshold-ийн def L32(F) болон L38/L50 дээр аль хэдийн use-д хүрдэг. Тэдгээрийг all-DU-pairs шатанд салаа ба хос хамрахад үлдээсэн. «5-аас бага болохгүй» гэсэн өмнөх үндэслэл буруу — L46, `null`, хоосон массивыг тусад нь салгах албагүй.

`mvn verify -Dtest=GcdTest,BonusTest$AllDefinitions` → **Tests run: 6, Failures: 0** (`results/mvn-verify-alldefs.txt`). Үүнээс bonus-ийн all-defs тест **2**.


| Тест ID                      | Def-үүдийг use-д хүргэнэ                                      |
| ---------------------------- | ------------------------------------------------------------- |
| `defs_score49_killThenBonus` | score@49→L54, score@54→L56, streak@46→L41, бусад def (score@51-ээс бусад) |
| `defs_score51_reachesReturn` | score@51→L56                                                  |




### B5 — JaCoCo (all-defs шат)

`results/jacoco-alldefs.csv`: **BRANCH_MISSED=2**, BRANCH_COVERED=14; LINE_MISSED=2, LINE_COVERED=22.

All-defs (2 тест) хангагдсан ч branch 100% биш. Дутуу 2 салаа нь L32-ын онцгой зам: `attendance == null` (T) ба `threshold < 0` (T). LINE_MISSED=2: далд конструктор ба L33 `throw` (all-defs олонлог тэр мөрийг гүйцэтгэхгүй). Эдгээр салааг all-DU-pairs тест (`invalid_nullAttendance`, `invalid_negativeThreshold`) нөхөнө.

### B4 (үргэлжлэл) — All-DU-pairs тестүүд

`mvn test -Dtest=BonusTest$AllDuPairs` → **Tests run: 10, Failures: 0**. Бүтэн `mvn verify` → **Tests run: 16, Failures: 0**.


| Тест ID                                 | Нэмэлт DU / branch                                      |
| --------------------------------------- | ------------------------------------------------------- |
| `baseOnly_exactThreshold`               | score(51,56), best(43,53) F                             |
| `bonusOnly_belowThreshold_longStreak`   | score(49,54), best(43,53) T                             |
| `bestUnchanged_whenStreakNotGreater`    | урт цуваа, present < threshold                          |
| `longStreakBroken_middle`               | best(43,42) F, streak(46,41)                            |
| `streakLongerThanFive`                  | present(40,40), streak(41,41), score(51,54)            |
| `invalid_negativeThreshold`             | threshold < 0                                           |
| `zeroScore_allAbsent`                   | present(35,50), best(37,53) F, score(49,56)             |
| `invalid_nullAttendance`                | attendance == null                                      |
| `edge_empty_thresholdZero`              | present(35,50) хоосон давталт, score(51,56)             |
| `m2_kill_bestMustTrackStreakNotPresent` | streak(41,42) F; L43 буруу use (M2 kill)                |




### B5 (үргэлжлэл) — JaCoCo all-DU-pairs


| Тайлан                       | BRANCH_MISSED | BRANCH_COVERED | LINE_MISSED | LINE_COVERED |
| ---------------------------- | ------------- | -------------- | ----------- | ------------ |
| `results/jacoco-alldefs.csv` | 2             | 14             | 2           | 22           |
| `results/jacoco-final.csv`   | 0             | 16             | 1           | 23           |


All-defs үед branch **14/16**; бүх 16 тестийн дараа **16/16**. Эцсийн `LINE_MISSED=1` — 7-р мөр, далд конструктор (хэвийн).

**Branch 100% байхад үлдэх бодит DU хос:** **score (51, 56)**. Энэ хос биелэнэ (`baseOnly_exactThreshold`: `{1,1,1}`, threshold=3, `best=3<5`, return 5; зам 51→53(F)→56). Гэвч L50 ба L53-ийн дөрвөн салааг дараах оролтоор бүрэн гүйцээж болно, тэр хосыг огт хүрэлгүй:

- `{1,1,1,1,1}`, threshold=1 → оноо 8: L50 **T**, L53 **T**. score@51 L54 дээр use болж L56 хүртэлх зам тасарна (L54 шинэ def).
- `{0,0,0}`, threshold=1 → оноо 0: L50 **F**, L53 **F**. L51 ажиллахгүй, return нь score@49.

Салаа тус бүр хамрагдсан ч **L50 T ба L53 F хамт** гарахгүй тул score@51 return дээр use болохгүй. Branch хамралт predicate бүрийн ирмэгийг тусад нь тоолдог; хоёр салааны хослолоор үүссэн def-clear замыг шаарддаггүй.

---



## Хэсэг C — Fault seeding

`DataFlowTargets.java` эх хувилбарыг мутаци бүрийн дараа `git restore`-оор буцаасан (эцэст commit-д өөрчлөлтгүй).


| ID  | Мутаци                               | Төрөл           | Үр дүн (`results/`)                                                                                                                       |
| --- | ------------------------------------ | --------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| M1  | `streak = streak + 1` устгасан       | Missing update  | **5** тест унав — `mvn-test-M1.txt` (ж: `streakLongerThanFive` expected 8, was 5; `defs_score49_killThenBonus` expected 3, was 0; `bonusOnly_...` expected 3, was 0) |
| M2  | `best = streak` → `best = present`   | Wrong use       | **1** тест унав — `m2_kill_...` (expected 5, was 8); `mvn-test-M2.txt`, `mvn-test-M2-kill.txt` |
| M3  | `int total = present + best;` нэмсэн | Dead definition | тест **унасангүй** — `mvn-test-M3.txt` BUILD SUCCESS; IDE: *Variable 'total' is never used*                                               |


**M2 тайлбар:** `{1,1,0,1,1,1}`, `threshold=0` — зөв код `best=3` (нийт оноо 5), M2 дээр L43 `best=present` болох үед `best=5` (+3) → 8. `streak > best` нь **хатуу** (`>`) тул зарим оролтоор алдаа нуугдана; kill тест `present` ба `streak`-ийг ялгана.

**M3 тайлбар:** Гүйцэтгэлийн тест илрүүлдэггүй; static analysis (IDE, SpotBugs) dead store гэж анхааруулна.

---



## Дүгнэлт

DU анализ def→use зам, kill, def-clear-ийг шууд заадаг тул урсгалын алдаа (M1 — streak def дутуу, M2 — буруу use) ихэнхдээ илэрдэг; JaCoCo branch 100% үүнийг бүрэн орлохгүй (M2 зарим сценариор амьд үлдэж, M3 тестэд огт илрэхгүй). Branch 16/16 байхад ч score(51,56) шиг биелэх хос тусдаа тестгүйгээр хамрагдахгүй. Хамгийн их цаг bonus-ийн DU хүснэгт, CFG, all-defs-ийн хоёр тестийн үндэслэл (score@49 ба score@51), M2 kill тестэнд орсон. Fault seeding-ийн үр дүнгээс M1 нь DU чиглэлийн тестийн давуу талыг, M3 нь static analysis-ийн шаардлагыг харуулна.

---



## Нэмэлт даалгавар (AI өнцөг)

`bonus()`-ийн эх кодыг AI туслахад өгч «бүх DU хосын хүснэгт» гэж аваад өөрийн B3 хүснэгттэй мөр мөрөөр тулгаж үзсэн. (*Энэ хэсгийг commit-д оруулаагүй — идэвх/оролцооны даалгавар.*)

### AI-ийн жишээ гаралт (товч)

AI ихэвчлэн **present, streak, best, score, a**-ийн олон def×use-ийг автоматаар үржүүлж гаргадаг: ж `streak(41,46)`-ийг c-use гэж (L46 бол use биш), `attendance(31,56)`, `score(49,51)` гинж гэх мэт. `streak(46,41)` бол биелэх хос (kill-ийн дараа L41). For-each-ийг `L38 def a → L39 use` гэж зөв дурдаж чаддаг ч **attendance p-use** (давталтын нөхцөл) ба **давталт бүрийн `a` def**-ийг задлан тайлбарлах нь бага. **Kill (L46)**-ийг ховор тэмдэглэсэн ч kill-ийн дараа **шинэ** streak def (L36/L41) ямар use руу явдгийг замтай нь заагаагүй.

### Тулгалт


| Төрөл        | AI                                                                              | Миний B3                                                            |
| ------------ | ------------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| Алдсан / сул | `score(51,56)`; `threshold(31,50)`; `streak(36,41)`; iteration-ийн олон `a` def | `score` гурван def; `edge_empty` / exception тестүүд                |
| Илүү / буруу | `streak(41,46)`-ийг use гэж; зарим замгүй хос                                   | L41→L46 **биелэшгүй** (L46 — kill def); kill-ээр тасарсан хос — DU биш |
| Kill         | Ерөнхий тайлбар                                                                 | L46 kill тусгай; post-kill def-clear                                |
| Биелэшгүй    | Ихэнхдээ байхгүй                                                                | Тусдаа мөр/тайлбар                                                  |




### Дүгнэлт (AI vs гар анализ)

AI-ийн хүснэгт **хэмжээсээр** том боловч удирдлагын урсгал (for-each, kill, олон def) дээр алдана. **L46**-ийг streak-ийн use гэж бичих нь буруу — энэ нь kill def. Миний хүснэгт kill болон def-clear замгүй хосыг DU гэж тоолохгүй гэж тодорхойлсон. **Score**-ийн гурван def-ийг нэгтгэж бичих нь all-defs тестийн үндэслэлийг сулруулдаг; би тус бүрийг өөр замаар тестлэсэн. For-each дээр attendance ба `a` use-ийг ялгах нь CFG болон B2-оор илүү тодорхой. AI биелэшгүй хосыг ихэвчлэн заадаггүй; хүн kill-ийн дараах def→use хос хоосон эсэхийг шийддэг. AI-г **эхлэл болгон** ашиглаж, kill / def-clear / биелэшгүй ялгалыг **гараар баталгаажуулах** нь зөв — branch 100% байсан ч DU-ийн нарийн шалгалт (ж: M2-ийн нууцлагдсан алдаа) үүнийг батална.