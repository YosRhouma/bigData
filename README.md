# Projet Big Data - Batch Processing Hadoop MapReduce avec Docker

Ce projet suit les etapes du TP: Hadoop tourne avec Docker, les donnees sont chargees dans HDFS, puis un job MapReduce calcule le chiffre d'affaires des ventes.

## Objectif

Analyser le fichier `data/sales.csv` et calculer:

```text
revenue = quantity * unit_price
```

Le regroupement peut se faire par:

- `city`
- `category`
- `product`
- `payment`

La demonstration principale utilise `city`.

## Structure du projet

```text
.
|-- data/sales.csv
|-- docker-compose.yml
|-- docker/hadoop.env
|-- expected/
|   |-- category/part-r-00000
|   |-- city/part-r-00000
|   |-- payment/part-r-00000
|   `-- product/part-r-00000
|-- pom.xml
|-- scripts/
|   |-- run_docker_hadoop.ps1
|   `-- validate_docker_expected.ps1
`-- src/main/java/tn/tekup/bigdata/sales/
    |-- RevenueStatsReducer.java
    |-- RevenueStatsWritable.java
    |-- SaleRecord.java
    |-- SalesRevenueDriver.java
    `-- SalesRevenueMapper.java
```

## Pre-requis Windows

- Windows avec Docker Desktop lance
- Docker Compose disponible
- Connexion Internet au premier lancement pour telecharger les images Docker

Verifier:

```powershell
docker --version
docker compose version
```

## Execution simple

Depuis le dossier du projet:

```powershell
cd C:\Users\ASUS\Desktop\BigData
.\scripts\run_docker_hadoop.ps1 city
```

Le script fait automatiquement:

1. Demarrage des conteneurs Hadoop.
2. Compilation Maven du projet Java.
3. Creation du dossier HDFS.
4. Envoi de `data/sales.csv` dans HDFS.
5. Suppression de l'ancienne sortie.
6. Execution du job MapReduce.
7. Affichage du resultat.

## Resultat attendu pour `city`

```text
Nabeul	orders=3	revenue=566.00
Sfax	orders=3	revenue=214.00
Sousse	orders=4	revenue=325.00
Tunis	orders=5	revenue=809.00
```

## Validation

Apres l'execution:

```powershell
.\scripts\validate_docker_expected.ps1 city
```

Si tout est correct:

```text
Validation OK pour group_by=city
```

## Autres executions possibles

```powershell
.\scripts\run_docker_hadoop.ps1 category
.\scripts\run_docker_hadoop.ps1 product
.\scripts\run_docker_hadoop.ps1 payment
```

Validation:

```powershell
.\scripts\validate_docker_expected.ps1 category
.\scripts\validate_docker_expected.ps1 product
.\scripts\validate_docker_expected.ps1 payment
```

## Interfaces Web Docker Hadoop

Apres le demarrage:

- HDFS NameNode: http://localhost:9870
- YARN ResourceManager: http://localhost:8088
- HistoryServer: http://localhost:8188

## Commandes utiles

Voir les conteneurs:

```powershell
docker compose ps
```

Afficher le resultat HDFS manuellement:

```powershell
docker compose exec namenode hdfs dfs -cat /user/root/sales-batch/output-city/part-r-*
```

Entrer dans le conteneur principal:

```powershell
docker compose exec namenode bash
```

Arreter les conteneurs:

```powershell
docker compose down
```

Supprimer aussi les volumes HDFS:

```powershell
docker compose down -v
```

## Explication MapReduce

### Mapper

Le mapper lit chaque ligne CSV, ignore l'en-tete, calcule le montant de la commande, puis emet:

```text
cle = ville
valeur = orders=1, revenue=montant
```

Exemple:

```text
Tunis -> orders=1, revenue=90.00
```

### Combiner

Le combiner additionne localement les resultats avant l'envoi au reducer.

### Reducer

Le reducer additionne toutes les valeurs d'une meme cle.

Exemple final:

```text
Tunis -> orders=5, revenue=809.00
```

### Driver

Le driver configure le job Hadoop: mapper, reducer, combiner, types de sortie, chemin HDFS d'entree, chemin HDFS de sortie et dimension de regroupement.
