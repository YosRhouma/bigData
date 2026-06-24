param(
    [ValidateSet("city", "category", "product", "payment")]
    [string]$GroupBy = "city"
)

$ErrorActionPreference = "Stop"

$HdfsBase = "/user/root/sales-batch"
$HdfsInput = "$HdfsBase/input"
$HdfsOutput = "$HdfsBase/output-$GroupBy"
$Driver = "tn.tekup.bigdata.sales.SalesRevenueDriver"
$Jar = "/project/target/sales-batch-mapreduce.jar"

docker compose up -d namenode datanode resourcemanager nodemanager historyserver
docker compose run --rm maven mvn clean package

Write-Host "Waiting for HDFS..."
$hdfsReady = $false
for ($i = 1; $i -le 30; $i++) {
    docker compose exec namenode hdfs dfs -ls / | Out-Null
    if ($LASTEXITCODE -eq 0) {
        $hdfsReady = $true
        break
    }
    Start-Sleep -Seconds 2
}

if (-not $hdfsReady) {
    throw "HDFS is not ready after 60 seconds"
}

docker compose exec namenode hdfs dfs -mkdir -p $HdfsInput
docker compose exec namenode hdfs dfs -put -f /project/data/sales.csv "$HdfsInput/"
docker compose exec namenode hdfs dfs -rm -r -f $HdfsOutput

docker compose exec namenode hadoop jar $Jar $Driver $HdfsInput $HdfsOutput $GroupBy

Write-Host ""
Write-Host "Resultat HDFS:"
docker compose exec namenode hdfs dfs -cat "$HdfsOutput/part-r-*"
