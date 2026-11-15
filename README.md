# Лаборатори 10 — Definition-Use хосууд (DataFlow)

**Нэр:** Түмэннасан Мөнхцэцэн  
**Оюутны код:** *B232270025*  

## Хэсэг A — `gcd` DU хосууд

Файлын мөр (`DataFlowTargets.java`): def `x`,`y` — **13**; `while` **15**; `tmp` **16**; `x` **17**; `y` **18**; `return` **20**.

| Хувьсагч | Def мөр | Use мөр | Төрөл | Def-clear зам | Тест ID | Хамрагдсан |
|---------|---------|---------|-------|---------------|---------|------------|
| x | 13 | 16 | c-use | 13→15(T)→16 | `gcd_oneIteration`, `gcd_multipleIterations` | тийм |
| x | 13 | 20 | c-use | 13→15(F)→20 | `gcd_noLoop_yZero` | тийм |
| x | 17 | 16 | c-use | 17→18→15(T)→16 | `gcd_multipleIterations` | тийм |
| x | 17 | 20 | c-use | 17→18→15(F)→20 | `gcd_multipleIterations` | тийм |
| y | 13 | 15 | p-use | 13→15 | `gcd_noLoop_yZero`, бусад тест | тийм |
| y | 13 | 16 | c-use | 13→15(T)→16 | `gcd_oneIteration` | тийм |
| y | 18 | 15 | p-use | 18→15 | `gcd_multipleIterations` | тийм |
| y | 18 | 16 | c-use | 18→15(T)→16 | `gcd_multipleIterations` | тийм |
| tmp | 16 | 18 | c-use | 16→17→18 | `gcd_oneIteration`, `gcd_multipleIterations` | тийм |

**Лекцэд дутуу байсан хос:** `y (18,16)` — давталтын дараагийн эргэн тойрогт `y`-ийн шинэ def-ийг `x % y` дээр c-use хийдэг.

### Тестүүд (`GcdTest.java`)

`mvn test -Dtest=GcdTest` → **Tests run: 4, Failures: 0**.

| Тест | Хамарсан DU хос (файлын мөр) |
|------|------------------------------|
| `gcd_noLoop_yZero` | x(13,20), y(13,15) |
| `gcd_oneIteration` | x(13,16), y(13,16), tmp(16,18) |
| `gcd_multipleIterations` | x(17,16), x(17,20), y(18,15), y(18,16), tmp(16,18) |
| `gcd_zeroX_entersLoop` | x(13,16) (`x=0` үеийн c-use) |

- `gcd(7,0)` дангаараа **x(13,20)** хосыг (давталтгүй) хамарна.
- `gcd(12,8)` **2 удаа** давталт хийж **x(17,·)**, **y(18,·)** хосуудыг хамарна.