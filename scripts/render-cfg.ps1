Add-Type -AssemblyName System.Drawing
$w = 720
$h = 920
$bmp = New-Object System.Drawing.Bitmap $w, $h
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$g.Clear([System.Drawing.Color]::White)
$font = New-Object System.Drawing.Font "Segoe UI", 9
$bold = New-Object System.Drawing.Font("Segoe UI", 9, [System.Drawing.FontStyle]::Bold)
$pen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(40, 40, 40)), 2
$brush = [System.Drawing.Brushes]::Black
$fill = [System.Drawing.Color]::FromArgb(230, 242, 255)

function Draw-Box($x, $y, $bw, $bh, $text, $isBold) {
    $rect = New-Object System.Drawing.RectangleF $x, $y, $bw, $bh
    $g.FillRectangle((New-Object System.Drawing.SolidBrush $fill), $rect)
    $g.DrawRectangle($pen, $x, $y, $bw, $bh)
    $sf = New-Object System.Drawing.StringFormat
    $sf.Alignment = [System.Drawing.StringAlignment]::Center
    $sf.LineAlignment = [System.Drawing.StringAlignment]::Center
    $f = if ($isBold) { $bold } else { $font }
    $g.DrawString($text, $f, $brush, $rect, $sf)
}

function Draw-Arrow($x1, $y1, $x2, $y2, $label) {
    $g.DrawLine($pen, $x1, $y1, $x2, $y2)
    if ($label) {
        $g.DrawString($label, $font, $brush, ($x1 + $x2) / 2, ($y1 + $y2) / 2 - 14)
    }
}

$cx = 360
Draw-Box 220 20 280 44 "L31-34: guard + throw" $false
Draw-Box 220 90 280 60 "L35-37: present,streak,best=0" $false
Draw-Box 190 180 340 44 "L38: for-each (attendance p-use)" $false
Draw-Box 220 250 280 44 "L39: a > 0 ?" $true
Draw-Box 40 330 250 80 "L40-43: present++, streak++`nL42 streak>best`nfullScore_thresholdAndStreakBonus" $false
Draw-Box 430 330 250 60 "L46: streak=0`nstreakKill_midStreakBreak" $false
Draw-Box 220 440 280 50 "loop back / exit" $false
Draw-Box 220 520 280 44 "L49: score=0" $false
Draw-Box 220 580 280 44 "L50: present >= threshold ?" $true
Draw-Box 220 650 280 44 "L53: best >= 5 ?" $true
Draw-Box 220 720 280 44 "L56: return score" $false

Draw-Arrow $cx 64 $cx 90 ""
Draw-Arrow $cx 150 $cx 180 ""
Draw-Arrow $cx 224 $cx 250 ""
Draw-Arrow $cx 294 165 370 "T fullScore..."
Draw-Arrow $cx 294 555 370 "F zeroScore..."
Draw-Arrow 290 370 310 440 "T"
Draw-Arrow 555 360 400 440 "F"
Draw-Arrow $cx 490 $cx 520 ""
Draw-Arrow $cx 564 $cx 580 ""
Draw-Arrow $cx 624 $cx 650 ""
Draw-Arrow $cx 694 $cx 720 ""

$out = Join-Path $PSScriptRoot "..\cfg-bonus.png"
$bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose(); $bmp.Dispose()
Write-Output "Wrote $out"