# Rapport court - Projet Big Data Batch Processing

## 1. Sujet

Le projet consiste a developper une application Big Data en batch processing avec Hadoop MapReduce et Docker. L'application analyse un fichier CSV de ventes et calcule le chiffre d'affaires par ville, categorie, produit ou moyen de paiement.

## 2. Jeu de donnees

Le fichier utilise est `data/sales.csv`.

Colonnes:

```text
order_id,date,city,category,product,quantity,unit_price,payment
```

Exemple:

```text
1,2026-01-02,Tunis,Electronics,Keyboard,2,45.00,Card
```

Pour cette ligne:

```text
revenue = 2 * 45.00 = 90.00
```

## 3. Architecture Docker et Hadoop

Le projet utilise Docker Compose pour lancer Hadoop:

| Service Docker | Role |
| --- | --- |
| `namenode` | Gestion des metadonnees HDFS |
| `datanode` | Stockage des blocs HDFS |
| `resourcemanager` | Gestion des ressources YARN |
| `nodemanager` | Execution des taches MapReduce |
| `historyserver` | Historique des jobs |
| `maven` | Compilation du projet Java |

Chemin HDFS d'entree:

```text
/user/root/sales-batch/input
```

Chemin HDFS de sortie:

```text
/user/root/sales-batch/output-city
```

## 4. Fonctionnement MapReduce

### Mapper

Le mapper lit chaque ligne du CSV, calcule:

```text
quantity * unit_price
```

Puis il emet une paire cle-valeur.

Exemple:

```text
Tunis -> orders=1, revenue=90.00
```

### Combiner

Le combiner additionne localement les valeurs produites par un mapper afin de reduire le transfert vers le reducer.

### Reducer

Le reducer recoit toutes les valeurs d'une meme cle et calcule le total final.

Exemple:

```text
Tunis -> orders=5, revenue=809.00
```

## 5. Classes principales

| Classe | Role |
| --- | --- |
| `SaleRecord` | Lecture et validation d'une ligne CSV |
| `SalesRevenueMapper` | Transformation des lignes en paires cle-valeur |
| `RevenueStatsWritable` | Type Hadoop personnalise contenant `orders` et `revenue` |
| `RevenueStatsReducer` | Agregation finale par cle |
| `SalesRevenueDriver` | Configuration et lancement du job |

## 6. Commande d'execution Windows

```powershell
.\scripts\run_docker_hadoop.ps1 city
```

## 7. Validation

```powershell
.\scripts\validate_docker_expected.ps1 city
```

Resultat attendu:

```text
Validation OK pour group_by=city
```

## 8. Resultat attendu

```text
Nabeul	orders=3	revenue=566.00
Sfax	orders=3	revenue=214.00
Sousse	orders=4	revenue=325.00
Tunis	orders=5	revenue=809.00
```

## 9. Conclusion

Ce projet montre un pipeline Big Data batch complet avec Docker et Hadoop: demarrage du cluster, stockage dans HDFS, execution d'un job MapReduce, agregation des donnees et validation du resultat.
