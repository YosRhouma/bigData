param(
    [ValidateSet("city", "category", "product", "payment")]
    [string]$GroupBy = "city"
)

$ErrorActionPreference = "Stop"

$HdfsOutput = "/user/root/sales-batch/output-$GroupBy"
$LocalOutput = "output/$GroupBy"

if (Test-Path $LocalOutput) {
    Remove-Item -Recurse -Force $LocalOutput
}
New-Item -ItemType Directory -Force $LocalOutput | Out-Null

docker compose exec namenode rm -f "/tmp/$GroupBy-part-r-00000"
docker compose exec namenode hdfs dfs -getmerge $HdfsOutput "/tmp/$GroupBy-part-r-00000"
docker compose cp "namenode:/tmp/$GroupBy-part-r-00000" "$LocalOutput/part-r-00000"

$expected = Get-Content "expected/$GroupBy/part-r-00000" | Sort-Object
$actual = Get-Content "$LocalOutput/part-r-00000" | Sort-Object
$diff = Compare-Object $expected $actual

if ($diff) {
    $diff
    throw "Validation failed for group_by=$GroupBy"
}

Write-Host "Validation OK pour group_by=$GroupBy"
