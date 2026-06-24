# Validation avec le prof

Cette demonstration utilise uniquement Windows, Docker Desktop et PowerShell.

## 1. Presenter le sujet

Le projet est une application Big Data batch avec Hadoop MapReduce. Elle analyse un fichier CSV de ventes et calcule le chiffre d'affaires par ville.

Question traitee:

```text
Quel est le chiffre d'affaires total des ventes par ville?
```

## 2. Montrer le fichier d'entree

```powershell
Get-Content data\sales.csv -TotalCount 5
```

Colonnes importantes:

- `city`: cle de regroupement
- `quantity`: quantite vendue
- `unit_price`: prix unitaire

## 3. Montrer les classes Java

- `SaleRecord`: lecture et validation d'une ligne CSV.
- `SalesRevenueMapper`: produit les paires cle-valeur.
- `RevenueStatsWritable`: transporte `orders` et `revenue`.
- `RevenueStatsReducer`: additionne les valeurs.
- `SalesRevenueDriver`: configure et lance le job Hadoop.

## 4. Lancer l'execution complete

```powershell
.\scripts\run_docker_hadoop.ps1 city
```

Cette commande lance:

1. Les conteneurs Hadoop avec Docker Compose.
2. La compilation Maven.
3. Le chargement du CSV dans HDFS.
4. Le job MapReduce.
5. L'affichage du resultat.

Points a montrer pendant l'execution:

```text
BUILD SUCCESS
map 100% reduce 100%
```

## 5. Montrer les conteneurs Docker

```powershell
docker compose ps
```

Conteneurs importants:

- `bigdata-namenode`
- `bigdata-datanode`
- `bigdata-resourcemanager`
- `bigdata-nodemanager`
- `bigdata-historyserver`

## 6. Montrer les interfaces Web

- HDFS NameNode: http://localhost:9870
- YARN ResourceManager: http://localhost:8088

## 7. Afficher le resultat HDFS

```powershell
docker compose exec namenode hdfs dfs -cat /user/root/sales-batch/output-city/part-r-*
```

Sortie attendue:

```text
Nabeul	orders=3	revenue=566.00
Sfax	orders=3	revenue=214.00
Sousse	orders=4	revenue=325.00
Tunis	orders=5	revenue=809.00
```

## 8. Valider automatiquement

```powershell
.\scripts\validate_docker_expected.ps1 city
```

Si le resultat est correct:

```text
Validation OK pour group_by=city
```

## Questions possibles

### Pourquoi Docker?

Docker permet de reproduire le meme environnement Hadoop pour tout le monde: HDFS, YARN et MapReduce tournent dans des conteneurs.

### Pourquoi Hadoop MapReduce?

Parce que le traitement est batch: les donnees sont d'abord stockees dans HDFS, puis traitees par un job distribue.

### Quelle est la cle MapReduce?

Pour la demonstration principale, la cle est la ville (`city`).

### Quelle est la valeur MapReduce?

La valeur contient le nombre de commandes et le chiffre d'affaires calcule.

### Pourquoi utiliser un combiner?

Le combiner fait une premiere addition cote mapper pour reduire les donnees envoyees au reducer.
