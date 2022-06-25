# KPS-Maven-Plugin

Currently, the KPS deployment process is manually by inputting it through the api gateway manager dashboard. With this plugin, the KPS data will be stored to csv format and automatic deployment to spesific api gateway instance.

## Deployment

The project can be deployed to a api gateway manager via the plugin.

Just invoke the `kps:deploy` goal within the deployment project and specify the target domain and instance. In the following example it is assumed that the Admin Node Manager is located at `localhost:8090` and `TEST_01` is the instance used. The user `admin` is allowed to deploy KPS and has the password `admin`. The example file to be used is `sample.csv` which is in the `resources` folder and KPS table name is `sample`.

```shell
mvn kps:deploy -Daxway.kps.csvfile=V1__sample__add_new_record.csv -Daxway.kps.tablename=sample -Daxway.kps.instance=TEST_01 -Daxway.anm.user=admin -Daxway.anm.password=admin -Daxway.anm.host=localhost -Daxway.anm.port=8090
```

## KPS Migration

With this plugin, all changes to the KPS are called migrations. There are 3 types

1. Versioned Migrations
   The most common type of migration is a versioned migration. Each versioned migration has a version, and a description. The version must be unique. The description is purely informative for you to be able to remember what each migration does.
   Versioned migrations are typically used for:
   - Create KPS
   - Update KPS
   - Delete KPS
2. Undo Migrations
   Undo migrations are the opposite of regular versioned migrations. An undo migration is responsible for undoing the effects of the versioned migration with the same version. Undo migrations are optional and not required to run regular versioned migrations.

### Naming KPS Migration

KPS migrations must comply with the following naming pattern

![naming KPS Migration](naming.svg)

The file name consists of the following parts:

1. Prefix: V for versioned, and U for undo.
2. Version: Version with dots or underscores separate as many parts as you like
3. Separator: __ (two underscores).
4. Description: Underscores or spaces separate the words
5. Suffix: .csv

### Action KPS Migration

Every csv file must add a column action. Column action is used to define what process will be executed. 
There are 3 actions that can be used

1. INSERT: insert new row, if data is exist then the process will not run.
2. UPDATE: update new row, if data is exist then the process will delete existing data and insert new row. And if data not exist then the process will insert new row.
3. DELETE: delete new row.

## Generate Plugin Documentation

If you want to read the detailed documentation, you can run the command below

```shell
mvn clean site
```

## License

KPS-Maven-Plugin is Open Source software released under the Apache 2.0 license.